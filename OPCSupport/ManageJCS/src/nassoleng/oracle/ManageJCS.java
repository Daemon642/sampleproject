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

    public Client getClient() throws NoSuchAlgorithmException,
                                     KeyManagementException {
        Client client = null;

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());

        HTTPSProperties prop = new HTTPSProperties(hv, sc);

        DefaultClientConfig dcc = new DefaultClientConfig();
        dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);

        client = Client.create(dcc);

        // client basic auth demonstration
        client.addFilter(new HTTPBasicAuthFilter(getUsername(), getPassword()));

        return client;
    }

    public JSONObject getJCSInstances() {
        String identityDomain = null;
        JSONObject jcsInstances = null;

        try {
            Client client = getClient();
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain());
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nJCS Instance = " + output);

                jcsInstances = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstances;
    }

    public JSONObject getJCSInstanceInfo(String instanceName) {
        String identityDomain = null;
        JSONObject jcsInstance = null;

        try {
            Client client = getClient();
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nJCS Instance = " + output);

                jcsInstance = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstance;
    }

    public void deleteJCS(String instanceName) {
        ClientResponse response = null;

        try {
            Client client = getClient();

            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + instanceName);

            String se =
                new String("{ \"dbaName\": \"SYSTEM\",\n" + "  \"dbaPassword\": \"Alpha2014_\",\n" +
                           "  \"forceDelete\": true\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type", "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME", getIdentityDomain()).put(ClientResponse.class, se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
            }

        } catch (Exception e) {
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

        System.out.println("Test output from Main");
        if (args.length < 3) {
            System.out.println("Usage: java ManageJCS username password identityDomain\n");
        } else {            
            ManageJCS  opcConnection = new ManageJCS ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            jcsInstance = opcConnection.getJCSInstances();
            //jcsInstance = opcConnection.getJCSInstanceInfo("Alpha01JCS");
            //opcConnection.deleteJCS("Alpha01JCS");
        }
    }
}
