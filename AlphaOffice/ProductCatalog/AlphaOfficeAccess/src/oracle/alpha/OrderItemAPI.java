package oracle.alpha;

import javax.persistence.Column;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OrderItemAPI")
public class OrderItemAPI {
    private Long lineItemId;
    private Integer quantity;
    private Double unitPrice;

    public void updateWithOrderItemBean (OrderItems orderItem) {
        this.setLineItemId(orderItem.getLineItemId());
        this.setQuantity(orderItem.getQuantity());
        this.setUnitPrice(orderItem.getUnitPrice());
    }
    
    public OrderItemAPI() {
        super();
    }

    public void setLineItemId(Long lineItemId) {
        this.lineItemId = lineItemId;
    }

    public Long getLineItemId() {
        return lineItemId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }
}
