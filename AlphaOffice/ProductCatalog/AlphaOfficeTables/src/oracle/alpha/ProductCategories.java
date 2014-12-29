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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({ @NamedQuery(name = "ProductCategories.findAll", query = "select o from ProductCategories o") })
@Table(name = "PRODUCT_CATEGORIES")
public class ProductCategories implements Serializable {
    @Id
    @Column(name = "CATEGORY_ID", nullable = false)
    private Long categoryId;
    @Column(name = "PARENT_CATEGORY_ID", nullable = false, insertable=false, updatable=false)
    private Long parentCategoryId;
    @Column(name = "CATEGORY_LEVEL", nullable = false)
    private Long categoryLevel;
    @Column(name = "CATEGORY_LOCKED_FLAG", nullable = false, length = 1)
    private String categoryLockedFlag;
    @Column(name = "CATEGORY_NAME", nullable = false, length = 40)
    private String categoryName;
    @Column(name = "CREATED_BY", nullable = false, length = 60)
    private String createdBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 60)
    private String lastUpdatedBy;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;
    @Column(name = "OBJECT_VERSION_ID", nullable = false)
    private Long objectVersionId;
    @OneToMany(mappedBy = "productCategories", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Products> productsList;
    @OneToMany(mappedBy = "productCategories1", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<CustomerInterests> customerInterestsList1;
    @ManyToOne
    @JoinColumn(name = "REPRESENTATIVE_PRODUCT_ID")
    private Products products;
    @ManyToOne
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    private ProductCategories productCategories2;
    @OneToMany(mappedBy = "productCategories2", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ProductCategories> productCategoriesList1;

    public ProductCategories() {
    }

    public ProductCategories(Long categoryId, Long parentCategoryId, Long categoryLevel, String categoryLockedFlag, String categoryName,
                             String createdBy, Date creationDate, Date lastUpdateDate, String lastUpdatedBy,
                             Long objectVersionId, ProductCategories productCategories2, Products products) {
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
        this.categoryLevel = categoryLevel;
        this.categoryLockedFlag = categoryLockedFlag;
        this.categoryName = categoryName;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.objectVersionId = objectVersionId;
        this.productCategories2 = productCategories2;
        this.products = products;
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

    public Long getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Long categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryLockedFlag() {
        return categoryLockedFlag;
    }

    public void setCategoryLockedFlag(String categoryLockedFlag) {
        this.categoryLockedFlag = categoryLockedFlag;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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


    public List<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }

    public Products addProducts(Products products) {
        getProductsList().add(products);
        products.setProductCategories(this);
        return products;
    }

    public Products removeProducts(Products products) {
        getProductsList().remove(products);
        products.setProductCategories(null);
        return products;
    }

    public List<CustomerInterests> getCustomerInterestsList1() {
        return customerInterestsList1;
    }

    public void setCustomerInterestsList1(List<CustomerInterests> customerInterestsList1) {
        this.customerInterestsList1 = customerInterestsList1;
    }

    public CustomerInterests addCustomerInterests(CustomerInterests customerInterests) {
        getCustomerInterestsList1().add(customerInterests);
        customerInterests.setProductCategories1(this);
        return customerInterests;
    }

    public CustomerInterests removeCustomerInterests(CustomerInterests customerInterests) {
        getCustomerInterestsList1().remove(customerInterests);
        customerInterests.setProductCategories1(null);
        return customerInterests;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public ProductCategories getProductCategories2() {
        return productCategories2;
    }

    public void setProductCategories2(ProductCategories productCategories2) {
        this.productCategories2 = productCategories2;
    }

    public List<ProductCategories> getProductCategoriesList1() {
        return productCategoriesList1;
    }

    public void setProductCategoriesList1(List<ProductCategories> productCategoriesList1) {
        this.productCategoriesList1 = productCategoriesList1;
    }

    public ProductCategories addProductCategories(ProductCategories productCategories) {
        getProductCategoriesList1().add(productCategories);
        productCategories.setProductCategories2(this);
        return productCategories;
    }

    public ProductCategories removeProductCategories(ProductCategories productCategories) {
        getProductCategoriesList1().remove(productCategories);
        productCategories.setProductCategories2(null);
        return productCategories;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        buffer.append('[');
        buffer.append("categoryId=");
        buffer.append(getCategoryId());
        buffer.append(',');
        buffer.append("categoryLevel=");
        buffer.append(getCategoryLevel());
        buffer.append(',');
        buffer.append("categoryLockedFlag=");
        buffer.append(getCategoryLockedFlag());
        buffer.append(',');
        buffer.append("categoryName=");
        buffer.append(getCategoryName());
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
        buffer.append("objectVersionId=");
        buffer.append(getObjectVersionId());
        buffer.append(',');
        buffer.append(']');
        return buffer.toString();
    }
}
