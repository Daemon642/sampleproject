package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ManageJCSBackups {
    private String username;
    private String password;
    private String identityDomain;
    private String opcJCSURL;

    public ManageJCSBackups() {
        super();
        this.setOpcJCSURL("https://jaas.oraclecloud.com/jaas/api/v1.1/instances/");
    }

    public JSONObject getJCSBackupDetails(String serviceName, String backupId) {
        JSONObject jcsBackups = null;

        try {
            System.out.println ("\nGet JCS Backup Details");
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + serviceName + "/backups/" + backupId);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nJCS Backup Details = " + output);

                jcsBackups = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsBackups;
    }

    public JSONObject getJCSBackups(String serviceName) {
        JSONObject jcsBackups = null;

        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + serviceName + "/backups");
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                //System.out.println ("\nJCS Backups = " + output);

                jcsBackups = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsBackups;
    }

    public void deleteJCSBackup(String serviceName, String backupId) {
        try {
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + serviceName + "/backups/" + backupId);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void paasDemoCleanupBackups () {
        JSONObject jcsBackups = null;
        JSONArray backupsArray = null;
        JSONObject jcsBackup = null;
        String notes = null;
        String goldBackupId = null;

        System.out.println ("\n**********************************");
        System.out.println ("Paas Demo Cleanup for JCS Backups");
        System.out.println ("**********************************\n");
        

        jcsBackups = getJCSBackups("MyJCS2");
        try {
            backupsArray = jcsBackups.getJSONArray("backups");
            for (int i = 0; i < backupsArray.length(); i++) {
                jcsBackup = backupsArray.getJSONObject(i);
                if (jcsBackup.has("notes")) {
                    notes = jcsBackup.getString("notes");
                }
                if (notes != null && notes.equals("Gold Backup for Paas Demo...")) {
                    goldBackupId = jcsBackup.getString("backupId");
                } else {
                    System.out.println ("\nDelete backup " + jcsBackup.getString("backupId"));
                    deleteJCSBackup("MyJCS2", jcsBackup.getString("backupId"));                                        
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
        if (args.length < 3) {
            System.out.println("Usage: java ManageJCSBackups username password identityDomain\n");
        } else {            
            ManageJCSBackups  opcConnection = new ManageJCSBackups ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            //opcConnection.getJCSBackups("MyJCS2");
            //opcConnection.getJCSBackupDetails("MyJCS2", "1422313568201");
            opcConnection.paasDemoCleanupBackups ();
            //opcConnection.deleteJCSBackup("MyJCS2", "1422313568201");
            //opcConnection.getJCSBackups("MyJCS2");
        }
    }

}
