package oracle.alpha;

import java.io.Serializable;

public class OrderItemsPK implements Serializable {
    private Long lineItemId;
    private Long orders;

    public OrderItemsPK() {
    }

    public OrderItemsPK(Long lineItemId, Long orders) {
        this.lineItemId = lineItemId;
        this.orders = orders;
    }

    public boolean equals(Object other) {
        if (other instanceof OrderItemsPK) {
            final OrderItemsPK otherOrderItemsPK = (OrderItemsPK) other;
            final boolean areEqual =
                (otherOrderItemsPK.lineItemId.equals(lineItemId) && otherOrderItemsPK.orders.equals(orders));
            return areEqual;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Long getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(Long lineItemId) {
        this.lineItemId = lineItemId;
    }

    public Long getOrders() {
        return orders;
    }

    public void setOrders(Long orders) {
        this.orders = orders;
    }
}
