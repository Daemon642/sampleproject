package oracle.alpha;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AddressAPIList")
public class AddressAPIList {
    private List<AddressAPI> addressAPI;

    public AddressAPIList() {
        super();
        this.addressAPI = new ArrayList<AddressAPI>();
    }

    public void setAddressAPI(List<AddressAPI> addressAPI) {
        this.addressAPI = addressAPI;
    }

    public List<AddressAPI> getAddressAPI() {
        return addressAPI;
    }
}
