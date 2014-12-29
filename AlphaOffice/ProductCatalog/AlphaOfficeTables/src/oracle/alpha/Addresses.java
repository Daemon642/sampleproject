package oracle.alpha;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "Addresses.findAll", query = "select o from Addresses o") })
public class Addresses implements Serializable {
    private static final long serialVersionUID = -225105301363982290L;
    @Column(nullable = false, length = 40)
    private String address1;
    @Column(length = 40)
    private String address2;
    @Id
    @Column(name = "ADDRESS_ID", nullable = false)
    private Long addressId;
    @Column(nullable = false, length = 40)
    private String city;
    @Column(name = "COUNTRY_ID", nullable = false)
    private String countryId;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "POSTAL_CODE", length = 12)
    private String postalCode;
    @Column(name = "STATE_PROVINCE", nullable = false, length = 40)
    private String stateProvince;
    @OneToMany(mappedBy = "addresses", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<AddressUsages> addressUsagesList;
    @OneToMany(mappedBy = "addresses1", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Orders> ordersList1;
    @OneToMany(mappedBy = "addresses2", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Customers> customersList;

    public Addresses() {
    }

    public Addresses(String address1, String address2, Long addressId, String city, String countryId, String createdBy,
                     Date creationDate, Date lastUpdateDate, String lastUpdatedBy, BigDecimal latitude,
                     BigDecimal longitude, Long objectVersionId, String postalCode, String stateProvince) {
        this.address1 = address1;
        this.address2 = address2;
        this.addressId = addressId;
        this.city = city;
        this.countryId = countryId;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.objectVersionId = objectVersionId;
        this.postalCode = postalCode;
        this.stateProvince = stateProvince;
    }


    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Long getObjectVersionId() {
        return objectVersionId;
    }

    public void setObjectVersionId(Long objectVersionId) {
        this.objectVersionId = objectVersionId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public List<AddressUsages> getAddressUsagesList() {
        return addressUsagesList;
    }

    public void setAddressUsagesList(List<AddressUsages> addressUsagesList) {
        this.addressUsagesList = addressUsagesList;
    }

    public AddressUsages addAddressUsages(AddressUsages addressUsages) {
        getAddressUsagesList().add(addressUsages);
        addressUsages.setAddresses(this);
        return addressUsages;
    }

    public AddressUsages removeAddressUsages(AddressUsages addressUsages) {
        getAddressUsagesList().remove(addressUsages);
        addressUsages.setAddresses(null);
        return addressUsages;
    }

    public List<Orders> getOrdersList1() {
        return ordersList1;
    }

    public void setOrdersList1(List<Orders> ordersList1) {
        this.ordersList1 = ordersList1;
    }

    public Orders addOrders(Orders orders) {
        getOrdersList1().add(orders);
        orders.setAddresses1(this);
        return orders;
    }

    public Orders removeOrders(Orders orders) {
        getOrdersList1().remove(orders);
        orders.setAddresses1(null);
        return orders;
    }

    public List<Customers> getCustomersList() {
        return customersList;
    }

    public void setCustomersList(List<Customers> customersList) {
        this.customersList = customersList;
    }

    public Customers addCustomers(Customers customers) {
        getCustomersList().add(customers);
        customers.setAddresses2(this);
        return customers;
    }

    public Customers removeCustomers(Customers customers) {
        getCustomersList().remove(customers);
        customers.setAddresses2(null);
        return customers;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("address1=");
        buffer.append(getAddress1());
        buffer.append(',');
        buffer.append("address2=");
        buffer.append(getAddress2());
        buffer.append(',');
        buffer.append("addressId=");
        buffer.append(getAddressId());
        buffer.append(',');
        buffer.append("city=");
        buffer.append(getCity());
        buffer.append(',');
        buffer.append("countryId=");
        buffer.append(getCountryId());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("lastUpdateDate=");
        buffer.append(getLastUpdateDate());
        buffer.append(',');
        buffer.append("lastUpdatedBy=");
        buffer.append(getLastUpdatedBy());
        buffer.append(',');
        buffer.append("latitude=");
        buffer.append(getLatitude());
        buffer.append(',');
        buffer.append("longitude=");
        buffer.append(getLongitude());
        buffer.append(',');
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append("postalCode=");
        buffer.append(getPostalCode());
        buffer.append(',');
        buffer.append("stateProvince=");
        buffer.append(getStateProvince());
        buffer.append(']');
        return buffer.toString();
    }
}
