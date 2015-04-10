package nassoleng.oracle;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;

public class ManageOPC {
    private String username;
    private String password;
    private String identityDomain;
   
    public ManageOPC() {
        super();
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
    public static void main(String[] args) {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;
        JSONObject dbcsInstance = null;
        JSONObject jcsInstance = null;

        if (args.length < 4) {
            System.out.println("Usage: java ManageOPC username password identityDomain method\n");
        } else {            
            // Connect to Storage
            DeleteStorageContainer  manageSC = new DeleteStorageContainer ();
            manageSC.setOpcUsername(args[0]);
            manageSC.setOpcPassword(args[1]);
            manageSC.setOpcDomain(args[2]);
            // Connect to DBCS
            ManageDBCS  manageDBCS = new ManageDBCS ();
            manageDBCS.setUsername(args[0]);
            manageDBCS.setPassword(args[1]);
            manageDBCS.setIdentityDomain(args[2]);
            // Connect to JCS
            ManageJCS  manageJCS = new ManageJCS ();
            manageJCS.setUsername(args[0]);
            manageJCS.setPassword(args[1]);
            manageJCS.setIdentityDomain(args[2]);

            if (args[3].contains("ReviewAccount")) {
                System.out.println ("\n*******************************************");
                System.out.println ("Review of OPC Account " + args[2]);
                System.out.println ("*******************************************\n");                    
                containerNames = manageSC.getContainerNames();
                System.out.println ("Storage Contain Names = " + containerNames);    
                dbcsNames = manageDBCS.getDBCSInstanceNames();
                System.out.println ("DBCS Instance Name = " + dbcsNames);      
                jcsNames = manageJCS.getJCSInstanceNames();
                System.out.println ("JCS Instance Name = " + jcsNames);                
            }
        }
    }
}
