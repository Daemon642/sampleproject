package oracle.alpha;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AddressAPI")
public class AddressAPI {
    private Long addressId;
    private String address1;
    private String address2;
    private String city;
    private String countryId;
    private String postalCode;
    private String stateProvince;

    public void updateWithAddressBean (Addresses address) {
        this.setAddressId(address.getAddressId());
        this.setAddress1(address.getAddress1());
        this.setAddress2(address.getAddress2());
        this.setCity(address.getCity());
        this.setCountryId(address.getCountryId());
        this.setPostalCode(address.getPostalCode());
        this.setStateProvince(address.getStateProvince());
    }
    
    public AddressAPI() {
        super();
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress2() {
        return address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getStateProvince() {
        return stateProvince;
    }
}
