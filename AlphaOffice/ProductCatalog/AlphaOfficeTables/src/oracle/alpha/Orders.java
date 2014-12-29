package oracle.alpha;

import java.io.Serializable;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "Orders.findAll", query = "select o from Orders o") })
public class Orders implements Serializable {
    private static final long serialVersionUID = 5546661520463118609L;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "CUSTOMER_COLLECT_FLAG", nullable = false, length = 1)
    private String customerCollectFlag;
    @Column(name = "GIFTWRAP_FLAG", nullable = false, length = 1)
    private String giftwrapFlag;
    @Column(name = "GIFTWRAP_MESSAGE", length = 2000)
    private String giftwrapMessage;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE", nullable = false)
    private Date orderDate;
    @Id
    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;
    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_SHIPPED_DATE")
    private Date orderShippedDate;
    @Column(name = "ORDER_STATUS_CODE", nullable = false, length = 30)
    private String orderStatusCode;
    @Column(name = "ORDER_TOTAL", nullable = false)
    private Double orderTotal;
    @Column(name = "SHIP_TO_NAME", length = 120)
    private String shipToName;
    @Column(name = "SHIP_TO_PHONE_NUMBER", length = 20)
    private String shipToPhoneNumber;
    @ManyToOne
    @JoinColumn(name = "PAYMENT_OPTION_ID")
    private PaymentOptions paymentOptions;
    @ManyToOne
    @JoinColumn(name = "SHIP_TO_ADDRESS_ID")
    private Addresses addresses1;
    @OneToMany(mappedBy = "orders", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<OrderItems> orderItemsList1;
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customers customers5;
    @ManyToOne
    @JoinColumn(name = "DISCOUNT_ID")
    private Discounts discounts1;
    @ManyToOne
    @JoinColumn(name = "COUPON_ID")
    private Discounts discounts2;

    public Orders() {
    }

    public Orders(Discounts discounts2, String createdBy, Date creationDate, String customerCollectFlag, Customers customers5,
                  Discounts discounts1, String giftwrapFlag, String giftwrapMessage, Date lastUpdateDate,
                  String lastUpdatedBy, Long objectVersionId, Date orderDate, Long orderId, Date orderShippedDate,
                  String orderStatusCode, Double orderTotal, PaymentOptions paymentOptions, Addresses addresses1,
                  String shipToName, String shipToPhoneNumber) {
        this.discounts2 = discounts2;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.customerCollectFlag = customerCollectFlag;
        this.customers5 = customers5;
        this.discounts1 = discounts1;
        this.giftwrapFlag = giftwrapFlag;
        this.giftwrapMessage = giftwrapMessage;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.orderShippedDate = orderShippedDate;
        this.orderStatusCode = orderStatusCode;
        this.orderTotal = orderTotal;
        this.paymentOptions = paymentOptions;
        this.addresses1 = addresses1;
        this.shipToName = shipToName;
        this.shipToPhoneNumber = shipToPhoneNumber;
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

    public String getCustomerCollectFlag() {
        return customerCollectFlag;
    }

    public void setCustomerCollectFlag(String customerCollectFlag) {
        this.customerCollectFlag = customerCollectFlag;
    }


    public String getGiftwrapFlag() {
        return giftwrapFlag;
    }

    public void setGiftwrapFlag(String giftwrapFlag) {
        this.giftwrapFlag = giftwrapFlag;
    }

    public String getGiftwrapMessage() {
        return giftwrapMessage;
    }

    public void setGiftwrapMessage(String giftwrapMessage) {
        this.giftwrapMessage = giftwrapMessage;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderShippedDate() {
        return orderShippedDate;
    }

    public void setOrderShippedDate(Date orderShippedDate) {
        this.orderShippedDate = orderShippedDate;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }


    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }

    public String getShipToPhoneNumber() {
        return shipToPhoneNumber;
    }

    public void setShipToPhoneNumber(String shipToPhoneNumber) {
        this.shipToPhoneNumber = shipToPhoneNumber;
    }

    public PaymentOptions getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(PaymentOptions paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public Addresses getAddresses1() {
        return addresses1;
    }

    public void setAddresses1(Addresses addresses1) {
        this.addresses1 = addresses1;
    }

    public List<OrderItems> getOrderItemsList1() {
        return orderItemsList1;
    }

    public void setOrderItemsList1(List<OrderItems> orderItemsList1) {
        this.orderItemsList1 = orderItemsList1;
    }

    public OrderItems addOrderItems(OrderItems orderItems) {
        getOrderItemsList1().add(orderItems);
        orderItems.setOrders(this);
        return orderItems;
    }

    public OrderItems removeOrderItems(OrderItems orderItems) {
        getOrderItemsList1().remove(orderItems);
        orderItems.setOrders(null);
        return orderItems;
    }

    public Customers getCustomers5() {
        return customers5;
    }

    public void setCustomers5(Customers customers5) {
        this.customers5 = customers5;
    }

    public Discounts getDiscounts1() {
        return discounts1;
    }

    public void setDiscounts1(Discounts discounts1) {
        this.discounts1 = discounts1;
    }

    public Discounts getDiscounts2() {
        return discounts2;
    }

    public void setDiscounts2(Discounts discounts2) {
        this.discounts2 = discounts2;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("customerCollectFlag=");
        buffer.append(getCustomerCollectFlag());
        buffer.append(',');
        buffer.append("giftwrapFlag=");
        buffer.append(getGiftwrapFlag());
        buffer.append(',');
        buffer.append("giftwrapMessage=");
        buffer.append(getGiftwrapMessage());
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
        buffer.append("orderDate=");
        buffer.append(getOrderDate());
        buffer.append(',');
        buffer.append("orderId=");
        buffer.append(getOrderId());
        buffer.append(',');
        buffer.append("orderShippedDate=");
        buffer.append(getOrderShippedDate());
        buffer.append(',');
        buffer.append("orderStatusCode=");
        buffer.append(getOrderStatusCode());
        buffer.append(',');
        buffer.append("orderTotal=");
        buffer.append(getOrderTotal());
        buffer.append(',');
        buffer.append("shipToName=");
        buffer.append(getShipToName());
        buffer.append(',');
        buffer.append("shipToPhoneNumber=");
        buffer.append(getShipToPhoneNumber());
        buffer.append(']');
        return buffer.toString();
    }
}
