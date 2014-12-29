package oracle.alpha;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OrderAPI")
public class OrderAPI {
    private Long orderId;
    private Date orderDate;
    private String orderStatusCode;
    private Double orderTotal;
    private Long shipToAddressId;

    public void updateWithOrderBean (Orders order) {
        this.setOrderId(order.getOrderId());
        this.setOrderDate(order.getOrderDate());
        this.setOrderStatusCode(order.getOrderStatusCode());
        this.setOrderTotal(order.getOrderTotal());
        this.setShipToAddressId(order.getAddresses1().getAddressId());
    }
    
    public OrderAPI() {
        super();
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setShipToAddressId(Long shipToAddressId) {
        this.shipToAddressId = shipToAddressId;
    }

    public Long getShipToAddressId() {
        return shipToAddressId;
    }
}
