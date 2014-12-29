package oracle.alpha;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="CustomerAPI")
public class CustomerAPI {
    private Long customerId;
    private String principalName;
    private String firstName;
    private String lastName;
    private String customerTypeCode;
    private String email;
    private String mobilePhoneNumber;
    private String phoneNumber;
    private Long primaryAddressId;

    public void updateWithCustomerBean (Customers customer) {
        this.customerId = customer.getCustomerId();
        this.principalName = customer.getPrincipalName();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.customerTypeCode = customer.getCustomerTypeCode();
        this.email = customer.getEmail();
        this.mobilePhoneNumber = customer.getMobilePhoneNumber();
        this.phoneNumber = customer.getPhoneNumber();
        if (customer.getAddresses2() != null) {
            this.primaryAddressId = customer.getAddresses2().getAddressId();
        }
    }
    
    public CustomerAPI() {
        super();
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPrimaryAddressId(Long primaryAddressId) {
        this.primaryAddressId = primaryAddressId;
    }

    public Long getPrimaryAddressId() {
        return primaryAddressId;
    }
}
