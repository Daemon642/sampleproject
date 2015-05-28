package nassoleng.oracle;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ManageDBCS {
    private String username;
    private String password;
    private String identityDomain;
    private String opcDBCSURL;
    private Properties configProperties;

    public ManageDBCS() {
        super();
        readConfigProperties ();
        this.setOpcDBCSURL(this.getConfigProperties().getProperty("opcDBCSURL"));
    }

    public void readConfigProperties () {
        InputStream input = null;
        
        try {
            this.configProperties = new Properties ();
            
            input = new FileInputStream ("config.properties");
            
            this.configProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace ();
        }
    }
    
    public JSONObject getDBCSInstances() {
        JSONObject jcsInstances = null;

        try {
            Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcDBCSURL() + getIdentityDomain());
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                //System.out.println ("\nDBCS Instance = " + output);

                jcsInstances = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstances;
    }

    public List <String> getDBCSInstanceNames () {
        JSONObject dbcsInstances = null;
        JSONObject dbcsInstance = null;
        JSONArray servicesArray = null;
        List <String> dbcsNames = null;
        
        dbcsInstances = getDBCSInstances ();
        dbcsNames = new ArrayList<String>();
        try {
            servicesArray = dbcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                dbcsInstance = servicesArray.getJSONObject(i);
                dbcsNames.add(dbcsInstance.getString("service_name") + " - " + dbcsInstance.getString("status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
                                             
        return dbcsNames;
    }

    public JSONObject getDBCSInstanceInfo(String instanceName) {
        JSONObject dbcsInstance = null;
        int retryCnt = 0;

        try {
            if (retryCnt <= 1) {
                retryCnt++;
                Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());
                WebResource webResource =
                    client.resource(getOpcDBCSURL() + getIdentityDomain() + "/" + instanceName);
                ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);
    
                if (response.getStatus() != 200) {
                    if (retryCnt <= 1) {
                        Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                    }
                    else
                       throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                } else {
                    retryCnt++;
                    String output = response.getEntity(String.class);
                    //System.out.println ("\nDBCS Instance = " + output);
    
                    dbcsInstance = new JSONObject(output);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbcsInstance;
    }

    public String getJobStatus(String jobURL) {
        JSONObject jobResponse = null;
        String jobStatus = null;

        try {
            Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(jobURL);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() == 202) {
                String output = response.getEntity(String.class);
                //System.out.println ("\nJob Status = " + output);

                jobResponse = new JSONObject(output);
                try {
                    jobStatus = jobResponse.getString("status");
                } catch (JSONException je) {
                    jobStatus = "Completed";                                    
                }
            } else if (response.getStatus() == 200) {
                jobStatus = "Completed";                
            } else  {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobStatus;
    }

    public void getDBCSInstanceIPs () {
        JSONObject dbcsInstances = null;
        JSONObject dbcsInstance = null;
        JSONObject dbcsInfo = null;
        JSONArray servicesArray = null;
        String dbcsName = null;
        String dbIP = null;
        
        dbcsInstances = getDBCSInstances ();
        try {
            servicesArray = dbcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                dbcsInstance = servicesArray.getJSONObject(i);
                if (dbcsInstance.getString("status").equals("Running")) {
                    dbcsName = dbcsInstance.getString("service_name");
                    dbcsInfo = getDBCSInstanceInfo(dbcsName);
                    dbIP = dbcsInfo.getString("em_url").substring(8);
                    dbIP = dbIP.substring(0,dbIP.indexOf(":"));
                    System.out.println (dbcsName + " DB IP = " + dbIP);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void createAlphaDBCS () {
        ClientResponse response = null;
        String jobURL = null;
        String instanceName = null;
        String domainName = null;

        try {  
            Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());
    
            WebResource webResource =
                client.resource(getOpcDBCSURL() + getIdentityDomain());

            instanceName = "AlphaDBCS";
            /* 11G
            String se = new String (
                "{\n" + 
                "    \"serviceName\" : \"" + instanceName + "\",\n" + 
                "    \"version\" : \"11.2.0.4\",\n" + 
                "    \"level\" : \"PAAS\",\n" + 
                "    \"description\" : \"Alpha Office Database Cloud Service\",\n" + 
                "    \"edition\" : \"EE\",\n" + 
                "    \"subscriptionType\" : \"HOURLY\",\n" + 
                "    \"shape\" : \"oc3\",\n" + 
                "\"parameters\" : [\n" + 
                "    {\n" + 
                "        \"type\" : \"db\",\n" + 
                "        \"usableStorage\" : \"10\",\n" + 
                "        \"adminPassword\" : \"Alpha2014_\",\n" + 
                "        \"sid\" : \"ORCL\",\n" + 
                "        \"failoverDatabase\" : \"no\",\n" + 
                "        \"backupDestination\" : \"BOTH\",\n" + 
                "        \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/AlphaDBCS_SC\",\n" + 
                "        \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
                "        \"cloudStoragePwd\" : \"" + getPassword() + "\"\n" + 
                "    }],\n" + 
                "    \"vmPublicKeyText\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
                "}");
            */
            String se = new String (
                "{\n" + 
                "    \"serviceName\" : \"" + instanceName + "\",\n" + 
                "    \"version\" : \"12.1.0.2\",\n" + 
                "    \"level\" : \"PAAS\",\n" + 
                "    \"description\" : \"Alpha Office Database Cloud Service\",\n" + 
                "    \"edition\" : \"EE\",\n" + 
                "    \"subscriptionType\" : \"HOURLY\",\n" + 
                "    \"shape\" : \"oc3\",\n" + 
                "\"parameters\" : [\n" + 
                "    {\n" + 
                "        \"type\" : \"db\",\n" + 
                "        \"usableStorage\" : \"10\",\n" + 
                "        \"adminPassword\" : \"Alpha2014_\",\n" + 
                "        \"sid\" : \"ORCL\",\n" + 
                "        \"pdf\" : \"PDB1\",\n" + 
                "        \"failoverDatabase\" : \"no\",\n" + 
                "        \"backupDestination\" : \"BOTH\",\n" + 
                "        \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/AlphaDBCS_SC\",\n" + 
                "        \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
                "        \"cloudStoragePwd\" : \"" + getPassword() + "\"\n" + 
                "    }],\n" + 
                "    \"vmPublicKeyText\" : \"" + this.getConfigProperties().getProperty("publicKey") + "\"\n" + 
                "}");
            
            System.out.println ("\nBody = " + se);
            response = webResource.header("Content-Type", "application/json").header("X-ID-TENANT-NAME", getIdentityDomain()).post(ClientResponse.class, se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                final MultivaluedMap<String,String> headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                }
                System.out.println("Output from Server .... \n");                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAlphaDBCS (String studentNumber) throws NoSuchAlgorithmException, KeyManagementException {
        ClientResponse response = null;
        String jobURL = null;
        String instanceName = null;
        String domainName = null;

        Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());
    
        WebResource webResource =
            client.resource(getOpcDBCSURL() + getIdentityDomain());

        instanceName = "Alpha" + studentNumber + "A-DBCS";
        /* 11G
        String se = new String (
            "{\n" + 
            "    \"serviceName\" : \"" + instanceName + "\",\n" + 
            "    \"version\" : \"11.2.0.4\",\n" + 
            "    \"level\" : \"PAAS\",\n" + 
            "    \"description\" : \"Alpha Office Database Cloud Service\",\n" + 
            "    \"edition\" : \"EE\",\n" + 
            "    \"subscriptionType\" : \"HOURLY\",\n" + 
            "    \"shape\" : \"oc3\",\n" + 
            "\"parameters\" : [\n" + 
            "    {\n" + 
            "        \"type\" : \"db\",\n" + 
            "        \"usableStorage\" : \"10\",\n" + 
            "        \"adminPassword\" : \"Alpha2014_\",\n" + 
            "        \"sid\" : \"ORCL\",\n" + 
            "        \"failoverDatabase\" : \"no\",\n" + 
            "        \"backupDestination\" : \"BOTH\",\n" + 
            "        \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/AlphaDBCS_SC\",\n" + 
            "        \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
            "        \"cloudStoragePwd\" : \"" + getPassword() + "\"\n" + 
            "    }],\n" + 
            "    \"vmPublicKeyText\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
            "}");
        */
        String se = new String (
            "{\n" + 
            "    \"serviceName\" : \"" + instanceName + "\",\n" + 
            "    \"version\" : \"12.1.0.2\",\n" + 
            "    \"level\" : \"PAAS\",\n" + 
            "    \"description\" : \"Alpha Office Database Cloud Service\",\n" + 
            "    \"edition\" : \"EE\",\n" + 
            "    \"subscriptionType\" : \"HOURLY\",\n" + 
            "    \"shape\" : \"oc3\",\n" + 
            "\"parameters\" : [\n" + 
            "    {\n" + 
            "        \"type\" : \"db\",\n" + 
            "        \"usableStorage\" : \"10\",\n" + 
            "        \"adminPassword\" : \"Alpha2014_\",\n" + 
            "        \"sid\" : \"ORCL\",\n" + 
            "        \"pdf\" : \"PDB1\",\n" + 
            "        \"failoverDatabase\" : \"no\",\n" + 
            "        \"backupDestination\" : \"BOTH\",\n" + 
            "        \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/Alpha" + studentNumber + "A-DBCS-SC\",\n" + 
            "        \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
            "        \"cloudStoragePwd\" : \"" + getPassword() + "\"\n" + 
            "    }],\n" + 
            "    \"vmPublicKeyText\" : \"" + this.getConfigProperties().getProperty("publicKey") + "\"\n" + 
            "}");
            
        System.out.println ("\nBody = " + se);
        response = webResource.header("Content-Type", "application/json").header("X-ID-TENANT-NAME", getIdentityDomain()).post(ClientResponse.class, se);

        if (response.getStatus() != 202) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        } else {
            final MultivaluedMap<String,String> headers = response.getHeaders();
            if (headers != null) {
                jobURL = headers.getFirst("Location");
            }
            System.out.println("Output from Server .... \n");                
        }
    }

    public void createDBCS () {
        JSONObject dbcsInstance = null;
        String status = "In Progress";
        
        System.out.println ("\n***************************");
        System.out.println ("Create AlphaDBCS Instance");
        System.out.println ("***************************\n");
        
        try {
            createAlphaDBCS ();
            System.out.print ("Waiting on Create of AlphaDBCS Instance....");
            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
            while (status.contains("In Progress")) {
                System.out.print (".");
                Thread.sleep(1000 * 10);
                dbcsInstance = getDBCSInstanceInfo("AlphaDBCS");
                if ((dbcsInstance !=null) && (dbcsInstance.has("status")))
                    status = dbcsInstance.getString("status");
            }
            System.out.println ("\nAlphaDBCS Instance Create finshied....");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
    public void createDBCS (String studentNumber) {
        JSONObject dbcsInstance = null;
        String status = "In Progress";
        
        System.out.println ("\n***************************");
        System.out.println ("Create AlphaDBCS Instance");
        System.out.println ("***************************\n");
        
        try {
            createAlphaDBCS (studentNumber);
            System.out.print ("Waiting on Create of AlphaDBCS Instance....");
            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
            while (status.contains("In Progress")) {
                System.out.print (".");
                Thread.sleep(1000 * 10);
                dbcsInstance = getDBCSInstanceInfo("Alpha" + studentNumber + "A-DBCS");
                if ((dbcsInstance !=null) && (dbcsInstance.has("status")))
                    status = dbcsInstance.getString("status");
            }
            System.out.println ("\nAlphaDBCS Instance Create finshied....");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public String deleteDBCS(String instanceName) {
        ClientResponse response = null;
        MultivaluedMap<String, String> headers = null;
        String jobURL = null;

        try {
            Client client = ManageDBCSUtil.getClient(getUsername(), getPassword());

            WebResource webResource =
                client.resource(getOpcDBCSURL() + getIdentityDomain() + "/" + instanceName);

            response =
                webResource.header("Content-Type", "application/json").header("X-ID-TENANT-NAME", getIdentityDomain()).delete(ClientResponse.class);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                    //System.out.println ("\nDelete DBCS JobURL = " + jobURL);
                }
                String output = response.getEntity(String.class);
                //System.out.println ("\nDelete PaaS Demo DBCS Output = " + output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobURL;
    }

    public void deleteAlphaDBCS() {
        JSONObject dbcsInstances = null;
        JSONObject dbcsInstance = null;
        JSONArray servicesArray = null;
        String serviceName = "AlphaDBCS";
        String status = "Terminating";
        String jobURL = null;
        
        try {
            status = "Terminating";
            System.out.println ("Delete DBCS " + serviceName);
            jobURL = deleteDBCS (serviceName);
            System.out.print ("Waiting on DBCS to be deleted ....");
            Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
            status = getJobStatus(jobURL);                        
            while (status.equals("Terminating")) {
                System.out.print (".");
                Thread.sleep(1000 * 10);
                status = getJobStatus(jobURL);                        
            }
            System.out.println ("\nDBCS " + serviceName + " has been deleted\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    public void deleteAllDBCS() {
        JSONObject dbcsInstances = null;
        JSONObject dbcsInstance = null;
        JSONArray servicesArray = null;
        String serviceName = null;
        String status = "Terminating";
        String jobURL = null;
        
        dbcsInstances = getDBCSInstances ();
        try {
            servicesArray = dbcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                dbcsInstance = servicesArray.getJSONObject(i);
                serviceName = dbcsInstance.getString("service_name");
                status = "Terminating";
                System.out.println ("Delete DBCS " + serviceName);
                jobURL = deleteDBCS (serviceName);
                System.out.print ("Waiting on DBCS to be deleted ....");
                Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                status = getJobStatus(jobURL);                        
                while (status.equals("Terminating")) {
                    System.out.print (".");
                    Thread.sleep(1000 * 10);
                    status = getJobStatus(jobURL);                        
                }
                System.out.println ("\nDBCS " + serviceName + " has been deleted\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setIdentityDomain(String identityDomain) {
        this.identityDomain = identityDomain;
    }

    public String getIdentityDomain() {
        return identityDomain;
    }

    public void setOpcDBCSURL(String opcDBCSURL) {
        this.opcDBCSURL = opcDBCSURL;
    }

    public String getOpcDBCSURL() {
        return opcDBCSURL;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public static void main(String[] args) {
        List <String> dbcsNames = null;
        JSONObject dbcsInstance = null;

        if (args.length < 4) {
            System.out.println("Usage: java ManageDBCS username password identityDomain method\n");
        } else {            
            ManageDBCS  opcConnection = new ManageDBCS ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            if (args[3].contains("GetDBCSInstanceNames")) {
                System.out.println ("\n***********************");
                System.out.println ("Get DBCS Instance Names");
                System.out.println ("***********************\n");                    
                dbcsNames = opcConnection.getDBCSInstanceNames();
                System.out.println ("DBCS Instance Name = " + dbcsNames);                
            } else if (args[3].contains("GetDBCSInstanceIPs")) {
                    System.out.println ("\n***********************");
                    System.out.println ("Get DBCS Instance IPs");
                    System.out.println ("***********************\n");                    
                    opcConnection.getDBCSInstanceIPs();
            } else if (args[3].contains("CreateAlphaDBCS")) {
                System.out.println ("\n******************");
                System.out.println ("Create AlphaDBCS");
                System.out.println ("******************\n");                    
                opcConnection.createDBCS();
                dbcsNames = opcConnection.getDBCSInstanceNames();
                System.out.println ("\nDBCS Instance Name = " + dbcsNames);                
            } else if (args[3].contains("GetDBCSInstanceInfo")) {
                System.out.println ("\n******************************");
                System.out.println ("Get AlphaDBCS Instance Details");
                System.out.println ("******************************\n");                    
                opcConnection.getDBCSInstanceInfo("AlphaDBCS");
            } else if (args[3].contains("DeleteAllDBCS")) {
                System.out.println ("\n***************");
                System.out.println ("Delete All DBCS");
                System.out.println ("***************\n");     
                opcConnection.deleteAllDBCS();
            } else if (args[3].contains("DeleteAlphaDBCS")) {
                System.out.println ("\n***************");
                System.out.println ("Delete AlphaDBCS");
                System.out.println ("***************\n");     
                opcConnection.deleteAlphaDBCS();
            }
        }
    }
}
