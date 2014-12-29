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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "Products.findAll", query = "select o from Products o") })
public class Products implements Serializable {
    @Column(length = 150)
    private String attribute1;
    @Column(length = 150)
    private String attribute2;
    @Column(length = 150)
    private String attribute3;
    @Column(length = 150)
    private String attribute4;
    @Column(length = 150)
    private String attribute5;
    @Column(name = "ATTRIBUTE_CATEGORY", length = 30)
    private String attributeCategory;
    @Column(name = "COST_PRICE")
    private Double costPrice;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "EXTERNAL_URL", length = 200)
    private String externalUrl;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "LIST_PRICE", nullable = false)
    private Integer listPrice;
    @Column(name = "MIN_PRICE", nullable = false)
    private Integer minPrice;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @Column(name = "CATEGORY_ID", insertable=false, updatable=false)
    private Long categoryId;
    @Column(name = "PARENT_CATEGORY_ID")
    private Long parentCategoryId;
    @Id
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;
    @Column(name = "PRODUCT_NAME", nullable = false, length = 150)
    private String productName;
    @Column(name = "PRODUCT_STATUS", nullable = false, length = 30)
    private String productStatus;
    @Column(name = "WARRANTY_PERIOD_MONTHS")
    private Integer warrantyPeriodMonths;
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private ProductCategories productCategories;
    @OneToMany(mappedBy = "products", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ProductCategories> productCategoriesList;
    @OneToMany(mappedBy = "products1", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<OrderItems> orderItemsList;
    @OneToOne(mappedBy = "products2")
    private ProductStockLevels productStockLevelsList;

    public Products() {
    }

    public Products(String attribute1, String attribute2, String attribute3, String attribute4, String attribute5,
                    String attributeCategory, ProductCategories productCategories, Double costPrice, String createdBy, Date creationDate,
                    String externalUrl, Date lastUpdateDate, String lastUpdatedBy, Integer listPrice, Integer minPrice,
                    Long objectVersionId, Long categoryId, Long parentCategoryId, Long productId, String productName,
                    String productStatus, Integer warrantyPeriodMonths) {
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.attribute3 = attribute3;
        this.attribute4 = attribute4;
        this.attribute5 = attribute5;
        this.attributeCategory = attributeCategory;
        this.productCategories = productCategories;
        this.costPrice = costPrice;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.externalUrl = externalUrl;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.listPrice = listPrice;
        this.minPrice = minPrice;
        this.objectVersionId = objectVersionId;
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
        this.productId = productId;
        this.productName = productName;
        this.productStatus = productStatus;
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }


    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public String getAttribute3() {
        return attribute3;
    }

    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }

    public String getAttribute4() {
        return attribute4;
    }

    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
    }

    public String getAttribute5() {
        return attribute5;
    }

    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5;
    }

    public String getAttributeCategory() {
        return attributeCategory;
    }

    public void setAttributeCategory(String attributeCategory) {
        this.attributeCategory = attributeCategory;
    }


    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
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

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
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

    public Integer getListPrice() {
        return listPrice;
    }

    public void setListPrice(Integer listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Long getObjectVersionId() {
        return objectVersionId;
    }

    public void setObjectVersionId(Long objectVersionId) {
        this.objectVersionId = objectVersionId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public Integer getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }

    public void setWarrantyPeriodMonths(Integer warrantyPeriodMonths) {
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    public ProductCategories getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(ProductCategories productCategories) {
        this.productCategories = productCategories;
    }

    public List<ProductCategories> getProductCategoriesList() {
        return productCategoriesList;
    }

    public void setProductCategoriesList(List<ProductCategories> productCategoriesList) {
        this.productCategoriesList = productCategoriesList;
    }

    public ProductCategories addProductCategories(ProductCategories productCategories) {
        getProductCategoriesList().add(productCategories);
        productCategories.setProducts(this);
        return productCategories;
    }

    public ProductCategories removeProductCategories(ProductCategories productCategories) {
        getProductCategoriesList().remove(productCategories);
        productCategories.setProducts(null);
        return productCategories;
    }

    public List<OrderItems> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItems> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    public OrderItems addOrderItems(OrderItems orderItems) {
        getOrderItemsList().add(orderItems);
        orderItems.setProducts1(this);
        return orderItems;
    }

    public OrderItems removeOrderItems(OrderItems orderItems) {
        getOrderItemsList().remove(orderItems);
        orderItems.setProducts1(null);
        return orderItems;
    }

    public ProductStockLevels getProductStockLevelsList() {
        return productStockLevelsList;
    }

    public void setProductStockLevelsList(ProductStockLevels productStockLevelsList) {
        this.productStockLevelsList = productStockLevelsList;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("attribute1=");
        buffer.append(getAttribute1());
        buffer.append(',');
        buffer.append("attribute2=");
        buffer.append(getAttribute2());
        buffer.append(',');
        buffer.append("attribute3=");
        buffer.append(getAttribute3());
        buffer.append(',');
        buffer.append("attribute4=");
        buffer.append(getAttribute4());
        buffer.append(',');
        buffer.append("attribute5=");
        buffer.append(getAttribute5());
        buffer.append(',');
        buffer.append("attributeCategory=");
        buffer.append(getAttributeCategory());
        buffer.append(',');
        buffer.append("costPrice=");
        buffer.append(getCostPrice());
        buffer.append(',');
        buffer.append("createdBy=");
        buffer.append(getCreatedBy());
        buffer.append(',');
        buffer.append("creationDate=");
        buffer.append(getCreationDate());
        buffer.append(',');
        buffer.append("externalUrl=");
        buffer.append(getExternalUrl());
        buffer.append(',');
        buffer.append("lastUpdateDate=");
        buffer.append(getLastUpdateDate());
        buffer.append(',');
        buffer.append("lastUpdatedBy=");
        buffer.append(getLastUpdatedBy());
        buffer.append(',');
        buffer.append("listPrice=");
        buffer.append(getListPrice());
        buffer.append(',');
        buffer.append("minPrice=");
        buffer.append(getMinPrice());
        buffer.append(',');
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append("parentCategoryId=");
        buffer.append(getParentCategoryId());
        buffer.append(',');
        buffer.append("productId=");
        buffer.append(getProductId());
        buffer.append(',');
        buffer.append("productName=");
        buffer.append(getProductName());
        buffer.append(',');
        buffer.append("productStatus=");
        buffer.append(getProductStatus());
        buffer.append(',');
        buffer.append("warrantyPeriodMonths=");
        buffer.append(getWarrantyPeriodMonths());
        buffer.append(']');
        return buffer.toString();
    }
}
