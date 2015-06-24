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
import oracle.cloud.storage.exception.SystemException;
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
        String serviceURL = null;
        int retryCnt = 0;

        while (retryCnt <= 1) {
            try {
                // Data Center specific URL
                //serviceURL = new String ("https://storage.us2.oraclecloud.com");
                ///myConfig.setServiceName("Storage-" + this.getOpcDomain()).setUsername(this.getOpcUsername()).setPassword(this.getOpcPassword().toCharArray()).setServiceUrl(serviceURL);
                //
                // Global Namespace URL
                //
                serviceURL = new String ("https://" + this.getOpcDomain() + ".storage.oraclecloud.com");
                myConfig.setServiceName("Storage-" + this.getOpcDomain()).setUsername(this.getOpcUsername()).setPassword(this.getOpcPassword().toCharArray()).setServiceUrl(serviceURL);
                myConnection = CloudStorageFactory.getStorage(myConfig);
                retryCnt = 2;
            } catch (MalformedURLException me) {
                me.printStackTrace();
            } catch (SystemException se) {
                retryCnt++;
                if (retryCnt == 1) {
                    try {
                        System.out.println ("Sleep before retry of Storage Connection");
                        System.out.println ("Storage Serivce URL = " + serviceURL);
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw se;
                }
            }
        }
        return myConnection;
    }


    public List <String> getContainerNames () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;
        List <String> containerNames = null;

        myConnection = getStorageConnection ();
        containerNames = new ArrayList<String>();
        try {
            myContainers = myConnection.listContainers();
                
            for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
                containerNames.add(myContainers.get(i).getName());
            }     
        } catch (Exception e) {
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
        int retryCnt = 0;

        myConnection = getStorageConnection ();            
        while (retryCnt <= 2) {                        
            try {
                myConnection.createContainer(containerName);
                retryCnt = 3;
            } catch (Exception e) {
                retryCnt++;
                if (retryCnt != 3) {
                    try {
                        System.out.println ("Sleep before retry of Storage Container Create");
                        Thread.sleep(1000 * 20);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    public void DeleteContainer (String containerName) {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;
        java.util.List<Key> myContainerObjs;
        Container myContainer = null;
        boolean containerEmpty = false;
        int retryCnt = 0;

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
                        retryCnt = 0;
                        while (retryCnt <= 2) {                        
                            try {
                                myConnection.deleteObject(containerName, myContainerObjs.get(j).getKey());                            
                                retryCnt = 3;
                            } catch (Exception e) {
                                retryCnt++;
                                if (retryCnt != 3) {
                                    try {
                                        System.out.println ("Sleep before retry of Storage Object Delete");
                                        Thread.sleep(1000 * 20);
                                    } catch (InterruptedException ie) {
                                        ie.printStackTrace();
                                    }
                                } else {
                                    throw e;
                                }
                            }
                        }
                    }
                }
                retryCnt = 0;
                while (retryCnt <= 1) {                        
                    try {
                        myConnection.deleteContainer(containerName);
                        i = myContainers.size();
                        retryCnt = 2;
                    } catch (Exception e) {
                        retryCnt++;
                        if (retryCnt == 1) {
                            try {
                                System.out.println ("Sleep before retry of Storage Container Delete");
                                Thread.sleep(1000 * 10);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                        } else {
                            throw e;
                        }
                    }
                }
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

    public void opcWorkshopCleanup () {
        CloudStorage myConnection = null;
        java.util.List<Container> myContainers;

        System.out.println ("\n********************************************");
        System.out.println ("OPC Workshop Cleanup for Storage Containers");
        System.out.println ("********************************************\n");

        myConnection = getStorageConnection ();            
        myContainers = myConnection.listContainers();
            
        for ( int i = 0; myContainers != null && i < myContainers.size(); i++ ) {
            if (myContainers.get(i).getName().equals("AlphaDBCS_SC")) {
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
        createContainer ("Alpha02_SC");
        containerNames = getContainerNames();
        
        return containerNames;
    }

    public List <String> opcWorkshopCreateContainers (String studentNumber) {
        List <String> containerNames = null;

        createContainer ("Alpha" + studentNumber + "A-DBCS-SC");
        createContainer ("Alpha" + studentNumber + "A-JCS-SC");
        createContainer ("Alpha" + studentNumber + "B-JCS-SC");
        containerNames = getContainerNames();
        
        return containerNames;
    }

    public List <String> opcGenericWorkshopCreateContainers () {
        List <String> containerNames = null;

        createContainer ("SalesDevCDB-SC");
        createContainer ("SalesDev-SC");
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

        if (args.length < 4) {
            System.out.println("Usage: java DeleteStorageContainer username password identityDomain method\n");
        } else {            
            DeleteStorageContainer  delSC = new DeleteStorageContainer ();
            delSC.setOpcUsername(args[0]);
            delSC.setOpcPassword(args[1]);
            delSC.setOpcDomain(args[2]);
            if (args[3].contains("GetContainerNames")) {
                containerNames = delSC.getContainerNames();
                System.out.println ("\n********************");
                System.out.println ("Get Container Names");
                System.out.println ("********************\n");
                System.out.println ("Storage Contain Names = " + containerNames);                
            } else if (args[3].contains("PaaSDemoCleanup")) {
                delSC.paasDemoCleanup ();                
            } else if (args[3].contains("OPCWorkshopCreateContainers")) {
                System.out.println ("\n*******************************");
                System.out.println ("Create OPC Workshop Containers");
                System.out.println ("*******************************\n");
                containerNames = delSC.opcWorkshopCreateContainers();
                System.out.println ("\nStorage Contain Names = " + containerNames);
            } else if (args[3].contains("OPCWorkshopCleanup")) {
                delSC.opcWorkshopCleanup ();                
            } else if (args[3].contains("DeleteAllContainers")) {
                System.out.println ("\n**********************");
                System.out.println ("Delete All Containers");
                System.out.println ("**********************\n");
                delSC.DeleteAllContainers();
            } else if (args[3].contains("CreateContainer")) {
                if (args.length < 5) {
                    System.out.println("Usage: java DeleteStorageContainer username password identityDomain method containerName\n");
                    System.out.println("This method requires an additional parameter - ContainerName\n");
                } else {                    
                    System.out.println ("\n*********************************");
                    System.out.println ("Create Container - " + args[4]);
                    System.out.println ("*********************************\n");                    
                    delSC.createContainer(args[4]);
                }
            } else if (args[3].contains("DeleteContainer")) {
                if (args.length < 5) {
                    System.out.println("Usage: java DeleteStorageContainer username password identityDomain method containerName\n");
                    System.out.println("This method requires an additional parameter - ContainerName\n");
                } else {                    
                    System.out.println ("\n*********************************");
                    System.out.println ("Delete Container - " + args[4]);
                    System.out.println ("*********************************\n");                    
                    delSC.DeleteContainer(args[4]);
                }
            }
        }
    }
}
