package provisiontables;

import java.io.Serializable;

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
  @NamedQuery(name = "PBusinessParam.findAll", query = "select o from PBusinessParam o")
})
@Table(name = "P_BUSINESS_PARAM")
public class PBusinessParam implements Serializable {
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
    @Column(name="PROVISION_CODE", length = 40)
    private String provisionCode;
    @Column(name="PROVISION_TYPE", length = 40)
    private String provisionType;
    @Column(name="PROVISION_VALUE", length = 4000)
    private String provisionValue;
    @ManyToOne
    @JoinColumn(name = "P_BUSINESS_SERVICE_ID")
    private PBusinessService PBusinessService;

    public PBusinessParam() {
    }

    public PBusinessParam(Double cost, String id, String lbl, Long listOrder,
                          PBusinessService PBusinessService, String paramType,
                          String paramValue, String provisionCode,
                          String provisionType, String provisionValue) {
        this.cost = cost;
        this.id = id;
        this.lbl = lbl;
        this.listOrder = listOrder;
        this.PBusinessService = PBusinessService;
        this.paramType = paramType;
        this.paramValue = paramValue;
        this.provisionCode = provisionCode;
        this.provisionType = provisionType;
        this.provisionValue = provisionValue;
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


    public PBusinessService getPBusinessService() {
        return PBusinessService;
    }

    public void setPBusinessService(PBusinessService PBusinessService) {
        this.PBusinessService = PBusinessService;
    }
}
