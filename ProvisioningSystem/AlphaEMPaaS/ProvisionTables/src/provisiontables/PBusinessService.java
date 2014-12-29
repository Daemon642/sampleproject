package provisiontables;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "PBusinessService.findAll", query = "select o from PBusinessService o"),
  @NamedQuery(name = "PBusinessService.findByQueueId", query = "select o from PBusinessService o WHERE o.sQueueId = :sQueueId"),
  @NamedQuery(name = "PBusinessService.findByBusinessServiceId", query = "select o from PBusinessService o WHERE o.sBusinessServiceId = :sBusinessServiceId")
})
@Table(name = "P_BUSINESS_SERVICE")
public class PBusinessService implements Serializable {
    @Column(name="BUSINESS_SERVICE_ID", length = 50)
    private String businessServiceId;
    @Column(name="CATALOG_USER_ID", length = 50)
    private String catalogUserId;
    @Column(name="CHARGE_SHOW_BACK", length = 20)
    private String chargeShowBack;
    private Double cost;
    @Column(name="COST_TYPE", length = 20)
    private String costType;
    @Column(name="CREATE_DATE")
    private Timestamp createDate;
    @Column(length = 4000)
    private String descr;
    @Column(name="FIRST_NAME", length = 80)
    private String firstName;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(name="LAST_NAME", length = 50)
    private String lastName;
    @Column(length = 40)
    private String name;
    @Column(name="PROVISIONING_STATUS", length = 50)
    private String provisioningStatus;
    @Column(name="PROVISIONING_STATUS_MSG", length = 400)
    private String provisioningStatusMsg;
    @Column(name="PROVISION_CODE", length = 40)
    private String provisionCode;
    @Column(name="PROVISION_TYPE", length = 40)
    private String provisionType;
    @Column(name="PROVISION_VALUE", length = 400)
    private String provisionValue;
    @Column(name="SESSION_ID", length = 50)
    private String sessionId;
    @Column(name="STATUS_CHANGE_DATE")
    private Timestamp statusChangeDate;
    @Column(name="S_BUSINESS_SERVICE_ID", length = 50)
    private String sBusinessServiceId;
    @Column(name="S_QUEUE_ID", length = 50)
    private String sQueueId;
    @Column(name="TOTAL_COST")
    private Double totalCost;
    @Column(name="USER_EMAIL", length = 200)
    private String userEmail;
    @Column(name="USER_NAME", length = 80)
    private String userName;
    @OneToMany(mappedBy = "PBusinessService")
    private List<PTechnicalService> PTechnicalServiceList;
    @OneToMany(mappedBy = "PBusinessService")
    private List<PBusinessParam> PBusinessParamList;
    @OneToMany(mappedBy = "PBusinessService")
    private List<PBusinessDetail> PBusinessDetailList;
    @OneToMany(mappedBy = "PBusinessService")
    private List<PBusinessValues> PBusinessValuesList;

    public PBusinessService() {
    }

    public PBusinessService(String businessServiceId, String catalogUserId,
                            String chargeShowBack, Double cost,
                            String costType, Timestamp createDate,
                            String descr, String firstName, String id,
                            String lastName, String name, String provisionCode,
                            String provisionType, String provisionValue,
                            String provisioningStatus,
                            String provisioningStatusMsg,
                            String sBusinessServiceId, String sQueueId,
                            String sessionId, Timestamp statusChangeDate,
                            Double totalCost, String userEmail,
                            String userName) {
        this.businessServiceId = businessServiceId;
        this.catalogUserId = catalogUserId;
        this.chargeShowBack = chargeShowBack;
        this.cost = cost;
        this.costType = costType;
        this.createDate = createDate;
        this.descr = descr;
        this.firstName = firstName;
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.provisionCode = provisionCode;
        this.provisionType = provisionType;
        this.provisionValue = provisionValue;
        this.provisioningStatus = provisioningStatus;
        this.provisioningStatusMsg = provisioningStatusMsg;
        this.sBusinessServiceId = sBusinessServiceId;
        this.sQueueId = sQueueId;
        this.sessionId = sessionId;
        this.statusChangeDate = statusChangeDate;
        this.totalCost = totalCost;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getBusinessServiceId() {
        return businessServiceId;
    }

    public void setBusinessServiceId(String businessServiceId) {
        this.businessServiceId = businessServiceId;
    }

    public String getCatalogUserId() {
        return catalogUserId;
    }

    public void setCatalogUserId(String catalogUserId) {
        this.catalogUserId = catalogUserId;
    }

    public String getChargeShowBack() {
        return chargeShowBack;
    }

    public void setChargeShowBack(String chargeShowBack) {
        this.chargeShowBack = chargeShowBack;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvisioningStatus() {
        return provisioningStatus;
    }

    public void setProvisioningStatus(String provisioningStatus) {
        this.provisioningStatus = provisioningStatus;
    }

    public String getProvisioningStatusMsg() {
        return provisioningStatusMsg;
    }

    public void setProvisioningStatusMsg(String provisioningStatusMsg) {
        this.provisioningStatusMsg = provisioningStatusMsg;
    }

    public String getProvisionCode() {
        return provisionCode;
    }

    public void setProvisionCode(String provisionCode) {
        this.provisionCode = provisionCode;
    }

    public String getProvisionType() {
        return provisionType;
    }

    public void setProvisionType(String provisionType) {
        this.provisionType = provisionType;
    }

    public String getProvisionValue() {
        return provisionValue;
    }

    public void setProvisionValue(String provisionValue) {
        this.provisionValue = provisionValue;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(Timestamp statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public String getSBusinessServiceId() {
        return sBusinessServiceId;
    }

    public void setSBusinessServiceId(String sBusinessServiceId) {
        this.sBusinessServiceId = sBusinessServiceId;
    }

    public String getSQueueId() {
        return sQueueId;
    }

    public void setSQueueId(String sQueueId) {
        this.sQueueId = sQueueId;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<PTechnicalService> getPTechnicalServiceList() {
        return PTechnicalServiceList;
    }

    public void setPTechnicalServiceList(List<PTechnicalService> PTechnicalServiceList) {
        this.PTechnicalServiceList = PTechnicalServiceList;
    }

    public PTechnicalService addPTechnicalService(PTechnicalService PTechnicalService) {
        getPTechnicalServiceList().add(PTechnicalService);
        PTechnicalService.setPBusinessService(this);
        return PTechnicalService;
    }

    public PTechnicalService removePTechnicalService(PTechnicalService PTechnicalService) {
        getPTechnicalServiceList().remove(PTechnicalService);
        PTechnicalService.setPBusinessService(null);
        return PTechnicalService;
    }

    public List<PBusinessParam> getPBusinessParamList() {
        return PBusinessParamList;
    }

    public void setPBusinessParamList(List<PBusinessParam> PBusinessParamList) {
        this.PBusinessParamList = PBusinessParamList;
    }

    public PBusinessParam addPBusinessParam(PBusinessParam PBusinessParam) {
        getPBusinessParamList().add(PBusinessParam);
        PBusinessParam.setPBusinessService(this);
        return PBusinessParam;
    }

    public PBusinessParam removePBusinessParam(PBusinessParam PBusinessParam) {
        getPBusinessParamList().remove(PBusinessParam);
        PBusinessParam.setPBusinessService(null);
        return PBusinessParam;
    }

    public List<PBusinessDetail> getPBusinessDetailList() {
        return PBusinessDetailList;
    }

    public void setPBusinessDetailList(List<PBusinessDetail> PBusinessDetailList) {
        this.PBusinessDetailList = PBusinessDetailList;
    }

    public PBusinessDetail addPBusinessDetail(PBusinessDetail PBusinessDetail) {
        getPBusinessDetailList().add(PBusinessDetail);
        PBusinessDetail.setPBusinessService(this);
        return PBusinessDetail;
    }

    public PBusinessDetail removePBusinessDetail(PBusinessDetail PBusinessDetail) {
        getPBusinessDetailList().remove(PBusinessDetail);
        PBusinessDetail.setPBusinessService(null);
        return PBusinessDetail;
    }

    public List<PBusinessValues> getPBusinessValuesList() {
        return PBusinessValuesList;
    }

    public void setPBusinessValuesList(List<PBusinessValues> PBusinessValuesList) {
        this.PBusinessValuesList = PBusinessValuesList;
    }

    public PBusinessValues addPBusinessValues(PBusinessValues PBusinessValues) {
        getPBusinessValuesList().add(PBusinessValues);
        PBusinessValues.setPBusinessService(this);
        return PBusinessValues;
    }

    public PBusinessValues removePBusinessValues(PBusinessValues PBusinessValues) {
        getPBusinessValuesList().remove(PBusinessValues);
        PBusinessValues.setPBusinessService(null);
        return PBusinessValues;
    }
}
