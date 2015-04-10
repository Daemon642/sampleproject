package nassoleng.oracle;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;

public class ManageOPC {
    private String username;
    private String password;
    private String identityDomain;
    private DeleteStorageContainer  manageSC;
    private ManageDBCS  manageDBCS;
    private ManageJCS  manageJCS;
   
    public ManageOPC() {
        super();
    }

    public void initOPC () {
        // Connect to Storage
        this.manageSC = new DeleteStorageContainer ();
        this.manageSC.setOpcUsername(this.getUsername());
        this.manageSC.setOpcPassword(this.getPassword());
        this.manageSC.setOpcDomain(this.getIdentityDomain());
        // Connect to DBCS
        this.manageDBCS = new ManageDBCS ();
        this.manageDBCS.setUsername(this.getUsername());
        this.manageDBCS.setPassword(this.getPassword());
        this.manageDBCS.setIdentityDomain(this.getIdentityDomain());
        // Connect to JCS
        this.manageJCS = new ManageJCS ();
        this.manageJCS.setUsername(this.getUsername());
        this.manageJCS.setPassword(this.getPassword());
        this.manageJCS.setIdentityDomain(this.getIdentityDomain());        
    }

    public void reviewAccount () {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;

        System.out.println ("\n*******************************************");
        System.out.println ("Review of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");                    
        containerNames = manageSC.getContainerNames();
        System.out.println ("Storage Contain Names = " + containerNames);    
        dbcsNames = manageDBCS.getDBCSInstanceNames();
        System.out.println ("DBCS Instance Name = " + dbcsNames);      
        jcsNames = manageJCS.getJCSInstanceNames();
        System.out.println ("JCS Instance Name = " + jcsNames);                
    }

    public void cleanupAccount () {
        List <String> containerNames = null;
        List <String> dbcsNames = null;
        List <String> jcsNames = null;

        System.out.println ("\n*******************************************");
        System.out.println ("Cleanup of OPC Account " + this.getIdentityDomain());
        System.out.println ("*******************************************\n");                    
        this.manageJCS.deleteAllJCS();
        this.manageDBCS.deleteAllDBCS();
        this.manageSC.DeleteAllContainers();
        reviewAccount();
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

    public void setManageSC(DeleteStorageContainer manageSC) {
        this.manageSC = manageSC;
    }

    public DeleteStorageContainer getManageSC() {
        return manageSC;
    }

    public void setManageDBCS(ManageDBCS manageDBCS) {
        this.manageDBCS = manageDBCS;
    }

    public ManageDBCS getManageDBCS() {
        return manageDBCS;
    }

    public void setManageJCS(ManageJCS manageJCS) {
        this.manageJCS = manageJCS;
    }

    public ManageJCS getManageJCS() {
        return manageJCS;
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java ManageOPC username password identityDomain method\n");
        } else {            
            ManageOPC manageOPC = new ManageOPC ();
            manageOPC.setUsername(args[0]);
            manageOPC.setPassword(args[1]);
            manageOPC.setIdentityDomain(args[2]);
            manageOPC.initOPC();

            if (args[3].contains("ReviewAccount")) {
                manageOPC.reviewAccount();
            } else if (args[3].contains("CleanupAccount")) {
                manageOPC.cleanupAccount();
            }
        }
    }
}
