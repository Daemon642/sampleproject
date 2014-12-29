package oracle.alpha;

import java.io.Serializable;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({ @NamedQuery(name = "SalesOrderLineItem.findAll", query = "select o from SalesOrderLineItem o") })
@Table(name = "SALES_ORDER_LINE_ITEM")
public class SalesOrderLineItem implements Serializable {
    @Column(name = "CALCULATE_PRICE_FLAG")
    private String calculatePriceFlag;
    @Column(name = "INVENTORY_ITEM_ID")
    private BigDecimal inventoryItemId;
    @Column(name = "ORDERED_QUANTITY")
    private BigDecimal orderedQuantity;
    @Id
    @Column(name = "SALES_ORDER_LINE_ITEM_ID", nullable = false)
    private Long salesOrderLineItemId;
    @Column(name = "UNIT_SELLING_PRICE")
    private BigDecimal unitSellingPrice;
    @ManyToOne
    @JoinColumn(name = "SALES_ORDER_ID")
    private SalesOrderHeader salesOrderHeader;

    public SalesOrderLineItem() {
    }

    public SalesOrderLineItem(String calculatePriceFlag, BigDecimal inventoryItemId, BigDecimal orderedQuantity,
                              SalesOrderHeader salesOrderHeader, Long salesOrderLineItemId, BigDecimal unitSellingPrice) {
        this.calculatePriceFlag = calculatePriceFlag;
        this.inventoryItemId = inventoryItemId;
        this.orderedQuantity = orderedQuantity;
        this.salesOrderHeader = salesOrderHeader;
        this.salesOrderLineItemId = salesOrderLineItemId;
        this.unitSellingPrice = unitSellingPrice;
    }


    public String getCalculatePriceFlag() {
        return calculatePriceFlag;
    }

    public void setCalculatePriceFlag(String calculatePriceFlag) {
        this.calculatePriceFlag = calculatePriceFlag;
    }

    public BigDecimal getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(BigDecimal inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public BigDecimal getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(BigDecimal orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }


    public Long getSalesOrderLineItemId() {
        return salesOrderLineItemId;
    }

    public void setSalesOrderLineItemId(Long salesOrderLineItemId) {
        this.salesOrderLineItemId = salesOrderLineItemId;
    }

    public BigDecimal getUnitSellingPrice() {
        return unitSellingPrice;
    }

    public void setUnitSellingPrice(BigDecimal unitSellingPrice) {
        this.unitSellingPrice = unitSellingPrice;
    }

    public SalesOrderHeader getSalesOrderHeader() {
        return salesOrderHeader;
    }

    public void setSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        this.salesOrderHeader = salesOrderHeader;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("calculatePriceFlag=");
        buffer.append(getCalculatePriceFlag());
        buffer.append(',');
        buffer.append("inventoryItemId=");
        buffer.append(getInventoryItemId());
        buffer.append(',');
        buffer.append("orderedQuantity=");
        buffer.append(getOrderedQuantity());
        buffer.append(',');
        buffer.append("salesOrderLineItemId=");
        buffer.append(getSalesOrderLineItemId());
        buffer.append(',');
        buffer.append("unitSellingPrice=");
        buffer.append(getUnitSellingPrice());
        buffer.append(']');
        return buffer.toString();
    }
}
