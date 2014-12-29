package oracle.alpha;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProductAPI")
public class ProductAPI {

    private Double costPrice;
    private String externalUrl;
    private Integer listPrice;
    private Integer minPrice;
    private Long categoryId;
    private Long parentCategoryId;
    private Long productId;
    private String productName;
    private String productStatus;
    private Integer warrantyPeriodMonths;

    public void updateWithProductBean (Products product) {
        this.setCostPrice(product.getCostPrice());
        this.setExternalUrl(product.getExternalUrl());
        this.setListPrice(product.getListPrice());
        this.setMinPrice(product.getMinPrice());
        this.setCategoryId(product.getCategoryId());
        this.setParentCategoryId(product.getParentCategoryId());
        this.setProductId(product.getProductId());
        this.setProductName(product.getProductName());
        this.setProductStatus(product.getProductStatus());
        this.setWarrantyPeriodMonths(product.getWarrantyPeriodMonths());
    }
    
    public ProductAPI() {
        super();
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setListPrice(Integer listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getListPrice() {
        return listPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setWarrantyPeriodMonths(Integer warrantyPeriodMonths) {
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    public Integer getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }
}
