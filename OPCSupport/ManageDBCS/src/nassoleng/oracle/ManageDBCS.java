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

public class ManageDBCS {
    private String username;
    private String password;
    private String identityDomain;
    private String opcDBCSURL;

    public ManageDBCS() {
        super();
        this.setOpcDBCSURL("https://dbaas.oraclecloud.com/jaas/db/api/v1.1/instances/");
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
                System.out.println ("\nDBCS Instance = " + output);

                jcsInstances = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstances;
    }

    public List <String> getDBCSInstanceNames () {
        JSONObject jcsInstances = null;
        JSONObject jcsInstance = null;
        JSONArray servicesArray = null;
        List <String> jcsNames = null;
        
        jcsInstances = getDBCSInstances ();
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

    public static void main(String[] args) {
        List <String> jcsNames = null;
        JSONObject jcsInstance = null;

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
                jcsNames = opcConnection.getDBCSInstanceNames();
                System.out.println ("DBCS Instance Name = " + jcsNames);                
            }
        }
    }
}
