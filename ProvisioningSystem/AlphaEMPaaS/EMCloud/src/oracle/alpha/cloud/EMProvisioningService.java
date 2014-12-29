package oracle.alpha.cloud;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import javax.net.ssl.X509TrustManager;

import javax.sql.DataSource;

import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONArray;
import org.json.JSONObject;

import provisiontables.JavaServiceFacade;
import provisiontables.PBusinessParam;
import provisiontables.PBusinessService;
import provisiontables.PBusinessValues;
import provisiontables.PTechnicalParam;
import provisiontables.PTechnicalService;

public class EMProvisioningService {
    private Properties configProperties;

    private static String STATUS_SUBMITTED = "Submitted";
    private static String STATUS_PENDING_APPROVAL = "PendingApproval";
    private static String STATUS_NOT_SUBMITTED = "NotSubmitted";
    private static String STATUS_PROVISIONING_STARTED = "ProvisioningStarted";
    private static String STATUS_APPROVAL_RECEVIED = "ApprovalReceived";
    private static String STATUS_APPROVAL_DENIED = "ApprovalDenied";
    private static String STATUS_PROVIONING_STARTED = "ProvisioningStarted";
    private static String STATUS_PROVIONING_SCHEDULED = "ProvisioningScheduled";
    private static String STATUS_PROVIONING_INITIATED = "ProvisioningInitiated";
    private static String STATUS_PROVIONING_COMPLETED = "ProvisioningCompleted";
    private static String STATUS_PROVIONING_FAILED = "ProvisioningFailed";
    private static String SCRIPT_LOCATION = "/u01/dbaas/SVNSource/CatalogProvisioning/scripts/";

    public EMProvisioningService() {
        super();
        readConfigProperties();
    }

    public void readConfigProperties() {
        InputStream input = null;

        try {
            this.configProperties = new Properties();

            input = new FileInputStream(SCRIPT_LOCATION + "config.properties");

            this.configProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static final int getImageId() {
        DataSource dataSource = null;
        Context ic = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int imageId = 1000;

        String selectString;
        selectString = "SELECT IMAGE_ID_SEQ.nextval FROM DUAL";

        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("jdbc/BusinessServiceDBConnection");
        } catch (NamingException e) {
        }
        System.out.println("lookup dataSource returned " + dataSource);
        try {
            Connection connection = dataSource.getConnection();
            System.out.println("Got connection: " + connection);

            stmt = connection.prepareStatement(selectString);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageId = rs.getInt(1);
            }
            stmt.close();
            connection.close();
        } catch (SQLException e) {
        }
        return imageId;
    }


    public void updateProvisioninStatus(String sQueueId, String statusCode, String statusMsg) {
        DataSource dataSource = null;
        Context ic = null;
        PreparedStatement stmt = null;

        String procedureString;
        procedureString = "{call UPDATE_PROVISIONING_STATUS (?, ?, ?)}";

        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("jdbc/BusinessServiceDBConnection");
        } catch (NamingException e) {
        }
        try {
            Connection connection = dataSource.getConnection();

            stmt = connection.prepareCall(procedureString);
            stmt.setString(1, sQueueId);
            stmt.setString(2, statusCode);
            stmt.setString(3, statusMsg);
            stmt.executeQuery();
            stmt.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
        }
    }

    public Client getClient(String sQueueId, String teamNum) throws NoSuchAlgorithmException, KeyManagementException {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());

        HTTPSProperties prop = new HTTPSProperties(hv, sc);

        DefaultClientConfig dcc = new DefaultClientConfig();
        dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);

        Client client = Client.create(dcc);

        // client basic auth demonstration
        if (teamNum != null) {
            String teamName = String.format("team%s", teamNum);
            client.addFilter(new HTTPBasicAuthFilter(teamName, this.getConfigProperties().getProperty(teamName)));
        } else {
            client.addFilter(new HTTPBasicAuthFilter(this.getConfigProperties().getProperty("emUsername"),
                                                     this.getConfigProperties().getProperty("emPassword")));
        }

        return client;
    }

    public Client getPublicCloudClient(String sQueueId, boolean useHTTPS) throws NoSuchAlgorithmException,
                                                                                 KeyManagementException {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        Client client = null;

        if (useHTTPS) {
            System.out.println("\ngetPublicCloudClient - use Https");
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Ignore differences between given hostname and certificate hostname
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());

            HTTPSProperties prop = new HTTPSProperties(hv, sc);

            DefaultClientConfig dcc = new DefaultClientConfig();
            dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);

            client = Client.create(dcc);
        } else {
            System.out.println("\ngetPublicCloudClient - use Http");
            client = Client.create();
        }

        // client basic auth demonstration
        client.addFilter(new HTTPBasicAuthFilter(this.getConfigProperties().getProperty("cloudUsername"),
                                                 this.getConfigProperties().getProperty("cloudPassword")));

        return client;
    }

    public String getRequestStatusInfo(String businessServiceId, String teamNum) {
        String uri = "/em/cloud";
        String statusStr = null;

        try {
            System.out.println("\n getRequestStatusInfo");
            Client client = getClient(businessServiceId, teamNum);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + uri);
            ClientResponse response =
                webResource.accept("application/oracle.com.cloud.common.Cloud+json").get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject requestObj = jsonObj.getJSONObject("service_requests");
            JSONArray elementsArray = requestObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                if (elementObj.getString("name").contains(businessServiceId)) {
                    statusStr = elementObj.getString("status");
                    System.out.println("\n getRequestStatusInfo - Status = " + statusStr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusStr;
    }

    public String getEMR4RequestStatusInfo(String sQueueId, String requestURIName, String teamNum) {
        String statusStr = null;
        String instanceURI = null;
        String requestURI = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;

        try {
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals(requestURIName)) {
                    requestURI = pBusinessValuesList.get(i).getVal();
                }
            }

            System.out.println("\nRequestURI = " + requestURI);
            if (requestURI != null) {
                Client client = getClient(sQueueId, teamNum);

                WebResource webResource =
                    client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
                ClientResponse response = webResource.get(ClientResponse.class);

                if (response.getStatus() != 200) {
                    statusStr = "FAILED";
                    //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                } else {

                    String output = response.getEntity(String.class);
                    System.out.println("\ngetEMR4RequestStatusInfo Ouput = " + output);

                    JSONObject jsonObj = new JSONObject(output);
                    statusStr = jsonObj.getString("status");
                    instanceURI = jsonObj.getString("uri");

                    if (statusStr.equals("RUNNING")) {
                        pBusinessValueID = UUID.randomUUID();
                        pBusinessValues =
                            new PBusinessValues(pBusinessValueID.toString(), "InstanceURI", pBusinessService,
                                                instanceURI);
                        businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nStatusStri = " + statusStr);
        return statusStr;
    }

    public JSONObject getJCSInstanceInfo(String sQueueId) {
        String instanceName = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        String identityDomain = null;
        JSONObject jcsInstance = null;

        try {
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("InstanceName")) {
                    instanceName = pBusinessValuesList.get(i).getVal();
                }
            }

            Client client = getPublicCloudClient(sQueueId, true);
            identityDomain = this.getConfigProperties().getProperty("identityDomain");
            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("publicCloudURL") + identityDomain + "/" +
                                instanceName);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", identityDomain).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);

                jcsInstance = new JSONObject(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jcsInstance;
    }

    public String getJCSRequestStatusInfo(String sQueueId) {
        String statusStr = null;
        String instanceURI = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        JSONObject jcsInstance;

        try {
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            jcsInstance = getJCSInstanceInfo(sQueueId);
            statusStr = jcsInstance.getString("status");
            instanceURI = jcsInstance.getString("service_uri");

            if (statusStr.equals("Running")) {
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "InstanceURI", pBusinessService, instanceURI);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nStatusStri = " + statusStr);
        return statusStr;
    }

    public String updateRequestStatus(String businessServiceId, String businessServiceName) {
        String requestStatus = null;

        try {
            requestStatus = getRequestStatusInfo(businessServiceId, null);
            if (requestStatus.equals("SCHEDULED")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Environment creation scheduled");
            } else if (requestStatus.equals("EXECUTING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Environment creation initiated");
            } else if (requestStatus.equals("SUCCESS")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateDBaaSRequestStatus(String sQueueId, String dbType, String businessServiceName) {
        String requestStatus = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService = null;

        try {
            if (dbType.equals("AlphaDBaaS")) {
                requestStatus = getRequestStatusInfo(sQueueId, null);
                if (requestStatus.equals("SCHEDULED")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Snap Clone creation scheduled");
                } else if (requestStatus.equals("IN_PROGRESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_INITIATED, "Snap Clone creation initiated");
                } else if (requestStatus.equals("SUCCESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_COMPLETED, businessServiceName);
                }
            } else if (dbType.equals("AlphaDBaaSExa")) {
                System.out.println("\n updateDBaaSRequestStatus - AlphaDBaaSExa");
                businessServiceCatalogFacade = new JavaServiceFacade();
                pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
                requestStatus = getRequestStatusInfo(sQueueId, pBusinessService.getUserName().substring(4));
                if (requestStatus.equals("SCHEDULED")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Thin Clone creation scheduled");
                } else if (requestStatus.equals("IN_PROGRESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_INITIATED, "Thin Clone creation initiated");
                } else if (requestStatus.equals("SUCCESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_COMPLETED, businessServiceName);
                }
            } else {
                requestStatus = getRequestStatusInfo(sQueueId, null);
                System.out.println("AlphaDBaaSHA Request Status = " + requestStatus + "\n");
                if (requestStatus.equals("SCHEDULED")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                            "2 Node RAC Cluster creation scheduled");
                } else if (requestStatus.equals("IN_PROGRESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_INITIATED,
                                            "2 Node RAC Cluster creation initiated");
                } else if (requestStatus.equals("SUCCESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_COMPLETED, businessServiceName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateMWaaSRequestStatus(String sQueueId, String businessServiceName) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updateMWaaSRequestStatus");
            requestStatus = getEMR4RequestStatusInfo(sQueueId, "RequestURI", null);
            if (requestStatus != null) {
                if (requestStatus.equals("EXECUTING")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                            "MWaaS Environment creation scheduled");
                } else if (requestStatus.equals("IN_PROGRESS")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_INITIATED,
                                            "MWaaS Environment creation initiated");
                } else if (requestStatus.equals("RUNNING")) {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_COMPLETED, businessServiceName);
                } else {
                    updateProvisioninStatus(sQueueId, STATUS_PROVIONING_FAILED, businessServiceName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateSchemaDBaaSRequestStatus(String businessServiceId, String businessServiceName) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updateSchemaDBaaSRequestStatus");
            requestStatus = getEMR4RequestStatusInfo(businessServiceId, "SchemaURI", null);
            if (requestStatus.equals("EXECUTING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Schema DBaaS Environment creation scheduled");
            } else if (requestStatus.equals("IN_PROGRESS")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Schema DBaaS Environment creation initiated");
            } else if (requestStatus.equals("RUNNING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            } else {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_FAILED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updatePluggableDBaaSRequestStatus(String businessServiceId, String businessServiceName,
                                                    String teamNum) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updatePluggableDBaaSRequestStatus");
            requestStatus = getEMR4RequestStatusInfo(businessServiceId, "PluggableURI", teamNum);
            if (requestStatus.equals("EXECUTING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Pluggable DBaaS Environment creation scheduled");
            } else if (requestStatus.equals("IN_PROGRESS")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Pluggable DBaaS Environment creation initiated");
            } else if (requestStatus.equals("RUNNING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateDatabaseDBaaSRequestStatus(String businessServiceId, String businessServiceName) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updateDatabaseDBaaSRequestStatus");
            requestStatus = getEMR4RequestStatusInfo(businessServiceId, "DatabaseURI", null);
            if (requestStatus.equals("EXECUTING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Database DBaaS Environment creation scheduled");
            } else if (requestStatus.equals("IN_PROGRESS")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Database DBaaS Environment creation initiated");
            } else if (requestStatus.equals("RUNNING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateCloneDBaaSRequestStatus(String businessServiceId, String businessServiceName) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updateCloneDBaaSRequestStatus");
            requestStatus = getEMR4RequestStatusInfo(businessServiceId, "DatabaseURI", null);
            if (requestStatus.equals("EXECUTING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Database Clone Environment creation scheduled");
            } else if (requestStatus.equals("IN_PROGRESS")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Database Clone Environment creation initiated");
            } else if (requestStatus.equals("RUNNING")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public String updateJCSRequestStatus(String businessServiceId, String businessServiceName) {
        String requestStatus = null;

        try {
            System.out.println("\nIn updateJCSRequestStatus");
            requestStatus = getJCSRequestStatusInfo(businessServiceId);
            if (requestStatus.equals("In Progress")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_INITIATED,
                                        "Oracle Java Cloud Service Environment creation initiated");
            } else if (requestStatus.equals("Running")) {
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_COMPLETED, businessServiceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public void createAlphaDevImage(String businessServiceId, String hostname) {
        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") +
                                this.getConfigProperties().getProperty("alphaBaseURI"));

            String se =
                new String("{\"params\":{\"request_name\":\"" + hostname + "\"," +
                           "             \"server_size\":\"Small\"," + "             \"server_prefix\":\"" + hostname +
                           "\"," +
                           "             \"networks\":[{\name\":\"eth0\",\"assignment_type\":\"Dhcp\":\"host_name\":\"" +
                           hostname + "\"}]," + "             \"vnc_password\":\"welcome1\"," +
                           "            \"root_password\":\"welcome1\"}," + "\"hostname\":\"" + hostname + "\"," +
                           "\"based_on\":\"/em/cloud/iaas/servicetemplate/vm/oracle%3AdefaultService%3Aem%3Aprovisioning%3A1%3Acmp%3AVirtualization%3ATemplate%3AEA0CD88DC9107A0CE043DC5B920A97B2%3A0.1\"," +
                           "}");


            ClientResponse response =
                webResource.header("Content-Type", "application/oracle.com.cloud.common.VM+json").header("Accept",
                                                                                                         "application/oracle.com.cloud.common.VM+json").accept("application/oracle.com.cloud.common.VM+json").post(ClientResponse.class,
                                                                                                                                                                                                                   se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer(String businessServiceId, String serverURI) {
        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + serverURI);

            String se = new String("{\"status\":\"STOPPED\"}");

            ClientResponse response =
                webResource.header("Content-Type", "application/oracle.com.cloud.common.VM+json").header("Accept",
                                                                                                         "application/oracle.com.cloud.common.VM+json").accept("application/oracle.com.cloud.common.VM+json").put(ClientResponse.class,
                                                                                                                                                                                                                  se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServiceInstance(String businessServiceId, String serviceInstanceURI) {
        String serverURI;

        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") + serviceInstanceURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject serviceInstancesObj = jsonObj.getJSONObject("servers");
            JSONArray elementsArray = serviceInstancesObj.getJSONArray("elements");
            serverURI = elementsArray.getJSONObject(0).getString("uri");
            stopServer(businessServiceId, serverURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteServiceInstance(String businessServiceId, String serviceInstanceURI) {
        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") + serviceInstanceURI);

            ClientResponse response = webResource.delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateServerParams(String serverURI, String sQueueId) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + serverURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), jsonObj.getString("name"), pBusinessService,
                                    jsonObj.getString("hostname"));
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessService.getPBusinessValuesList().add(pBusinessValues);
            businessServiceCatalogFacade.mergePBusinessService(pBusinessService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getServiceInstanceURI(String sQueueId, String requestURI) {
        String serviceURI = null;
        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject serviceInstancesObj = jsonObj.getJSONObject("service_instances");
            JSONArray elementsArray = serviceInstancesObj.getJSONArray("elements");
            serviceURI = elementsArray.getJSONObject(0).getString("uri");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceURI;
    }

    public void updateServiceInstanceParams(String serverURI, String sQueueId) {
        String serviceURI = null;
        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + serverURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject serviceInstancesObj = jsonObj.getJSONObject("servers");
            JSONArray elementsArray = serviceInstancesObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                serviceURI = elementObj.getString("uri");
                updateServerParams(serviceURI, sQueueId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateAssemblyParams(String sQueueId, String imageName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String requestURI = null;
        String serviceInstanceURI;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + "/em/cloud");
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);
            System.out.println("\nUpdateAssemblyParams Output...\n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject serviceInstancesObj = jsonObj.getJSONObject("service_requests");
            JSONArray elementsArray = serviceInstancesObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                if (elementObj.getString("name").equals(sQueueId)) {
                    requestURI = elementObj.getString("uri");
                    i = elementsArray.length();
                }
            }
            serviceInstanceURI = getServiceInstanceURI(sQueueId, requestURI);
            updateServiceInstanceParams(serviceInstanceURI, sQueueId);

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            System.out.println("\nInstanceURI = " + serviceInstanceURI);
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "InstanceURI", pBusinessService, serviceInstanceURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deprovisionAssembly(String businessServiceId, String imageName, String requestURI) {
        String serviceInstanceURI = null;
        String assemblInstanceURI = null;
        WebResource webResource = null;

        try {
            Client client = getClient(businessServiceId, null);

            webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + requestURI);

            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject requestsObj = jsonObj.getJSONObject("requests");
            JSONArray elementsArray = requestsObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                if (elementObj.getString("name").equals(businessServiceId)) {
                    assemblInstanceURI = elementObj.getString("uri");
                }
            }
            serviceInstanceURI = getServiceInstanceURI(businessServiceId, assemblInstanceURI);
            deleteServiceInstance(businessServiceId, serviceInstanceURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAlphaWLSAssembly(MachineSize machineSizeWLS, String businessServiceId) {
        String hostname;
        boolean retry = true;
        ClientResponse response = null;

        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") +
                                this.getConfigProperties().getProperty("wlsURI"));

            hostname = new String("AlphaWLS_" + String.format("%04d", getImageId()));
            String se =
                new String("{\"based_on\":\"" + this.getConfigProperties().getProperty("wlsURI") + "\"," +
                           " \"zone\":\"" + this.getConfigProperties().getProperty("ovmZone") + "\"," +
                           " \"params\":{" + "    \"request_name\":\"" + businessServiceId + "\"," +
                           "    \"server_prefix\":\"" + hostname + "\"," + "    \"vnc_password\":\"Welcome1\"," +
                           "    \"root_password\":\"Welcome1\"," + "    \"assembly_instance_name\": \"" + hostname +
                           "\"" + "  }," +
                           " \"deployment_plan\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\\n<ns2:ConfigurationData xmlns:ns2=\\\"http://www.oracle.com/sysman/vt/RequestConfigData\\\" PlanVersion=\\\"1.0.0\\\">\\n    <AssemblyDeployment assemblyInstanceName=\\\"WLStest\\\">\\n        <SourceBinary name=\\\"oracle:defaultService:em:provisioning:1:cmp:Virtualization:Assembly:ED1E82FF79B81760E043DC5B920AF565:0.1\\\" type=\\\"TYPE_SWLIB\\\"/>\\n        <DeploymentTarget name=\\\"2840412B9E3DD33DC4E0D2DD4FE079B1\\\" type=\\\"oracle_vm_zone\\\"/>\\n        <AcceptedAllEULAs>false</AcceptedAllEULAs>\\n        <InstallEMAgent>false</InstallEMAgent>\\n        <DisableCleanup>true</DisableCleanup>\\n        <EMAgentConfig installBaseDirectory=\\\"/home/oracle/agent\\\" instanceDirectory=\\\"/home/oracle/agent/agent_inst\\\" port=\\\"3872\\\" installUserName=\\\"oracle\\\" installUserPassword=\\\"  \\\" additionalParams=\\\"-ignoreDirPrereq -enablePty\\\" sshTimeoutInMinutes=\\\"10\\\" privilegeDelegationSetting=\\\"/bin/su - %RUNAS% -c &quot;%COMMAND%&quot;\\\"/>\\n        <AssemblyNetworkConfig>\\n            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n        </AssemblyNetworkConfig>\\n        <VirtualSystemCollectionConfig id=\\\"WLStest\\\">\\n            <ProductConfiguration>\\n                <Product>\\n                    <Properties/>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n                <Product>\\n                    <Properties>\\n                        <Property id=\\\"preserve-setDomainEnv\\\">\\n                            <Name>preserve-setDomainEnv</Name>\\n                            <Required>true</Required>\\n                            <Value>true</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"admin-password\\\">\\n                            <Name>admin-password</Name>\\n                            <Required>true</Required>\\n                            <Value>  </Value>\\n                            <ValueGuid>EF62DE4837AD2EE9E043DC5B920A385A</ValueGuid>\\n                            <Secret>true</Secret>\\n                        </Property>\\n                    </Properties>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n            </ProductConfiguration>\\n            <VirtualSystemConfig targetName=\\\"%assembly_instance_name%/esgpaas-ovabtestWLS/AdminServer:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"WLStest/esgpaas-ovabtestWLS/AdminServer\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"readymetric-verify\\\">\\n<Name>readymetric-verify</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-instance-name-0\\\">\\n<Name>readymetric-instance-name-0</Name>\\n<Required>false</Required>\\n<Value>com.bea:Name=AdminServer,Type=ServerRuntime</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-compare-type\\\">\\n<Name>readymetric-attribute-compare-type</Name>\\n<Required>false</Required>\\n<Value>EQUALS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.runConfiguration\\\">\\n<Name>ocm.runConfiguration</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.CSI\\\">\\n<Name>ocm.metalinkCsiRegistration.CSI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-max-wait-period\\\">\\n<Name>readymetric-max-wait-period</Name>\\n<Required>false</Required>\\n<Value>600</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-name\\\">\\n<Name>readymetric-attribute-name</Name>\\n<Required>false</Required>\\n<Value>State</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyHost\\\">\\n<Name>ocm.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.repeaterURI\\\">\\n<Name>ocm.repeaterURI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-trust-store-0\\\">\\n<Name>readymetric-trust-store-0</Name>\\n<Required>false</Required>\\n<Value>/u01/Oracle/Middleware/wlserver_10.3/server/lib/DemoTrust.jks</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.metalinkId\\\">\\n<Name>ocm.metalinkCsiRegistration.metalinkId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPassword\\\">\\n<Name>ocm.proxyPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-type\\\">\\n<Name>readymetric-attribute-type</Name>\\n<Required>false</Required>\\n<Value>STRING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.countryCode\\\">\\n<Name>ocm.metalinkCsiRegistration.countryCode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-polling-period\\\">\\n<Name>readymetric-polling-period</Name>\\n<Required>false</Required>\\n<Value>5</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.anonymousEmailRegistration.emailId\\\">\\n<Name>ocm.anonymousEmailRegistration.emailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPort\\\">\\n<Name>ocm.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyUsername\\\">\\n<Name>ocm.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|Default|port\\\">\\n<Name>input|Default|port</Name>\\n<Required>false</Required>\\n<Value>7001</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkPassword\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-value\\\">\\n<Name>readymetric-attribute-value</Name>\\n<Required>false</Required>\\n<Value>RUNNING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkEmailId\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkEmailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>0</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                           "                           <Memory>" + Integer.toString(machineSizeWLS.getMemory()) +
                           "</Memory>\\n                    " + "                           <VCPUs>" +
                           Integer.toString(machineSizeWLS.getCores()) +
                           "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"TwKw4cOOFBzhn_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"TwKw4cOOFBzhn_sys-JavaHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>345</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"TwKw4cOOFBzhn_usr-MiddlewareHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>1075</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>EF62DE4837AB2EE9E043DC5B920A385A</RootPasswordGuid>\\n                    " +
                           "                           <VmSize>" + machineSizeWLS.getName() +
                           "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n        </VirtualSystemCollectionConfig>\\n    </AssemblyDeployment>\\n</ns2:ConfigurationData>\"" +
                           "}");

            while (retry) {
                response =
                    webResource.header("Content-Type",
                                       "application/oracle.com.cloud.common.AssemblyInstance+json").header("Accept",
                                                                                                           "application/oracle.com.cloud.common.AssemblyInstance+json").accept("application/oracle.com.cloud.common.AssemblyInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                 se);

                if (response.getStatus() != 200) {
                    if (retry && response.getStatus() == 500) {
                        retry = false;
                        System.out.println("\nCreateAlphaWLSAssembly: 500 Error Received...\n");
                        Thread.sleep(30000);
                    } else
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                retry = false;
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED, "Environment creation scheduled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createAlphaBaseAssembly(MachineSize machineSizeWLS, MachineSize machineSizeDBaaS,
                                       String businessServiceId, String startDate, String endDate) {
        String hostname;
        String se = null;
        ClientResponse response = null;
        int statusCode = 200;

        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") +
                                this.getConfigProperties().getProperty("alphaBaseURI"));

            hostname = new String("AlphaOfficeBase_" + String.format("%04d", getImageId()));
            if (startDate != null) {
                se =
                    new String("{\"based_on\":\"" + this.getConfigProperties().getProperty("alphaBaseURI") + "\"," +
                               " \"zone\":\"" + this.getConfigProperties().getProperty("ovmZone") + "\"," +
                               " \"params\":{" + "    \"request_name\":\"" + businessServiceId + "\"," +
                               "    \"server_prefix\":\"" + hostname + "\"," + "    \"vnc_password\":\"Welcome1\"," +
                               "    \"root_password\":\"Welcome1\"," + "    \"start_date\": \"" + startDate + "\"," +
                               "    \"end_date\": \"" + endDate + "\"," + "    \"assembly_instance_name\": \"" +
                               hostname + "\"" + "  }," +
                               " \"deployment_plan\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\\n<ns2:ConfigurationData xmlns:ns2=\\\"http://www.oracle.com/sysman/vt/RequestConfigData\\\" PlanVersion=\\\"1.0.0\\\">\\n    <AssemblyDeployment assemblyInstanceName=\\\"AlphaOfficeBase_3\\\">\\n        <SourceBinary name=\\\"oracle:defaultService:em:provisioning:1:cmp:Virtualization:Assembly:F1C929A8EE5D1B9BE043DC5B920A6E45:0.1\\\" type=\\\"TYPE_SWLIB\\\"/>\\n        <DeploymentTarget name=\\\"2840412B9E3DD33DC4E0D2DD4FE079B1\\\" type=\\\"oracle_vm_zone\\\"/>\\n        <AcceptedAllEULAs>false</AcceptedAllEULAs>\\n        <InstallEMAgent>false</InstallEMAgent>\\n        <DisableCleanup>true</DisableCleanup>\\n        <EMAgentConfig installBaseDirectory=\\\"/home/oracle/agent\\\" instanceDirectory=\\\"/home/oracle/agent/agent_inst\\\" port=\\\"3872\\\" installUserName=\\\"oracle\\\" installUserPassword=\\\"  \\\" additionalParams=\\\"-ignoreDirPrereq -enablePty\\\" sshTimeoutInMinutes=\\\"10\\\" privilegeDelegationSetting=\\\"/bin/su - %RUNAS% -c &quot;%COMMAND%&quot;\\\"/>\\n        <AssemblyNetworkConfig>\\n            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n        </AssemblyNetworkConfig>\\n        <VirtualSystemCollectionConfig id=\\\"AlphaOfficeBase\\\">\\n            <ProductConfiguration>\\n                <Product>\\n                    <Properties/>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n                <Product>\\n                    <Properties>\\n                        <Property id=\\\"preserve-setDomainEnv\\\">\\n                            <Name>preserve-setDomainEnv</Name>\\n                            <Required>true</Required>\\n                            <Value>true</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"admin-password\\\">\\n                            <Name>admin-password</Name>\\n                            <Required>true</Required>\\n                            <Value>  </Value>\\n                            <ValueGuid>F1D250B129FB66CDE043DC5B920AC1B9</ValueGuid>\\n                            <Secret>true</Secret>\\n                        </Property>\\n                    </Properties>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n            </ProductConfiguration>\\n            " +
                               "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeBase/AOBase_WLS/AdminServer:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeBase/AOBase_WLS/AdminServer\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"output|jdbc0|password\\\">\\n<Name>output|jdbc0|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F366CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-instance-name-0\\\">\\n<Name>readymetric-instance-name-0</Name>\\n<Required>false</Required>\\n<Value>com.bea:Name=AdminServer,Type=ServerRuntime</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-compare-type\\\">\\n<Name>readymetric-attribute-compare-type</Name>\\n<Required>false</Required>\\n<Value>EQUALS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.runConfiguration\\\">\\n<Name>ocm.runConfiguration</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.CSI\\\">\\n<Name>ocm.metalinkCsiRegistration.CSI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-max-wait-period\\\">\\n<Name>readymetric-max-wait-period</Name>\\n<Required>false</Required>\\n<Value>600</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-name\\\">\\n<Name>readymetric-attribute-name</Name>\\n<Required>false</Required>\\n<Value>State</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-trust-store-0\\\">\\n<Name>readymetric-trust-store-0</Name>\\n<Required>false</Required>\\n<Value>/u01/middleware/JDev11124/wlserver_10.3/server/lib/DemoTrust.jks</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.metalinkId\\\">\\n<Name>ocm.metalinkCsiRegistration.metalinkId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPassword\\\">\\n<Name>ocm.proxyPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-type\\\">\\n<Name>readymetric-attribute-type</Name>\\n<Required>false</Required>\\n<Value>STRING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.countryCode\\\">\\n<Name>ocm.metalinkCsiRegistration.countryCode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-polling-period\\\">\\n<Name>readymetric-polling-period</Name>\\n<Required>false</Required>\\n<Value>5</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|username\\\">\\n<Name>output|jdbc1|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|Default|port\\\">\\n<Name>input|Default|port</Name>\\n<Required>false</Required>\\n<Value>7001</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkEmailId\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkEmailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>0</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-verify\\\">\\n<Name>readymetric-verify</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyHost\\\">\\n<Name>ocm.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc0|username\\\">\\n<Name>output|jdbc0|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.repeaterURI\\\">\\n<Name>ocm.repeaterURI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.anonymousEmailRegistration.emailId\\\">\\n<Name>ocm.anonymousEmailRegistration.emailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|password\\\">\\n<Name>output|jdbc1|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F566CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPort\\\">\\n<Name>ocm.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyUsername\\\">\\n<Name>ocm.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkPassword\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-value\\\">\\n<Name>readymetric-attribute-value</Name>\\n<Required>false</Required>\\n<Value>RUNNING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                               "                           <Memory>" + Integer.toString(machineSizeWLS.getMemory()) +
                               "</Memory>\\n                    <VCPUs>" + Integer.toString(machineSizeWLS.getCores()) +
                               "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"V59yZm77YRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"V59yZm77YRccFJ3_sys-JavaHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>314</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"V59yZm77YRccFJ3_usr-MiddlewareHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2479</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>45795</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F1D250B129F166CDE043DC5B920AC1B9</RootPasswordGuid>\\n                    " +
                               "                           <VmSize>" + machineSizeWLS.getName() +
                               "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n            " +
                               "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeBase/AOBase_DB:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeBase/AOBase_DB\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"input|listener-1|global-db-name\\\">\\n<Name>input|listener-1|global-db-name</Name>\\n<Required>true</Required>\\n<Value>orcl.us.oracle.com</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"oui.configureCentralInventory\\\">\\n<Name>oui.configureCentralInventory</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|listener-1|port\\\">\\n<Name>input|listener-1|port</Name>\\n<Required>false</Required>\\n<Value>1521</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"db-account-password\\\">\\n<Name>db-account-password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F966CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                               "                           <Memory>" + Integer.toString(machineSizeDBaaS.getMemory()) +
                               "</Memory>\\n                    <VCPUs>" +
                               Integer.toString(machineSizeDBaaS.getCores()) +
                               "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-db_base\\\" fromDefinition=\\\"true\\\">\\n                            <Size>557</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-db_home\\\" fromDefinition=\\\"true\\\">\\n                            <Size>8699</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-oradata\\\" fromDefinition=\\\"true\\\">\\n                            <Size>6228</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-recovery\\\" fromDefinition=\\\"true\\\">\\n                            <Size>10660</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>22444</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F1D250B129F766CDE043DC5B920AC1B9</RootPasswordGuid>\\n                    " +
                               "                           <VmSize>" + machineSizeDBaaS.getName() +
                               "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n        </VirtualSystemCollectionConfig>\\n    </AssemblyDeployment>\\n</ns2:ConfigurationData>\"" +
                               "}");
            } else {
                se =
                    new String("{\"based_on\":\"" + this.getConfigProperties().getProperty("alphaBaseURI") + "\"," +
                               " \"zone\":\"" + this.getConfigProperties().getProperty("ovmZone") + "\"," +
                               " \"params\":{" + "    \"request_name\":\"" + businessServiceId + "\"," +
                               "    \"server_prefix\":\"" + hostname + "\"," + "    \"vnc_password\":\"Welcome1\"," +
                               "    \"root_password\":\"Welcome1\"," + "    \"assembly_instance_name\": \"" + hostname +
                               "\"" + "  }," +
                               " \"deployment_plan\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\\n<ns2:ConfigurationData xmlns:ns2=\\\"http://www.oracle.com/sysman/vt/RequestConfigData\\\" PlanVersion=\\\"1.0.0\\\">\\n    <AssemblyDeployment assemblyInstanceName=\\\"AlphaOfficeBase_3\\\">\\n        <SourceBinary name=\\\"oracle:defaultService:em:provisioning:1:cmp:Virtualization:Assembly:F1C929A8EE5D1B9BE043DC5B920A6E45:0.1\\\" type=\\\"TYPE_SWLIB\\\"/>\\n        <DeploymentTarget name=\\\"2840412B9E3DD33DC4E0D2DD4FE079B1\\\" type=\\\"oracle_vm_zone\\\"/>\\n        <AcceptedAllEULAs>false</AcceptedAllEULAs>\\n        <InstallEMAgent>false</InstallEMAgent>\\n        <DisableCleanup>true</DisableCleanup>\\n        <EMAgentConfig installBaseDirectory=\\\"/home/oracle/agent\\\" instanceDirectory=\\\"/home/oracle/agent/agent_inst\\\" port=\\\"3872\\\" installUserName=\\\"oracle\\\" installUserPassword=\\\"  \\\" additionalParams=\\\"-ignoreDirPrereq -enablePty\\\" sshTimeoutInMinutes=\\\"10\\\" privilegeDelegationSetting=\\\"/bin/su - %RUNAS% -c &quot;%COMMAND%&quot;\\\"/>\\n        <AssemblyNetworkConfig>\\n            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n        </AssemblyNetworkConfig>\\n        <VirtualSystemCollectionConfig id=\\\"AlphaOfficeBase\\\">\\n            <ProductConfiguration>\\n                <Product>\\n                    <Properties/>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n                <Product>\\n                    <Properties>\\n                        <Property id=\\\"preserve-setDomainEnv\\\">\\n                            <Name>preserve-setDomainEnv</Name>\\n                            <Required>true</Required>\\n                            <Value>true</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"admin-password\\\">\\n                            <Name>admin-password</Name>\\n                            <Required>true</Required>\\n                            <Value>  </Value>\\n                            <ValueGuid>F1D250B129FB66CDE043DC5B920AC1B9</ValueGuid>\\n                            <Secret>true</Secret>\\n                        </Property>\\n                    </Properties>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n            </ProductConfiguration>\\n            " +
                               "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeBase/AOBase_WLS/AdminServer:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeBase/AOBase_WLS/AdminServer\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"output|jdbc0|password\\\">\\n<Name>output|jdbc0|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F366CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-instance-name-0\\\">\\n<Name>readymetric-instance-name-0</Name>\\n<Required>false</Required>\\n<Value>com.bea:Name=AdminServer,Type=ServerRuntime</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-compare-type\\\">\\n<Name>readymetric-attribute-compare-type</Name>\\n<Required>false</Required>\\n<Value>EQUALS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.runConfiguration\\\">\\n<Name>ocm.runConfiguration</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.CSI\\\">\\n<Name>ocm.metalinkCsiRegistration.CSI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-max-wait-period\\\">\\n<Name>readymetric-max-wait-period</Name>\\n<Required>false</Required>\\n<Value>600</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-name\\\">\\n<Name>readymetric-attribute-name</Name>\\n<Required>false</Required>\\n<Value>State</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-trust-store-0\\\">\\n<Name>readymetric-trust-store-0</Name>\\n<Required>false</Required>\\n<Value>/u01/middleware/JDev11124/wlserver_10.3/server/lib/DemoTrust.jks</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.metalinkId\\\">\\n<Name>ocm.metalinkCsiRegistration.metalinkId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPassword\\\">\\n<Name>ocm.proxyPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-type\\\">\\n<Name>readymetric-attribute-type</Name>\\n<Required>false</Required>\\n<Value>STRING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.countryCode\\\">\\n<Name>ocm.metalinkCsiRegistration.countryCode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-polling-period\\\">\\n<Name>readymetric-polling-period</Name>\\n<Required>false</Required>\\n<Value>5</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|username\\\">\\n<Name>output|jdbc1|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|Default|port\\\">\\n<Name>input|Default|port</Name>\\n<Required>false</Required>\\n<Value>7001</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkEmailId\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkEmailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>0</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-verify\\\">\\n<Name>readymetric-verify</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyHost\\\">\\n<Name>ocm.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc0|username\\\">\\n<Name>output|jdbc0|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.repeaterURI\\\">\\n<Name>ocm.repeaterURI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.anonymousEmailRegistration.emailId\\\">\\n<Name>ocm.anonymousEmailRegistration.emailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|password\\\">\\n<Name>output|jdbc1|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F566CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPort\\\">\\n<Name>ocm.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyUsername\\\">\\n<Name>ocm.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkPassword\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-value\\\">\\n<Name>readymetric-attribute-value</Name>\\n<Required>false</Required>\\n<Value>RUNNING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                               "                           <Memory>" + Integer.toString(machineSizeWLS.getMemory()) +
                               "</Memory>\\n                    <VCPUs>" + Integer.toString(machineSizeWLS.getCores()) +
                               "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"V59yZm77YRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"V59yZm77YRccFJ3_sys-JavaHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>314</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"V59yZm77YRccFJ3_usr-MiddlewareHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2479</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>45795</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F1D250B129F166CDE043DC5B920AC1B9</RootPasswordGuid>\\n                    " +
                               "                           <VmSize>" + machineSizeWLS.getName() +
                               "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n            " +
                               "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeBase/AOBase_DB:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeBase/AOBase_DB\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"input|listener-1|global-db-name\\\">\\n<Name>input|listener-1|global-db-name</Name>\\n<Required>true</Required>\\n<Value>orcl.us.oracle.com</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"oui.configureCentralInventory\\\">\\n<Name>oui.configureCentralInventory</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|listener-1|port\\\">\\n<Name>input|listener-1|port</Name>\\n<Required>false</Required>\\n<Value>1521</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"db-account-password\\\">\\n<Name>db-account-password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F1D250B129F966CDE043DC5B920AC1B9</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                               "                           <Memory>" + Integer.toString(machineSizeDBaaS.getMemory()) +
                               "</Memory>\\n                    <VCPUs>" +
                               Integer.toString(machineSizeDBaaS.getCores()) +
                               "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-db_base\\\" fromDefinition=\\\"true\\\">\\n                            <Size>557</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-db_home\\\" fromDefinition=\\\"true\\\">\\n                            <Size>8699</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-oradata\\\" fromDefinition=\\\"true\\\">\\n                            <Size>6228</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"PvA7krS7YRccFJ3_sys-recovery\\\" fromDefinition=\\\"true\\\">\\n                            <Size>10660</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>22444</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F1D250B129F766CDE043DC5B920AC1B9</RootPasswordGuid>\\n                    " +
                               "                           <VmSize>" + machineSizeDBaaS.getName() +
                               "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n        </VirtualSystemCollectionConfig>\\n    </AssemblyDeployment>\\n</ns2:ConfigurationData>\"" +
                               "}");
            }

            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.AssemblyInstance+json").header("Accept",
                                                                                                       "application/oracle.com.cloud.common.AssemblyInstance+json").accept("application/oracle.com.cloud.common.AssemblyInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                             se);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 500) {
                    statusCode = response.getStatus();
                } else
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);

                System.out.println("Output from Server .... \n");
                System.out.println(output);
                updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED,
                                        "Environment creation scheduled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusCode;
    }

    public void createAlphaSOAAssembly(MachineSize machineSizeSOA, MachineSize machineSizeWLS,
                                       MachineSize machineSizeDBaaS, String businessServiceId) {
        String hostname;
        boolean retry = true;
        ClientResponse response = null;

        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("emR3URL") +
                                this.getConfigProperties().getProperty("alphaSOAURI"));

            hostname = new String("AlphaOfficeSOA_" + String.format("%04d", getImageId()));
            String se =
                new String("{\"based_on\":\"" + this.getConfigProperties().getProperty("alphaSOAURI") + "\"," +
                           " \"zone\":\"" + this.getConfigProperties().getProperty("ovmZone") + "\"," +
                           " \"params\":{" + "    \"request_name\":\"" + businessServiceId + "\"," +
                           "    \"server_prefix\":\"" + hostname + "\"," + "    \"vnc_password\":\"Welcome1\"," +
                           "    \"root_password\":\"Welcome1\"," + "    \"assembly_instance_name\": \"" + hostname +
                           "\"" + "  }," +
                           " \"deployment_plan\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\\n<ns2:ConfigurationData xmlns:ns2=\\\"http://www.oracle.com/sysman/vt/RequestConfigData\\\" PlanVersion=\\\"1.0.0\\\">\\n    <AssemblyDeployment assemblyInstanceName=\\\"AlphaOfficeSOA_2001\\\">\\n        <SourceBinary name=\\\"oracle:defaultService:em:provisioning:1:cmp:Virtualization:Assembly:F274CC568EB57C5DE043DC5B920A97D1:0.1\\\" type=\\\"TYPE_SWLIB\\\"/>\\n        <DeploymentTarget name=\\\"2840412B9E3DD33DC4E0D2DD4FE079B1\\\" type=\\\"oracle_vm_zone\\\"/>\\n        <AcceptedAllEULAs>false</AcceptedAllEULAs>\\n        <InstallEMAgent>false</InstallEMAgent>\\n        <DisableCleanup>true</DisableCleanup>\\n        <EMAgentConfig installBaseDirectory=\\\"/home/oracle/agent\\\" instanceDirectory=\\\"/home/oracle/agent/agent_inst\\\" port=\\\"3872\\\" installUserName=\\\"oracle\\\" installUserPassword=\\\"  \\\" additionalParams=\\\"-ignoreDirPrereq -enablePty\\\" sshTimeoutInMinutes=\\\"10\\\" privilegeDelegationSetting=\\\"/bin/su - %RUNAS% -c &quot;%COMMAND%&quot;\\\"/>\\n        <AssemblyNetworkConfig>\\n            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n        </AssemblyNetworkConfig>\\n        <VirtualSystemCollectionConfig id=\\\"AlphaOfficeSOA5\\\">\\n            <ProductConfiguration>\\n                <Product>\\n                    <Properties/>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n                <Product>\\n                    <Properties>\\n                        <Property id=\\\"CoherenceCluster1.soa_server.localport\\\">\\n                            <Name>CoherenceCluster1.soa_server.localport</Name>\\n                            <Required>false</Required>\\n                            <Value>8088</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"CoherenceCluster1.multicast-port\\\">\\n                            <Name>CoherenceCluster1.multicast-port</Name>\\n                            <Required>false</Required>\\n                            <Value>9778</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"preserve-setDomainEnv\\\">\\n                            <Name>preserve-setDomainEnv</Name>\\n                            <Required>true</Required>\\n                            <Value>true</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"CoherenceCluster1.multicast-address\\\">\\n                            <Name>CoherenceCluster1.multicast-address</Name>\\n                            <Required>true</Required>\\n                            <Value>227.7.7.9</Value>\\n                            <Secret>false</Secret>\\n                        </Property>\\n                        <Property id=\\\"admin-password\\\">\\n                            <Name>admin-password</Name>\\n                            <Required>true</Required>\\n                            <Value>  </Value>\\n                            <ValueGuid>F2B60D9A18F05D3BE043DC5B920A292C</ValueGuid>\\n                            <Secret>true</Secret>\\n                        </Property>\\n                    </Properties>\\n                    <EMAgent>false</EMAgent>\\n                </Product>\\n            </ProductConfiguration>\\n            " +
                           "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeSOA5/AOSOA_WLS/soa_server:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeSOA5/AOSOA_WLS/soa_server\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.CSI\\\">\\n<Name>ocm.metalinkCsiRegistration.CSI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://xmlns.oracle.com/AlphaOrderOrchestration/SalesOrder/SalesOrderProcess#wsdl.endpoint(salesorderprocess_client_ep/SalesOrderProcess_pt)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-max-wait-period\\\">\\n<Name>readymetric-max-wait-period</Name>\\n<Required>false</Required>\\n<Value>600</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://alpha.oracle/#wsdl.endpoint(CustomerInfoServiceFacadeService/CustomerInfoServiceFacadePort)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPassword\\\">\\n<Name>ocm.proxyPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-type\\\">\\n<Name>readymetric-attribute-type</Name>\\n<Required>false</Required>\\n<Value>STRING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.import.search_1\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.import.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.interval\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.interval</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.search_1\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-polling-period\\\">\\n<Name>readymetric-polling-period</Name>\\n<Required>false</Required>\\n<Value>5</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.interval\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.interval</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|Default|port\\\">\\n<Name>input|Default|port</Name>\\n<Required>false</Required>\\n<Value>8001</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.interval\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.interval</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://www.alphaoffice.com/ns/CreditAuthorizationService#wsdl.endpoint(CreditAuthorizationService/CreditAuthorizationPort)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkEmailId\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkEmailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.import.search_1\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.import.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.import.replace_1\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.import.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.repeaterURI\\\">\\n<Name>ocm.repeaterURI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.count\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.count</Name>\\n<Required>false</Required>\\n<Value>4</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.import.replace_1\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.import.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.count\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.count</Name>\\n<Required>false</Required>\\n<Value>4</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.backoff\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.backoff</Name>\\n<Required>false</Required>\\n<Value>2</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost:7001/ProductCatalog-PaymentOptions-context-root/PaymentOptionServiceFacadePort?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPort\\\">\\n<Name>ocm.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.fileset\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.fileset</Name>\\n<Required>false</Required>\\n<Value>OrderProcess.wsdl|AlphaServiceStats.wsdl|PaymentOptionServiceFacadeServiceWrapper.wsdl|AlphaServiceStats_db.jca|ProductServiceFacadeServiceWrapper.wsdl|CouponUsageServiceFacadeServiceWrapper.wsdl|xsd/fault-bindings.xsd|xsd/fault-policies.xsd|xsd/FaultHandlingFrameworkSample.xsd|xsd/ALPHA_UPDATE_STATS.xsd|xsd/OrderBookingPO.xsd|xsd/AlphaServiceStats_table.xsd|CustomerInfoServiceFacadeServiceWrapper.wsdl|AlphaCreditCardAuthorizationWrapper.wsdl</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.replace_1\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.maxInterval\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaServiceStats.property_jca.retry.maxInterval</Name>\\n<Required>false</Required>\\n<Value>120</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.backoff\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.backoff</Name>\\n<Required>false</Required>\\n<Value>2</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaSalesOrder.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost.localdomain:8001/soa-infra/services/default/SalesOrder/salesorderprocess_client_ep?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"nodeManagerPort\\\">\\n<Name>nodeManagerPort</Name>\\n<Required>false</Required>\\n<Value>5556</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-compare-type\\\">\\n<Name>readymetric-attribute-compare-type</Name>\\n<Required>false</Required>\\n<Value>EQUALS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-instance-name-0\\\">\\n<Name>readymetric-instance-name-0</Name>\\n<Required>false</Required>\\n<Value>com.bea:Name=soa_server,Type=ServerRuntime</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.runConfiguration\\\">\\n<Name>ocm.runConfiguration</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-name\\\">\\n<Name>readymetric-attribute-name</Name>\\n<Required>false</Required>\\n<Value>State</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-trust-store-0\\\">\\n<Name>readymetric-trust-store-0</Name>\\n<Required>false</Required>\\n<Value>/u01/middleware/wlserver_10.3/server/lib/DemoTrust.jks</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CustomerInformationService.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost:7001/ProductCatalog-CustomerInformation-context-root/CustomerInfoServiceFacadePort?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.search_1\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.wsdlAndSchema_1.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.metalinkId\\\">\\n<Name>ocm.metalinkCsiRegistration.metalinkId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.fileset\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.fileset</Name>\\n<Required>false</Required>\\n<Value>monitor.config|xsd/SimpleApprovalTaskPayloadParameters.xsd|xsd/TaskStateMachine.xsd|xsd/TaskSequenceChanges.xsd|xsd/SimpleApprovalTaskWorkflowTask.xsd|xsd/SimpleApprovalTaskPayload.xsd|xsd/WorkflowCommon.xsd|xsd/TaskEvidenceService.xsd</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_AlphaCreditCardAuthorization.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost:7001/AlphaCreditAuthorization/CreditAuthorizationPort?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.count\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.count</Name>\\n<Required>false</Required>\\n<Value>4</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.fileset\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.fileset</Name>\\n<Required>false</Required>\\n<Value>AlphaServiceStats.wsdl|AlphaServiceStats_db.jca|SalesOrderLocal_db.jca|xsd/SalesOrderLocal_table.xsd|xsd/ALPHA_UPDATE_STATS.xsd|xsd/APPS_XX_SPD_MANAGESALESORDER_XX_CREATE_SALES_ORDER.xsd|xsd/AlphaSalesOrder.xsd|SalesOrderProcess.wsdl|SalesOrderLocal.wsdl</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.countryCode\\\">\\n<Name>ocm.metalinkCsiRegistration.countryCode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_PaymentOptions.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://alpha.oracle/#wsdl.endpoint(PaymentOptionServiceFacadeService/PaymentOptionServiceFacadePort)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.maxInterval\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.maxInterval</Name>\\n<Required>false</Required>\\n<Value>120</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.backoff\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_SalesOrderLocal.property_jca.retry.backoff</Name>\\n<Required>false</Required>\\n<Value>2</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.search_1\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost:7001/ProductCatalog-Products-context-root/ProductServiceFacadePort?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.replace_1\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.wsdlAndSchema_1.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>0</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-verify\\\">\\n<Name>readymetric-verify</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyHost\\\">\\n<Name>ocm.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.import.replace_1\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.import.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.import.search_1\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.import.search_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.property_weblogic.wsee.wsat.transaction.flowOption</Name>\\n<Required>false</Required>\\n<Value>WSDLDriven</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.attribute_location\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.attribute_location</Name>\\n<Required>false</Required>\\n<Value>http://localhost:7001/ProductCatalog-CouponUsages-context-root/CouponUsageServiceFacadePort?WSDL</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_Products.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://alpha.oracle/#wsdl.endpoint(ProductServiceFacadeService/ProductServiceFacadePort)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.service_orderprocess_client_ep.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.service_orderprocess_client_ep.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://xmlns.oracle.com/AlphaOrderOrchestration/OrderCreate/OrderProcess#wsdl.endpoint(orderprocess_client_ep/OrderProcess_pt)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.anonymousEmailRegistration.emailId\\\">\\n<Name>ocm.anonymousEmailRegistration.emailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.maxInterval\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.reference_AlphaServiceStats.property_jca.retry.maxInterval</Name>\\n<Required>false</Required>\\n<Value>120</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.replace_1\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.wsdlAndSchema_1.replace_1</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyUsername\\\">\\n<Name>ocm.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SalesOrder_1-0.service_salesorderprocess_client_ep.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_SalesOrder_1-0.service_salesorderprocess_client_ep.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://xmlns.oracle.com/AlphaOrderOrchestration/SalesOrder/SalesOrderProcess#wsdl.endpoint(salesorderprocess_client_ep/SalesOrderProcess_pt)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkPassword\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-value\\\">\\n<Name>readymetric-attribute-value</Name>\\n<Required>false</Required>\\n<Value>RUNNING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.service_client.callback.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.service_client.callback.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://xmlns.oracle.com/bpel/workflow/taskService#wsdl.endpoint(client/TaskServiceCallback_pt)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_SimpleApproval_1-0.service_client.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_SimpleApproval_1-0.service_client.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://xmlns.oracle.com/bpel/workflow/taskService#wsdl.endpoint(client/TaskService_pt)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.attribute_port\\\">\\n<Name>soa.cp.composite_OrderCreate_1-0.reference_CouponUsage.binding_ws.attribute_port</Name>\\n<Required>false</Required>\\n<Value>http://alpha.oracle/#wsdl.endpoint(CouponUsageServiceFacadeService/CouponUsageServiceFacadePort)</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                           "                           <Memory>" + Integer.toString(machineSizeWLS.getMemory()) +
                           "</Memory>\\n                    <VCPUs>" + Integer.toString(machineSizeWLS.getCores()) +
                           "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_sys-JavaHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>314</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_usr-MiddlewareHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>9013</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>39261</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F2B60D9A18D65D3BE043DC5B920A292C</RootPasswordGuid>\\n                    " +
                           "                           <VmSize>" + machineSizeWLS.getName() +
                           "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n            " +
                           "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeSOA5/AOSOA_DB:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeSOA5/AOSOA_DB\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"input|listener-1|global-db-name\\\">\\n<Name>input|listener-1|global-db-name</Name>\\n<Required>true</Required>\\n<Value>orcl</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"oui.configureCentralInventory\\\">\\n<Name>oui.configureCentralInventory</Name>\\n<Required>false</Required>\\n<Value>true</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|listener-1|port\\\">\\n<Name>input|listener-1|port</Name>\\n<Required>false</Required>\\n<Value>1521</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"db-account-password\\\">\\n<Name>db-account-password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18DA5D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                           "                           <Memory>" + Integer.toString(machineSizeSOA.getMemory()) +
                           "</Memory>\\n                    <VCPUs>" + Integer.toString(machineSizeSOA.getCores()) +
                           "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"RnJQ0ZcJYRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"RnJQ0ZcJYRccFJ3_sys-db_base\\\" fromDefinition=\\\"true\\\">\\n                            <Size>557</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"RnJQ0ZcJYRccFJ3_sys-db_home\\\" fromDefinition=\\\"true\\\">\\n                            <Size>8699</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"RnJQ0ZcJYRccFJ3_sys-oradata\\\" fromDefinition=\\\"true\\\">\\n                            <Size>10354</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"RnJQ0ZcJYRccFJ3_sys-recovery\\\" fromDefinition=\\\"true\\\">\\n                            <Size>10660</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>18318</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F2B60D9A18D85D3BE043DC5B920A292C</RootPasswordGuid>\\n                    " +
                           "                           <VmSize>" + machineSizeSOA.getName() +
                           "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n            " +
                           "                       <VirtualSystemConfig targetName=\\\"AlphaOfficeSOA5/AOSOA_WLS/AdminServer:%assembly_instance_name%\\\" vmInstance=\\\"true\\\" agentInstallationType=\\\"Push Agent Installation\\\" agentPushInstallationEnabled=\\\"true\\\" id=\\\"AlphaOfficeSOA5/AOSOA_WLS/AdminServer\\\">\\n                <ProductConfiguration>\\n                    <Product>\\n                        <Properties>\\n                            <Property id=\\\"readymetric-attribute-type\\\">\\n<Name>readymetric-attribute-type</Name>\\n<Required>false</Required>\\n<Value>STRING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.recentDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.recentDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.outboundDataSourceLocal\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.Password\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keystoreAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keystoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc4|username\\\">\\n<Name>output|jdbc4|username</Name>\\n<Required>false</Required>\\n<Value>DEV_SOAINFRA</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundLockTypeForWrite\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.preferredCipherSuite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.preferredCipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.publicKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.publicKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpPathSeparator\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpPathSeparator</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-soap.Password\\\">\\n<Name>OracleBamAdapter_eis-bam-soap.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.Username\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.clientEncoding\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.clientEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.ftpClientClass\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.ftpClientClass</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.inboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"DbAdapter_eis-DB-SOADemo.xADataSourceName\\\">\\n<Name>DbAdapter_eis-DB-SOADemo.xADataSourceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyUsername\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keystoreAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keystoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleCountry\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleCountry</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.channelMask\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.channelMask</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.username\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.Password\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.preferredPKIAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.preferredPKIAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.inboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.username\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.workingDirectory\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.outboundLockTypeForWrite\\\">\\n<Name>FileAdapter_eis-FileAdapter.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.changeDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.changeDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundDataSourceLocal\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.outboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.useFtps\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.useFtps</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.ignoreFilesWithoutTimeFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.ignoreFilesWithoutTimeFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.outboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.controlDir\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundLockTypeForWrite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-verify\\\">\\n<Name>readymetric-verify</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.securePort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.securePort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.IsTopic\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.jsseProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.jsseProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.accountName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.accountName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyHost\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-wls-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.controlDir\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.pkiProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.pkiProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredCipherSuite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredCipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.port\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.SSLEnable\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.SSLEnable</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.defaultDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.defaultDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.inboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.channelName\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.channelName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.Password\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.KeyStorePassword\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.KeyStorePassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.hostOSType\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.hostOSType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.workingDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.publicKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.publicKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc6|username\\\">\\n<Name>output|jdbc6|username</Name>\\n<Required>false</Required>\\n<Value>DEV_MDS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useProxy\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useProxy</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.useProxy\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.useProxy</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.enforceFileTypeFromSpec\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.enforceFileTypeFromSpec</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.inboundDataSource\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc7|username\\\">\\n<Name>output|jdbc7|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.ignorePermissionsOnFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.ignorePermissionsOnFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc5|password\\\">\\n<Name>output|jdbc5|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18DE5D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.outboundDataSourceLocal\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.keepConnections\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.keepConnections</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.workingDirectory\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.timeParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.timeParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverEncoding\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useSftp\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useSftp</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredPKIAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredPKIAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-wls-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.ignorePermissionsOnFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.ignorePermissionsOnFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyPort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.Password\\\">\\n<Name>JmsAdapter_eis-wls-Topic.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.jsseProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.jsseProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-rmi.InstanceName\\\">\\n<Name>OracleBamAdapter_eis-bam-rmi.InstanceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.securePort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.securePort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.inboundDataSource\\\">\\n<Name>FileAdapter_eis-FileAdapter.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.walletPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.walletPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverEncoding\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.listParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.listParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.connectionFactoryLocation\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.connectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.pkiProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.pkiProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.receiveExit\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.receiveExit</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.Password\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.changeDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.changeDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-name\\\">\\n<Name>readymetric-attribute-name</Name>\\n<Required>false</Required>\\n<Value>State</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.inboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.workingDirectory\\\">\\n<Name>FileAdapter_eis-FileAdapter.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.Password\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.preferredDataIntegrityAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.preferredDataIntegrityAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-rmi.Password\\\">\\n<Name>OracleBamAdapter_eis-bam-rmi.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"SocketAdapter_eis-socket-SocketAdapter.Timeout\\\">\\n<Name>SocketAdapter_eis-socket-SocketAdapter.Timeout</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.authenticationType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.authenticationType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.preferredKeyExchangeAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.preferredKeyExchangeAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.defaultDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.defaultDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.walletLocation\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.walletLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyHost\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useSftp\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useSftp</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc8|password\\\">\\n<Name>output|jdbc8|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18E05D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverEncoding\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-wls-Topic.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyHost\\\">\\n<Name>ocm.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keepConnections\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keepConnections</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.keystoreAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.keystoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.walletPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.walletPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.username\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.outboundDataSourceLocal\\\">\\n<Name>FileAdapter_eis-FileAdapter.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.channelMask\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.channelMask</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredKeyExchangeAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredKeyExchangeAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.port\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundLockTypeForWrite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.host\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyUsername\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpClientClass\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpClientClass</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.hostName\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.hostName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.recentDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.recentDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"UMSAdapter_eis-ums-UMSAdapterOutbound.XATransaction\\\">\\n<Name>UMSAdapter_eis-ums-UMSAdapterOutbound.XATransaction</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.defaultDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.defaultDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.CSI\\\">\\n<Name>ocm.metalinkCsiRegistration.CSI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPassword\\\">\\n<Name>ocm.proxyPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.walletLocation\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.walletLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.ftpAbsolutePathBegin\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.ftpAbsolutePathBegin</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.ignorePermissionsOnFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.ignorePermissionsOnFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.IsTransacted\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.host\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.Username\\\">\\n<Name>JmsAdapter_eis-wls-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.enableCipherSuits\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.enableCipherSuits</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.timeParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.timeParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.IsTransacted\\\">\\n<Name>JmsAdapter_eis-wls-Topic.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.controlDir\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.ftpClientClass\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.ftpClientClass</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleLanguage\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleLanguage</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.XADataSourceName\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.XADataSourceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.walletPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.walletPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.Username\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.port\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.Password\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverEncoding\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.preferredCompressionAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.preferredCompressionAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            " +
                           "                           <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keystoreType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keystoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredCompressionAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredCompressionAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.Password\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.pkiProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.pkiProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-compare-type\\\">\\n<Name>readymetric-attribute-compare-type</Name>\\n<Required>false</Required>\\n<Value>EQUALS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.userID\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.userID</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.IsTopic\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.TrustStorePassword\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.TrustStorePassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-trust-store-0\\\">\\n<Name>readymetric-trust-store-0</Name>\\n<Required>false</Required>\\n<Value>/u01/middleware/wlserver_10.3/server/lib/DemoTrust.jks</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundDataSourceLocal\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreType\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.publicKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.publicKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.outboundDataSourceLocal\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.keystoreType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.keystoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.username\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.ftpAbsolutePathBegin\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.ftpAbsolutePathBegin</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.accountName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.accountName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc0|username\\\">\\n<Name>output|jdbc0|username</Name>\\n<Required>false</Required>\\n<Value>DEV_MDS</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.outboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.Username\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.listParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.listParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc8|username\\\">\\n<Name>output|jdbc8|username</Name>\\n<Required>false</Required>\\n<Value>alpha</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.publicKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.publicKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.IsTransacted\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyHost\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.keyStoreProviderName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.keyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.transportProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.transportProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpPathSeparator\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpPathSeparator</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyDefinitionFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyDefinitionFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.outboundLockTypeForWrite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.outboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc4|password\\\">\\n<Name>output|jdbc4|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18E25D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreAlgorithm\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.host\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.port\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.portNumber\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.portNumber</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.password\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverTimeZone\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverTimeZone</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredPKIAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredPKIAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"DbAdapter_eis-DB-SOADemoLocalTx.platformClassName\\\">\\n<Name>DbAdapter_eis-DB-SOADemoLocalTx.platformClassName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredPKIAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredPKIAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.preferredKeyExchangeAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.preferredKeyExchangeAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.ftpPathSeparator\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.ftpPathSeparator</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.useProxy\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.useProxy</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.controlDir\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.securePort\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.securePort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.authenticationType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.authenticationType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.authenticationType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.authenticationType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.DataSourceName\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.DataSourceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.transportProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.transportProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyDefinitionFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyDefinitionFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.useFtps\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.useFtps</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.controlDir\\\">\\n<Name>FileAdapter_eis-FileAdapter.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.useSftp\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.useSftp</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.XATransaction\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.XATransaction</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"DbAdapter_eis-DB-SOADemo.platformClassName\\\">\\n<Name>DbAdapter_eis-DB-SOADemo.platformClassName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.keystoreType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.keystoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.CipherSuite\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.CipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.jsseProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.jsseProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.enforceFileTypeFromSpec\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.enforceFileTypeFromSpec</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.Protocol\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.Protocol</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.accountName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.accountName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.host\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.Username\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpAbsolutePathBegin\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.ftpAbsolutePathBegin</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredCompressionAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredCompressionAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.anonymousEmailRegistration.emailId\\\">\\n<Name>ocm.anonymousEmailRegistration.emailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleLanguage\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleLanguage</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkPassword\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkPassword</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.UseDefaultConnectionManager\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.UseDefaultConnectionManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyPort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpAbsolutePathBegin\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpAbsolutePathBegin</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.trustManager\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.trustManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyPort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc0|password\\\">\\n<Name>output|jdbc0|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18E45D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.inboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundDataSourceLocal\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.password\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.enforceFileTypeFromSpec\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.enforceFileTypeFromSpec</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleVariant\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleVariant</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleVariant\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleVariant</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-soap.UserName\\\">\\n<Name>OracleBamAdapter_eis-bam-soap.UserName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundDataSource\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyUsername\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.recentDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.recentDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.preferredCompressionAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.preferredCompressionAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc3|password\\\">\\n<Name>output|jdbc3|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18E65D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.publicKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.publicKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyPort\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreLocation\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.Password\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverEncoding\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverEncoding</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.trustManager\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.trustManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.workingDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.keepConnections\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.keepConnections</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.Password\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.controlDir\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.changeDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.changeDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.jsseProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.jsseProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.SSLPeerName\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.SSLPeerName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-wls-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.securePort\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.securePort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.controlDir\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.metalinkId\\\">\\n<Name>ocm.metalinkCsiRegistration.metalinkId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.jsseProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.jsseProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkCsiRegistration.countryCode\\\">\\n<Name>ocm.metalinkCsiRegistration.countryCode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-rmi.HostName\\\">\\n<Name>OracleBamAdapter_eis-bam-rmi.HostName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.Username\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.Username\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc3|username\\\">\\n<Name>output|jdbc3|username</Name>\\n<Required>false</Required>\\n<Value>DEV_SOAINFRA</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.enableCipherSuits\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.enableCipherSuits</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.ConnectionString\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.ConnectionString</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleLanguage\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleLanguage</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.useImplicitSSL\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.useImplicitSSL</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"DbAdapter_eis-DB-SOADemo.dataSourceName\\\">\\n<Name>DbAdapter_eis-DB-SOADemo.dataSourceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.inboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.pkiProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.pkiProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyUsername\\\">\\n<Name>ocm.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredKeyExchangeAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredKeyExchangeAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"DbAdapter_eis-DB-SOADemoLocalTx.dataSourceName\\\">\\n<Name>DbAdapter_eis-DB-SOADemoLocalTx.dataSourceName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.username\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.workingDirectory\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.transportProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.transportProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.CCDTurl\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.CCDTurl</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredCompressionAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredCompressionAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpClientClass\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpClientClass</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.Password\\\">\\n<Name>JmsAdapter_eis-wls-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useFtps\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useFtps</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyUsername\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.recentDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.recentDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.walletPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.walletPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.privateKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.privateKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyDefinitionFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyDefinitionFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.Username\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-wls-Topic.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredDataIntegrityAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredDataIntegrityAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyHost\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.recentDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.recentDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.workingDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.password\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.timeParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.timeParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            " +
                           "                           <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keyStoreProviderName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.outboundLockTypeForWrite\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.outboundDataSourceLocal\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.metalinkEmailRegistration.metalinkEmailId\\\">\\n<Name>ocm.metalinkEmailRegistration.metalinkEmailId</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.workingDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredCipherSuite\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredCipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.outboundLockTypeForWrite\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverTimeZone\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverTimeZone</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.workingDirectory\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleCountry\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleCountry</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.sendExit\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.sendExit</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.Username\\\">\\n<Name>JmsAdapter_eis-wls-Topic.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"UMSAdapter_eis-ums-UMSAdapterInbound.XATransaction\\\">\\n<Name>UMSAdapter_eis-ums-UMSAdapterInbound.XATransaction</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.TrustStoreLocation\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.TrustStoreLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpPathSeparator\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpPathSeparator</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.IsTopic\\\">\\n<Name>JmsAdapter_eis-wls-Topic.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.proxyPort\\\">\\n<Name>ocm.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.Password\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.ftpPathSeparator\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.ftpPathSeparator</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.preferredDataIntegrityAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.preferredDataIntegrityAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.enableCipherSuits\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.enableCipherSuits</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-instance-name-0\\\">\\n<Name>readymetric-instance-name-0</Name>\\n<Required>false</Required>\\n<Value>com.bea:Name=AdminServer,Type=ServerRuntime</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ignorePermissionsOnFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ignorePermissionsOnFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredDataIntegrityAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.preferredDataIntegrityAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.useProxy\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.useProxy</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.keyStoreProviderName\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.keyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc5|username\\\">\\n<Name>output|jdbc5|username</Name>\\n<Required>false</Required>\\n<Value>DEV_SOAINFRA</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.keepConnections\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.keepConnections</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.walletLocation\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.walletLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpClientClass\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ftpClientClass</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.host\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.pkiProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.pkiProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.useSftp\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.useSftp</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleCountry\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.serverLocaleCountry</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ignorePermissionsOnFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ignorePermissionsOnFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-fioranomq-Topic.IsTopic\\\">\\n<Name>JmsAdapter_eis-fioranomq-Topic.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.defaultDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.defaultDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-attribute-value\\\">\\n<Name>readymetric-attribute-value</Name>\\n<Required>false</Required>\\n<Value>RUNNING</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.Username\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.Username\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.password\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.password\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keyStoreProviderName\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.keyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverTimeZone\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverTimeZone</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useProxy\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useProxy</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-max-wait-period\\\">\\n<Name>readymetric-max-wait-period</Name>\\n<Required>false</Required>\\n<Value>600</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.outboundLockTypeForWrite\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.queueManagerName\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.queueManagerName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"readymetric-polling-period\\\">\\n<Name>readymetric-polling-period</Name>\\n<Required>false</Required>\\n<Value>5</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"SocketAdapter_eis-socket-SocketAdapter.Port\\\">\\n<Name>SocketAdapter_eis-socket-SocketAdapter.Port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.privateKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.privateKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-rmi.PortNumber\\\">\\n<Name>OracleBamAdapter_eis-bam-rmi.PortNumber</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"input|Default|port\\\">\\n<Name>input|Default|port</Name>\\n<Required>false</Required>\\n<Value>7001</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.listParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.listParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.timeParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.timeParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.changeDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.changeDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.Username\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.useSftp\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.useSftp</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"SocketAdapter_eis-socket-SocketAdapter.KeepAlive\\\">\\n<Name>SocketAdapter_eis-socket-SocketAdapter.KeepAlive</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.proxyDefinitionFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.proxyDefinitionFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.keystoreType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.keystoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.password\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-soap.IsHTTPSEnabledWebService\\\">\\n<Name>OracleBamAdapter_eis-bam-soap.IsHTTPSEnabledWebService</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.privateKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.privateKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.inboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Queue.Password\\\">\\n<Name>JmsAdapter_eis-tibjms-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.useFtps\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.useFtps</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredCipherSuite\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredCipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundLockTypeForWrite\\\">\\n<Name>FileAdapter_eis-ExalogicOptimizedFileAdapter.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.listParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.listParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.authenticationType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.authenticationType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.accountName\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.accountName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc2|password\\\">\\n<Name>output|jdbc2|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18E85D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverTimeZone\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverTimeZone</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleLanguage\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverLocaleLanguage</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.walletLocation\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.walletLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.Password\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-soap.HostName\\\">\\n<Name>OracleBamAdapter_eis-bam-soap.HostName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.Password\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.Password</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.transportProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.transportProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredDataIntegrityAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.preferredDataIntegrityAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapter.controlDir\\\">\\n<Name>FileAdapter_eis-HAFileAdapter.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyHost\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyHost</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.outboundDataSourceLocal\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.changeDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.changeDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.useImplicitSSL\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.useImplicitSSL</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.FactoryProperties\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.FactoryProperties</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.listParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.listParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-webspheremq-Queue.Username\\\">\\n<Name>JmsAdapter_eis-webspheremq-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-sunmq-Queue.Username\\\">\\n<Name>JmsAdapter_eis-sunmq-Queue.Username</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-rmi.UserName\\\">\\n<Name>OracleBamAdapter_eis-bam-rmi.UserName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.preferredPKIAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.preferredPKIAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.ignoreFilesWithoutTimeFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.ignoreFilesWithoutTimeFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-max\\\">\\n<Name>scaling|abs-max</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjms-Topic.IsTransacted\\\">\\n<Name>JmsAdapter_eis-tibjms-Topic.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keystoreType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keystoreType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.trustManager\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.trustManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleVariant\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.serverLocaleVariant</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.trustManager\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.trustManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.preferredCipherSuite\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.preferredCipherSuite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.privateKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.privateKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ignoreFilesWithoutTimeFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ignoreFilesWithoutTimeFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverTimeZone\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.serverTimeZone</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Topic.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-wls-Topic.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"SocketAdapter_eis-socket-SocketAdapter.BacklogQueue\\\">\\n<Name>SocketAdapter_eis-socket-SocketAdapter.BacklogQueue</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.timeParserKey\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.timeParserKey</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.keyStoreProviderName\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.keyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc6|password\\\">\\n<Name>output|jdbc6|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18EA5D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.trustManager\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.trustManager</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.securityExit\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.securityExit</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.transportProvider\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.transportProvider</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ignoreFilesWithoutTimeFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.ignoreFilesWithoutTimeFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.proxyPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.channelMask\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.channelMask</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.privateKeyFile\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.privateKeyFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.keystoreAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.keystoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.authenticationType\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.authenticationType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"OracleBamAdapter_eis-bam-soap.PortNumber\\\">\\n<Name>OracleBamAdapter_eis-bam-soap.PortNumber</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundDataSourceLocal\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.outboundDataSourceLocal</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.accountName\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.accountName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterDB2.inboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapterDB2.inboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.ConnectionFactoryLocation\\\">\\n<Name>JmsAdapter_eis-wls-Queue.ConnectionFactoryLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleVariant\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleVariant</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-jbossmq-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-jbossmq-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.enableCipherSuits\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.enableCipherSuits</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.controlDir\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-pramati-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-pramati-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleVariant\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleVariant</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.enforceFileTypeFromSpec\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.enforceFileTypeFromSpec</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.port\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.port</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Topic.IsTransacted\\\">\\n<Name>JmsAdapter_eis-aqjms-Topic.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.keystoreAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.keystoreAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.workingDirectory\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.workingDirectory</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.outboundLockTypeForWrite\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.outboundLockTypeForWrite</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.walletLocation\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.walletLocation</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.repeaterURI\\\">\\n<Name>ocm.repeaterURI</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.proxyPort\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.proxyPort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-activemq-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-activemq-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.walletPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.walletPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keepConnections\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.keepConnections</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc7|password\\\">\\n<Name>output|jdbc7|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18EC5D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.channelMask\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.channelMask</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.ignoreFilesWithoutTimeFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.ignoreFilesWithoutTimeFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|password\\\">\\n<Name>output|jdbc1|password</Name>\\n<Required>false</Required>\\n<Value>  </Value>\\n<ValueGuid>F2B60D9A18EE5D3BE043DC5B920A292C</ValueGuid>\\n<Secret>true</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapter.channelMask\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapter.channelMask</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-FileAdapter.outboundDataSource\\\">\\n<Name>FileAdapter_eis-FileAdapter.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpAbsolutePathBegin\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.ftpAbsolutePathBegin</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredKeyExchangeAlgorithm\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.preferredKeyExchangeAlgorithm</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"SocketAdapter_eis-socket-SocketAdapter.Host\\\">\\n<Name>SocketAdapter_eis-socket-SocketAdapter.Host</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.enableCipherSuits\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.enableCipherSuits</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useImplicitSSL\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useImplicitSSL</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreProviderName\\\">\\n<Name>MQSeriesAdapter_eis-MQ-MQAdapter.KeyStoreProviderName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.defaultDateFormat\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.defaultDateFormat</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useFtps\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.useFtps</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyUsername\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyUsername</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyPassword\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyPassword</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"ocm.runConfiguration\\\">\\n<Name>ocm.runConfiguration</Name>\\n<Required>false</Required>\\n<Value>false</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyDefinitionFile\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.proxyDefinitionFile</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.enforceFileTypeFromSpec\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.enforceFileTypeFromSpec</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.UserName\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.UserName</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-wls-Queue.IsTransacted\\\">\\n<Name>JmsAdapter_eis-wls-Queue.IsTransacted</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.securePort\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.securePort</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc2|username\\\">\\n<Name>output|jdbc2|username</Name>\\n<Required>false</Required>\\n<Value>DEV_SOAINFRA</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"output|jdbc1|username\\\">\\n<Name>output|jdbc1|username</Name>\\n<Required>false</Required>\\n<Value>DEV_ORASDPM</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-tibjmsDirect-Topic.IsTopic\\\">\\n<Name>JmsAdapter_eis-tibjmsDirect-Topic.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"scaling|abs-min\\\">\\n<Name>scaling|abs-min</Name>\\n<Required>false</Required>\\n<Value>1</Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleCountry\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.serverLocaleCountry</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.AcknowledgeMode\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.AcknowledgeMode</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useImplicitSSL\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.useImplicitSSL</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyType\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.proxyType</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterDB2.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleCountry\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleCountry</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"JmsAdapter_eis-aqjms-Queue.IsTopic\\\">\\n<Name>JmsAdapter_eis-aqjms-Queue.IsTopic</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundDataSource\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter_VMS.useImplicitSSL\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter_VMS.useImplicitSSL</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleLanguage\\\">\\n<Name>FtpAdapter_eis-Ftp-HAFtpAdapterMSSQL.serverLocaleLanguage</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"AqAdapter_eis-AQ-aqSample.DefaultNChar\\\">\\n<Name>AqAdapter_eis-AQ-aqSample.DefaultNChar</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FileAdapter_eis-HAFileAdapterMSSQL.outboundDataSource\\\">\\n<Name>FileAdapter_eis-HAFileAdapterMSSQL.outboundDataSource</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                            <Property id=\\\"FtpAdapter_eis-Ftp-FtpAdapter.controlDir\\\">\\n<Name>FtpAdapter_eis-Ftp-FtpAdapter.controlDir</Name>\\n<Required>false</Required>\\n<Value></Value>\\n<Secret>false</Secret>\\n                            </Property>\\n                        </Properties>\\n                        <EMAgent>false</EMAgent>\\n                    </Product>\\n                </ProductConfiguration>\\n                <HardwareConfiguration>\\n                    " +
                           "                           <Memory>" + Integer.toString(machineSizeDBaaS.getMemory()) +
                           "</Memory>\\n                    <VCPUs>" + Integer.toString(machineSizeDBaaS.getCores()) +
                           "</VCPUs>\\n                    <HaEnabled>false</HaEnabled>\\n                    <StartAfterCreation>true</StartAfterCreation>\\n                    <CPUSchedulingPriority>50</CPUSchedulingPriority>\\n                    <CPUCap>100</CPUCap>\\n                    <LocalDisks>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_System\\\" fromDefinition=\\\"true\\\">\\n                            <Size>2612</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"AB\\\" fromDefinition=\\\"true\\\">\\n                            <Size>0</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_sys-JavaHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>314</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"xY2sVKdJYRccFJ3_usr-MiddlewareHome\\\" fromDefinition=\\\"true\\\">\\n                            <Size>9013</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>false</CreatedByVmSize>\\n                        </disk>\\n                        <disk name=\\\"Local Storage\\\" fromDefinition=\\\"false\\\">\\n                            <Size>39261</Size>\\n                            <Mode>Read-Write</Mode>\\n                            <CreatedByVmSize>true</CreatedByVmSize>\\n                        </disk>\\n                    </LocalDisks>\\n                    <SharedDisks/>\\n                    <Nics>\\n                        <NetworkInterface name=\\\"eth0\\\" fromDefinition=\\\"true\\\">\\n                            <IPAssignmentMode>Dhcp</IPAssignmentMode>\\n                            <AssemblyNetwork name=\\\"vnet-1\\\"/>\\n                        </NetworkInterface>\\n                    </Nics>\\n                    <RootPassword>  </RootPassword>\\n                    <RootPasswordGuid>F2B60D9A18DC5D3BE043DC5B920A292C</RootPasswordGuid>\\n                    " +
                           "                           <VmSize>" + machineSizeDBaaS.getName() +
                           "</VmSize>\\n                    <Keymap>en-us</Keymap>\\n                    <NetworkConfigurationTimeout>-1</NetworkConfigurationTimeout>\\n                    <ProductConfigurationTimeout>-1</ProductConfigurationTimeout>\\n                </HardwareConfiguration>\\n            </VirtualSystemConfig>\\n        </VirtualSystemCollectionConfig>\\n    </AssemblyDeployment>\\n</ns2:ConfigurationData>\"" +
                           "}");

            while (retry) {
                response =
                    webResource.header("Content-Type",
                                       "application/oracle.com.cloud.common.AssemblyInstance+json").header("Accept",
                                                                                                           "application/oracle.com.cloud.common.AssemblyInstance+json").accept("application/oracle.com.cloud.common.AssemblyInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                 se);

                if (response.getStatus() != 200) {
                    if (retry && response.getStatus() == 500) {
                        retry = false;
                        System.out.println("\nCreateAlphaSOAAssembly: 500 Error Received...\n");
                        Thread.sleep(30000);
                    } else
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                retry = false;
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(businessServiceId, STATUS_PROVIONING_SCHEDULED, "Environment creation scheduled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createSnapClone(MachineSize machineSize, String sQueueId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        boolean retry = true;
        ClientResponse response = null;

        try {
            Client client = getClient(sQueueId, null);

            if (machineSize.getName().equals("AlphaOffice-LARGE")) {
                uri = this.getConfigProperties().getProperty("snapCloneLargeURI");
            } else {
                uri = this.getConfigProperties().getProperty("snapCloneSmallURI");
            }
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + uri);

            String se =
                new String("{\"zone\": \"" + this.getConfigProperties().getProperty("snapCloneZoneURI") + "\",\n" +
                           " \"name\": \"" + sQueueId + "\",\n" + " \"description\": \"Alpha Office Snap Clone\",\n" +
                           " \"params\":\n" + "     {\"username\": \"alpha\",\n" +
                           "      \"password\": \"Welcome1\"\n" + "     } \n" + "}");

            while (retry) {
                response =
                    webResource.header("Content-Type",
                                       "application/oracle.com.cloud.common.DbPlatformInstance+json").header("Accept",
                                                                                                             "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                       se);

                if (response.getStatus() != 200) {
                    if (retry && response.getStatus() == 500) {
                        retry = false;
                        System.out.println("\nCreateSnapClone: 500 Error Received...\n");
                        Thread.sleep(30000);
                    } else
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                retry = false;
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Snap Clone creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "RequestURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRACCluster(MachineSize machineSize, String sQueueId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        boolean retry = true;
        ClientResponse response = null;

        try {
            while (retry) {
                Client client = getClient(sQueueId, null);

                uri = this.getConfigProperties().getProperty("racTwoNodeURI");
                WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + uri);

                String se =
                    new String("{\"zone\": \"" + this.getConfigProperties().getProperty("racZoneURI") + "\",\n" +
                               " \"name\": \"" + sQueueId + "\",\n" +
                               " \"description\": \"Alpha Office 2 Node Cluster\",\n" + " \"params\":\n" +
                               "     {\"username\": \"alpha\",\n" + "      \"password\": \"Welcome1\"\n" + "     } \n" +
                               "}");

                response =
                    webResource.header("Content-Type",
                                       "application/oracle.com.cloud.common.DbPlatformInstance+json").header("Accept",
                                                                                                             "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                       se);

                if (response.getStatus() != 200) {
                    if (retry && response.getStatus() == 500) {
                        retry = false;
                        System.out.println("\nCreateRACCluster: 500 Error Received...\n");
                        Thread.sleep(30000);
                    } else
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                retry = false;
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "2 Node RAC Cluster creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "RequestURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDBaaS(String sBusinessServiceId, String requestURI, String teamNum) {
        try {
            Client client = getClient(sBusinessServiceId, teamNum);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + requestURI);

            ClientResponse response =
                webResource.accept("application/oracle.com.cloud.common.DbPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMWaaS(String sQueueId, String requestURI) {
        try {
            System.out.println("\nDelete MWaaS - RequestURI = " + requestURI);
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);

            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.jaas.JavaPlatformInstance+json").accept("application/oracle.com.cloud.jaas.JavaPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSchemaDBaaS(String sQueueId, String requestURI) {
        try {
            System.out.println("\nDelete Schema DBaaS - RequestURI = " + requestURI);
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);

            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.SchemaPlatformInstance+json").accept("application/oracle.com.cloud.common.SchemaPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePluggableDBaaS(String sQueueId, String requestURI, String teamNum) {
        try {
            System.out.println("\nDelete Pluggable DBaaS - RequestURI = " + requestURI);
            Client client = getClient(sQueueId, teamNum);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);

            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").accept("application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDatabaseDBaaS(String sQueueId, String requestURI) {
        try {
            System.out.println("\nDelete Database DBaaS - RequestURI = " + requestURI);
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);

            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCloneDBaaS(String sQueueId, String requestURI) {
        try {
            System.out.println("\nDelete Database Clone DBaaS - RequestURI = " + requestURI);
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);

            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteJCS(String sQueueId, String instanceName) {
        ClientResponse response = null;
        String identityDomain = null;

        try {
            System.out.println("\nin deleteJCS");
            Client client = getPublicCloudClient(sQueueId, true);

            identityDomain = this.getConfigProperties().getProperty("identityDomain");
            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("publicCloudURL") + identityDomain + "/" +
                                instanceName);

            String se =
                new String("{ \"dbaName\": \"SYSTEM\",\n" + "  \"dbaPassword\": \"Alpha2014_\",\n" +
                           "  \"forceDelete\": true\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME",
                                                                                                           identityDomain).put(ClientResponse.class,
                                                                                                                               se);

            if (response.getStatus() != 202) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scaleMWaaS(String sQueueId, String instanceURI, int numServers) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;

        try {
            System.out.println("\nScale MWaaS - InstanceURI = " + instanceURI);
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + instanceURI);

            String se = new String("{\"server_count\": \"" + String.valueOf(numServers) + "\"\n" + "}");

            System.out.println("\nBody...\n" + se);
            ClientResponse response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.jaas.JavaPlatformInstance+json").accept("application/oracle.com.cloud.jaas.JavaPlatformInstance+json").put(ClientResponse.class,
                                                                                                                                                                            se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);
            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("RequestURI")) {
                    pBusinessValuesList.get(i).setVal(requestURI);
                    businessServiceCatalogFacade.mergePBusinessValues(pBusinessValuesList.get(i));
                    i = pBusinessValuesList.size();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDBaaSParams(String sQueueId, String requestURI, String teamNum) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;

        try {
            Client client = getClient(sQueueId, teamNum);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR3URL") + requestURI);

            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            String name = jsonObj.getString("name");
            String connectString = jsonObj.getString("connect_string");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "Name", pBusinessService, name);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, connectString);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getApplicationURL(String businessServiceId, String requestURI) {
        String applicationURL = null;

        try {
            Client client = getClient(businessServiceId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            JSONObject deployObj = jsonObj.getJSONObject("http_application_invocation_url");
            applicationURL = deployObj.getString("apps_server1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationURL;
    }

    public String getWLSConsoleURL(String sQueueId, String wlsDomainName) {
        String wlsConsole = null;
        String wlsPort = null;

        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService = null;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;

        try {
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            batchFile = new File(SCRIPT_LOCATION + "GetWLSConsolePort.sh");
            //Usage: GetWLSConsolePort.sh <Weblogic Domain Name>
            procBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), wlsDomainName);
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                wlsPort = inputLine;
            }
            in.close();
            int exitValue = process.waitFor();
            System.out.println("\n\ngetWLSConsoleURL - Exit Value is " + exitValue + "\n");

            wlsConsole =
                String.format("http://%s:%s/console", this.getConfigProperties().getProperty("wlsConsoleHost"),
                              wlsPort);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "Port", pBusinessService, wlsPort);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
        return wlsConsole;
    }

    public void assignDSTarget(String sQueueId) {
        String wlsDomainName = null;
        String wlsPort = null;
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JavaServiceFacade businessServiceCatalogFacade;
        List<PBusinessValues> pBusinessValuesList;
        PBusinessService pBusinessService = null;

        try {
            System.out.println("\nAssignDSTarget");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("RequestName")) {
                    wlsDomainName =
                        pBusinessValuesList.get(i).getVal() +
                        this.getConfigProperties().getProperty("wlsDomainNameExt");
                }
                if (pBusinessValuesList.get(i).getName().equals("Port")) {
                    wlsPort = pBusinessValuesList.get(i).getVal();
                }
            }
            System.out.println("\nAssignDSTarget - WLS Domain = " + wlsDomainName);
            System.out.println("\nAssignDSTarget - WLS Port = " + wlsPort);

            batchFile = new File(SCRIPT_LOCATION + "AssignDS.sh");
            //Usage: AssignDS.sh <Weblogic Domain Name> <Weblogic Console Port>
            procBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), wlsDomainName, wlsPort);
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("\nassignDSTarget - inputLine =  " + inputLine);
            }
            in.close();
            int exitValue = process.waitFor();
            System.out.println("\nassignDSTarget - Exit Value is " + exitValue + "\n");
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
    }

    public void shutdownDS(String sQueueId) {
        String wlsDomainName = null;
        String wlsPort = null;
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        JavaServiceFacade businessServiceCatalogFacade;
        List<PBusinessValues> pBusinessValuesList;
        PBusinessService pBusinessService = null;

        try {
            System.out.println("\nshutdownDS");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("RequestName")) {
                    wlsDomainName =
                        pBusinessValuesList.get(i).getVal() +
                        this.getConfigProperties().getProperty("wlsDomainNameExt");
                }
                if (pBusinessValuesList.get(i).getName().equals("Port")) {
                    wlsPort = pBusinessValuesList.get(i).getVal();
                }
            }
            System.out.println("\nAssignDSTarget - WLS Domain = " + wlsDomainName);
            System.out.println("\nAssignDSTarget - WLS Port = " + wlsPort);

            batchFile = new File(SCRIPT_LOCATION + "ShutdownDS.sh");
            //Usage: AssignDS.sh <Weblogic Domain Name> <Weblogic Console Port>
            procBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), wlsDomainName, wlsPort);
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("\nassignDSTarget - inputLine =  " + inputLine);
            }
            in.close();
            int exitValue = process.waitFor();
            System.out.println("\nassignDSTarget - Exit Value is " + exitValue + "\n");
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
    }

    public void updateMWaaSParams(String sQueueId, String requestURI) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        List<PBusinessValues> pBusinessValuesList;
        UUID pBusinessValueID;
        String appInstanceURI = null;
        String applicationURL = null;
        String wlsConsole = null;
        String dsURI = null;
        String wlsDomainName = null;
        JSONObject requestsObj;
        JSONArray elementsArray;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            requestsObj = jsonObj.getJSONObject("application_instance_deployments");
            elementsArray = requestsObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                if (elementObj.getString("name").equals("AlphaProductCatalog")) {
                    appInstanceURI = elementObj.getString("uri");
                }
            }

            requestsObj = jsonObj.getJSONObject("data_sources");
            elementsArray = requestsObj.getJSONArray("elements");
            for (int i = 0; i < elementsArray.length(); i++) {
                JSONObject elementObj = elementsArray.getJSONObject(i);
                if (elementObj.getString("name").equals("AlphaOfficeAccessDS")) {
                    dsURI = elementObj.getString("uri");
                }
            }

            applicationURL = getApplicationURL(sQueueId, appInstanceURI);

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("RequestName")) {
                    wlsDomainName = pBusinessValuesList.get(i).getVal();
                }
            }

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ApplicationURL", pBusinessService, applicationURL);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

            wlsConsole = getWLSConsoleURL(sQueueId, wlsDomainName);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConsoleURL", pBusinessService, wlsConsole);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "dsURI", pBusinessService, dsURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSchemaDBaaSParams(String sQueueId, String requestURI, String schemaName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String connectString = null;
        Boolean workAround = false;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                workAround = true;
                //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (workAround) {
                connectString =
                    "(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" +
                    this.getConfigProperties().getProperty("schemaHostname") +
                    ")(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=" + schemaName + ".us.oracle.com)))";
            } else {
                String output = response.getEntity(String.class);

                System.out.println("Output from Server .... \n");
                System.out.println(output);

                JSONObject jsonObj = new JSONObject(output);
                connectString = jsonObj.getString("connect_string");
            }
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, connectString);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePluggableDBaaSParams(String sQueueId, String requestURI, String schemaName, String teamNum) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String connectString = null;
        Boolean workAround = false;

        try {
            Client client = getClient(sQueueId, teamNum);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                workAround = true;
                //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (workAround) {
                connectString =
                    "(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" +
                    this.getConfigProperties().getProperty("schemaHostname") +
                    ")(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=" + schemaName + ".us.oracle.com)))";
            } else {
                String output = response.getEntity(String.class);

                System.out.println("Output from Server .... \n");
                System.out.println(output);

                JSONObject jsonObj = new JSONObject(output);
                connectString = jsonObj.getString("connect_string");
            }
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, connectString);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDatabaseDBaaSParams(String sQueueId, String requestURI, String schemaName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String connectString = null;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            connectString = jsonObj.getString("connect_string");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, connectString);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCloneDBaaSParams(String sQueueId, String requestURI, String schemaName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String connectString = null;

        try {
            Client client = getClient(sQueueId, null);

            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + requestURI);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

            JSONObject jsonObj = new JSONObject(output);
            connectString = jsonObj.getString("connect_string");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, connectString);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String updateJCSParams(String sQueueId) {
        String statusStr = null;
        String instanceURI = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        JSONObject jcsInstance;

        try {
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            jcsInstance = getJCSInstanceInfo(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ConsoleURL", pBusinessService,
                                    jcsInstance.getString("wls_admin_url"));
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "AppsLoginURL", pBusinessService,
                                    jcsInstance.getString("fmw_control_url"));
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ApplicationURL", pBusinessService,
                                    jcsInstance.getString("otd_admin_url"));
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nStatusStri = " + statusStr);
        return statusStr;
    }

    public void createMWaaSDriver(final MachineSize machineSize, final int numServers, final String dbSelection,
                                  final String sQueueId) {

        new Thread(new Runnable() {
            public void run() {
                int imageId = 0;
                if (dbSelection.equals("New Schema/DB")) {
                    imageId = createSchemaDBaaS(machineSize, sQueueId, "Alpha", "AlphaDBaaS_", "", 0);
                }
                createMWaaS(machineSize, sQueueId, numServers, imageId);
            }
        }).start();
    }


    public void createMWaaS(MachineSize machineSize, String sQueueId, int numServers, int imageId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;

        try {
            System.out.println("\nin createMWaaS");
            Client client = getClient(sQueueId, null);

            String mwName = new String("AlphaWLS_" + String.format("%04d", imageId));
            uri = this.getConfigProperties().getProperty("wls1212URI");
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            String se =
                new String("{\"zone\": \"" + this.getConfigProperties().getProperty("mwZoneURI") + "\",\n" +
                           " \"name\": \"" + mwName + "\",\n" + " \"description\": \"Weblogic Cluster\"\n" + "}");

            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.jaas.JavaPlatformInstance+json").header("Accept",
                                                                                                         "application/oracle.com.cloud.jaas.JavaPlatformInstance+json").accept("application/oracle.com.cloud.jaas.JavaPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                   se);

            if (response.getStatus() != 200) {
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_FAILED, "MWaaS Environment creation failed");
                //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);

                System.out.println("Output from Server .... \n");
                System.out.println(output);
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "MWaaS Environment creation scheduled");
                JSONObject jsonObj = new JSONObject(output);
                String requestURI = jsonObj.getString("uri");
                String requestName = jsonObj.getString("name");

                businessServiceCatalogFacade = new JavaServiceFacade();
                pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "RequestURI", pBusinessService, requestURI);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "RequestName", pBusinessService,
                                        requestName.substring(0, 24));
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "WLSDomainName", pBusinessService, mwName);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "MWaaS", pBusinessService, "EM");
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createSchemaDBaaS(MachineSize machineSize, String sQueueId, String schema, String serviceName,
                                 String workload, int updateId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        String se = null;
        String username = null;
        int imageId = 0;

        try {
            System.out.println("\nin createSchemaDBaaS");
            Client client = getClient(sQueueId, null);

            imageId = getImageId();
            String dbName = new String(serviceName + String.format("%04d", imageId));
            String tblspName = new String(dbName + "_tblsp");
            System.out.println("\nCreateSchemaDBaaS Schema = " + schema);
            if (schema.equals("hr")) {
                uri = this.getConfigProperties().getProperty("alphaSchemaHRURI");
                username = new String("HRNEW" + String.format("%04d", imageId));
                se =
                    new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                               " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                               dbName + "\",\n" + "     \"workload_name\" : \"" + workload + "\",\n" +
                               "     \"schema\" :\n" + "       [{\n" + "        \"username\" : \"" + username +
                               "\",\n" + "        \"original_name\" : \"HR\",\n" +
                               "        \"password\" : \"Welcome1\"\n" + "       }],\n" + "  \"tablespaces\" :\n" +
                               "   [{\n" + "        \"old_tablespace_name\" : \"EXAMPLE\",\n" +
                               "        \"new_tablespace_name\" : \"" + tblspName + "\"\n" + "      }]\n" + "   }\n" +
                               "}");
            } else if (schema.equals("Alpha")) {
                uri = this.getConfigProperties().getProperty("alphaSchemaAlphaURI");
                username = new String("ALPHA" + String.format("%04d", imageId));
                se =
                    new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                               " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                               dbName + "\",\n" + "     \"workload_name\" : \"" +
                               this.getConfigProperties().getProperty("workloadName") + "\",\n" +
                               "     \"schema\" :\n" + "       [{\n" + "        \"username\" : \"" + username +
                               "\",\n" + "        \"original_name\" : \"ALPHA\",\n" +
                               "        \"password\" : \"Welcome1\"\n" + "       }],\n" + "  \"tablespaces\" :\n" +
                               "   [{\n" + "        \"old_tablespace_name\" : \"USERS\",\n" +
                               "        \"new_tablespace_name\" : \"" + tblspName + "\"\n" + "      }]\n" + "   }\n" +
                               "}");
            } else {
                uri = this.getConfigProperties().getProperty("alphaSchemaEmptyURI");
                username = new String("EMPTY" + String.format("%04d", imageId));
                se =
                    new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                               " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                               dbName + "\",\n" + "     \"masterAccount\" : \"" + username + "\",\n" +
                               "     \"workload_name\" : \"" + workload + "\",\n" + "     \"schema\" :\n" +
                               "       [{\n" + "        \"username\" : \"" + username + "\",\n" +
                               "        \"password\" : \"Welcome1\",\n" + "        \"tablespace_name\" : \"" +
                               tblspName + "\"\n" + "       }]\n" + "   }\n" + "}");
            }
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.SchemaPlatformInstance+json").header("Accept",
                                                                                                             "application/oracle.com.cloud.common.SchemaPlatformInstance+json").accept("application/oracle.com.cloud.common.SchemaPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                               se);

            if (response.getStatus() != 200) {
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_FAILED,
                                        "Schema as a Service Environment creation failed");
                //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String output = response.getEntity(String.class);

                System.out.println("Output from Server .... \n");
                System.out.println(output);
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                        "Schema as a Service Environment creation scheduled");
                JSONObject jsonObj = new JSONObject(output);
                String requestURI = jsonObj.getString("uri");

                businessServiceCatalogFacade = new JavaServiceFacade();
                pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "SchemaURI", pBusinessService, requestURI);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "SchemaName", pBusinessService, dbName);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "username", pBusinessService, username);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public int createPluggableDBaaS(MachineSize machineSize, String sQueueId, String dbType, String serviceName,
                                    String workload, String username, int updateId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        String pdbName = null;
        int imageId = 0;

        try {
            System.out.println("\nin createPluggableDBaaS");
            Client client = getClient(sQueueId, null);

            imageId = getImageId();
            String dbName = new String(serviceName + String.format("%04d", imageId));
            //String username = new String ("ALPHA" + String.format("%04d", imageId));
            String tblspName = new String(dbName + "_tblsp");
            if (dbType.equals("Sales")) {
                uri = this.getConfigProperties().getProperty("alphaPluggableSalesURI");
                pdbName = new String("SALES" + String.format("%04d", imageId));
            } else {
                uri = this.getConfigProperties().getProperty("alphaPluggableEmptyURI");
                pdbName = new String("PDB" + String.format("%04d", imageId));
            }
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            String se =
                new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                           " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                           dbName + "\",\n" + "     \"pdb_name\" : \"" + pdbName + "\",\n" + "     \"username\" : \"" +
                           username + "\",\n" + "     \"password\" : \"Welcome1\",\n" + "     \"workload_name\" : \"" +
                           workload + "\",\n" + "     \"tablespaces\" : [\"" + tblspName + "\"]\n" + "   }\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").header("Accept",
                                                                                                                  "application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").accept("application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                                              se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                    "Pluggable Database Environment creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "PluggableURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "PDBName", pBusinessService, dbName);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "username", pBusinessService, username);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public int createDatabaseDBaaS(MachineSize machineSize, String sQueueId, String serviceName, String username,
                                   int updateId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        int imageId = 0;

        try {
            System.out.println("\nin createDatabaseDBaaS");
            Client client = getClient(sQueueId, null);

            imageId = getImageId();
            String dbName = new String(serviceName + String.format("%04d", imageId));
            //String username = new String ("ALPHA" + String.format("%04d", imageId));
            uri = this.getConfigProperties().getProperty("alphaDatabaseURI");
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            String se =
                new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                           " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                           dbName + "\",\n" + "     \"username\" : \"" + username + "\",\n" +
                           "     \"password\" : \"Welcome1\"\n" + "   }\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.DbPlatformInstance+json").header("Accept",
                                                                                                         "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                   se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Database Environment creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "DatabaseURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "databaseName", pBusinessService, dbName);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "username", pBusinessService, username);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public int createCloneDBaaS(MachineSize machineSize, String sQueueId, String serviceName, String username,
                                int updateId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        int imageId = 0;

        try {
            System.out.println("\nin createCloneDBaaS");
            Client client = getClient(sQueueId, null);

            imageId = getImageId();
            String dbName = new String(serviceName + String.format("%04d", imageId));
            //String username = new String ("ALPHA" + String.format("%04d", imageId));
            uri = this.getConfigProperties().getProperty("alphaCloneURI");
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            String se =
                new String("{\"zone\" : \"" + this.getConfigProperties().getProperty("schemaZoneURI") + "\",\n" +
                           " \"name\" : \"" + dbName + "\",\n" + " \"params\": {\n" + "     \"service_name\" : \"" +
                           dbName + "\",\n" + "     \"username\" : \"" + username + "\",\n" +
                           "     \"password\" : \"Welcome1\"\n" + "   }\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.DbPlatformInstance+json").header("Accept",
                                                                                                         "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                   se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Database Environment creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "DatabaseURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "databaseName", pBusinessService, dbName);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "username", pBusinessService, username);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public void deleteStorageContainer(String sQueueId, String containerName) {
        String identityDomain = null;

        try {
            Client client = getPublicCloudClient(sQueueId, true);

            identityDomain = this.getConfigProperties().getProperty("identityDomain");
            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("storageCloudURL") + identityDomain + "/" +
                                containerName);
            ClientResponse response =
                webResource.header("X-ID-TENANT-NAME", identityDomain).delete(ClientResponse.class);

            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createStorageContainer(String sQueueId) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        int imageId = 0;
        String identityDomain = null;

        try {
            imageId = getImageId();
            String containerName = new String("Alpha" + String.format("%04d", imageId) + "_SC");

            Client client = getPublicCloudClient(sQueueId, true);

            identityDomain = this.getConfigProperties().getProperty("identityDomain");
            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("storageCloudURL") + identityDomain + "/" +
                                containerName);
            ClientResponse response = webResource.header("X-ID-TENANT-NAME", identityDomain).put(ClientResponse.class);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "ContainerName", pBusinessService, containerName);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageId;
    }

    public void createJCS(MachineSize machineSize, String sQueueId, int imageId) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        String identityDomain = null;
        String jobURL = null;
        String instanceName = null;
        String domainName = null;

        try {
            System.out.println("\nin createJCS");
            Client client = getPublicCloudClient(sQueueId, true);

            identityDomain = this.getConfigProperties().getProperty("identityDomain");
            WebResource webResource =
                client.resource(this.getConfigProperties().getProperty("publicCloudURL") + identityDomain);

            instanceName = "Alpha" + String.format("%04d", imageId) + "JCS";
            domainName = "Alpha" + String.format("%04d", imageId) + "J_domain";
            String se =
                new String("{\n" + "    \"serviceName\" : \"" + instanceName + "\",\n" + "    \"level\" : \"PAAS\",\n" +
                           "    \"subscriptionType\" : \"HOURLY\",\n" +
                           "    \"description\" : \"Alpha Office Java Cloud Service\",\n" +
                           "    \"provisionOTD\" : true,\n" + "    \"cloudStorageContainer\" : \"Storage-" +
                           this.getConfigProperties().getProperty("identityDomain") + "/Alpha" +
                           String.format("%04d", imageId) + "_SC\",\n" + "    \"cloudStorageUser\" : \"" +
                           this.getConfigProperties().getProperty("cloudUsername") + "\",\n" +
                           "    \"cloudStoragePassword\" : \"" +
                           this.getConfigProperties().getProperty("cloudPassword") + "\",\n" + " \n" +
                           "\"parameters\" : [\n" + "    {\n" + "        \"type\" : \"weblogic\",\n" +
                           "        \"version\" : \"12.1.2.0.3\",\n" + "        \"edition\" : \"EE\",\n" +
                           "        \"domainMode\" : \"PRODUCTION\",\n" + "        \"managedServerCount\" : \"1\",\n" +
                           "        \"adminPort\" : \"7001\",\n" + "        \"deploymentChannelPort\" : \"9001\",\n" +
                           "        \"securedAdminPort\" : \"7002\",\n" + "        \"contentPort\" : \"7003\",\n" +
                           "        \"securedContentPort\" : \"7004\",\n" + "        \"domainName\" : \"" + domainName +
                           "\",\n" + "        \"clusterName\" : \"Alpha" + String.format("%04d", imageId) +
                           "J_cluster\",\n" + "        \"adminUserName\" : \"weblogic\",\n" +
                           "        \"adminPassword\" : \"Alpha2014_\",\n" +
                           "        \"nodeManagerPort\" : \"6555\",\n" +
                           "        \"nodeManagerUserName\" : \"weblogic\",\n" +
                           "        \"nodeManagerPassword\" : \"Alpha2014_\",\n" + "        \"dbServiceName\" : \"" +
                           this.getConfigProperties().getProperty("dbServiceName") + "\",\n" +
                           "        \"dbaName\" : \"SYSTEM\",\n" + "        \"dbaPassword\" : \"Alpha2014_\",\n" +
                           "        \"shape\" : \"oc3\",\n" + "        \"domainVolumeSize\" : \"10240M\",\n" +
                           "        \"backupVolumeSize\" : \"20480M\",\n" +
                           "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDK44PtRnT9HaZE0coQZRhsfh2SSi7nT0DGgUf3u37U2sbQ0QjRFmV9NZ5eIK+u34xfG9jYt1Lxz8dQTCf4pcjOzX65wbcQDEXx2vkAXNUk7trjAiIGs73kKNX//gTIPV4nnyY77lO5NGymx1JP3/6X8paMduEFFEBKkhZkLscAtQ== JCS HOL\"\n" +
                           "    },\n" + "    {\n" + "        \"type\" : \"otd\",\n" +
                           "        \"adminUserName\" : \"weblogic\",\n" +
                           "        \"adminPassword\" : \"Alpha2014_\",\n" +
                           "        \"listenerPortsEnabled\" : true,\n" + "        \"listenerPort\" : \"8080\",\n" +
                           "        \"listenerType\" : \"http\",\n" + "        \"securedListenerPort\" : \"8081\",\n" +
                           "        \"loadBalancingPolicy\" : \"least_connection_count\",\n" +
                           "        \"adminPort\" : \"8989\",\n" + "        \"shape\" : \"oc3\",\n" +
                           "        \"VMsPublicKey\" : \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDK44PtRnT9HaZE0coQZRhsfh2SSi7nT0DGgUf3u37U2sbQ0QjRFmV9NZ5eIK+u34xfG9jYt1Lxz8dQTCf4pcjOzX65wbcQDEXx2vkAXNUk7trjAiIGs73kKNX//gTIPV4nnyY77lO5NGymx1JP3/6X8paMduEFFEBKkhZkLscAtQ== JCS HOL\"\n" +
                           "    }\n" + "]\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/vnd.com.oracle.oracloud.provisioning.Service+json").header("X-ID-TENANT-NAME",
                                                                                                           identityDomain).post(ClientResponse.class,
                                                                                                                                se);

            if (response.getStatus() != 202) {
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_FAILED,
                                        "Oracle Public Cloud JCS Environment creation failed");
                //throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                final MultivaluedMap<String, String> headers = response.getHeaders();
                if (headers != null) {
                    jobURL = headers.getFirst("Location");
                }
                System.out.println("Output from Server .... \n");
                System.out.println(jobURL);
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                        "Oracle Public Cloud JCS creation scheduled");

                businessServiceCatalogFacade = new JavaServiceFacade();
                pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

                pBusinessValueID = UUID.randomUUID();
                pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "JobURL", pBusinessService, jobURL);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "InstanceName", pBusinessService, instanceName);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "soa_server", pBusinessService, domainName);
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "MWaaS", pBusinessService, "OPC");
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                pBusinessValueID = UUID.randomUUID();
                pBusinessValues =
                    new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService,
                                        this.getConfigProperties().getProperty("dbServiceName"));
                businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void editMWaaSDataSource(String sQueueId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        ClientResponse response = null;
        String username = null;
        String connectString = null;

        try {
            System.out.println("\nIn editMWaaSDataSource");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for (int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++) {
                if (pBusinessValuesList.get(i).getName().equals("username")) {
                    username = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("dsURI")) {
                    uri = pBusinessValuesList.get(i).getVal();
                }
            }

            connectString =
                "jdbc:oracle:thin:" + username + "/oracle@" + this.getConfigProperties().getProperty("schemaHostname") +
                ":1521:" + this.getConfigProperties().getProperty("schemaSID");

            Client client = getClient(sQueueId, null);

            System.out.println("\nURL = " + this.getConfigProperties().getProperty("emR4URL") + uri);
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            String se =
                new String("{\"username\" : \"" + username + "\",\n" + " \"password\" : \"oracle\",\n" +
                           " \"database_connect_string\" : \"" + connectString + "\"\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type", "application/oracle.com.cloud.jaas.DataSource+json").header("Accept",
                                                                                                               "application/oracle.com.cloud.jaas.DataSource+json").accept("application/oracle.com.cloud.jaas.DataSource+json").put(ClientResponse.class,
                                                                                                                                                                                                                                    se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createExaCloneDriver(final MachineSize machineSize, final String teamNum, final int numInstances,
                                     final String sQueueId, final String provisionCode, final int hostNumber,
                                     final String emPassword, final String scriptLocation) {

        new Thread(new Runnable() {
            public void run() {
                ProcessBuilder procBuilder;
                Process process;
                File batchFile;
                String sga = String.format("%dG", machineSize.getMemory() + machineSize.getMemoryAdder());
                String hostNumStr = String.valueOf(hostNumber);
                JavaServiceFacade businessServiceCatalogFacade;
                PBusinessService pBusinessService = null;
                PBusinessValues pBusinessValues;
                UUID pBusinessValueID;
                String jobId = null;
                String lastLine = null;
                String backupTag = null;
                String fileName = null;
                String statusStr = "Running";

                try {
                    System.out.println("\n createExaCloneDriver");
                    businessServiceCatalogFacade = new JavaServiceFacade();
                    pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

                    pBusinessValueID = UUID.randomUUID();
                    pBusinessValues =
                        new PBusinessValues(pBusinessValueID.toString(), "HostNumber", pBusinessService, hostNumStr);
                    businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
                    if (provisionCode.equals("ExaDBaaSRMANProd")) {
                        backupTag = "CLONEBKUP4";
                        batchFile = new File(scriptLocation + "/SubmitCloneBackgroundDriver.sh");
                    } else {
                        DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
                        df.setTimeZone(TimeZone.getTimeZone("PST"));
                        backupTag = "CLONEBKUP_" + df.format(new Date());
                        batchFile = new File(scriptLocation + "/SubmitClonewithBackupBackgroundDriver.sh");
                    }
                    System.out.println("\n createExaCloneDriver - Batch File = " + batchFile);
                    //Usage: SubmitCloneBackgroundDriver.sh <team number> <# of instances> <SGA Size> <backup tag>
                    procBuilder =
                        new ProcessBuilder(batchFile.getAbsolutePath(), teamNum, String.valueOf(numInstances), sga,
                                           backupTag, String.valueOf(hostNumber), emPassword);
                    process = procBuilder.start();
                    InputStream procIn = process.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
                    String inputLine;
                    fileName = String.format("/u01/dbaas/Oracle/logs/Team%sClone.log", teamNum);
                    File outputFile = new File(fileName);
                    BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
                    while ((inputLine = in.readLine()) != null) {
                        jobId = lastLine;
                        lastLine = inputLine;
                        output.write(inputLine);
                        output.write("\n");
                        output.flush();
                    }
                    in.close();
                    output.close();
                    int exitValue = process.waitFor();
                    System.out.println("\n\ncreateExaCloneDriver - Exit Value is " + exitValue + "\n");

                    pBusinessValueID = UUID.randomUUID();
                    pBusinessValues =
                        new PBusinessValues(pBusinessValueID.toString(), "JobId", pBusinessService, jobId);
                    businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

                    System.out.println("\nSleep for 10 minutes");
                    Thread.sleep(1000 * 60 * 10);
                    while (statusStr.contains("Running")) {
                        System.out.println("\nCall checkExaDBaaSJobStatus");
                        statusStr =
                            checkExaDBaaSJobStatus(sQueueId, provisionCode, jobId, hostNumStr, teamNum, scriptLocation,
                                                   emPassword);
                        System.out.println("\ncheckExaDBaaSJobStatus - Status = " + statusStr);
                        if (statusStr.contains("Running")) {
                            Thread.sleep(1000 * 60 * 10);
                        }
                    }


                } catch (IOException e) {
                } catch (InterruptedException e) {
                }
            }
        }).start();
        updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "EBS Database Clone creation scheduled");
    }

    public String checkExaDBaaSJobStatus(String sQueueId, String provisionCode, String jobId, String hostNumber,
                                         String teamNum, String scriptLocation, String emPassword) {
        String statusStr = null;
        String teamName = null;
        ProcessBuilder procBuilder;
        Process process;
        String fileName = null;
        File batchFile = new File(scriptLocation + "/CheckSmartClone.sh");

        try {
            System.out.println("\nIn checkExaDBaaSJobStatus");
            System.out.println("\ncheckExaDBaaSJobStatus - BatchFile = " + batchFile);
            teamName = String.format("team%sd", teamNum);

            System.out.println("\ncheckExaDBaaSJobStatus - Call ProcessBuilder - JobId = " + jobId + " TeamNum = " +
                               teamNum + " HostNum = " + hostNumber + " EM Password = " + emPassword);
            procBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), jobId, teamNum, hostNumber, emPassword);
            process = procBuilder.start();
            fileName = String.format("/u01/dbaas/Oracle/logs/Team%sCloneStatus.log", teamNum);
            File outputFile = new File(fileName);
            BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
            InputStream procIn = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                statusStr = inputLine;
                output.write(inputLine);
                output.write("\n");
                output.flush();
            }
            in.close();
            output.close();
            int exitValue = process.waitFor();
            System.out.println("\nStatus = " + statusStr);
            if (statusStr.contains("Running")) {
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_INITIATED,
                                        "EBS Application Tier creation scheduled");
            } else if (statusStr.contains("Succeeded")) {
                updateProvisioninStatus(sQueueId, STATUS_PROVIONING_COMPLETED,
                                        "EBS Application Tier creation completed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return statusStr;
    }

    public String updateExaDBaaSJobStatus(String sQueueId) {
        String requestStatus = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;

        try {
            System.out.println("\nIn updateExaDBaaSJobStatus");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            requestStatus = pBusinessService.getProvisioningStatus();
            System.out.println("\nRequestStatus = " + requestStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }

    public void updateExaEBSParams(String sQueueId, String provisionCode) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        List<PTechnicalService> pTechnicalServiceList = null;
        List<PBusinessParam> pBusinessParamList = null;
        List<PTechnicalParam> pTechnicalParamList;
        String homeURL = null;
        String dbConnectStr = null;
        String hostname;
        String dbHostname;
        String teamNum;

        if (provisionCode.equals("ExaDBaaSRMANProd")) {
            hostname = "exl10cn18.us.oracle.com";
            dbHostname = "rstsun3-scan.us.oracle.com";
        } else {
            hostname = "exl10cn18.us.oracle.com";
            dbHostname = "rstsun3-scan.us.oracle.com";
        }

        businessServiceCatalogFacade = new JavaServiceFacade();
        pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        teamNum = pBusinessService.getUserName().substring(4);

        homeURL = String.format("http://%s:80%s/OA_HTML/AppsLogin", hostname, teamNum);
        pBusinessValueID = UUID.randomUUID();
        pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "AppsLoginURL", pBusinessService, homeURL);
        businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);

        String dbName = String.format("EBS%sCL", teamNum);
        dbConnectStr =
            String.format("(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=%s)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=%s)(INSTANCE_NAME=%s)))",
                          dbHostname, dbName, dbName);
        pBusinessValueID = UUID.randomUUID();
        pBusinessValues =
            new PBusinessValues(pBusinessValueID.toString(), "ConnectString", pBusinessService, dbConnectStr);
        businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
    }

    public void deleteExaRMANClone(String sQueueId, String provisionCode) {
        JavaServiceFacade businessServiceCatalogFacade;
        List<PTechnicalService> pTechnicalServiceList = null;
        List<PTechnicalParam> pTechnicalParamList;
        PBusinessService pBusinessService;
        String teamNum = null;
        int numInstances = 2;
        String teamName = null;
        ProcessBuilder procBuilder;
        Process process;
        File batchFile;
        String scriptLocation = this.getConfigProperties().getProperty("scriptsLocation");

        try {
            batchFile = new File(scriptLocation + "/DeprovisionClone.sh");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
            teamNum = pBusinessService.getUserName().substring(4);
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            for (int idx = 0; pTechnicalServiceList != null && idx < pTechnicalServiceList.size(); idx++) {
                if (pTechnicalServiceList.get(idx).getProvisionCode().equals("DBaaS")) {
                    pTechnicalParamList = pTechnicalServiceList.get(idx).getPTechnicalParamList();
                    for (int i = 0; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++) {
                        if (pTechnicalParamList.get(i) != null &&
                            pTechnicalParamList.get(i).getProvisionCode() != null &&
                            pTechnicalParamList.get(i).getProvisionCode().equals("HA")) {
                            numInstances = Integer.parseInt(pTechnicalParamList.get(i).getProvisionValue());
                        }
                    }
                }
            }
            teamName = String.format("team%s", teamNum);
            procBuilder =
                new ProcessBuilder(batchFile.getAbsolutePath(), teamNum, String.valueOf(numInstances),
                                   this.getConfigProperties().getProperty(teamName));
            process = procBuilder.start();
            InputStream procIn = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(procIn));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createExaThinClone(String sQueueId, String teamNum, String haValue) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        boolean retry = true;
        ClientResponse response = null;
        String databaseSID;
        String serviceName;
        String dbUsername;

        try {
            int imageId = getImageId();
            databaseSID = String.format("T%s%04d", teamNum, imageId);
            dbUsername = String.format("team%s", teamNum);
            serviceName = String.format("T%s%04d_ServiceName", teamNum, imageId);
            System.out.println("\ncreateSnapClone\n");
            System.out.println("\nProperties File ....\n");
            System.out.println(this.getConfigProperties().toString());

            Client client = getClient(sQueueId, teamNum);

            System.out.println("\nCreateSnapClone havVlue = " + haValue + "\n");
            if (haValue.equals("Gold")) {
                uri = this.getConfigProperties().getProperty("thinCloneGoldURI");
            } else if (haValue.equals("Silver")) {
                uri = this.getConfigProperties().getProperty("thinCloneSilverURI");
            } else {
                uri = this.getConfigProperties().getProperty("thinCloneBronzeURI");
            }
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("PST"));
            Calendar c = Calendar.getInstance();
            //c.add(Calendar.HOUR, 8);
            c.add(Calendar.DATE, Integer.parseInt(this.getConfigProperties().getProperty("deprovisionDays")));
            String requestEndDate = (String) (df.format(c.getTime()));
            String se =
                new String("{\"zone\": \"/em/cloud/dbaas/zone/1FA469CEEEF7F10FA1F9F3573AA0F7A8\",\n" + " \"name\": \"" +
                           sQueueId + "\",\n" + " \"description\": \"EBS Snap Clone\",\n" + " \"end_date\": \"" +
                           requestEndDate + "\",\n" + " \"params\":\n" + "     {\"username\": \"" + dbUsername +
                           "\",\n" + "      \"password\": \"oracle\",\n" + "      \"database_sid\": \"" + databaseSID +
                           "\",\n" + "      \"service_name\": \"" + serviceName + "\"\n" + "     } \n" + "}");

            System.out.println("\nBody = " + se + "\n");
            while (retry) {
                response =
                    webResource.header("Content-Type",
                                       "application/oracle.com.cloud.common.DbPlatformInstance+json").header("Accept",
                                                                                                             "application/oracle.com.cloud.common.DbPlatformInstance+json").accept("application/oracle.com.cloud.common.DbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                       se);

                if (response.getStatus() != 200) {
                    if (retry && response.getStatus() == 500) {
                        retry = false;
                        System.out.println("\nCreateSnapClone: 500 Error Received...\n");
                        Thread.sleep(30000);
                    } else
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
                retry = false;
            }

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED, "Snap Clone creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "RequestURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_FAILED, "EM Thin Clone Environment creation failed");
            e.printStackTrace();
        }
    }

    public int createPluggableDBaaSExa(String sQueueId) {
        String uri = null;
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        ClientResponse response = null;
        int imageId = 0;

        try {
            System.out.println("\nin createPluggableDBaaS");
            businessServiceCatalogFacade = new JavaServiceFacade();
            pBusinessService = businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);

            Client client = getClient(sQueueId, pBusinessService.getUserName().substring(4));

            imageId = getImageId();
            String dbName = new String("AlphaDBaaS_" + String.format("%04d", imageId));
            String pdbName = new String("PDB" + String.format("%04d", imageId));
            String username = new String("ALPHA" + String.format("%04d", imageId));
            String tblsp1Name = new String(dbName + "_tblsp1");
            String tblsp2Name = new String(dbName + "_tblsp2");
            uri = this.getConfigProperties().getProperty("alphaPluggableURI");
            WebResource webResource = client.resource(this.getConfigProperties().getProperty("emR4URL") + uri);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("PST"));
            Calendar c = Calendar.getInstance();
            //c.add(Calendar.HOUR, 8);
            c.add(Calendar.DATE, Integer.parseInt(this.getConfigProperties().getProperty("deprovisionDays")));
            String requestEndDate = (String) (df.format(c.getTime()));
            String se =
                new String("{\"zone\": \"/em/cloud/dbaas/zone/1FA469CEEEF7F10FA1F9F3573AA0F7A8\",\n" +
                           " \"name\" : \"" + dbName + "\",\n" + " \"end_date\": \"" + requestEndDate + "\",\n" +
                           " \"params\": {\n" + "     \"service_name\" : \"" + dbName + "\",\n" +
                           "     \"pdb_name\" : \"" + pdbName + "\",\n" + "     \"username\" : \"" + username +
                           "\",\n" + "     \"password\" : \"welcome1\",\n" +
                           "     \"workload_name\" : \"Small Workload\",\n" + "     \"tablespaces\" :\n" +
                           "       [\"" + tblsp1Name + "\", \"" + tblsp2Name + "\"]\n" + "   }\n" + "}");

            System.out.println("\nBody = " + se);
            response =
                webResource.header("Content-Type",
                                   "application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").header("Accept",
                                                                                                                  "application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").accept("application/oracle.com.cloud.common.PluggableDbPlatformInstance+json").post(ClientResponse.class,
                                                                                                                                                                                                                                                                              se);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
            updateProvisioninStatus(sQueueId, STATUS_PROVIONING_SCHEDULED,
                                    "Pluggable Database Environment creation scheduled");
            JSONObject jsonObj = new JSONObject(output);
            String requestURI = jsonObj.getString("uri");

            pBusinessValueID = UUID.randomUUID();
            pBusinessValues =
                new PBusinessValues(pBusinessValueID.toString(), "PluggableURI", pBusinessService, requestURI);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "PDBName", pBusinessService, dbName);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
            pBusinessValueID = UUID.randomUUID();
            pBusinessValues = new PBusinessValues(pBusinessValueID.toString(), "username", pBusinessService, username);
            businessServiceCatalogFacade.persistPBusinessValues(pBusinessValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageId;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public static void main(String[] args) {
        MachineSize machineSize = null;

        System.out.println("Test output from EMApiTest");
        EMProvisioningService ps = new EMProvisioningService();
    }
}
