package nassoleng.oracle;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.net.MalformedURLException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.CloudStorageConfig;
import oracle.cloud.storage.CloudStorageFactory;
import oracle.cloud.storage.model.Container;
import oracle.cloud.storage.model.Key;

public class DeleteStorageContainer {
    private String opcUsername;
    private String opcPassword;
    private String opcDomain;

    public DeleteStorageContainer() {
        super();                //System.out.println ("JCS Name = " + jcsInstance.getString("service_name"));

    }

    public CloudStorage getStorageConnection () {
        CloudStorageConfig myConfig = new CloudStorageConfig();
        CloudStorage myConnection = null;

        try {
            myConfig.setServiceName("Storage-" + this.getOpcDomain()).setUsername(this.getOpcUsername()).setPassword(this.getOpcPassword().toCharArray()).setServiceUrl("https://storage.us2.oraclecloud.com");
            myConnection = CloudStorageFactory.getStorage(myConfig);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return myConnection;
    }


    public List <String> getContainerNames () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;
        List <String> containerNames = null;

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        containerNames = new ArrayList<String>();
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            containerNames.add(myContainers.get(i).getName());
        }     
        
        return containerNames;
    }

    public void DeleteAllContainers () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            DeleteContainer (myContainers.get(i).getName());
        }            
    }

    public void createContainer (String containerName) {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        myConnection = getStorageConnection ();            
        myConnection.createContainer(containerName);
    }

    public void DeleteContainer (String containerName) {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;
        java.util.List<Key> myContainerObjs;
        Container myContainer = null;
        boolean containerEmpty = false;
        
        myConnection = getStorageConnection ();            

        myContainers = myConnection.listContainers();

        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            if (myContainers.get(i).getName().equals(containerName)) {
                while (!containerEmpty) {
                    myContainer = myContainers.get(i);
                    myContainerObjs = myConnection.listObjects(containerName, null);
                    if (myContainerObjs.size() < 10000) {
                        containerEmpty = true;
                    }
                    for ( int j = 0; myContainerObjs != null && j < myContainerObjs.size(); j++ ) {
                        //System.out.println ("Object Key = " + myContainerObjs.get(j).getKey());
                        myConnection.deleteObject(containerName, myContainerObjs.get(j).getKey());
                    }
                }
                myConnection.deleteContainer(containerName);
                i = myContainers.size();
            }
        }            
    }

    public void paasDemoCleanup () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        System.out.println ("\n*****************************************");
        System.out.println ("Paas Demo Cleanup for Storage Containers");
        System.out.println ("*****************************************\n");

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            if (myContainers.get(i).getName().equals("MyJCS2")) {
                System.out.println ("Do not Delete " + myContainers.get(i).getName());
            } else {
                System.out.println ("Delete " + myContainers.get(i).getName());
                DeleteContainer (myContainers.get(i).getName());
            }
        }            
    }

    public List <String> opcWorkshopCreateContainers () {
        List <String> containerNames = null;

        createContainer ("AlphaDBCS_SC");
        createContainer ("Alpha01_SC");
        containerNames = getContainerNames();
        
        return containerNames;
    }
    public void setOpcUsername(String opcUsername) {
        this.opcUsername = opcUsername;
    }

    public String getOpcUsername() {
        return opcUsername;
    }

    public void setOpcPassword(String opcPassword) {
        this.opcPassword = opcPassword;
    }

    public String getOpcPassword() {
        return opcPassword;
    }

    public void setOpcDomain(String opcDomain) {
        this.opcDomain = opcDomain;
    }

    public String getOpcDomain() {
        return opcDomain;
    }
    
    public static void main(String[] args) {
        List <String> containerNames = null;

        if (args.length < 3) {
            System.out.println("Usage: java DeleteStorageContainer username password identityDomain\n");
        } else {            
            DeleteStorageContainer  delSC = new DeleteStorageContainer ();
            delSC.setOpcUsername(args[0]);
            delSC.setOpcPassword(args[1]);
            delSC.setOpcDomain(args[2]);
            //containerNames = delSC.getContainerNames();
            containerNames = delSC.opcWorkshopCreateContainers();
            System.out.println ("\nStorage Contain Names = " + containerNames);
            //delSC.paasDemoCleanup ();
            //delSC.DeleteAllContainers();
            //delSC.ListContainers();
        }
    }
}
