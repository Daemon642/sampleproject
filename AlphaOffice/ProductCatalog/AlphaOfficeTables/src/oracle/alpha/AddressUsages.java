package oracle.alpha;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "AddressUsages.findAll", query = "select o from AddressUsages o") })
@Table(name = "ADDRESS_USAGES")
public class AddressUsages implements Serializable {
    private static final long serialVersionUID = 5094928922546846782L;
    @Id
    @Column(name = "ADDRESS_USAGES_ID", nullable = false)
    private BigDecimal addressUsagesId;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "EXPIRED_FLAG", nullable = false, length = 1)
    private String expiredFlag;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "OWNER_TYPE_CODE", nullable = false, length = 30)
    private String ownerTypeCode;
    @Column(name = "USAGE_TYPE_CODE", nullable = false, length = 30)
    private String usageTypeCode;
    @ManyToOne
    @JoinColumn(name = "ADDRESS_ID")
    private Addresses addresses;
    @ManyToOne
    @JoinColumn(name = "ASSOCIATED_OWNER_ID")
    private Customers customers6;

    public AddressUsages() {
    }

    public AddressUsages(Addresses addresses, BigDecimal addressUsagesId, Customers customers6, String createdBy,
                         Date creationDate, String expiredFlag, Date lastUpdateDate, String lastUpdatedBy,
                         Long objectVersionId, String ownerTypeCode, String usageTypeCode) {
        this.addresses = addresses;
        this.addressUsagesId = addressUsagesId;
        this.customers6 = customers6;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.expiredFlag = expiredFlag;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.ownerTypeCode = ownerTypeCode;
        this.usageTypeCode = usageTypeCode;
    }


    public BigDecimal getAddressUsagesId() {
        return addressUsagesId;
    }

    public void setAddressUsagesId(BigDecimal addressUsagesId) {
        this.addressUsagesId = addressUsagesId;
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

    public String getExpiredFlag() {
        return expiredFlag;
    }

    public void setExpiredFlag(String expiredFlag) {
        this.expiredFlag = expiredFlag;
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

    public Long getObjectVersionId() {
        return objectVersionId;
    }

    public void setObjectVersionId(Long objectVersionId) {
        this.objectVersionId = objectVersionId;
    }

    public String getOwnerTypeCode() {
        return ownerTypeCode;
    }

    public void setOwnerTypeCode(String ownerTypeCode) {
        this.ownerTypeCode = ownerTypeCode;
    }

    public String getUsageTypeCode() {
        return usageTypeCode;
    }

    public void setUsageTypeCode(String usageTypeCode) {
        this.usageTypeCode = usageTypeCode;
    }

    public Addresses getAddresses() {
        return addresses;
    }

    public void setAddresses(Addresses addresses) {
        this.addresses = addresses;
    }

    public Customers getCustomers6() {
        return customers6;
    }

    public void setCustomers6(Customers customers6) {
        this.customers6 = customers6;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("addressUsagesId=");
        buffer.append(getAddressUsagesId());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("expiredFlag=");
        buffer.append(getExpiredFlag());
        buffer.append(',');
        buffer.append("lastUpdateDate=");
        buffer.append(getLastUpdateDate());
        buffer.append(',');
        buffer.append("lastUpdatedBy=");
        buffer.append(getLastUpdatedBy());
        buffer.append(',');
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append("ownerTypeCode=");
        buffer.append(getOwnerTypeCode());
        buffer.append(',');
        buffer.append("usageTypeCode=");
        buffer.append(getUsageTypeCode());
        buffer.append(']');
        return buffer.toString();
    }
}
