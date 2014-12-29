package oracle.alpha;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "PaymentOptions.findAll", query = "select o from PaymentOptions o") })
@Table(name = "PAYMENT_OPTIONS")
public class PaymentOptions implements Serializable {
    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private BigDecimal accountNumber;
    @Column(name = "BILLING_ADDRESS_ID")
    private BigDecimal billingAddressId;
    @Column(name = "CARD_TYPE_CODE", length = 30)
    private String cardTypeCode;
    @Column(name = "CHECK_DIGITS")
    private Integer checkDigits;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRE_DATE")
    private Date expireDate;
    @Column(name = "INSTITUTION_NAME", length = 120)
    private String institutionName;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Id
    @Column(name = "PAYMENT_OPTION_ID", nullable = false)
    private Long paymentOptionId;
    @Column(name = "PAYMENT_TYPE_CODE", nullable = false, length = 30)
    private String paymentTypeCode;
    @Column(name = "ROUTING_IDENTIFIER", length = 15)
    private String routingIdentifier;
    @Temporal(TemporalType.DATE)
    @Column(name = "VALID_FROM_DATE")
    private Date validFromDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "VALID_TO_DATE")
    private Date validToDate;
    @OneToMany(mappedBy = "paymentOptions", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Orders> ordersList;
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customers customers3;

    public PaymentOptions() {
    }

    public PaymentOptions(BigDecimal accountNumber, BigDecimal billingAddressId, String cardTypeCode,
                          Integer checkDigits, String createdBy, Date creationDate, Customers customers3, Date expireDate,
                          String institutionName, Date lastUpdateDate, String lastUpdatedBy, Long objectVersionId,
                          Long paymentOptionId, String paymentTypeCode, String routingIdentifier, Date validFromDate,
                          Date validToDate) {
        this.accountNumber = accountNumber;
        this.billingAddressId = billingAddressId;
        this.cardTypeCode = cardTypeCode;
        this.checkDigits = checkDigits;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.customers3 = customers3;
        this.expireDate = expireDate;
        this.institutionName = institutionName;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.paymentOptionId = paymentOptionId;
        this.paymentTypeCode = paymentTypeCode;
        this.routingIdentifier = routingIdentifier;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
    }


    public BigDecimal getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(BigDecimal accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(BigDecimal billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public String getCardTypeCode() {
        return cardTypeCode;
    }

    public void setCardTypeCode(String cardTypeCode) {
        this.cardTypeCode = cardTypeCode;
    }

    public Integer getCheckDigits() {
        return checkDigits;
    }

    public void setCheckDigits(Integer checkDigits) {
        this.checkDigits = checkDigits;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getObjectVersionId() {
        return objectVersionId;
    }

    public void setObjectVersionId(Long objectVersionId) {
        this.objectVersionId = objectVersionId;
    }

    public Long getPaymentOptionId() {
        return paymentOptionId;
    }

    public void setPaymentOptionId(Long paymentOptionId) {
        this.paymentOptionId = paymentOptionId;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public String getRoutingIdentifier() {
        return routingIdentifier;
    }

    public void setRoutingIdentifier(String routingIdentifier) {
        this.routingIdentifier = routingIdentifier;
    }

    public Date getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Date validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Date getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Date validToDate) {
        this.validToDate = validToDate;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    public Orders addOrders(Orders orders) {
        getOrdersList().add(orders);
        orders.setPaymentOptions(this);
        return orders;
    }

    public Orders removeOrders(Orders orders) {
        getOrdersList().remove(orders);
        orders.setPaymentOptions(null);
        return orders;
    }

    public Customers getCustomers3() {
        return customers3;
    }

    public void setCustomers3(Customers customers3) {
        this.customers3 = customers3;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("accountNumber=");
        buffer.append(getAccountNumber());
        buffer.append(',');
        buffer.append("billingAddressId=");
        buffer.append(getBillingAddressId());
        buffer.append(',');
        buffer.append("cardTypeCode=");
        buffer.append(getCardTypeCode());
        buffer.append(',');
        buffer.append("checkDigits=");
        buffer.append(getCheckDigits());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("expireDate=");
        buffer.append(getExpireDate());
        buffer.append(',');
        buffer.append("institutionName=");
        buffer.append(getInstitutionName());
        buffer.append(',');
        buffer.append("lastUpdateDate=");
        buffer.append(getLastUpdateDate());
        buffer.append(',');
        buffer.append("lastUpdatedBy=");
        buffer.append(getLastUpdatedBy());
        buffer.append(',');
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append("paymentOptionId=");
        buffer.append(getPaymentOptionId());
        buffer.append(',');
        buffer.append("paymentTypeCode=");
        buffer.append(getPaymentTypeCode());
        buffer.append(',');
        buffer.append("routingIdentifier=");
        buffer.append(getRoutingIdentifier());
        buffer.append(',');
        buffer.append("validFromDate=");
        buffer.append(getValidFromDate());
        buffer.append(',');
        buffer.append("validToDate=");
        buffer.append(getValidToDate());
        buffer.append(']');
        return buffer.toString();
    }
}
