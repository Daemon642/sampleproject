package nassoleng.oracle;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public String scaleDown (String instanceName, String serverName) {
        ClientResponse response = null;
        MultivaluedMap<String, String> headers = null;
        String jobURL = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            System.out.println ("\nScale Down Instance = " + instanceName + " Server = " + serverName);

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName + "/servers/" + serverName);

            response =
                webResource.header("Content-Type", "ahttps://csbduser_us%40oracle.com@developer.us2.oraclecloud.com/developer81763-usoracletrial92622/s/developer81763-usoracletrial92622_oracle-fmw-cloud-workshop/scm/developer81763-usoracletrial92622_oracle-fmw-cloud-workshop.gitpplication/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).delete(ClientResponse.class);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                    //System.out.println ("\nScale Down PaaS Demo JCS JobURL = " + jobURL);
                }
                String output = response.getEntity(String.class);
                //System.out.println ("\nScale Down JCS Output = " + output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobURL;
    }

    public void deleteJCS(String instanceName) {
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
                            status = "Terminating";
                            jobURL = scaleDown (serviceName, serverName);
                            System.out.println ("Waiting on Scale Down of " + serverName + " ....");
                            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
                            System.out.println ("Waiting on Scale Down of " + serverName + " ....");
                            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
                            System.out.println ("Waiting on Scale Down of " + serverName + " ....");
                            Thread.sleep(1000 * 60 * 2); // Sleep for 2 minutes
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
        JSONObject jcsInstance = null;

        if (args.length < 3) {
            System.out.println("Usage: java ManageJCS username password identityDomain\n");
        } else {            
            ManageJCS  opcConnection = new ManageJCS ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            opcConnection.paasDemoCleanup();
            //jcsInstance = opcConnection.getJCSInstanceInfo("Alpha01JCS");
            //opcConnection.deleteJCS("Alpha01JCS");
        }
    }
}
