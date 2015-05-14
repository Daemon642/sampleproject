package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

import java.util.Set;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import javax.ws.rs.core.NewCookie;

import org.codehaus.jettison.json.JSONException;
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
    public void testComputeCLI (String dbcsName) {
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        // JSONObject dbcsInstance = null;
        String dbcsIP = null;

        try {
            // dbcsInstance = this.manageDBCS.getDBCSInstanceInfo(dbcsName);
            // dbcsIP = dbcsInstance.getString("em_url").substring(8);
            // dbcsIP = dbcsIP.substring(0,dbcsIP.indexOf(":"));
            // batchFile = new File(this.getConfigProperties().getProperty("scriptLocation") + "runOPCWorkshopDatabaseSetup.sh");
            batchFile = new File("/u01/OPCWorkshop/lab/GSEScripts/reviewComputeOrchestration.sh");
            procBuilder =
                //new ProcessBuilder(batchFile.getAbsolutePath(), dbcsIP);
                new ProcessBuilder(batchFile.getAbsolutePath(), "z12");
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
        //} catch (JSONException e) {
        //    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public JSONObject securityApplication() {
        JSONObject jsonOutput = null;

        try {
            // Client client = ManageComputeUtil.getClient();
            Client client = new Client();

            WebResource webResource =
                client.resource(getOpcComputeURL() + "/authenticate/");
            
            String body = new String (
            "{\n" + 
            "    \"password\" : \"" + getPassword() + "\",\n" + 
            "    \"user\" : \"/Compute-" + getIdentityDomain() + "/" + getUsername() + "\"\n" + 
            "}");

            System.out.println ("\nBody = " + body);       
            WebResource.Builder webResourceBuilder = webResource.getRequestBuilder();
            
            // ClientResponse response = webResource.post(ClientResponse.class, body);
            ClientResponse response = webResourceBuilder.post(ClientResponse.class, body);

            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                final MultivaluedMap<String,String> headers = response.getHeaders();
                System.out.println ("Headers = " + headers);
            }
            
            webResource = client.resource(getOpcComputeURL() + "/secapplication/Compute-usoracle16033/");
            webResourceBuilder = webResource.getRequestBuilder();
            
            
            System.out.println("Response Header="+response.getHeaders().getFirst("Set-Cookie"));
            
            for (NewCookie c : response.getCookies() ) {
                System.out.println("Adding cookie \n"+c.toString()+"\n" + c.getName()+"\nValue=("+c.getValue()+")\nPath="+c.getPath()+"\nAge="+c.getMaxAge());
    
                webResourceBuilder.cookie(c);
            }
            
            
            
             response = webResourceBuilder.get(ClientResponse.class);
            //response = webResource.get(ClientResponse.class);

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
    
    public static void main(String[] args) {
        
             
        ManageCompute manageCompute = new ManageCompute ();
            
            
        List <String> jcsNames = null;
        JSONObject jcsInstance = null;
        
        // ./runManageOPC_JCS.sh pat.davies@oracle.com Alpha2014_ usoracle16033 Z12 Rev
     
         // Connect to Compute
        manageCompute = new ManageCompute ();
        manageCompute.setUsername("pat.davies@oracle.com");
        manageCompute.setPassword("Alpha2014_");
        manageCompute.setComputeZone("Z12");
        manageCompute.setIdentityDomain("usoracle16033");
        
        //manageCompute.authCompute();
        //manageCompute.securityApplication();
        
        manageCompute.testComputeCLI("null");
                 

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
