package oracle.alpha;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="CustomerAPIList")
public class CustomerAPIList {
    private List<CustomerAPI> customerAPI;
    
    public CustomerAPIList() {
        super();
        this.customerAPI = new ArrayList<CustomerAPI>();
    }

    public void setCustomerAPI(List<CustomerAPI> customerAPI) {
        this.customerAPI = customerAPI;
    }

    public List<CustomerAPI> getCustomerAPI() {
        return customerAPI;
    }
}
