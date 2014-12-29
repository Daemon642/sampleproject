package oracle.alpha.cloud;

import java.io.IOException;
import java.io.StringReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.List;

import java.util.Random;
import java.util.UUID;

import javax.ejb.MessageDriven;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import provisiontables.JavaServiceFacade;
import provisiontables.PBusinessParam;
import provisiontables.PBusinessService;
import provisiontables.PBusinessValues;
import provisiontables.PTechnicalDetail;
import provisiontables.PTechnicalDetailValue;
import provisiontables.PTechnicalParam;
import provisiontables.PTechnicalService;

@MessageDriven(name = "EMMessageDrivenEJBBean")
public class EMMessageDrivenEJBBean implements MessageListener {
    private EMProvisioningService ps;
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

    public EMMessageDrivenEJBBean () {
        this.ps = new EMProvisioningService ();   
    }
                                 
    public EMIaaSMessage createMessageFromXML (String xmlString) {
        EMIaaSMessage emMsg = null;
        try {
            System.out.println ("\nMessage = " + xmlString);
            emMsg = new EMIaaSMessage ();
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new InputSource( new StringReader(xmlString)));

            // normalize text representation
            doc.getDocumentElement ().normalize ();

            NodeList EMIaaSNodes = doc.getElementsByTagName("EMIaaSMessage");
            Node EMIaaSNode = EMIaaSNodes.item(0);
            Element EMIaaSMessage = (Element)EMIaaSNode;
        
            NodeList methodNodes = EMIaaSMessage.getElementsByTagName("Method");
            Element methodElement = (Element)methodNodes.item(0);
            NodeList textMethodList = methodElement.getChildNodes();
            emMsg.setMethod(((Node)textMethodList.item(0)).getNodeValue().trim());

            NodeList imageNodes = EMIaaSMessage.getElementsByTagName("ImageName");
            Element imageElement = (Element)imageNodes.item(0);
            NodeList textImageList = imageElement.getChildNodes();
            emMsg.setImageName(((Node)textImageList.item(0)).getNodeValue().trim());

            NodeList sQueueIdNodes = EMIaaSMessage.getElementsByTagName("SQueueId");
            Element sQueueIdElement = (Element)sQueueIdNodes.item(0);
            NodeList textSQueueIdList = sQueueIdElement.getChildNodes();
            emMsg.setSQueueId(((Node)textSQueueIdList.item(0)).getNodeValue().trim());
            
            NodeList businessServiceIdNodes = EMIaaSMessage.getElementsByTagName("SBusinessServiceId");
            Element businessServiceIdElement = (Element)businessServiceIdNodes.item(0);
            NodeList textBusinessServiceIdList = businessServiceIdElement.getChildNodes();
            emMsg.setSBusinessServiceId(((Node)textBusinessServiceIdList.item(0)).getNodeValue().trim());
            
        } catch (SAXException e) {
            System.out.println("Exception: " + e.toString());
        } catch (IOException e) {
            System.out.println("Exception: " + e.toString());
        } catch (ParserConfigurationException e) {
            System.out.println("Exception: " + e.toString());
        }        
        
        return emMsg;
    }
    
    public String getBusinessServiceParam (PBusinessService pBusinessService, String provisionCode) {
        String provisionValue = null;
        List<PBusinessParam> pBusinessParamList;
        
        System.out.println ("\ngetBusinessServiceParam: ProvisionCode = " + provisionCode);
        pBusinessParamList = pBusinessService.getPBusinessParamList();
        for ( int i = 0 ; pBusinessParamList != null && i < pBusinessParamList.size(); i++ ) {
            if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals(provisionCode)) {
                provisionValue = pBusinessParamList.get(i).getProvisionValue(); 
            }            
        }
        return provisionValue;
    }

    public void processDBaaSTechnicalService (PTechnicalService pTechnicalService, String sQueueId, String dbType, int numClones) {
        MachineSize machineSize = null;
        List<PTechnicalDetail> pTechnicalDetailList;
        List<PTechnicalDetailValue> pTechnicalDetailValueList;
        
        pTechnicalDetailList = pTechnicalService.getPTechnicalDetailList();
        for ( int i = 0 ; pTechnicalDetailList != null && i < pTechnicalDetailList.size(); i++ ) {
            pTechnicalDetailValueList = pTechnicalDetailList.get(i).getPTechnicalDetailValueList();
            for ( int j = 0 ; pTechnicalDetailValueList != null && j < pTechnicalDetailValueList.size(); j++ ) { 
                if (pTechnicalDetailValueList.get(j).getProvisionType() != null && pTechnicalDetailValueList.get(j).getProvisionType().equals("TableBlockSize") && pTechnicalDetailValueList.get(j).getProvisionCode().equals("AlphaOfficeBase")) {
                    ObjectMapper objectMapper = new ObjectMapper(); 
                    try {
                        machineSize =
                                objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), MachineSize.class);
                    } catch (JsonParseException e) {
                        System.out.println("JsonParseException: " + e.toString());
                    } catch (JsonMappingException e) {
                        System.out.println("JsonMappingException: " + e.toString());
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.toString());
                    }
                    System.out.println ("MachineSizeName = " + machineSize.getName());
                    if (dbType.equals("AlphaDBaaS")) {
                        for ( int idx = 0; idx < numClones; idx++) {
                            this.ps.createSnapClone(machineSize, sQueueId);
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        this.ps.createRACCluster(machineSize, sQueueId);
                    }
                }
            }        
        }
    }
    
    public void processDBaaSExaTechnicalService (PTechnicalService pTechnicalService, String sQueueId, String teamNum) {
        List<PTechnicalParam> pTechnicalParamList;
        String haValue = "";
        
        System.out.println ("\nprocessDBaaSExaTechnicalService\n");
        pTechnicalParamList = pTechnicalService.getPTechnicalParamList();
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("HA")) {
                haValue = pTechnicalParamList.get(i).getProvisionValue();
            }
        }
        this.ps.createExaThinClone(sQueueId, teamNum, haValue);
    }

    
    public void processMWaaSTechnicalService (PBusinessService pBusinessService, String sQueueId) {
        MachineSize machineSize = null;
        List<PBusinessParam> pBusinessParamList = null;
        String dbSelection = null;
        String mwaasSelection = "Yes";
        int numServers = 1;
        
        System.out.println ("\nin processMWaaSTechnicalService");
        pBusinessParamList = pBusinessService.getPBusinessParamList();
        for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
            if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("DBaaS")) {
                dbSelection = pBusinessParamList.get(i).getProvisionValue();
            }
            if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("RequiresCorporateNetwork")) {
                mwaasSelection = pBusinessParamList.get(i).getProvisionValue();
            }
        }
        if (mwaasSelection.equals("Yes")) {
            this.ps.createMWaaSDriver(machineSize, numServers, dbSelection, sQueueId);
        } else {
            processJCSTechnicalService (sQueueId);            
        }
    }
        
    public void processSchemaDBaaSTechnicalService (PTechnicalService pTechnicalService, String sQueueId) {
        MachineSize machineSize = null;
        String schema = null;
        String serviceName = null;
        String workload = null;
        List<PTechnicalParam> pTechnicalParamList;
        
        System.out.println ("\nin processSchemaDBaaSTechnicalService");
        
        pTechnicalParamList = pTechnicalService.getPTechnicalParamList();
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("Schema")) {
                schema = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("ServiceName")) {
                serviceName = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("Workload")) {
                workload = pTechnicalParamList.get(i).getProvisionValue();
            }
        }
        this.ps.createSchemaDBaaS(machineSize, sQueueId, schema, serviceName, workload, 1);
    }
        
    public void processPluggableDBaaSTechnicalService (PTechnicalService pTechnicalService, String sQueueId) {
        MachineSize machineSize = null;
        String dbType = null;
        String serviceName = null;
        String workload = null;
        String username = null;
        List<PTechnicalParam> pTechnicalParamList;
        
        System.out.println ("\nin processPluggableDBaaSTechnicalService");
        pTechnicalParamList = pTechnicalService.getPTechnicalParamList();
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("DatabaseType")) {
                dbType = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("ServiceName")) {
                serviceName = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("Workload")) {
                workload = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("username")) {
                username = pTechnicalParamList.get(i).getProvisionValue();
            }
        }
        this.ps.createPluggableDBaaS(machineSize, sQueueId, dbType, serviceName, workload, username, 1);
    }
        
    public void processPluggableDBaaSExaTechnicalService (PTechnicalService pTechnicalService, String sQueueId) {
        this.ps.createPluggableDBaaSExa(sQueueId);
    }
    

    public void processDatabaseDBaaSTechnicalService (PTechnicalService pTechnicalService, String sQueueId) {
        MachineSize machineSize = null;
        String serviceName = null;
        String username = null;
        List<PTechnicalParam> pTechnicalParamList;

        System.out.println ("\nin processDatabaseDBaaSTechnicalService");
        pTechnicalParamList = pTechnicalService.getPTechnicalParamList();
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("ServiceName")) {
                serviceName = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("username")) {
                username = pTechnicalParamList.get(i).getProvisionValue();
            }
        }
        this.ps.createDatabaseDBaaS(machineSize, sQueueId, serviceName, username, 1);
    }
        
    public void processCloneDBaaSTechnicalService (PTechnicalService pTechnicalService, String sQueueId) {
        MachineSize machineSize = null;
        String serviceName = null;
        String username = null;
        List<PTechnicalParam> pTechnicalParamList;

        System.out.println ("\nin processCloneDBaaSTechnicalService");
        pTechnicalParamList = pTechnicalService.getPTechnicalParamList();
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("ServiceName")) {
                serviceName = pTechnicalParamList.get(i).getProvisionValue();
            }
            if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("username")) {
                username = pTechnicalParamList.get(i).getProvisionValue();
            }
        }
        this.ps.createCloneDBaaS(machineSize, sQueueId, serviceName, username, 1);
    }
        
    public void processJCSTechnicalService (String sQueueId) {
        MachineSize machineSize = null;
        int imageId = 0;

        System.out.println ("\nin processJCSTechnicalService");
        imageId = this.ps.createStorageContainer(sQueueId);
        this.ps.createJCS(machineSize, sQueueId, imageId);
    }
        
    public void processOVMTechnicalService (List<PTechnicalService> pTechnicalServiceList, String sQueueId, String imageName, String startDate, String endDate) {
        MachineSize machineSizeWLS = null;
        MachineSize machineSizeSOA = null;
        MachineSize machineSizeDBaaS = null;
        String provisionCode;
        List<PTechnicalDetail> pTechnicalDetailList;
        List<PTechnicalDetailValue> pTechnicalDetailValueList;
        int statusCode = 200;
        
        for ( int idx = 0; pTechnicalServiceList != null && idx < pTechnicalServiceList.size(); idx++ ) {
            if (pTechnicalServiceList.get(idx).getProvisionCode().equals("DBaaS") || pTechnicalServiceList.get(idx).getProvisionCode().equals("WLaaS") || pTechnicalServiceList.get(idx).getProvisionCode().equals("SOAaaS")) {
                provisionCode = pTechnicalServiceList.get(idx).getProvisionCode();
                pTechnicalDetailList = pTechnicalServiceList.get(idx).getPTechnicalDetailList();
                for ( int i = 0 ; pTechnicalDetailList != null && i < pTechnicalDetailList.size(); i++ ) {
                    pTechnicalDetailValueList = pTechnicalDetailList.get(i).getPTechnicalDetailValueList();
                    for ( int j = 0 ; pTechnicalDetailValueList != null && j < pTechnicalDetailValueList.size(); j++ ) { 
                        if (pTechnicalDetailValueList.get(j).getProvisionType() != null && pTechnicalDetailValueList.get(j).getProvisionType().equals("TableBlockSize") && pTechnicalDetailValueList.get(j).getProvisionCode().equals("AlphaOfficeBase")) {
                            ObjectMapper objectMapper = new ObjectMapper(); 
                            try {
                                if (provisionCode.equals("DBaaS")) {
                                    System.out.println ("DBaaS MachineSizeName JSON = " + pTechnicalDetailValueList.get(j).getProvisionValue());
                                    machineSizeDBaaS = objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), MachineSize.class);                                    
                                    System.out.println ("DBaaS MachineSizeName = " + machineSizeDBaaS.toString());
                                    machineSizeDBaaS.setMemory((((machineSizeDBaaS.getMemory() > 4) ? 4 : machineSizeDBaaS.getMemory()) + machineSizeDBaaS.getMemoryAdder())*1024);
                                    machineSizeDBaaS.setCores(machineSizeDBaaS.getCores() + machineSizeDBaaS.getCoresAdder());
                                    System.out.println ("DBaaS MachineSizeName = " + machineSizeDBaaS.toString());
                                    machineSizeDBaaS.setCores(1);
                                } else if (provisionCode.equals("WLaaS")) {
                                    System.out.println ("WLaaS MachineSizeName JSON = " + pTechnicalDetailValueList.get(j).getProvisionValue());
                                    machineSizeWLS = objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), MachineSize.class); 
                                    System.out.println ("WLS MachineSizeName = " + machineSizeWLS.toString());
                                    machineSizeWLS.setMemory((((machineSizeWLS.getMemory() > 4) ? 4 : machineSizeWLS.getMemory()) + machineSizeWLS.getMemoryAdder())*1024);
                                    machineSizeWLS.setCores(machineSizeWLS.getCores() + machineSizeWLS.getCoresAdder());
                                    System.out.println ("WLS MachineSizeName = " + machineSizeWLS.toString());
                                    machineSizeWLS.setCores(1);
                                } else if (provisionCode.equals("SOAaaS")) {
                                    machineSizeSOA = objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), MachineSize.class);                                    
                                    System.out.println ("SOA MachineSizeName = " + machineSizeSOA.toString());
                                    machineSizeSOA.setMemory((((machineSizeSOA.getMemory() > 4) ? 4 : machineSizeSOA.getMemory()) + machineSizeSOA.getMemoryAdder())*1024);
                                    machineSizeSOA.setCores(machineSizeSOA.getCores() + machineSizeSOA.getCoresAdder());
                                    System.out.println ("SOA MachineSizeName = " + machineSizeSOA.toString());
                                    machineSizeSOA.setCores(1);
                                }
                            } catch (JsonParseException e) {
                                System.out.println("JsonParseException: " + e.toString());
                            } catch (JsonMappingException e) {
                                System.out.println("JsonMappingException: " + e.toString());
                            } catch (IOException e) {
                                System.out.println("IOException: " + e.toString());
                            }
                        }
                    }        
                }
            }            
        }
        if (imageName.equals("AlphaBase")) {
            statusCode = this.ps.createAlphaBaseAssembly(machineSizeWLS, machineSizeDBaaS, sQueueId, startDate, endDate);                        
            if (statusCode == 500) {
                System.out.println ("\nCreateAlphaBaseAssembly: HTTP 500 error (RETRY)\n");
                statusCode = this.ps.createAlphaBaseAssembly(machineSizeWLS, machineSizeDBaaS, sQueueId, startDate, endDate);                        
            }
        } else if (imageName.equals("AlphaSOA")) {
            this.ps.createAlphaSOAAssembly(machineSizeSOA, machineSizeWLS, machineSizeDBaaS, sQueueId);                        
        } else if (imageName.equals("AlphaWLS")) {
            this.ps.createAlphaWLSAssembly(machineSizeDBaaS, sQueueId);                        
        }
    }
    
    public void processExaDBaaSTechnicalService (List<PTechnicalService> pTechnicalServiceList, String sQueueId, String provisionCode, String username) {
        MachineSize machineSize = null;
        List<PTechnicalDetail> pTechnicalDetailList;
        List<PTechnicalDetailValue> pTechnicalDetailValueList;
        List<PTechnicalParam> pTechnicalParamList;
        String teamName = null;
        String teamNumStr = null;
        int numInstances = 2;
        int hostNumber = 301;
        
        System.out.println ("\n processExaDBaaSTechnicalService");
        teamNumStr = username.substring(4);
        teamName = String.format ("team%s", teamNumStr);
        for ( int idx = 0; pTechnicalServiceList != null && idx < pTechnicalServiceList.size(); idx++ ) {
            if (pTechnicalServiceList.get(idx).getProvisionCode().equals("DBaaS")) {
                pTechnicalParamList = pTechnicalServiceList.get(idx).getPTechnicalParamList();
                for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
                    if (pTechnicalParamList.get(i) != null && pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionCode().equals("HA")) {
                        numInstances = Integer.parseInt(pTechnicalParamList.get(i).getProvisionValue());
                    }
                }
                System.out.println ("\n processExaDBaaSTechnicalService - TeamName = " + teamName);
                pTechnicalDetailList = pTechnicalServiceList.get(idx).getPTechnicalDetailList();
                for ( int i = 0 ; pTechnicalDetailList != null && i < pTechnicalDetailList.size(); i++ ) {
                        pTechnicalDetailValueList = pTechnicalDetailList.get(i).getPTechnicalDetailValueList();
                    for ( int j = 0 ; pTechnicalDetailValueList != null && j < pTechnicalDetailValueList.size(); j++ ) { 
                        if (pTechnicalDetailValueList.get(j).getProvisionType() != null && pTechnicalDetailValueList.get(j).getProvisionType().equals("TableBlockSize") && pTechnicalDetailValueList.get(j).getProvisionCode().equals("AlphaOfficeBase")) {
                            ObjectMapper objectMapper = new ObjectMapper(); 
                            try {
                                machineSize = objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), MachineSize.class);
                                System.out.println ("DBaaS MachineSizeName = " + machineSize.toString());
                                Random generator = new Random ();
                                hostNumber = generator.nextInt (8) + 301;
                                this.ps.createExaCloneDriver (machineSize, teamNumStr, numInstances, sQueueId, provisionCode, hostNumber, this.ps.getConfigProperties().getProperty(teamName), this.ps.getConfigProperties().getProperty("scriptsLocation"));
                            } catch (JsonParseException e) {
                                System.out.println("JsonParseException: " + e.toString());
                            } catch (JsonMappingException e) {
                                System.out.println("JsonMappingException: " + e.toString());
                            } catch (IOException e) {
                                System.out.println("IOException: " + e.toString());
                            }
                        }
                    }
                }
            }        
        }
    }
            
    public void processBusinessService (String sQueueId, String imageName) {
        String startDate = null;
        String endDate = null;
        SimpleDateFormat fromFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        System.out.println ("\nProcessBusinessService - QueueId: " + sQueueId);
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService = null;
        List<PTechnicalService> pTechnicalServiceList = null;
        List<PBusinessParam> pBusinessParamList = null;
        int numClones = 1;
        
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        System.out.println ("\npBusinessService = " + pBusinessService);
        try {
            startDate = getBusinessServiceParam (pBusinessService, "StartDate");
            if (startDate != null) {
                startDate = toFormat.format(fromFormat.parse (startDate));                
            }
            endDate = getBusinessServiceParam (pBusinessService, "EndDate");
            if (endDate != null) {
                endDate = toFormat.format(fromFormat.parse (endDate));                
            }
        } catch (ParseException e) {
        }
        System.out.println ("StartDate = " + startDate + " EndDate = " + endDate);
        System.out.println ("\nProvisionCode = " + pBusinessService.getProvisionCode());
        if (pBusinessService.getProvisionCode().equals("OrderEntryTest")) {
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processOVMTechnicalService (pTechnicalServiceList, sQueueId, imageName, startDate, endDate);
        } else if (pBusinessService.getProvisionCode().equals("AlphaDBaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("NumberOfClones")) {
                    numClones = Integer.parseInt(pBusinessParamList.get(i).getProvisionValue());
                    i = pBusinessParamList.size();
                }
            }
            processDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId, "AlphaDBaaS", numClones);
        } else if (pBusinessService.getProvisionCode().equals("AlphaDBaaSHA")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId, "AlphaDBaaSHA", 1);
        } else if (pBusinessService.getProvisionCode().equals("AlphaDBaaSExa")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processDBaaSExaTechnicalService (pTechnicalServiceList.get(1), sQueueId, pBusinessService.getUserName().substring(4));
        } else if (pBusinessService.getProvisionCode().equals("AlphaMWaaS")) {
            processMWaaSTechnicalService (pBusinessService, sQueueId);            
        } else if (pBusinessService.getProvisionCode().equals("AlphaSchemaDBaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processSchemaDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId);            
        } else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processPluggableDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId);            
        } else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaSExa")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processPluggableDBaaSExaTechnicalService (pTechnicalServiceList.get(0), sQueueId);            
        } else if (pBusinessService.getProvisionCode().equals("AlphaDatabaseDBaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processDatabaseDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId);            
        } else if (pBusinessService.getProvisionCode().equals("AlphaCloneDBaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processCloneDBaaSTechnicalService (pTechnicalServiceList.get(0), sQueueId);            
        } else if (pBusinessService.getProvisionCode().contains("ExaDBaaSRMAN")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            processExaDBaaSTechnicalService (pTechnicalServiceList, sQueueId, pBusinessService.getProvisionCode(), pBusinessService.getUserName());            
        } else if (pBusinessService.getProvisionCode().equals("OraclePublicCloudJCS")) {
            processJCSTechnicalService (sQueueId);            
        }
    }

    public void deprovisionBusinessService (String sBusinessServiceId, String sQueueId, String imageName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        List<PBusinessParam> pBusinessParamList = null;
        String dbSelection = null;
        String mwaasSelection = null;
        String instanceName = null;
        String containerName = null;
        
        System.out.println ("\nsBusinessServiceId = " + sBusinessServiceId);
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByBusinessServiceId(sBusinessServiceId);
        
        if (pBusinessService != null) {
            System.out.println ("\npBusinessService is not NULL");
        } else {
            System.out.println ("\npBusinessService is NULL");            
        }
        if (pBusinessService.getProvisionCode().equals("OrderEntryTest")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("InstanceURI")) {
                    ps.deleteServiceInstance(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaDBaaSExa")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("RequestURI")) {
                    ps.deleteDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal(), pBusinessService.getUserName().substring(4));
                }
            }            
        } else if (pBusinessService.getProvisionCode().contains("AlphaDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("RequestURI")) {
                    ps.deleteDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal(), null);
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaMWaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("RequiresCorporateNetwork")) {
                    mwaasSelection = pBusinessParamList.get(i).getProvisionValue();
                }
            }
            if (mwaasSelection.equals("Yes")) {
                pBusinessValuesList = pBusinessService.getPBusinessValuesList();
                for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                    if (pBusinessValuesList.get(i).getName().equals("InstanceURI")) {
                        ps.shutdownDS (sQueueId);
                        ps.deleteMWaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                        i = pBusinessValuesList.size();
                    }
                }            
                pBusinessParamList = pBusinessService.getPBusinessParamList();
                for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                    if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("DBaaS")) {
                        dbSelection = pBusinessParamList.get(i).getProvisionValue();
                        i = pBusinessParamList.size();
                    }
                }
                if (dbSelection.equals ("New Schema/DB")) {
                    try {
                        Thread.sleep(1000*60*2);
                    } catch (InterruptedException e) {
                    }
                    for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                        if (pBusinessValuesList.get(i).getName().equals("SchemaURI")) {
                            ps.deleteSchemaDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                            i = pBusinessValuesList.size();
                        }
                    }            
                }
            } else {
                pBusinessValuesList = pBusinessService.getPBusinessValuesList();
                for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                    if (pBusinessValuesList.get(i).getName().equals("InstanceName")) {
                        instanceName = pBusinessValuesList.get(i).getVal();
                    }
                    if (pBusinessValuesList.get(i).getName().equals("ContainerName")) {
                        containerName = pBusinessValuesList.get(i).getVal();
                    }
                }            
                ps.deleteJCS(sBusinessServiceId, instanceName);
                ps.deleteStorageContainer(sQueueId, containerName);
            }
        } else if (pBusinessService.getProvisionCode().equals("AlphaSchemaDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("SchemaURI")) {
                    ps.deleteSchemaDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                    i = pBusinessValuesList.size();
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("PluggableURI")) {
                    ps.deletePluggableDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal(), null);
                    i = pBusinessValuesList.size();
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaSExa")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("PluggableURI")) {
                    ps.deletePluggableDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal(), pBusinessService.getUserName().substring(4));
                    i = pBusinessValuesList.size();
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaDatabaseDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("DatabaseURI")) {
                    ps.deleteDatabaseDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                    i = pBusinessValuesList.size();
                }
            }            
        } else if (pBusinessService.getProvisionCode().equals("AlphaCloneDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("DatabaseURI")) {
                    ps.deleteCloneDBaaS(sBusinessServiceId, pBusinessValuesList.get(i).getVal());
                    i = pBusinessValuesList.size();
                }
            }    
        } else if (pBusinessService.getProvisionCode().contains("ExaDBaaSRMAN")) {
            ps.deleteExaRMANClone (sQueueId, pBusinessService.getProvisionCode());
        } else if (pBusinessService.getProvisionCode().equals("OraclePublicCloudJCS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("InstanceName")) {
                    instanceName = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("ContainerName")) {
                    containerName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.deleteJCS(sBusinessServiceId, instanceName);
            ps.deleteStorageContainer(sQueueId, containerName);
        }
    }

    public void reprovisionBusinessService (String sQueueId) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        List<PTechnicalService> pTechnicalServiceList = null;
        List<PTechnicalDetail> pTechnicalDetailList;
        List<PTechnicalDetailValue> pTechnicalDetailValueList;
        String instanceURI = null;
        Availability haObj = null;
        int numServers = 1;
        
        System.out.println ("\nReprovision - sQueueId = " + sQueueId);
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        
        if (pBusinessService.getProvisionCode().equals("AlphaMWaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("InstanceURI")) {
                    instanceURI = pBusinessValuesList.get(i).getVal();
                    i = pBusinessValuesList.size();
                }
            }
            pTechnicalServiceList = pBusinessService.getPTechnicalServiceList();
            pTechnicalDetailList = pTechnicalServiceList.get(0).getPTechnicalDetailList();
            for ( int i = 0 ; pTechnicalDetailList != null && i < pTechnicalDetailList.size(); i++ ) {
                pTechnicalDetailValueList = pTechnicalDetailList.get(i).getPTechnicalDetailValueList();
                for ( int j = 0 ; pTechnicalDetailValueList != null && j < pTechnicalDetailValueList.size(); j++ ) { 
                    if (pTechnicalDetailValueList.get(j).getProvisionType() != null && pTechnicalDetailValueList.get(j).getProvisionType().equals("TableHALevel") && pTechnicalDetailValueList.get(j).getProvisionCode().equals("HA")) {
                        haObj = new Availability ();
                        haObj.setProvisionValue(pTechnicalDetailValueList.get(j).getProvisionValue().substring(19, 20));
                        System.out.println ("\nProvisionValue = " + haObj.getProvisionValue());
                        /*
                        ObjectMapper objectMapper = new ObjectMapper(); 
                        try {
                            haObj = objectMapper.readValue (pTechnicalDetailValueList.get(j).getProvisionValue(), Availability.class);
                        } catch (JsonParseException e) {
                            System.out.println("JsonParseException: " + e.toString());
                        } catch (JsonMappingException e) {
                            System.out.println("JsonMappingException: " + e.toString());
                        } catch (IOException e) {
                            System.out.println("IOException: " + e.toString());
                        }
                        */
                    }
                }        
            }
            numServers = Integer.parseInt(haObj.getProvisionValue());
            ps.scaleMWaaS (sQueueId, instanceURI, numServers);
        }
    }

    public void updateFailedStatus (String sQueueId) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        String requestStatus = null;
        String businessServiceName;
        
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        businessServiceName = pBusinessService.getName();

        ps.updateProvisioninStatus (sQueueId, STATUS_PROVIONING_FAILED, "MWaaS Environment creation failed");
    }

    public void updateRequestStatus (String sQueueId, String imageName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessParam> pBusinessParamList = null;
        String mwaasSelection = "Yes";
        String requestStatus = null;
        String businessServiceName;
        
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        businessServiceName = pBusinessService.getName();

        if (pBusinessService.getProvisionCode().equals("OrderEntryTest")) {
            requestStatus = ps.updateRequestStatus(sQueueId, businessServiceName);
            if (requestStatus.equals("SUCCESS")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        } else if (pBusinessService.getProvisionCode().contains("AlphaDBaaS")) {
            requestStatus = ps.updateDBaaSRequestStatus(sQueueId, pBusinessService.getProvisionCode(), businessServiceName);
            if (requestStatus.equals("SUCCESS")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaMWaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("RequiresCorporateNetwork")) {
                    mwaasSelection = pBusinessParamList.get(i).getProvisionValue();
                }
            }
            if (mwaasSelection.equals("Yes")) {
                requestStatus = ps.updateMWaaSRequestStatus(sQueueId, businessServiceName);
                if (requestStatus.equals("RUNNING")) {
                    updateBusinessServiceParams (sQueueId, imageName);                
                }
            } else {
                requestStatus = ps.updateJCSRequestStatus(sQueueId, businessServiceName);
                if (requestStatus.equals("Running")) {
                    updateBusinessServiceParams (sQueueId, imageName);                                
                }
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaSchemaDBaaS")) {
            requestStatus = ps.updateSchemaDBaaSRequestStatus(sQueueId, businessServiceName);
            if (requestStatus.equals("RUNNING")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaS")) {
            requestStatus = ps.updatePluggableDBaaSRequestStatus(sQueueId, businessServiceName, null);
            if (requestStatus.equals("RUNNING")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaSExa")) {
            requestStatus = ps.updatePluggableDBaaSRequestStatus(sQueueId, businessServiceName, pBusinessService.getUserName().substring(4));
            if (requestStatus.equals("RUNNING")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaDatabaseDBaaS")) {
            requestStatus = ps.updateDatabaseDBaaSRequestStatus(sQueueId, businessServiceName);
            if (requestStatus.equals("RUNNING")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaCloneDBaaS")) {
            requestStatus = ps.updateCloneDBaaSRequestStatus(sQueueId, businessServiceName);
            if (requestStatus.equals("RUNNING")) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        } else if (pBusinessService.getProvisionCode().contains("ExaDBaaSRMAN")) {
            requestStatus = ps.updateExaDBaaSJobStatus(sQueueId);
            if ((requestStatus != null) && requestStatus.contains(STATUS_PROVIONING_COMPLETED)) {
                updateBusinessServiceParams (sQueueId, imageName);                
            }
        }  else if (pBusinessService.getProvisionCode().equals("OraclePublicCloudJCS")) {
            requestStatus = ps.updateJCSRequestStatus(sQueueId, businessServiceName);
            if (requestStatus.equals("Running")) {
                updateBusinessServiceParams (sQueueId, imageName);                                
            }
        }
    }


    public void updateBusinessServiceParams (String sQueueId, String imageName) {
        JavaServiceFacade businessServiceCatalogFacade;
        PBusinessService pBusinessService;
        List<PBusinessValues> pBusinessValuesList;
        List<PBusinessParam> pBusinessParamList = null;
        PBusinessValues pBusinessValues;
        UUID pBusinessValueID;
        String mwaasSelection = "Yes";
        String dbSelection = null;
        String schemaURI = null;
        String schemaName = null;

        System.out.println ("\nupdateBusinessServiceParams - sQueueId: " + sQueueId);
        businessServiceCatalogFacade = new JavaServiceFacade();    
        pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindByQueueId(sQueueId);
        if (pBusinessService.getProvisionCode().equals("OrderEntryTest")) {
            ps.updateAssemblyParams(sQueueId, imageName);
        } else if (pBusinessService.getProvisionCode().equals("AlphaDBaaSExa")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("RequestURI")) {
                    ps.updateDBaaSParams(sQueueId, pBusinessValuesList.get(i).getVal(), pBusinessService.getUserName().substring(4));
                }
            }            
        } else if (pBusinessService.getProvisionCode().contains("AlphaDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("RequestURI")) {
                    ps.updateDBaaSParams(sQueueId, pBusinessValuesList.get(i).getVal(), null);
                }
            }            
        }  else if (pBusinessService.getProvisionCode().equals("AlphaMWaaS")) {
            pBusinessParamList = pBusinessService.getPBusinessParamList();
            for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("DBaaS")) {
                    dbSelection = pBusinessParamList.get(i).getProvisionValue();
                }
                if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("RequiresCorporateNetwork")) {
                    mwaasSelection = pBusinessParamList.get(i).getProvisionValue();
                }
            }
            if (mwaasSelection.equals("Yes")) {
                pBusinessValuesList = pBusinessService.getPBusinessValuesList();
                for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                    if (pBusinessValuesList.get(i).getName().equals("InstanceURI")) {
                        ps.updateMWaaSParams(sQueueId, pBusinessValuesList.get(i).getVal());
                        i = pBusinessValuesList.size();
                    }
                }       
                pBusinessParamList = pBusinessService.getPBusinessParamList();
                for ( int i =0; pBusinessParamList != null && i < pBusinessParamList.size(); i++) {
                    if (pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionCode().equals("DBaaS")) {
                        dbSelection = pBusinessParamList.get(i).getProvisionValue();
                        i = pBusinessParamList.size();
                    }
                }
                if (dbSelection.equals ("New Schema/DB")) {
                    ps.editMWaaSDataSource (sQueueId);
                    ps.assignDSTarget (sQueueId);
                    for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                        if (pBusinessValuesList.get(i).getName().equals("SchemaURI")) {
                            schemaURI = pBusinessValuesList.get(i).getVal();
                        }
                        if (pBusinessValuesList.get(i).getName().equals("SchemaName")) {
                            schemaName = pBusinessValuesList.get(i).getVal();
                        }
                    }            
                    ps.updateSchemaDBaaSParams(sQueueId, schemaURI, schemaName);
                } else {
                    pBusinessValueID = UUID.randomUUID();
                    pBusinessValues = new PBusinessValues (pBusinessValueID.toString(), "ConnectString", pBusinessService, "(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" + ps.getConfigProperties().getProperty("testSchemaHostname") + ")(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=" + ps.getConfigProperties().getProperty("testSchemaSID") + ")))");
                    businessServiceCatalogFacade.persistPBusinessValues (pBusinessValues);
                    pBusinessValueID = UUID.randomUUID();
                    pBusinessValues = new PBusinessValues (pBusinessValueID.toString(), "username", pBusinessService, "alpha");
                    businessServiceCatalogFacade.persistPBusinessValues (pBusinessValues);
                }
            } else {
                pBusinessValuesList = pBusinessService.getPBusinessValuesList();
                ps.updateJCSParams(sQueueId);
            }
        }  else if (pBusinessService.getProvisionCode().equals("AlphaSchemaDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("SchemaURI")) {
                    schemaURI = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("SchemaName")) {
                    schemaName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.updateSchemaDBaaSParams(sQueueId, schemaURI, schemaName);
        }  else if (pBusinessService.getProvisionCode().equals("AlphaPluggableDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("PluggableURI")) {
                    schemaURI = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("PDBName")) {
                    schemaName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.updatePluggableDBaaSParams(sQueueId, schemaURI, schemaName, null);
        }  else if (pBusinessService.getProvisionCode().contains("AlphaPluggableDBaaSExa")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("PluggableURI")) {
                    schemaURI = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("PDBName")) {
                    schemaName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.updatePluggableDBaaSParams(sQueueId, schemaURI, schemaName, pBusinessService.getUserName().substring(4));
        }  else if (pBusinessService.getProvisionCode().equals("AlphaDatabaseDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("DatabaseURI")) {
                    schemaURI = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("databaseName")) {
                    schemaName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.updateDatabaseDBaaSParams(sQueueId, schemaURI, schemaName);
        }  else if (pBusinessService.getProvisionCode().equals("AlphaCloneDBaaS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            for ( int i = 0; pBusinessValuesList != null && i < pBusinessValuesList.size(); i++ ) {
                if (pBusinessValuesList.get(i).getName().equals("DatabaseURI")) {
                    schemaURI = pBusinessValuesList.get(i).getVal();
                }
                if (pBusinessValuesList.get(i).getName().equals("databaseName")) {
                    schemaName = pBusinessValuesList.get(i).getVal();
                }
            }            
            ps.updateCloneDBaaSParams(sQueueId, schemaURI, schemaName);
        } else if (pBusinessService.getProvisionCode().contains("ExaDBaaSRMAN")) {
            ps.updateExaEBSParams(sQueueId, pBusinessService.getProvisionCode());
        }  else if (pBusinessService.getProvisionCode().equals("OraclePublicCloudJCS")) {
            pBusinessValuesList = pBusinessService.getPBusinessValuesList();
            ps.updateJCSParams(sQueueId);
        }
    }

    public void onMessage(Message message) {
        EMIaaSMessage emMsg = null;
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) message;
                emMsg = (EMIaaSMessage) msg.getObject();
            } else if (message instanceof MapMessage) {
                MapMessage msg = (MapMessage) message;
                String payload = (String)msg.getObject("payload");
                emMsg = createMessageFromXML (payload);
            }
            else {
                System.out.println("NOT Instanceof ObjectMessage");                
            }
            EMProvisioningService ps = new EMProvisioningService ();
            try {
                if (emMsg.getMethod().equals("CreateImage")) {
                    processBusinessService (emMsg.getSQueueId(), emMsg.getImageName());
                } else if (emMsg.getMethod().equals("UpdateRequestStatus")) {
                    updateRequestStatus(emMsg.getSQueueId(), emMsg.getImageName());
                } else if (emMsg.getMethod().equals("PopulateParams")) {
                    updateBusinessServiceParams (emMsg.getSQueueId(), emMsg.getImageName());
                } else if (emMsg.getMethod().equals("DeleteImage")) {
                    deprovisionBusinessService (emMsg.getSBusinessServiceId(), emMsg.getSQueueId(), emMsg.getImageName());
                } else if (emMsg.getMethod().equals("Reprovision")) {
                    reprovisionBusinessService (emMsg.getSQueueId());
                }
            } catch (Exception e) {
                System.out.println("Provisioning Excpetion: " + e.toString());
                updateFailedStatus(emMsg.getSQueueId());
            }
        } catch (JMSException e) {
            System.out.println("Could not create Connection: " + e.toString());
        }
    }
}
