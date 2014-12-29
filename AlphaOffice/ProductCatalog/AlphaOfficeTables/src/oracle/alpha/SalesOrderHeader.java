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
@NamedQueries({ @NamedQuery(name = "SalesOrderHeader.findAll", query = "select o from SalesOrderHeader o") })
@Table(name = "SALES_ORDER_HEADER")
public class SalesOrderHeader implements Serializable {
    @Column(name = "CUST_PO_NO", nullable = false, length = 40)
    private String custPoNo;
    @Column(name = "FLOW_STATUS_CODE", length = 40)
    private String flowStatusCode;
    @Column(name = "INV_TO_ORG_ID")
    private BigDecimal invToOrgId;
    @Column(name = "ORDER_BOOKED_FLAG")
    private String orderBookedFlag;
    @Column(name = "ORDER_SOURCE_ID")
    private BigDecimal orderSourceId;
    @Column(name = "ORDER_TYPE_ID")
    private BigDecimal orderTypeId;
    @Column(name = "PRICE_LIST_ID")
    private BigDecimal priceListId;
    @Temporal(TemporalType.DATE)
    @Column(name = "PRICING_DATE")
    private Date pricingDate;
    @Column(name = "SALESREP_ID")
    private BigDecimal salesrepId;
    @Id
    @Column(name = "SALES_ORDER_ID", nullable = false)
    private Long salesOrderId;
    @Column(name = "SHIP_TO_ORG_ID")
    private BigDecimal shipToOrgId;
    @Column(name = "SOLD_FROM_ORG_ID")
    private BigDecimal soldFromOrgId;
    @Column(name = "SOLD_TO_ORG_ID")
    private BigDecimal soldToOrgId;
    @Column(name = "TRANSACTION_CURR_CODE", length = 10)
    private String transactionCurrCode;
    @OneToMany(mappedBy = "salesOrderHeader", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<SalesOrderLineItem> salesOrderLineItemList;
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customers customers4;

    public SalesOrderHeader() {
    }

    public SalesOrderHeader(String custPoNo, Customers customers4, String flowStatusCode, BigDecimal invToOrgId,
                            String orderBookedFlag, BigDecimal orderSourceId, BigDecimal orderTypeId,
                            BigDecimal priceListId, Date pricingDate, Long salesOrderId, BigDecimal salesrepId,
                            BigDecimal shipToOrgId, BigDecimal soldFromOrgId, BigDecimal soldToOrgId,
                            String transactionCurrCode) {
        this.custPoNo = custPoNo;
        this.customers4 = customers4;
        this.flowStatusCode = flowStatusCode;
        this.invToOrgId = invToOrgId;
        this.orderBookedFlag = orderBookedFlag;
        this.orderSourceId = orderSourceId;
        this.orderTypeId = orderTypeId;
        this.priceListId = priceListId;
        this.pricingDate = pricingDate;
        this.salesOrderId = salesOrderId;
        this.salesrepId = salesrepId;
        this.shipToOrgId = shipToOrgId;
        this.soldFromOrgId = soldFromOrgId;
        this.soldToOrgId = soldToOrgId;
        this.transactionCurrCode = transactionCurrCode;
    }


    public String getCustPoNo() {
        return custPoNo;
    }

    public void setCustPoNo(String custPoNo) {
        this.custPoNo = custPoNo;
    }

    public String getFlowStatusCode() {
        return flowStatusCode;
    }

    public void setFlowStatusCode(String flowStatusCode) {
        this.flowStatusCode = flowStatusCode;
    }

    public BigDecimal getInvToOrgId() {
        return invToOrgId;
    }

    public void setInvToOrgId(BigDecimal invToOrgId) {
        this.invToOrgId = invToOrgId;
    }

    public String getOrderBookedFlag() {
        return orderBookedFlag;
    }

    public void setOrderBookedFlag(String orderBookedFlag) {
        this.orderBookedFlag = orderBookedFlag;
    }

    public BigDecimal getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(BigDecimal orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public BigDecimal getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(BigDecimal orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public BigDecimal getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(BigDecimal priceListId) {
        this.priceListId = priceListId;
    }

    public Date getPricingDate() {
        return pricingDate;
    }

    public void setPricingDate(Date pricingDate) {
        this.pricingDate = pricingDate;
    }

    public BigDecimal getSalesrepId() {
        return salesrepId;
    }

    public void setSalesrepId(BigDecimal salesrepId) {
        this.salesrepId = salesrepId;
    }

    public Long getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(Long salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public BigDecimal getShipToOrgId() {
        return shipToOrgId;
    }

    public void setShipToOrgId(BigDecimal shipToOrgId) {
        this.shipToOrgId = shipToOrgId;
    }

    public BigDecimal getSoldFromOrgId() {
        return soldFromOrgId;
    }

    public void setSoldFromOrgId(BigDecimal soldFromOrgId) {
        this.soldFromOrgId = soldFromOrgId;
    }

    public BigDecimal getSoldToOrgId() {
        return soldToOrgId;
    }

    public void setSoldToOrgId(BigDecimal soldToOrgId) {
        this.soldToOrgId = soldToOrgId;
    }

    public String getTransactionCurrCode() {
        return transactionCurrCode;
    }

    public void setTransactionCurrCode(String transactionCurrCode) {
        this.transactionCurrCode = transactionCurrCode;
    }

    public List<SalesOrderLineItem> getSalesOrderLineItemList() {
        return salesOrderLineItemList;
    }

    public void setSalesOrderLineItemList(List<SalesOrderLineItem> salesOrderLineItemList) {
        this.salesOrderLineItemList = salesOrderLineItemList;
    }

    public SalesOrderLineItem addSalesOrderLineItem(SalesOrderLineItem salesOrderLineItem) {
        getSalesOrderLineItemList().add(salesOrderLineItem);
        salesOrderLineItem.setSalesOrderHeader(this);
        return salesOrderLineItem;
    }

    public SalesOrderLineItem removeSalesOrderLineItem(SalesOrderLineItem salesOrderLineItem) {
        getSalesOrderLineItemList().remove(salesOrderLineItem);
        salesOrderLineItem.setSalesOrderHeader(null);
        return salesOrderLineItem;
    }

    public Customers getCustomers4() {
        return customers4;
    }

    public void setCustomers4(Customers customers4) {
        this.customers4 = customers4;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("custPoNo=");
        buffer.append(getCustPoNo());
        buffer.append(',');
        buffer.append("flowStatusCode=");
        buffer.append(getFlowStatusCode());
        buffer.append(',');
        buffer.append("invToOrgId=");
        buffer.append(getInvToOrgId());
        buffer.append(',');
        buffer.append("orderBookedFlag=");
        buffer.append(getOrderBookedFlag());
        buffer.append(',');
        buffer.append("orderSourceId=");
        buffer.append(getOrderSourceId());
        buffer.append(',');
        buffer.append("orderTypeId=");
        buffer.append(getOrderTypeId());
        buffer.append(',');
        buffer.append("priceListId=");
        buffer.append(getPriceListId());
        buffer.append(',');
        buffer.append("pricingDate=");
        buffer.append(getPricingDate());
        buffer.append(',');
        buffer.append("salesOrderId=");
        buffer.append(getSalesOrderId());
        buffer.append(',');
        buffer.append("salesrepId=");
        buffer.append(getSalesrepId());
        buffer.append(',');
        buffer.append("shipToOrgId=");
        buffer.append(getShipToOrgId());
        buffer.append(',');
        buffer.append("soldFromOrgId=");
        buffer.append(getSoldFromOrgId());
        buffer.append(',');
        buffer.append("soldToOrgId=");
        buffer.append(getSoldToOrgId());
        buffer.append(',');
        buffer.append("transactionCurrCode=");
        buffer.append(getTransactionCurrCode());
        buffer.append(']');
        return buffer.toString();
    }
}
