package provisiontables;

import java.io.Serializable;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "PTechnicalParam.findAll", query = "select o from PTechnicalParam o")
})
@Table(name = "P_TECHNICAL_PARAM")
public class PTechnicalParam implements Serializable {
    private Double cost;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(length = 80)
    private String lbl;
    @Column(name="LIST_ORDER")
    private Long listOrder;
    @Column(name="PARAM_TYPE", length = 40)
    private String paramType;
    @Column(name="PARAM_VALUE", length = 200)
    private String paramValue;
    @Column(name="PROVISIONING_STATUS", length = 50)
    private String provisioningStatus;
    @Column(name="PROVISIONING_STATUS_MSG", length = 4000)
    private String provisioningStatusMsg;
    @Column(name="PROVISION_CODE", length = 40)
    private String provisionCode;
    @Column(name="PROVISION_TYPE", length = 40)
    private String provisionType;
    @Column(name="PROVISION_VALUE", length = 4000)
    private String provisionValue;
    @Column(name="STATUS_CHANGE_DATE")
    private Timestamp statusChangeDate;
    @ManyToOne
    @JoinColumn(name = "P_TECHNICAL_SERVICE_ID")
    private PTechnicalService PTechnicalService;

    public PTechnicalParam() {
    }

    public PTechnicalParam(Double cost, String id, String lbl, Long listOrder,
                           PTechnicalService PTechnicalService, String paramType,
                           String paramValue, String provisionCode,
                           String provisionType, String provisionValue,
                           String provisioningStatus,
                           String provisioningStatusMsg,
                           Timestamp statusChangeDate) {
        this.cost = cost;
        this.id = id;
        this.lbl = lbl;
        this.listOrder = listOrder;
        this.PTechnicalService = PTechnicalService;
        this.paramType = paramType;
        this.paramValue = paramValue;
        this.provisionCode = provisionCode;
        this.provisionType = provisionType;
        this.provisionValue = provisionValue;
        this.provisioningStatus = provisioningStatus;
        this.provisioningStatusMsg = provisioningStatusMsg;
        this.statusChangeDate = statusChangeDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }

    public Long getListOrder() {
        return listOrder;
    }

    public void setListOrder(Long listOrder) {
        this.listOrder = listOrder;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
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


    public Timestamp getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(Timestamp statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public PTechnicalService getPTechnicalService() {
        return PTechnicalService;
    }

    public void setPTechnicalService(PTechnicalService PTechnicalService) {
        this.PTechnicalService = PTechnicalService;
    }
}
