package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
            System.out.println ("\nGet JCS Backups");
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + serviceName + "/backups");
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nJCS Backups = " + output);

                jcsBackups = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsBackups;
    }

    public void deleteJCSBackup(String serviceName, String backupId) {
        try {
            System.out.println ("\nDelete JCS Backup");
            Client client = ManageJCSUtil.getClient(getUsername(), getPassword());
            WebResource webResource =
                client.resource(getOpcJCSURL() + getIdentityDomain() + "/" + serviceName + "/backups/" + backupId);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", getIdentityDomain()).delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);
                System.out.println ("\nDelete JCS Backup Output = " + output);
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
        if (args.length < 3) {
            System.out.println("Usage: java ManageJCSBackups username password identityDomain\n");
        } else {            
            ManageJCSBackups  opcConnection = new ManageJCSBackups ();
            opcConnection.setUsername(args[0]);
            opcConnection.setPassword(args[1]);
            opcConnection.setIdentityDomain(args[2]);
            opcConnection.getJCSBackups("MyJCS2");
            opcConnection.getJCSBackupDetails("MyJCS2", "1422313568201");
            opcConnection.deleteJCSBackup("MyJCS2", "1422313568201");
            opcConnection.getJCSBackups("MyJCS2");
        }
    }

}
