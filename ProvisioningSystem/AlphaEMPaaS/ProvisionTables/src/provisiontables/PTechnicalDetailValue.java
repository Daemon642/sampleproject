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
  @NamedQuery(name = "PTechnicalDetailValue.findAll", query = "select o from PTechnicalDetailValue o")
})
@Table(name = "P_TECHNICAL_DETAIL_VALUE")
public class PTechnicalDetailValue implements Serializable {
    private Double cost;
    @Id
    @Column(nullable = false, length = 50)
    private String id;
    @Column(name="LIST_ORDER")
    private Long listOrder;
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
    @Column(length = 400)
    private String txt;
    @ManyToOne
    @JoinColumn(name = "P_TECHNICAL_DETAIL_ID")
    private PTechnicalDetail PTechnicalDetail;
    @OneToMany(mappedBy = "PTechnicalDetailValue")
    private List<PDetailValueAdder> PDetailValueAdderList;

    public PTechnicalDetailValue() {
    }

    public PTechnicalDetailValue(Double cost, String id, Long listOrder,
                                 PTechnicalDetail PTechnicalDetail,
                                 String provisionCode, String provisionType,
                                 String provisionValue,
                                 String provisioningStatus,
                                 String provisioningStatusMsg,
                                 Timestamp statusChangeDate, String txt) {
        this.cost = cost;
        this.id = id;
        this.listOrder = listOrder;
        this.PTechnicalDetail = PTechnicalDetail;
        this.provisionCode = provisionCode;
        this.provisionType = provisionType;
        this.provisionValue = provisionValue;
        this.provisioningStatus = provisioningStatus;
        this.provisioningStatusMsg = provisioningStatusMsg;
        this.statusChangeDate = statusChangeDate;
        this.txt = txt;
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

    public Long getListOrder() {
        return listOrder;
    }

    public void setListOrder(Long listOrder) {
        this.listOrder = listOrder;
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

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public PTechnicalDetail getPTechnicalDetail() {
        return PTechnicalDetail;
    }

    public void setPTechnicalDetail(PTechnicalDetail PTechnicalDetail) {
        this.PTechnicalDetail = PTechnicalDetail;
    }

    public List<PDetailValueAdder> getPDetailValueAdderList() {
        return PDetailValueAdderList;
    }

    public void setPDetailValueAdderList(List<PDetailValueAdder> PDetailValueAdderList) {
        this.PDetailValueAdderList = PDetailValueAdderList;
    }

    public PDetailValueAdder addPDetailValueAdder(PDetailValueAdder PDetailValueAdder) {
        getPDetailValueAdderList().add(PDetailValueAdder);
        PDetailValueAdder.setPTechnicalDetailValue(this);
        return PDetailValueAdder;
    }

    public PDetailValueAdder removePDetailValueAdder(PDetailValueAdder PDetailValueAdder) {
        getPDetailValueAdderList().remove(PDetailValueAdder);
        PDetailValueAdder.setPTechnicalDetailValue(null);
        return PDetailValueAdder;
    }
}
