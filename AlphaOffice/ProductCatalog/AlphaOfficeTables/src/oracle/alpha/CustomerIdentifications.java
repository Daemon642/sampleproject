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
@NamedQueries({
              @NamedQuery(name = "CustomerIdentifications.findAll",
                          query = "select o from CustomerIdentifications o") })
@Table(name = "CUSTOMER_IDENTIFICATIONS")
public class CustomerIdentifications implements Serializable {
    @Column(name = "ADDITIONAL_INFORMATION", length = 1000)
    private String additionalInformation;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Id
    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;
    @Column(name = "ID_DETAIL", nullable = false, length = 20)
    private String idDetail;
    @Column(name = "ID_TYPE_CODE", nullable = false, length = 30)
    private String idTypeCode;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "VERIFICATION_METHOD_CODE", length = 30)
    private String verificationMethodCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "VERIFIED_DATE")
    private Date verifiedDate;
    @Column(name = "VERIFIED_FLAG", nullable = false, length = 1)
    private String verifiedFlag;
    @ManyToOne
    @JoinColumn(name = "VERIFIED_BY")
    private Customers customers1;

    public CustomerIdentifications() {
    }

    public CustomerIdentifications(String additionalInformation, String createdBy, Date creationDate, Long customerId,
                                   String idDetail, String idTypeCode, Date lastUpdateDate, String lastUpdatedBy,
                                   Long objectVersionId, String verificationMethodCode, Customers customers1,
                                   Date verifiedDate, String verifiedFlag) {
        this.additionalInformation = additionalInformation;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.customerId = customerId;
        this.idDetail = idDetail;
        this.idTypeCode = idTypeCode;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.verificationMethodCode = verificationMethodCode;
        this.customers1 = customers1;
        this.verifiedDate = verifiedDate;
        this.verifiedFlag = verifiedFlag;
    }


    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIdTypeCode() {
        return idTypeCode;
    }

    public void setIdTypeCode(String idTypeCode) {
        this.idTypeCode = idTypeCode;
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

    public String getVerificationMethodCode() {
        return verificationMethodCode;
    }

    public void setVerificationMethodCode(String verificationMethodCode) {
        this.verificationMethodCode = verificationMethodCode;
    }


    public Date getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(Date verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public String getVerifiedFlag() {
        return verifiedFlag;
    }

    public void setVerifiedFlag(String verifiedFlag) {
        this.verifiedFlag = verifiedFlag;
    }

    public Customers getCustomers1() {
        return customers1;
    }

    public void setCustomers1(Customers customers1) {
        this.customers1 = customers1;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("additionalInformation=");
        buffer.append(getAdditionalInformation());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("customerId=");
        buffer.append(getCustomerId());
        buffer.append(',');
        buffer.append("idDetail=");
        buffer.append(getIdDetail());
        buffer.append(',');
        buffer.append("idTypeCode=");
        buffer.append(getIdTypeCode());
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
        buffer.append("verificationMethodCode=");
        buffer.append(getVerificationMethodCode());
        buffer.append(',');
        buffer.append("verifiedDate=");
        buffer.append(getVerifiedDate());
        buffer.append(',');
        buffer.append("verifiedFlag=");
        buffer.append(getVerifiedFlag());
        buffer.append(']');
        return buffer.toString();
    }
}
