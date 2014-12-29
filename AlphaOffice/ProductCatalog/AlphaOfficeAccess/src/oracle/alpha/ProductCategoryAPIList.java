package oracle.alpha;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProductCategoryAPIList")
public class ProductCategoryAPIList {
    
    private List<ProductCategoryAPI> productCategoryAPI;
    
    public ProductCategoryAPIList() {
        super();
        this.productCategoryAPI = new ArrayList<ProductCategoryAPI>();
    }

    public void setProductCategoryAPI(List<ProductCategoryAPI> productCategoryAPI) {
        this.productCategoryAPI = productCategoryAPI;
    }

    public List<ProductCategoryAPI> getProductCategoryAPI() {
        return productCategoryAPI;
    }
}
