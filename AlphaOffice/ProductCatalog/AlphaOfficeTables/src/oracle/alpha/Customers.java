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
@NamedQueries({ @NamedQuery(name = "Customers.findAll", query = "select o from Customers o") })
public class Customers implements Serializable {
    @Column(name = "APPROXIMATE_INCOME")
    private Long approximateIncome;
    @Column(name = "CHILDREN_UNDER_18")
    private Integer childrenUnder18;
    @Column(name = "CONFIRMED_EMAIL", length = 25)
    private String confirmedEmail;
    @Column(name = "CONTACTABLE_FLAG", nullable = false, length = 1)
    private String contactableFlag;
    @Column(name = "CONTACT_BY_AFFILLIATES_FLAG", nullable = false, length = 1)
    private String contactByAffilliatesFlag;
    @Column(name = "CONTACT_METHOD_CODE", length = 30)
    private String contactMethodCode;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "CREDIT_LIMIT")
    private Integer creditLimit;
    @Id
    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;
    @Column(name = "CUSTOMER_TYPE_CODE", nullable = false, length = 30)
    private String customerTypeCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;
    @Column(nullable = false, length = 25)
    private String email;
    @Column(name = "FIRST_NAME", length = 30)
    private String firstName;
    @Column(nullable = false, length = 1)
    private String gender;
    @Column(name = "LAST_NAME", length = 30)
    private String lastName;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "MARITAL_STATUS_CODE", nullable = false, length = 30)
    private String maritalStatusCode;
    @Column(name = "MOBILE_PHONE_NUMBER", length = 20)
    private String mobilePhoneNumber;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;
    @Column(name = "PRINCIPAL_NAME", nullable = false, length = 60)
    private String principalName;
    @Column(name = "PROVISIONED_FLAG", length = 1)
    private String provisionedFlag;
    @Temporal(TemporalType.DATE)
    @Column(name = "REGISTERED_DATE")
    private Date registeredDate;
    @Column(length = 12)
    private String title;
    @OneToMany(mappedBy = "customers", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<CustomerInterests> customerInterestsList;
    @OneToMany(mappedBy = "customers1", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<CustomerIdentifications> customerIdentificationsList;
    @OneToMany(mappedBy = "customers2", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<CouponUsages> couponUsagesList1;
    @OneToMany(mappedBy = "customers3", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<PaymentOptions> paymentOptionsList;
    @OneToMany(mappedBy = "customers4", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<SalesOrderHeader> salesOrderHeaderList;
    @ManyToOne
    @JoinColumn(name = "PRIMARY_ADDRESS_ID")
    private Addresses addresses2;
    @OneToMany(mappedBy = "customers5", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Orders> ordersList2;
    @OneToMany(mappedBy = "customers6", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<AddressUsages> addressUsagesList1;

    public Customers() {
    }

    public Customers(Long approximateIncome, Integer childrenUnder18, String confirmedEmail,
                     String contactByAffilliatesFlag, String contactMethodCode, String contactableFlag,
                     String createdBy, Date creationDate, Integer creditLimit, Long customerId, String customerTypeCode,
                     Date dateOfBirth, String email, String firstName, String gender, String lastName,
                     Date lastUpdateDate, String lastUpdatedBy, String maritalStatusCode, String mobilePhoneNumber,
                     Long objectVersionId, String phoneNumber, Addresses addresses2, String principalName,
                     String provisionedFlag, Date registeredDate, String title) {
        this.approximateIncome = approximateIncome;
        this.childrenUnder18 = childrenUnder18;
        this.confirmedEmail = confirmedEmail;
        this.contactByAffilliatesFlag = contactByAffilliatesFlag;
        this.contactMethodCode = contactMethodCode;
        this.contactableFlag = contactableFlag;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.creditLimit = creditLimit;
        this.customerId = customerId;
        this.customerTypeCode = customerTypeCode;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.firstName = firstName;
        this.gender = gender;
        this.lastName = lastName;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.maritalStatusCode = maritalStatusCode;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.objectVersionId = objectVersionId;
        this.phoneNumber = phoneNumber;
        this.addresses2 = addresses2;
        this.principalName = principalName;
        this.provisionedFlag = provisionedFlag;
        this.registeredDate = registeredDate;
        this.title = title;
    }


    public Long getApproximateIncome() {
        return approximateIncome;
    }

    public void setApproximateIncome(Long approximateIncome) {
        this.approximateIncome = approximateIncome;
    }

    public Integer getChildrenUnder18() {
        return childrenUnder18;
    }

    public void setChildrenUnder18(Integer childrenUnder18) {
        this.childrenUnder18 = childrenUnder18;
    }

    public String getConfirmedEmail() {
        return confirmedEmail;
    }

    public void setConfirmedEmail(String confirmedEmail) {
        this.confirmedEmail = confirmedEmail;
    }

    public String getContactableFlag() {
        return contactableFlag;
    }

    public void setContactableFlag(String contactableFlag) {
        this.contactableFlag = contactableFlag;
    }

    public String getContactByAffilliatesFlag() {
        return contactByAffilliatesFlag;
    }

    public void setContactByAffilliatesFlag(String contactByAffilliatesFlag) {
        this.contactByAffilliatesFlag = contactByAffilliatesFlag;
    }

    public String getContactMethodCode() {
        return contactMethodCode;
    }

    public void setContactMethodCode(String contactMethodCode) {
        this.contactMethodCode = contactMethodCode;
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

    public Integer getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getMaritalStatusCode() {
        return maritalStatusCode;
    }

    public void setMaritalStatusCode(String maritalStatusCode) {
        this.maritalStatusCode = maritalStatusCode;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Long getObjectVersionId() {
        return objectVersionId;
    }

    public void setObjectVersionId(Long objectVersionId) {
        this.objectVersionId = objectVersionId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getProvisionedFlag() {
        return provisionedFlag;
    }

    public void setProvisionedFlag(String provisionedFlag) {
        this.provisionedFlag = provisionedFlag;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CustomerInterests> getCustomerInterestsList() {
        return customerInterestsList;
    }

    public void setCustomerInterestsList(List<CustomerInterests> customerInterestsList) {
        this.customerInterestsList = customerInterestsList;
    }

    public CustomerInterests addCustomerInterests(CustomerInterests customerInterests) {
        getCustomerInterestsList().add(customerInterests);
        customerInterests.setCustomers(this);
        return customerInterests;
    }

    public CustomerInterests removeCustomerInterests(CustomerInterests customerInterests) {
        getCustomerInterestsList().remove(customerInterests);
        customerInterests.setCustomers(null);
        return customerInterests;
    }

    public List<CustomerIdentifications> getCustomerIdentificationsList() {
        return customerIdentificationsList;
    }

    public void setCustomerIdentificationsList(List<CustomerIdentifications> customerIdentificationsList) {
        this.customerIdentificationsList = customerIdentificationsList;
    }

    public CustomerIdentifications addCustomerIdentifications(CustomerIdentifications customerIdentifications) {
        getCustomerIdentificationsList().add(customerIdentifications);
        customerIdentifications.setCustomers1(this);
        return customerIdentifications;
    }

    public CustomerIdentifications removeCustomerIdentifications(CustomerIdentifications customerIdentifications) {
        getCustomerIdentificationsList().remove(customerIdentifications);
        customerIdentifications.setCustomers1(null);
        return customerIdentifications;
    }

    public List<CouponUsages> getCouponUsagesList1() {
        return couponUsagesList1;
    }

    public void setCouponUsagesList1(List<CouponUsages> couponUsagesList1) {
        this.couponUsagesList1 = couponUsagesList1;
    }

    public CouponUsages addCouponUsages(CouponUsages couponUsages) {
        getCouponUsagesList1().add(couponUsages);
        couponUsages.setCustomers2(this);
        return couponUsages;
    }

    public CouponUsages removeCouponUsages(CouponUsages couponUsages) {
        getCouponUsagesList1().remove(couponUsages);
        couponUsages.setCustomers2(null);
        return couponUsages;
    }

    public List<PaymentOptions> getPaymentOptionsList() {
        return paymentOptionsList;
    }

    public void setPaymentOptionsList(List<PaymentOptions> paymentOptionsList) {
        this.paymentOptionsList = paymentOptionsList;
    }

    public PaymentOptions addPaymentOptions(PaymentOptions paymentOptions) {
        getPaymentOptionsList().add(paymentOptions);
        paymentOptions.setCustomers3(this);
        return paymentOptions;
    }

    public PaymentOptions removePaymentOptions(PaymentOptions paymentOptions) {
        getPaymentOptionsList().remove(paymentOptions);
        paymentOptions.setCustomers3(null);
        return paymentOptions;
    }

    public List<SalesOrderHeader> getSalesOrderHeaderList() {
        return salesOrderHeaderList;
    }

    public void setSalesOrderHeaderList(List<SalesOrderHeader> salesOrderHeaderList) {
        this.salesOrderHeaderList = salesOrderHeaderList;
    }

    public SalesOrderHeader addSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        getSalesOrderHeaderList().add(salesOrderHeader);
        salesOrderHeader.setCustomers4(this);
        return salesOrderHeader;
    }

    public SalesOrderHeader removeSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        getSalesOrderHeaderList().remove(salesOrderHeader);
        salesOrderHeader.setCustomers4(null);
        return salesOrderHeader;
    }

    public Addresses getAddresses2() {
        return addresses2;
    }

    public void setAddresses2(Addresses addresses2) {
        this.addresses2 = addresses2;
    }

    public List<Orders> getOrdersList2() {
        return ordersList2;
    }

    public void setOrdersList2(List<Orders> ordersList2) {
        this.ordersList2 = ordersList2;
    }

    public Orders addOrders(Orders orders) {
        getOrdersList2().add(orders);
        orders.setCustomers5(this);
        return orders;
    }

    public Orders removeOrders(Orders orders) {
        getOrdersList2().remove(orders);
        orders.setCustomers5(null);
        return orders;
    }

    public List<AddressUsages> getAddressUsagesList1() {
        return addressUsagesList1;
    }

    public void setAddressUsagesList1(List<AddressUsages> addressUsagesList1) {
        this.addressUsagesList1 = addressUsagesList1;
    }

    public AddressUsages addAddressUsages(AddressUsages addressUsages) {
        getAddressUsagesList1().add(addressUsages);
        addressUsages.setCustomers6(this);
        return addressUsages;
    }

    public AddressUsages removeAddressUsages(AddressUsages addressUsages) {
        getAddressUsagesList1().remove(addressUsages);
        addressUsages.setCustomers6(null);
        return addressUsages;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("approximateIncome=");
        buffer.append(getApproximateIncome());
        buffer.append(',');
        buffer.append("childrenUnder18=");
        buffer.append(getChildrenUnder18());
        buffer.append(',');
        buffer.append("confirmedEmail=");
        buffer.append(getConfirmedEmail());
        buffer.append(',');
        buffer.append("contactByAffilliatesFlag=");
        buffer.append(getContactByAffilliatesFlag());
        buffer.append(',');
        buffer.append("contactMethodCode=");
        buffer.append(getContactMethodCode());
        buffer.append(',');
        buffer.append("contactableFlag=");
        buffer.append(getContactableFlag());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("creditLimit=");
        buffer.append(getCreditLimit());
        buffer.append(',');
        buffer.append("customerId=");
        buffer.append(getCustomerId());
        buffer.append(',');
        buffer.append("customerTypeCode=");
        buffer.append(getCustomerTypeCode());
        buffer.append(',');
        buffer.append("dateOfBirth=");
        buffer.append(getDateOfBirth());
        buffer.append(',');
        buffer.append("email=");
        buffer.append(getEmail());
        buffer.append(',');
        buffer.append("firstName=");
        buffer.append(getFirstName());
        buffer.append(',');
        buffer.append("gender=");
        buffer.append(getGender());
        buffer.append(',');
        buffer.append("lastName=");
        buffer.append(getLastName());
        buffer.append(',');
        buffer.append("lastUpdateDate=");
        buffer.append(getLastUpdateDate());
        buffer.append(',');
        buffer.append("lastUpdatedBy=");
        buffer.append(getLastUpdatedBy());
        buffer.append(',');
        buffer.append("maritalStatusCode=");
        buffer.append(getMaritalStatusCode());
        buffer.append(',');
        buffer.append("mobilePhoneNumber=");
        buffer.append(getMobilePhoneNumber());
        buffer.append(',');
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append("phoneNumber=");
        buffer.append(getPhoneNumber());
        buffer.append(',');
        buffer.append("principalName=");
        buffer.append(getPrincipalName());
        buffer.append(',');
        buffer.append("provisionedFlag=");
        buffer.append(getProvisionedFlag());
        buffer.append(',');
        buffer.append("registeredDate=");
        buffer.append(getRegisteredDate());
        buffer.append(',');
        buffer.append("title=");
        buffer.append(getTitle());
        buffer.append(']');
        return buffer.toString();
    }
}
