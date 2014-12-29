package oracle.alpha;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oracle.alpha.CustomerAccess;
import oracle.alpha.ProductAccess;

@Path("/AlphaOfficeAccess/Orders")
public class OrderAccess {
    final JavaServiceFacade javaServiceFacade;

    public OrderAccess() {
        super();
        javaServiceFacade = new JavaServiceFacade();
    }

    @GET
    @Path("/getOrders")
    @Produces(MediaType.APPLICATION_XML)
    public OrderAPIList getOrders() {
        List<Orders> orderList;
        
        OrderAPIList orderAPIList = new OrderAPIList ();
        
        orderList = javaServiceFacade.getOrdersFindAll();
        for ( int i = 0; orderList != null && i < orderList.size(); i++ ) {
            OrderAPI orderAPI = new OrderAPI ();
            orderAPI.updateWithOrderBean(orderList.get(i));
            orderAPIList.getOrderAPI().add(orderAPI);
        }
        
        return orderAPIList;
    }

    @GET
    @Path("/getOrdersJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderAPIList getOrdersJSON() {
        return getOrders();
    }    

    @GET
    @Path("/getOrderItems")
    @Produces(MediaType.APPLICATION_XML)
    public OrderItemAPIList getOrderItems() {
        List<OrderItems> orderItemList;
        
        OrderItemAPIList orderItemAPIList = new OrderItemAPIList ();
        
        orderItemList = javaServiceFacade.getOrderItemsFindAll();
        for ( int i = 0; orderItemList != null && i < orderItemList.size(); i++ ) {
            OrderItemAPI orderItemAPI = new OrderItemAPI ();
            orderItemAPI.updateWithOrderItemBean(orderItemList.get(i));
            orderItemAPIList.getOrderItemAPI().add(orderItemAPI);
        }
        
        return orderItemAPIList;
    }

    @GET
    @Path("/getOrderItemsJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderItemAPIList getOrderItemsJSON() {
        return getOrderItems();
    }    

    @GET
    @Path("/getOrderItems/{orderId}")
    @Produces(MediaType.APPLICATION_XML)
    public OrderItemAPIList getOrderItems(@PathParam("orderId") Long orderId) {
        List<OrderItems> orderItemList;
        
        OrderItemAPIList orderItemAPIList = new OrderItemAPIList ();
        
        orderItemList = javaServiceFacade.getOrderItemsFindAll();
        for ( int i = 0; orderItemList != null && i < orderItemList.size(); i++ ) {
            if (orderItemList.get(i).getOrders().getOrderId().equals(orderId)) {
                OrderItemAPI orderItemAPI = new OrderItemAPI ();
                orderItemAPI.updateWithOrderItemBean(orderItemList.get(i));
                orderItemAPIList.getOrderItemAPI().add(orderItemAPI);
            }
        }
        
        return orderItemAPIList;
    }

    @GET
    @Path("/getOrderItemsJSON/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderItemAPIList getOrderItemsJSON(@PathParam("orderId") Long orderId) {
        return getOrderItems (orderId);
    }
    
    @GET
    @Path("/getOrdersByCustomerId/{customerId}")
    @Produces(MediaType.APPLICATION_XML)
    public OrderAPIList getOrdersByCustomerId(@PathParam("customerId") Long customerId) {
        List<Orders> orderList;
        
        OrderAPIList orderAPIList = new OrderAPIList ();
        
        orderList = javaServiceFacade.getOrdersFindAll();
        for ( int i = 0; orderList != null && i < orderList.size(); i++ ) {
            if (orderList.get(i).getCustomers5().getCustomerId().equals(customerId)) {
                OrderAPI orderAPI = new OrderAPI ();
                orderAPI.updateWithOrderBean(orderList.get(i));
                orderAPIList.getOrderAPI().add(orderAPI);
            }
        }
        
        return orderAPIList;
    }

    @GET
    @Path("/getOrdersByCustomerIdJSON/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderAPIList getOrdersByCustomerIdJSON(@PathParam("customerId") Long customerId) {
        return getOrdersByCustomerId (customerId);
    }
    
    @GET
    @Path("/createOrder/{customerId}/{addressId}/{orderStatusCode}/{orderTotal}")
    @Produces(MediaType.APPLICATION_XML)
    public OrderAPI createOrder (@PathParam("customerId") Long customerId,
                                 @PathParam("addressId") Long addressId,
                                 @PathParam("orderStatusCode") String orderStatusCode, 
                                 @PathParam("orderTotal") Double orderTotal) {
        Orders order;
        OrderAPI orderAPI;
        List<Customers> customerList;
        List<Addresses> addressList;
        
        order = new Orders ();
        order.setOrderId(dbUtilities.getDatabaseId ("ORDER_SEQ"));
        order.setOrderStatusCode(orderStatusCode);
        order.setOrderTotal(orderTotal);
        customerList = javaServiceFacade.getCustomersFindAll();
        for ( int i = 0; customerList != null && i < customerList.size(); i++ ) {
            if (customerList.get(i).getCustomerId().equals(customerId)) {
                order.setCustomers5(customerList.get(i));
                i = customerList.size();
            }
        }
        addressList = javaServiceFacade.getAddressesFindAll();
        for ( int i = 0; addressList != null && i < addressList.size(); i++ ) {
            if (addressList.get(i).getAddressId().equals(addressId)) {
                order.setAddresses1(addressList.get(i));
                i = addressList.size();
            }
        }
        order.setCreatedBy("alpha");
        order.setOrderDate(new Date());
        order.setCustomerCollectFlag("N");
        order.setGiftwrapFlag("N");
        order.setCreationDate(new Date());
        order.setLastUpdatedBy("alpha");
        order.setLastUpdateDate(new Date());
        order.setObjectVersionId(new Long(1));

        order = javaServiceFacade.persistOrders(order);
        
        orderAPI = new OrderAPI ();
        orderAPI.updateWithOrderBean(order);
        
        return orderAPI;
    }

    @GET
    @Path("/createOrderJSON/{customerId}/{addressId}/{orderStatusCode}/{orderTotal}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderAPI createOrderJSON (@PathParam("customerId") Long customerId,
                                     @PathParam("addressId") Long addressId,
                                     @PathParam("orderStatusCode") String orderStatusCode, 
                                     @PathParam("orderTotal") Double orderTotal) {
        return createOrder (customerId, addressId, orderStatusCode, orderTotal);
    }
    
    @GET
    @Path("/createOrderItem/{orderId}/{productId}/{quantity}/{unitPrice}")
    @Produces(MediaType.APPLICATION_XML)
    public OrderItemAPI createOrderItem (@PathParam("orderId") Long orderId,
                                         @PathParam("productId") Long productId,
                                         @PathParam("quantity") Integer quantity, 
                                         @PathParam("unitPrice") Double unitPrice) {
        OrderItems orderItem;
        OrderItemAPI orderItemAPI;
        List<Products> productsList;
        List<Orders> orderList;
        
        orderItem = new OrderItems ();
        orderItem.setLineItemId(dbUtilities.getDatabaseId ("ORDER_SEQ"));
        orderList = javaServiceFacade.getOrdersFindAll();
        for ( int i = 0; orderList != null && i < orderList.size(); i++ ) {
            if (orderList.get(i).getOrderId().equals(orderId)) {
                orderItem.setOrders(orderList.get(i));
                i = orderList.size();
            }
        }
        productsList = javaServiceFacade.getProductsFindAll();
        for ( int i = 0; productsList != null && i < productsList.size(); i++ ) {
            if (productsList.get(i).getProductId().equals(productId)) {
                orderItem.setProducts1(productsList.get(i));
                i = productsList.size();
            }
        }
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setCreatedBy("alpha");
        orderItem.setCreationDate(new Date());
        orderItem.setLastUpdatedBy("alpha");
        orderItem.setLastUpdateDate(new Date());
        orderItem.setObjectVersionId(new Long(1));

        orderItem = javaServiceFacade.persistOrderItems(orderItem);
        
        orderItemAPI = new OrderItemAPI ();
        orderItemAPI.updateWithOrderItemBean(orderItem);
        
        return orderItemAPI;
    }
    
    @GET
    @Path("/createOrderItemJSON/{orderId}/{productId}/{quantity}/{unitPrice}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderItemAPI createOrderItemJSON (@PathParam("orderId") Long orderId,
                                             @PathParam("productId") Long productId,
                                             @PathParam("quantity") Integer quantity, 
                                             @PathParam("unitPrice") Double unitPrice) {
        return createOrderItem (orderId, productId, quantity, unitPrice);
    }
}
