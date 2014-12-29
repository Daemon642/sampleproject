package oracle.alpha;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OrderItemAPIList")
public class OrderItemAPIList {
    private List<OrderItemAPI> orderItemAPI;

    public OrderItemAPIList() {
        super();
        this.orderItemAPI = new ArrayList<OrderItemAPI>();
    }

    public void setOrderItemAPI(List<OrderItemAPI> orderItemAPI) {
        this.orderItemAPI = orderItemAPI;
    }

    public List<OrderItemAPI> getOrderItemAPI() {
        return orderItemAPI;
    }
}
