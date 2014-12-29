package oracle.alpha;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OrderAPIList")
public class OrderAPIList {
    private List<OrderAPI> orderAPI;

    public OrderAPIList() {
        super();
        this.orderAPI = new ArrayList<OrderAPI>();
    }

    public void setOrderAPI(List<OrderAPI> orderAPI) {
        this.orderAPI = orderAPI;
    }

    public List<OrderAPI> getOrderAPI() {
        return orderAPI;
    }
}
