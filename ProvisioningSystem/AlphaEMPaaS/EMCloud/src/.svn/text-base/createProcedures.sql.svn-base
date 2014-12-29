create or replace 
PROCEDURE update_provisioning_status (
    sQueueId IN p_business_service.s_queue_id%TYPE,
    provStatus IN p_business_service.provisioning_status%TYPE,
    provStatusMsg IN p_business_service.provisioning_status_msg%TYPE) AS
BEGIN
    UPDATE p_business_service
       SET provisioning_status = provStatus,
           provisioning_status_msg = provStatusMsg
     WHERE s_queue_id = sQueueId;
     
    UPDATE p_technical_service
       SET provisioning_status = provStatus,
           provisioning_msg = provStatusMSg
     WHERE provision_type = 'DBaaS'
       AND p_business_service_id = 
            (SELECT id
               FROM p_business_service
              WHERE s_queue_id = sQueueId);
       
END;
    