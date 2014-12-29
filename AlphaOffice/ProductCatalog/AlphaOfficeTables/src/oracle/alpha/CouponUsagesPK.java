package oracle.alpha;

import java.io.Serializable;

import java.util.Date;

public class CouponUsagesPK implements Serializable {
    private Date appliedDate;
    private Long discounts;
    private Long customers2;

    public CouponUsagesPK() {
    }

    public CouponUsagesPK(Date appliedDate, Long discounts, Long customers2) {
        this.appliedDate = appliedDate;
        this.discounts = discounts;
        this.customers2 = customers2;
    }

    public boolean equals(Object other) {
        if (other instanceof CouponUsagesPK) {
            final CouponUsagesPK otherCouponUsagesPK = (CouponUsagesPK) other;
            final boolean areEqual =
                (otherCouponUsagesPK.appliedDate.equals(appliedDate) &&
                 otherCouponUsagesPK.discounts.equals(discounts) && otherCouponUsagesPK.customers2.equals(customers2));
            return areEqual;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Long getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Long discounts) {
        this.discounts = discounts;
    }

    public Long getCustomers2() {
        return customers2;
    }

    public void setCustomers2(Long customers2) {
        this.customers2 = customers2;
    }
}
