package oracle.alpha;

import java.io.Serializable;

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
@NamedQueries({ @NamedQuery(name = "CustomerInterests.findAll", query = "select o from CustomerInterests o") })
@Table(name = "CUSTOMER_INTERESTS")
public class CustomerInterests implements Serializable {
    private static final long serialVersionUID = -4254311638442579831L;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Id
    @Column(name = "CUSTOMER_INTERESTS_ID", nullable = false)
    private Long customerInterestsId;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customers customers;
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private ProductCategories productCategories1;

    public CustomerInterests() {
    }

    public CustomerInterests(ProductCategories productCategories1, String createdBy, Date creationDate, Customers customers,
                             Long customerInterestsId, Date lastUpdateDate, String lastUpdatedBy,
                             Long objectVersionId) {
        this.productCategories1 = productCategories1;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.customers = customers;
        this.customerInterestsId = customerInterestsId;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
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


    public Long getCustomerInterestsId() {
        return customerInterestsId;
    }

    public void setCustomerInterestsId(Long customerInterestsId) {
        this.customerInterestsId = customerInterestsId;
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

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }

    public ProductCategories getProductCategories1() {
        return productCategories1;
    }

    public void setProductCategories1(ProductCategories productCategories1) {
        this.productCategories1 = productCategories1;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("customerInterestsId=");
        buffer.append(getCustomerInterestsId());
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
        buffer.append(']');
        return buffer.toString();
    }
}
