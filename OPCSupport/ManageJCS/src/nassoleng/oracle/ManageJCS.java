package nassoleng.oracle;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ManageJCS {
    private String username;
    private String password;
    private String identityDomain;
    private String opcJCSURL;

    public ManageJCS() {
        super();
        this.setOpcJCSURL("https://jaas.oraclecloud.com/jaas/api/v1.1/instances/");
    }

    public JSONObject getJCSInstances() {
        JSONObject jcsInstances = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain());
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                //System.out.println ("\nJCS Instance = " + output);

                jcsInstances = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstances;
    }

    public List <String> getJCSInstanceNames () {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONArray servicesArray = null;
        List <String> jcsNames = null;
        
        jcsInstances = getJCSInstances ();
        jcsNames = new ArrayList<String>();
        try {
            servicesArray = jcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                jcsInstance = servicesArray.getJSONObject(i);
                jcsNames.add(jcsInstance.getString("service_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
                                             
        return jcsNames;
    }

    public void getJCSInstanceIPs () {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONObject jcsInfo = null;
        JSONArray servicesArray = null;
        String jcsName = null;
        String wlsIP = null;
        String otdIP = null;
        
        jcsInstances = getJCSInstances ();
        try {
            servicesArray = jcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                jcsInstance = servicesArray.getJSONObject(i);
                jcsName = jcsInstance.getString("service_name");
                jcsInfo = getJCSInstanceInfo(jcsName);
                wlsIP = jcsInfo.getString("wls_admin_url").substring(8);
                wlsIP = wlsIP.substring(0,wlsIP.indexOf(":"));
                otdIP = jcsInfo.getString("otd_admin_url").substring(8);
                otdIP = otdIP.substring(0,otdIP.indexOf(":"));
                System.out.println (jcsName + " WLS IP = " +  wlsIP);
                System.out.println (jcsName + " OTD IP = " + otdIP);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJCSServerDetails(String instanceName) {
        JSONObject servers = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName + "/servers");
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                //System.out.println ("\n" + instanceName + " Server Detail = " + output);

                servers = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servers;
    }

    public JSONObject getJCSInstanceInfo(String instanceName) {
        JSONObject jcsInstance = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                //System.out.println ("\nJCS Instance = " + output);

                jcsInstance = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstance;
    }

    public String getJobStatus(String jobURL) {
        JSONObject jobResponse = null;
        String jobStatus = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(jobURL);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() == 202) {
                String output = response.getEntity(String.class);
                //System.out.println ("\nJob Status = " + output);

                jobResponse = new JSONObject(output);
                jobStatus = jobResponse.getString("status");
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

    public void createAlphaJCS (String instanceNum) {
        ClientResponse response = null;
        String jobURL = null;
        String instanceName = null;
        String domainName = null;

        try {  
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
 
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain());

            instanceName = "Alpha" + instanceNum + "JCS";
            domainName = "Alpha" + instanceNum + "J_domain";
            String se = new String (
                "{\n" + 
                "    \"serviceName\" : \"" + instanceName + "\",\n" + 
                "    \"level\" : \"PAAS\",\n" + 
                "    \"subscriptionType\" : \"HOURLY\",\n" + 
                "    \"description\" : \"Alpha Office Java Cloud Service\",\n" + 
                "    \"provisionOTD\" : true,\n" + 
                "    \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/Alpha" + instanceNum + "_SC\",\n" + 
                "    \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
                "    \"cloudStoragePassword\" : \"" + getPassword() + "\",\n" + 
                " \n" + 
                "\"parameters\" : [\n" + 
                "    {\n" + 
                "        \"type\" : \"weblogic\",\n" + 
                "        \"version\" : \"12.1.3.0.2\",\n" + 
                "        \"edition\" : \"EE\",\n" + 
                "        \"domainMode\" : \"PRODUCTION\",\n" + 
                "        \"managedServerCount\" : \"1\",\n" + 
                "        \"adminPort\" : \"7001\",\n" + 
                "        \"deploymentChannelPort\" : \"9001\",\n" + 
                "        \"securedAdminPort\" : \"7002\",\n" + 
                "        \"contentPort\" : \"7003\",\n" + 
                "        \"securedContentPort\" : \"7004\",\n" + 
                "        \"domainName\" : \"" + domainName + "\",\n" + 
                "        \"clusterName\" : \"Alpha" + instanceNum + "J_cluster\",\n" + 
                "        \"adminUserName\" : \"weblogic\",\n" + 
                "        \"adminPassword\" : \"Alpha2014_\",\n" + 
                "        \"nodeManagerPort\" : \"5556\",\n" + 
                "        \"nodeManagerUserName\" : \"weblogic\",\n" + 
                "        \"nodeManagerPassword\" : \"Alpha2014_\",\n" + 
                "        \"dbServiceName\" : \"AlphaDBCS\",\n" + 
                "        \"dbaName\" : \"SYS\",\n" + 
                "        \"dbaPassword\" : \"Alpha2014_\",\n" + 
                "        \"shape\" : \"oc3\",\n" + 
                /*
                "        \"domainVolumeSize\" : \"10240M\",\n" + 
                "        \"backupVolumeSize\" : \"20480M\",\n" + 
*/
                "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
                "    },\n" + 
                "    {\n" + 
                "        \"type\" : \"otd\",\n" + 
                "        \"adminUserName\" : \"weblogic\",\n" + 
                "        \"adminPassword\" : \"Alpha2014_\",\n" + 
                "        \"listenerPortsEnabled\" : \"false\",\n" + 
                "        \"listenerPort\" : \"8080\",\n" + 
                "        \"listenerType\" : \"http\",\n" + 
                "        \"securedListenerPort\" : \"8081\",\n" + 
                "        \"loadBalancingPolicy\" : \"least_connection_count\",\n" + 
                "        \"adminPort\" : \"8989\",\n" + 
                "        \"shape\" : \"oc3\",\n" + 
                "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
                "    }\n" + 
                "]\n" + 
                "}");
            
            System.out.println ("\nBody = " + se);
            response = webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).post(ClientResponse.class, se);

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

    public void createAlpha01JCS () {
        JSONObject jcsInstance = null;
        String status = "In Progress";
        
        System.out.println ("\n***************************");
        System.out.println ("Create Alpha01JCS Instance");
        System.out.println ("***************************\n");
        
        try {
            createAlphaJCS ("01");
            System.out.println ("Waiting on Create of Alpha01JCS Instance....");
            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
            while (status.contains("In Progress")) {
                System.out.println ("Waiting on Create of Alpha01JCS Instance....");
                Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
                jcsInstance = getJCSInstanceInfo("Alpha01JCS");
                status = jcsInstance.getString("status");
            }
            System.out.println ("Alpha01JCS Instance Create finshied....");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
    public void createMyJCS2Instance () {
        ClientResponse response = null;
        String jobURL = null;
        String instanceName = null;
        String domainName = null;

        try {  
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
    
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain());

            instanceName = "MyJCS2";
            domainName = "MyJCS2_domain";
            String se = new String (
                "{\n" + 
                "    \"serviceName\" : \"" + instanceName + "\",\n" + 
                "    \"level\" : \"PAAS\",\n" + 
                "    \"subscriptionType\" : \"HOURLY\",\n" + 
                "    \"description\" : \"Paas Demo JCS\",\n" + 
                "    \"provisionOTD\" : true,\n" + 
                "    \"cloudStorageContainer\" : \"Storage-" + getIdentityDomain() + "/MyJCS2\",\n" + 
                "    \"cloudStorageUser\" : \"" + getUsername() + "\",\n" + 
                "    \"cloudStoragePassword\" : \"" + getPassword() + "\",\n" + 
                " \n" + 
                "\"parameters\" : [\n" + 
                "    {\n" + 
                "        \"type\" : \"weblogic\",\n" + 
                "        \"version\" : \"12.1.3.0.2\",\n" + 
                "        \"edition\" : \"EE\",\n" + 
                "        \"domainMode\" : \"PRODUCTION\",\n" + 
                "        \"managedServerCount\" : \"1\",\n" + 
                "        \"adminPort\" : \"7001\",\n" + 
                "        \"deploymentChannelPort\" : \"9001\",\n" + 
                "        \"securedAdminPort\" : \"7002\",\n" + 
                "        \"contentPort\" : \"7003\",\n" + 
                "        \"securedContentPort\" : \"7004\",\n" + 
                "        \"domainName\" : \"" + domainName + "\",\n" + 
                "        \"clusterName\" : \"MyJCS2_cluster\",\n" + 
                "        \"adminUserName\" : \"weblogic\",\n" + 
                "        \"adminPassword\" : \"JCSDem0#\",\n" + 
                "        \"nodeManagerPort\" : \"5556\",\n" + 
                "        \"nodeManagerUserName\" : \"weblogic\",\n" + 
                "        \"nodeManagerPassword\" : \"JCSDem0#\",\n" + 
                "        \"dbServiceName\" : \"MyDB2\",\n" + 
                "        \"dbaName\" : \"SYS\",\n" + 
                "        \"dbaPassword\" : \"JCSDem0#\",\n" + 
                "        \"shape\" : \"oc3\",\n" + 
                /*
                "        \"domainVolumeSize\" : \"10240M\",\n" + 
                "        \"backupVolumeSize\" : \"20480M\",\n" + 
*/
                "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
                "    },\n" + 
                "    {\n" + 
                "        \"type\" : \"otd\",\n" + 
                "        \"adminUserName\" : \"weblogic\",\n" + 
                "        \"adminPassword\" : \"JCSDem0#\",\n" + 
                "        \"listenerPortsEnabled\" : \"true\",\n" + 
                "        \"listenerPort\" : \"8080\",\n" + 
                "        \"listenerType\" : \"http\",\n" + 
                "        \"securedListenerPort\" : \"8081\",\n" + 
                "        \"loadBalancingPolicy\" : \"least_connection_count\",\n" + 
                "        \"adminPort\" : \"8989\",\n" + 
                "        \"shape\" : \"oc3\",\n" + 
                "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEArn21PGy1SZ6AYFlztFUL1gv63EXMbSb4qo1SzPAwZgcQXjciU8YsettV81YIFzvIedEn4mhD8ebGKK1k8oYB7HYNsSywbXmqisI+75xY37EZT6ah+cxENmVxmzpOjOYH31wj792tf/WpUUpnN8MdIlTW8uAWNIa6Mz9YhAZ0sJILDOlSNr/rorrGYyYLBtJqbVAZlwEfUSgQTkMwBWK4L7aXOLMDFFAi2oEqsjmT3rWX55YzrwXIMvNXjslen6gXqrdoCeakKMbQ788fQqb1P9hgsmHhkERJfwhgFy+R1RUfPMHdZG7P2vNLUZDd54ROCmj2F852HkertpDMFNMWrQ== oracle@oraclelinux6.localdomain\"\n" + 
                "    }\n" + 
                "]\n" + 
                "}");
            
            System.out.println ("\nBody = " + se);
            response = webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).post(ClientResponse.class, se);

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

    public void createMyJCS2 () {
        JSONObject jcsInstance = null;
        String status = "In Progress";
        
        System.out.println ("\n***************************");
        System.out.println ("Create MyJCS2 Instance");
        System.out.println ("***************************\n");
        
        try {
            createMyJCS2Instance ();
            System.out.println ("Waiting on Create of MyJCS2 Instance....");
            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
            while (status.contains("In Progress")) {
                System.out.println ("Waiting on Create of MyJCS2 Instance....");
                Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
                jcsInstance = getJCSInstanceInfo("MyJCS2");
                status = jcsInstance.getString("status");
            }
            System.out.println ("MyJCS2 Instance Create finshied....");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
    public String scaleDown (String instanceName, String serverName) {
        ClientResponse response = null;
        JSONObject jobResponse = null;
        JSONObject details = null;
        String jobId = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            System.out.println ("\nScale Down Instance = " + instanceName + " Server = " + serverName);

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName + "/servers/" + serverName);

            response =
                webResource.header("Content-Type", "application/json").header("X-ID-TENANT-NAME", getIdentityDomain()).delete(ClientResponse.class);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                jobResponse = new JSONObject(output);
                details = jobResponse.getJSONObject("details");
                jobId = details.getString("jobId");
                //System.out.println ("\nScale Down JCS Output = " + output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobId;
    }

    public String getScaleDownStatus (String instanceName, String serverName, String jobId) {
        ClientResponse response = null;
        JSONObject job = null;
        JSONArray jobsArray = null;
        String inProgress = "true";

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            //System.out.println ("\nGet Job Status Instance = " + instanceName + " Server = " + serverName);

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName + "/servers/history");

            response =
                webResource.header("Content-Type", "application/json").header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                jobsArray = new JSONArray(output);
                for (int i = 0; i < jobsArray.length(); i++) {
                    job = jobsArray.getJSONObject(i);
                    if (job.getString("jobId").equals(jobId)) {
                        inProgress = job.getString("inProgress");                        
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inProgress;
    }

    public String deleteJCS(String instanceName) {
        ClientResponse response = null;
        MultivaluedMap<String, String> headers = null;
        String jobURL = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName);

            String se =
                new String("{ \"dbaName\": \"SYSTEM\",\n" + "  \"dbaPassword\": \"Alpha2014_\",\n" +
                           "  \"forceDelete\": true\n" + "}");

            //System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).put(ClientResponse.class, se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                    //System.out.println ("\nDelete JCS JobURL = " + jobURL);
                }
                String output = response.getEntity(String.class);
                //System.out.println ("\nDelete PaaS Demo JCS Output = " + output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobURL;
    }

    public void deleteMyJCS2() {
        ClientResponse response = null;
        MultivaluedMap<String, String> headers = null;
        String jobURL = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/MyJCS2");

            String se =
                new String("{ \"dbaName\": \"SYS\",\n" + "  \"dbaPassword\": \"JCSDem0#\",\n" +
                           "  \"forceDelete\": true\n" + "}");

            //System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).put(ClientResponse.class, se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                    System.out.println ("\nDelete JCS JobURL = " + jobURL);
                }
                String output = response.getEntity(String.class);
                System.out.println ("\nDelete PaaS Demo JCS Output = " + output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String deletePaasDemoJCS(String instanceName) {
        ClientResponse response = null;
        MultivaluedMap<String, String> headers = null;
        String jobURL = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName);

            String se =
                new String("{ \"dbaName\": \"SYSTEM\",\n" + "  \"dbaPassword\": \"JCSDem0#\",\n" +
                           "  \"forceDelete\": true\n" + "}");

            //System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).put(ClientResponse.class, se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                    //System.out.println ("\nDelete PaaS Demo JCS JobURL = " + jobURL);
                }
                String output = response.getEntity(String.class);
                //System.out.println ("\nDelete PaaS Demo JCS Output = " + output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobURL;
    }

    public void deleteAllJCS() {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONArray servicesArray = null;
        String serviceName = null;
        String status = "Terminating";
        String jobURL = null;
        
        jcsInstances = getJCSInstances ();
        try {
            servicesArray = jcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                jcsInstance = servicesArray.getJSONObject(i);
                serviceName = jcsInstance.getString("service_name");
                status = "Terminating";
                System.out.println ("Delete JCS " + serviceName);
                jobURL = deleteJCS (serviceName);
                System.out.println ("Waiting on JCS to be deleted ....");
                Thread.sleep(1000 * 60 * 2); // Sleep for 1 minutes
                status = getJobStatus(jobURL);                        
                while (status.equals("Terminating")) {
                    System.out.println ("Waiting on JCS to be deleted ....");
                    Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                    status = getJobStatus(jobURL);                        
                }
                System.out.println ("JCS " + serviceName + " has been deleted\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    public void paasDemoCleanup() {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONObject server = null;
        JSONArray servicesArray = null;
        JSONArray serversArray = null;
        String serviceName = null;
        String serverName = null;
        String status = "Terminating";
        String jobURL = null;
        
        jcsInstances = getJCSInstances ();

        System.out.println ("\n**************************");
        System.out.println ("Paas Demo Cleanup for JCS");
        System.out.println ("**************************\n");
        
        try {
            servicesArray = jcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                jcsInstance = servicesArray.getJSONObject(i);
                serviceName = jcsInstance.getString("service_name");
                if (serviceName.equals("MyJCS2")) {
                    System.out.println ("Check for extra nodes " + serviceName);
                    jcsInstance = getJCSServerDetails(serviceName);
                    serversArray = jcsInstance.getJSONArray("servers");
                    for (int j = 0; j < serversArray.length(); j++) {
                        server = serversArray.getJSONObject(j);
                        serverName = server.getString("name");
                        if (!serverName.equals("MyJCS2_server_1")) {
                            status = "true";
                            jobURL = scaleDown (serviceName, serverName);
                            System.out.println ("Waiting on Scale Down of " + serverName + " ....");
                            Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                            while (status.equals("true")) {
                                System.out.println ("Waiting on Scale Down of " + serverName + " ....");
                                Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                                status = getScaleDownStatus (serviceName, serverName, jobURL);
                            }
                            System.out.println ("Server " + serverName + " has been removed\n");
                        }
                    }
                } else {
                    status = "Terminating";
                    System.out.println ("Delete JCS " + serviceName);
                    jobURL = deletePaasDemoJCS (serviceName);
                    System.out.println ("Waiting on JCS to be deleted ....");
                    Thread.sleep(1000 * 60 * 2); // Sleep for 1 minutes
                    status = getJobStatus(jobURL);                        
                    while (status.equals("Terminating")) {
                        System.out.println ("Waiting on JCS to be deleted ....");
                        Thread.sleep(1000 * 60 * 1); // Sleep for 1 minutes
                        status = getJobStatus(jobURL);                        
                    }
                    System.out.println ("JCS " + serviceName + " has been deleted\n");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    public void paasDemoReview() {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONObject server = null;
        JSONArray servicesArray = null;
        JSONArray serversArray = null;
        String serviceName = null;
        String serverName = null;
        
        jcsInstances = getJCSInstances ();

        System.out.println ("\n**************************");
        System.out.println ("Paas Demo Review for JCS");
        System.out.println ("**************************\n");
        
        try {
            servicesArray = jcsInstances.getJSONArray("services");
            for (int i = 0; i < servicesArray.length(); i++) {
                jcsInstance = servicesArray.getJSONObject(i);
                serviceName = jcsInstance.getString("service_name");
                if (serviceName.equals("MyJCS2")) {
                    System.out.println ("Instance MyJCS2 does exist...");
                    jcsInstance = getJCSServerDetails(serviceName);
                    serversArray = jcsInstance.getJSONArray("servers");
                    for (int j = 0; j < serversArray.length(); j++) {
                        server = serversArray.getJSONObject(j);
                        serverName = server.getString("name");
                        if (!serverName.equals("MyJCS2_server_1")) {
                            System.out.println ("Extra Server " + serverName + " created on MyJCS2 ....");
                        }
                    }
                } else {
                    System.out.println ("Instance " + serviceName + " exists ...");
                }
            }
        } catch (JSONException e) {
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

    public void setOpcJCSURL(String opcJCSURL) {
        this.opcJCSURL = opcJCSURL;
    }

    public String getOpcJCSURL() {
        return opcJCSURL;
    }

    public static void main(String[] args) {
        List <String> jcsNames = null;
        JSONObject jcsInstance = null;

        if (args.length < 4) {
            System.out.println("Usage: java ManageJCS username password identityDomain method\n");
        } else {            
            ManageJCS  opcConnection = new ManageJCS ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            if (args[3].contains("GetJCSInstanceNames")) {
                System.out.println ("\n***********************");
                System.out.println ("Get JCS Instance Names");
                System.out.println ("***********************\n");                    
                jcsNames = opcConnection.getJCSInstanceNames();
                System.out.println ("JCS Instance Name = " + jcsNames);                
            } else if (args[3].contains("GetJCSInstanceIPs")) {
                    System.out.println ("\n***********************");
                    System.out.println ("Get JCS Instance IPs");
                    System.out.println ("***********************\n");                    
                    opcConnection.getJCSInstanceIPs();
            } else if (args[3].contains("PaaSDemoCleanup")) {
                opcConnection.paasDemoCleanup();
            } else if (args[3].contains("PaaSDemoReview")) {
                opcConnection.paasDemoReview();
            } else if (args[3].contains("DeleteAlpha01JCS")) {
                System.out.println ("\n******************");
                System.out.println ("Delete Alpha01JCS");
                System.out.println ("******************\n");                    
                opcConnection.deleteJCS("Alpha01JCS");
            } else if (args[3].contains("DeleteJCS")) {
                if (args.length < 5) {
                    System.out.println("Usage: java ManageJCS username password identityDomain method JCSName\n");
                    System.out.println("This method requires an additional parameter - JCSName\n");
                } else {                    
                    System.out.println ("\n*****************************");
                    System.out.println ("Delete JCS - " + args[4]);
                    System.out.println ("*****************************\n");                    
                    opcConnection.deleteJCS(args[4]);
                }
            } else if (args[3].contains("DeleteAllJCS")) {
                System.out.println ("\n***************");
                System.out.println ("Delete All JCS");
                System.out.println ("***************\n");                    
                opcConnection.deleteAllJCS();
            } else if (args[3].contains("DeleteMyJCS2")) {
                System.out.println ("\n***************");
                System.out.println ("Delete MyJCS02");
                System.out.println ("***************\n");                    
                opcConnection.deleteMyJCS2();
            } else if (args[3].contains("CreateAlpha01JCS")) {
                System.out.println ("\n******************");
                System.out.println ("Create Alpha01JCS");
                System.out.println ("******************\n");                    
                opcConnection.createAlpha01JCS();
                jcsNames = opcConnection.getJCSInstanceNames();
                System.out.println ("\nJCS Instance Name = " + jcsNames);                
            } else if (args[3].contains("CreateMyJCS2")) {
                System.out.println ("\n***************");
                System.out.println ("Create MyJCS02");
                System.out.println ("***************\n");                    
                opcConnection.createMyJCS2();
                jcsNames = opcConnection.getJCSInstanceNames();
                System.out.println ("\nJCS Instance Name = " + jcsNames);                
            }
        }
    }
}
