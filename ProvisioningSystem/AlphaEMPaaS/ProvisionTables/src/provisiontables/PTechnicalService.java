package provisiontables;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "PTechnicalService.findAll", query = "select o from PTechnicalService o")
})
@Table(name = "P_TECHNICAL_SERVICE")
public class PTechnicalService implements Serializable {
    @Column(name="CHARGE_SHOW_BACK", length = 20)
    private String chargeShowBack;
    private Double cost;
    @Column(length = 4000)
    private String descr;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(length = 1)
    private String initialshowparams;
    @Column(length = 80)
    private String name;
    @Column(name="PROVISIONING_MSG", length = 400)
    private String provisioningMsg;
    @Column(name="PROVISIONING_STATUS", length = 50)
    private String provisioningStatus;
    @Column(name="PROVISION_CODE", length = 40)
    private String provisionCode;
    @Column(name="PROVISION_TYPE", length = 40)
    private String provisionType;
    @Column(name="PROVISION_VALUE", length = 4000)
    private String provisionValue;
    @Column(name="SERVICE_TYPE", length = 40)
    private String serviceType;
    @Column(name="STATUS_CHANGE_DATE")
    private Timestamp statusChangeDate;
    @Column(name="TOTAL_TECHNICAL_SERVICE_COST")
    private Double totalTechnicalServiceCost;
    @OneToMany(mappedBy = "PTechnicalService")
    private List<PTechnicalDetail> PTechnicalDetailList;
    @OneToMany(mappedBy = "PTechnicalService")
    private List<PTechnicalValues> PTechnicalValuesList;
    @ManyToOne
    @JoinColumn(name = "P_BUSINESS_SERVICE_ID")
    private PBusinessService PBusinessService;
    @OneToMany(mappedBy = "PTechnicalService")
    private List<PTechnicalParam> PTechnicalParamList;

    public PTechnicalService() {
    }

    public PTechnicalService(String chargeShowBack, Double cost, String descr,
                             String id, String initialshowparams, String name,
                             PBusinessService PBusinessService, String provisionCode,
                             String provisionType, String provisionValue,
                             String provisioningMsg, String provisioningStatus,
                             String serviceType, Timestamp statusChangeDate,
                             Double totalTechnicalServiceCost) {
        this.chargeShowBack = chargeShowBack;
        this.cost = cost;
        this.descr = descr;
        this.id = id;
        this.initialshowparams = initialshowparams;
        this.name = name;
        this.PBusinessService = PBusinessService;
        this.provisionCode = provisionCode;
        this.provisionType = provisionType;
        this.provisionValue = provisionValue;
        this.provisioningMsg = provisioningMsg;
        this.provisioningStatus = provisioningStatus;
        this.serviceType = serviceType;
        this.statusChangeDate = statusChangeDate;
        this.totalTechnicalServiceCost = totalTechnicalServiceCost;
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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInitialshowparams() {
        return initialshowparams;
    }

    public void setInitialshowparams(String initialshowparams) {
        this.initialshowparams = initialshowparams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvisioningMsg() {
        return provisioningMsg;
    }

    public void setProvisioningMsg(String provisioningMsg) {
        this.provisioningMsg = provisioningMsg;
    }

    public String getProvisioningStatus() {
        return provisioningStatus;
    }

    public void setProvisioningStatus(String provisioningStatus) {
        this.provisioningStatus = provisioningStatus;
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


    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Timestamp getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(Timestamp statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public Double getTotalTechnicalServiceCost() {
        return totalTechnicalServiceCost;
    }

    public void setTotalTechnicalServiceCost(Double totalTechnicalServiceCost) {
        this.totalTechnicalServiceCost = totalTechnicalServiceCost;
    }

    public List<PTechnicalDetail> getPTechnicalDetailList() {
        return PTechnicalDetailList;
    }

    public void setPTechnicalDetailList(List<PTechnicalDetail> PTechnicalDetailList) {
        this.PTechnicalDetailList = PTechnicalDetailList;
    }

    public PTechnicalDetail addPTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        getPTechnicalDetailList().add(PTechnicalDetail);
        PTechnicalDetail.setPTechnicalService(this);
        return PTechnicalDetail;
    }

    public PTechnicalDetail removePTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        getPTechnicalDetailList().remove(PTechnicalDetail);
        PTechnicalDetail.setPTechnicalService(null);
        return PTechnicalDetail;
    }

    public List<PTechnicalValues> getPTechnicalValuesList() {
        return PTechnicalValuesList;
    }

    public void setPTechnicalValuesList(List<PTechnicalValues> PTechnicalValuesList) {
        this.PTechnicalValuesList = PTechnicalValuesList;
    }

    public PTechnicalValues addPTechnicalValues(PTechnicalValues PTechnicalValues) {
        getPTechnicalValuesList().add(PTechnicalValues);
        PTechnicalValues.setPTechnicalService(this);
        return PTechnicalValues;
    }

    public PTechnicalValues removePTechnicalValues(PTechnicalValues PTechnicalValues) {
        getPTechnicalValuesList().remove(PTechnicalValues);
        PTechnicalValues.setPTechnicalService(null);
        return PTechnicalValues;
    }

    public PBusinessService getPBusinessService() {
        return PBusinessService;
    }

    public void setPBusinessService(PBusinessService PBusinessService) {
        this.PBusinessService = PBusinessService;
    }

    public List<PTechnicalParam> getPTechnicalParamList() {
        return PTechnicalParamList;
    }

    public void setPTechnicalParamList(List<PTechnicalParam> PTechnicalParamList) {
        this.PTechnicalParamList = PTechnicalParamList;
    }

    public PTechnicalParam addPTechnicalParam(PTechnicalParam PTechnicalParam) {
        getPTechnicalParamList().add(PTechnicalParam);
        PTechnicalParam.setPTechnicalService(this);
        return PTechnicalParam;
    }

    public PTechnicalParam removePTechnicalParam(PTechnicalParam PTechnicalParam) {
        getPTechnicalParamList().remove(PTechnicalParam);
        PTechnicalParam.setPTechnicalService(null);
        return PTechnicalParam;
    }
}
