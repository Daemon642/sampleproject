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
@NamedQueries({ @NamedQuery(name = "Discounts.findAll", query = "select o from Discounts o") })
public class Discounts implements Serializable {
    @Column(name = "APPLY_AS_PERCENTAGE_FLAG", nullable = false, length = 1)
    private String applyAsPercentageFlag;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "DISCOUNT_AMOUNT", nullable = false)
    private BigDecimal discountAmount;
    @Id
    @Column(name = "DISCOUNT_ID", nullable = false)
    private Long discountId;
    @Column(name = "DISCOUNT_TYPE_CODE", nullable = false, length = 30)
    private String discountTypeCode;
    @Column(name = "EASY_CODE", length = 20)
    private String easyCode;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "ONETIME_DISCOUNT_FLAG", nullable = false, length = 1)
    private String onetimeDiscountFlag;
    @OneToMany(mappedBy = "discounts", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<CouponUsages> couponUsagesList;
    @OneToMany(mappedBy = "discounts1", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Orders> ordersList3;
    @OneToMany(mappedBy = "discounts2", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Orders> ordersList4;

    public Discounts() {
    }

    public Discounts(String applyAsPercentageFlag, String createdBy, Date creationDate, BigDecimal discountAmount,
                     Long discountId, String discountTypeCode, String easyCode, Date lastUpdateDate,
                     String lastUpdatedBy, Long objectVersionId, String onetimeDiscountFlag) {
        this.applyAsPercentageFlag = applyAsPercentageFlag;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.discountAmount = discountAmount;
        this.discountId = discountId;
        this.discountTypeCode = discountTypeCode;
        this.easyCode = easyCode;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.onetimeDiscountFlag = onetimeDiscountFlag;
    }


    public String getApplyAsPercentageFlag() {
        return applyAsPercentageFlag;
    }

    public void setApplyAsPercentageFlag(String applyAsPercentageFlag) {
        this.applyAsPercentageFlag = applyAsPercentageFlag;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public String getDiscountTypeCode() {
        return discountTypeCode;
    }

    public void setDiscountTypeCode(String discountTypeCode) {
        this.discountTypeCode = discountTypeCode;
    }

    public String getEasyCode() {
        return easyCode;
    }

    public void setEasyCode(String easyCode) {
        this.easyCode = easyCode;
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

    public String getOnetimeDiscountFlag() {
        return onetimeDiscountFlag;
    }

    public void setOnetimeDiscountFlag(String onetimeDiscountFlag) {
        this.onetimeDiscountFlag = onetimeDiscountFlag;
    }

    public List<CouponUsages> getCouponUsagesList() {
        return couponUsagesList;
    }

    public void setCouponUsagesList(List<CouponUsages> couponUsagesList) {
        this.couponUsagesList = couponUsagesList;
    }

    public CouponUsages addCouponUsages(CouponUsages couponUsages) {
        getCouponUsagesList().add(couponUsages);
        couponUsages.setDiscounts(this);
        return couponUsages;
    }

    public CouponUsages removeCouponUsages(CouponUsages couponUsages) {
        getCouponUsagesList().remove(couponUsages);
        couponUsages.setDiscounts(null);
        return couponUsages;
    }

    public List<Orders> getOrdersList3() {
        return ordersList3;
    }

    public void setOrdersList3(List<Orders> ordersList3) {
        this.ordersList3 = ordersList3;
    }

    public Orders addOrders(Orders orders) {
        getOrdersList3().add(orders);
        orders.setDiscounts1(this);
        return orders;
    }

    public Orders removeOrders(Orders orders) {
        getOrdersList3().remove(orders);
        orders.setDiscounts1(null);
        return orders;
    }

    public List<Orders> getOrdersList4() {
        return ordersList4;
    }

    public void setOrdersList4(List<Orders> ordersList4) {
        this.ordersList4 = ordersList4;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("applyAsPercentageFlag=");
        buffer.append(getApplyAsPercentageFlag());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("discountAmount=");
        buffer.append(getDiscountAmount());
        buffer.append(',');
        buffer.append("discountId=");
        buffer.append(getDiscountId());
        buffer.append(',');
        buffer.append("discountTypeCode=");
        buffer.append(getDiscountTypeCode());
        buffer.append(',');
        buffer.append("easyCode=");
        buffer.append(getEasyCode());
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
        buffer.append("onetimeDiscountFlag=");
        buffer.append(getOnetimeDiscountFlag());
        buffer.append(',');
        buffer.append(']');
        return buffer.toString();
    }
}
