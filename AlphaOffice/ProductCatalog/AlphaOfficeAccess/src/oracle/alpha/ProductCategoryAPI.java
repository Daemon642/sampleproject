package oracle.alpha;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProductCategoryAPI")
public class ProductCategoryAPI {
    private Long categoryId;
    private Long parentCategoryId;
    private Long categoryLevel;
    private String categoryName;

    public ProductCategoryAPI() {
        super();
    }

    public void updateWithProductCategoryBean (ProductCategories pcBean) {
        this.setCategoryId (pcBean.getCategoryId());
        this.setParentCategoryId(pcBean.getParentCategoryId());
        this.setCategoryLevel(pcBean.getCategoryLevel());
        this.setCategoryName(pcBean.getCategoryName());
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

    public void setCategoryLevel(Long categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public Long getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
