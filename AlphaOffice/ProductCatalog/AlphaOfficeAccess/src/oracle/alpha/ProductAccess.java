package oracle.alpha;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/AlphaOfficeAccess/Products")
public class ProductAccess {

    final JavaServiceFacade javaServiceFacade;

    public ProductAccess() {
        super();
        javaServiceFacade = new JavaServiceFacade();
    }
    
    @GET
    @Path("/getProducts")
    @Produces(MediaType.APPLICATION_XML)
    public ProductAPIList getProducts() {
        List<Products> productList;
        
        ProductAPIList productAPIList = new ProductAPIList ();
        
        productList = javaServiceFacade.getProductsFindAll();
        for ( int i = 0; productList != null && i < productList.size(); i++ ) {
            ProductAPI productAPI = new ProductAPI ();
            productAPI.updateWithProductBean(productList.get(i));
            productAPIList.getProducts().add(productAPI);
        }
        
        return productAPIList;
    }

    @GET
    @Path("/getProductsJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductAPIList getProductsJSON() {
        return getProducts();
    }
    
    @GET
    @Path("/getProductById/{productId}")
    @Produces(MediaType.APPLICATION_XML)
    public ProductAPI getProductById (@PathParam("productId") Long productId) {
        ProductAPI productAPI = null;
        List<Products> productList;
        
        ProductAPIList productAPIList = new ProductAPIList ();
        
        productList = javaServiceFacade.getProductsFindAll();
        for ( int i = 0; productList != null && i < productList.size(); i++ ) {
            if (productList.get(i).getProductId().equals(productId)) {
                productAPI = new ProductAPI ();
                productAPI.updateWithProductBean(productList.get(i));
                i = productList.size();
            }
        }
        
        return productAPI;
    }
    
    @GET
    @Path("/getProductByIdJSON/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductAPI getProductsByIdJSON (@PathParam("productId") Long productId) {
        return getProductById (productId);
    }

    @GET
    @Path("/getProductsByCategoryId/{categoryId}")
    @Produces(MediaType.APPLICATION_XML)
    public ProductAPIList getProductsByCategoryId(@PathParam("categoryId") Long categoryId) {
        List<Products> productList;
        
        ProductAPIList productAPIList = new ProductAPIList ();
        
        productList = javaServiceFacade.getProductsFindAll();
        for ( int i = 0; productList != null && i < productList.size(); i++ ) {
            if (productList.get(i).getCategoryId().equals(categoryId)) {
                ProductAPI productAPI = new ProductAPI ();
                productAPI.updateWithProductBean(productList.get(i));
                productAPIList.getProducts().add(productAPI);
            }
        }
        
        return productAPIList;
    }

    @GET
    @Path("/getProductsByCategoryIdJSON/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductAPIList getProductsByCategoryIdJSON(@PathParam("categoryId") Long categoryId) {
        return getProductsByCategoryId(categoryId);
    }
    
    @GET
    @Path("/getProductCategories")
    @Produces(MediaType.APPLICATION_XML)
    public ProductCategoryAPIList getProductCategories() {
        List<ProductCategories> productCategoryList;
        
        ProductCategoryAPIList productCategoryAPIList = new ProductCategoryAPIList ();
        
        productCategoryList = javaServiceFacade.getProductCategoriesFindAll();
        for ( int i = 0; productCategoryList != null && i < productCategoryList.size(); i++ ) {
            ProductCategoryAPI productCategoryAPI = new ProductCategoryAPI ();
            productCategoryAPI.updateWithProductCategoryBean(productCategoryList.get(i));
            productCategoryAPIList.getProductCategoryAPI().add(productCategoryAPI);
        }
        
        return productCategoryAPIList;
    }

    @GET
    @Path("/getProductCategoriesJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductCategoryAPIList getProductCategoriesJSON() {
        return getProductCategories();
    }

    @GET
    @Path("/getProductCategories/{level}")
    @Produces(MediaType.APPLICATION_XML)
    public ProductCategoryAPIList getProductCategories(@PathParam("level") Long level) {
        List<ProductCategories> productCategoryList;
        
        ProductCategoryAPIList productCategoryAPIList = new ProductCategoryAPIList ();
        
        productCategoryList = javaServiceFacade.getProductCategoriesFindAll();
        for ( int i = 0; productCategoryList != null && i < productCategoryList.size(); i++ ) {
            if (productCategoryList.get(i).getCategoryLevel().equals(level)) {
                ProductCategoryAPI productCategoryAPI = new ProductCategoryAPI ();
                productCategoryAPI.updateWithProductCategoryBean(productCategoryList.get(i));
                productCategoryAPIList.getProductCategoryAPI().add(productCategoryAPI);
            }
        }
        
        return productCategoryAPIList;
    }

    @GET
    @Path("/getProductCategoriesJSON/{level}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductCategoryAPIList getProductCategoriesJSON(@PathParam("level") Long level) {
        return getProductCategories(level);
    }
}
