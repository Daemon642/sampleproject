package oracle.alpha;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProductAPIList")
public class ProductAPIList {

    private List<ProductAPI> productAPI;

    public ProductAPIList() {
        super();
        this.productAPI = new ArrayList<ProductAPI>();
    }

    public void setProducts(List<ProductAPI> products) {
        this.productAPI = products;
    }

    public List<ProductAPI> getProducts() {
        return productAPI;
    }
}
