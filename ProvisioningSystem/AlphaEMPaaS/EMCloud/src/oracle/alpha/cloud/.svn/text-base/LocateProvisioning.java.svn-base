package oracle.alpha.cloud;

import provisiontables.*;



import java.util.List;

public class LocateProvisioning {
    
    // final JavaServiceFacade businessServiceCatalogFacade;

    public LocateProvisioning() {
        super();
    }

    public static void main(String[] args) {
        LocateProvisioning locateProvisioning = new LocateProvisioning();
        
        findPBusinessService();
        
    }
    
    private static void findPBusinessService( ) {
        JavaServiceFacade businessServiceCatalogFacade;
        
        businessServiceCatalogFacade = new JavaServiceFacade();
        
        
        
        List<PBusinessService> pBusinessService =  businessServiceCatalogFacade.getPBusinessServiceFindAll();
        
        for ( int i = 0; pBusinessService != null && i < pBusinessService.size(); i++ ) {
            if ( pBusinessService.get(i).getProvisionCode() != null && pBusinessService.get(i).getProvisionValue() != null ) {
                System.out.println("Business Service Type = ("+pBusinessService.get(i).getProvisionType()+"), Code = ("+pBusinessService.get(i).getProvisionCode()+"), Value=("+pBusinessService.get(i).getProvisionValue()+")"+
                                   ",         id=("+pBusinessService.get(i).getId()+")");
                
                if ( pBusinessService.get(i).getPBusinessParamList() != null ) {
                    findPBusinessParam( pBusinessService.get(i).getPBusinessParamList() );
                }
            }
            
            findPTechnicalService( pBusinessService.get(i).getPTechnicalServiceList());
        }   
    }
    
    private static void findPTechnicalService( List<PTechnicalService> pTechnicalServiceList ) {
        
        for ( int i = 0; pTechnicalServiceList != null && i < pTechnicalServiceList.size(); i++ ) {
            if ( pTechnicalServiceList.get(i).getProvisionCode() != null && pTechnicalServiceList.get(i).getProvisionValue() != null ) {
                System.out.println("    TechnicalService Type = ("+pTechnicalServiceList.get(i).getProvisionType()+"), Code = ("+pTechnicalServiceList.get(i).getProvisionCode()+"), Value=("+pTechnicalServiceList.get(i).getProvisionValue()+")"+
                                   ",         id=("+pTechnicalServiceList.get(i).getId()+")");
                
                findPTechincalParam(pTechnicalServiceList.get(i).getPTechnicalParamList());
                
                findPTechincalDetail(pTechnicalServiceList.get(i).getPTechnicalDetailList());
            }
        }
        
    }
    
    private static void findPTechincalDetail( List<PTechnicalDetail> pTechnicalDetailList ) {
        
        for ( int i = 0 ; pTechnicalDetailList != null && i < pTechnicalDetailList.size(); i++ ) {
            
            
            findPTechincalDetailValue(pTechnicalDetailList.get(i).getPTechnicalDetailValueList());
        }
    }

    private static void findPTechincalDetailValue( List<PTechnicalDetailValue> pTechnicalDetailValueList ) {
        
        for ( int i = 0 ; pTechnicalDetailValueList != null && i < pTechnicalDetailValueList.size(); i++ ) {
            
             
            if ( pTechnicalDetailValueList.get(i).getProvisionCode() != null && pTechnicalDetailValueList.get(i).getProvisionValue() != null ) {
                System.out.println("        Technical DetailValue Type = ("+pTechnicalDetailValueList.get(i).getProvisionType()+"), Code = ("+pTechnicalDetailValueList.get(i).getProvisionCode()+"), Value=("+pTechnicalDetailValueList.get(i).getProvisionValue()+")"+
                                   ",         id=("+pTechnicalDetailValueList.get(i).getId()+")");

            }
        }
    }
    
    
    private static void findPTechincalParam( List<PTechnicalParam> pTechnicalParamList ) {
        
        for ( int i = 0 ; pTechnicalParamList != null && i < pTechnicalParamList.size(); i++ ) {
            
            if ( pTechnicalParamList.get(i).getProvisionCode() != null && pTechnicalParamList.get(i).getProvisionValue() != null ) {
                System.out.println("        Technical Parameter Type = ("+pTechnicalParamList.get(i).getProvisionType()+"), Code = ("+pTechnicalParamList.get(i).getProvisionCode()+"), Value=("+pTechnicalParamList.get(i).getProvisionValue()+")"+
                                   ",         id=("+pTechnicalParamList.get(i).getId()+")");

            }
        }
    }
    


    private static void findPBusinessParam( List<PBusinessParam> pBusinessParamList ) {
        
        for ( int i = 0 ; pBusinessParamList != null && i < pBusinessParamList.size(); i++ ) {
            
            if ( pBusinessParamList.get(i).getProvisionCode() != null && pBusinessParamList.get(i).getProvisionValue() != null ) {
                System.out.println("    Business Parameter Type = ("+pBusinessParamList.get(i).getProvisionType()+"), Code = ("+pBusinessParamList.get(i).getProvisionCode()+"), Value=("+pBusinessParamList.get(i).getProvisionValue()+")"+
                                   ",         id=("+pBusinessParamList.get(i).getId()+")");

            }
        }
    }
}

