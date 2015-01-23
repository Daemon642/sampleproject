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
        super();
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


    public void ListContainers () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        System.out.println ("CloudStorage List of Containers:");
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            System.out.println ("\n   CloudStorage Container Name = " + myContainers.get(i).getName());
        }            
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

    public void DeleteContainer (String containerName) {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;
        java.util.List<Key> myContainerObjs;
        Container myContainer = null;
        
        myConnection = getStorageConnection ();            

        myContainers = myConnection.listContainers();

        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            if (myContainers.get(i).getName().equals(containerName)) {
                myContainer = myContainers.get(i);
                myContainerObjs = myConnection.listObjects(containerName, null);
                for ( int j = 0; myContainerObjs != null && j < myContainerObjs.size(); j++ ) {
                    System.out.println ("Object Key = " + myContainerObjs.get(j).getKey());
                    myConnection.deleteObject(containerName, myContainerObjs.get(j).getKey());
                }
                myConnection.deleteContainer(containerName);
                i = myContainers.size();
            }
        }            
    }

    public void paasDemoCleanup () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        System.out.println ("*****************************************");
        System.out.println ("Paas Demo Cleanup for Storage Containers");
        System.out.println ("*****************************************\n");

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            if (myContainers.get(i).getName().equals("MyJCS1")) {
                System.out.println ("Do not Delete " + myContainers.get(i).getName());
            } else if (myContainers.get(i).getName().equals("MyJCS2")) {
                System.out.println ("Do not Delete " + myContainers.get(i).getName());                
            } else {
                System.out.println ("Delete " + myContainers.get(i).getName());
            }
        }            
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
        int firstArg;
        if (args.length < 3) {
            System.out.println("Usage: java DeleteStorageContainer username password identityDomain\n");
        } else {            
            DeleteStorageContainer  delSC = new DeleteStorageContainer ();
            delSC.setOpcUsername(args[0]);
            delSC.setOpcPassword(args[1]);
            delSC.setOpcDomain(args[2]);
            delSC.paasDemoCleanup ();
            //delSC.DeleteAllContainers();
            //delSC.ListContainers();
        }
    }
}
