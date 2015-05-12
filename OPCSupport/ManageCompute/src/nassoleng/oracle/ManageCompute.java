package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONObject;

public class ManageCompute {
    private String username;
    private String password;
    private String identityDomain;
    private String computeZone;
    private String opcComputeURL;
    private String nimbulaCookie;
    private Properties configProperties;

    public ManageCompute() {
        super();
        readConfigProperties ();
        //this.setOpcComputeURL("https://api-z15.compute.us2.oraclecloud.com");
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
    
    public void authCompute() {
        JSONObject jsonOutput = null;

        try {
            Client client = ManageComputeUtil.getClient();
            WebResource webResource =
                client.resource(getOpcComputeURL() + "/authenticate/");
            
            String se = new String (
            "{\n" + 
            "    \"password\" : \"" + getPassword() + "\",\n" + 
            "    \"user\" : \"/Compute-" + getIdentityDomain() + "/" + getUsername() + "\"\n" + 
            "}");

            System.out.println ("\nBody = " + se);                
            ClientResponse response = webResource.header("Content-Type", "application/oracle-compute-v3+json").header("Accept", "application/oracle-compute-v3+json").post(ClientResponse.class, se);

            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                final MultivaluedMap<String,String> headers = response.getHeaders();
                System.out.println ("Headers = " + headers.getFirst("Set-Cookie"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject securityApplication() {
        JSONObject jsonOutput = null;

        try {
            Client client = ManageComputeUtil.getClient();
            WebResource webResource =
                client.resource(getOpcComputeURL() + "/authenticate/");
            
            String se = new String (
            "{\n" + 
            "    \"password\" : \"" + getPassword() + "\",\n" + 
            "    \"user\" : \"/Compute-" + getIdentityDomain() + "/" + getUsername() + "\"\n" + 
            "}");

            System.out.println ("\nBody = " + se);                
            ClientResponse response = webResource.post(ClientResponse.class, se);

            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                final MultivaluedMap<String,String> headers = response.getHeaders();
                System.out.println ("Headers = " + headers);
            }

            webResource = client.resource(getOpcComputeURL() + "/secapplication/Compute-dfoleyoracle/");
            
            response = webResource.post(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nCompute Output = " + output);
                jsonOutput = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonOutput;
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

    public void setComputeZone(String computeZone) {
        this.computeZone = computeZone;
        this.setOpcComputeURL("https://api-" + computeZone + ".compute.us2.oraclecloud.com");
    }

    public String getComputeZone() {
        return computeZone;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public void setOpcComputeURL(String opcComputeURL) {
        this.opcComputeURL = opcComputeURL;
    }

    public String getOpcComputeURL() {
        return opcComputeURL;
    }

    public void setNimbulaCookie(String nimbulaCookie) {
        this.nimbulaCookie = nimbulaCookie;
    }

    public String getNimbulaCookie() {
        return nimbulaCookie;
    }
}
