package oracle.alpha;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class JavaServiceFacade {
    private final EntityManager em;

    public JavaServiceFacade() {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlphaOfficeTables");
        em = emf.createEntityManager();
    }

    /**
     * All changes that have been made to the managed entities in the
     * persistence context are applied to the database and committed.
     */
    public void commitTransaction() {
        final EntityTransaction entityTransaction = em.getTransaction();
        if (!entityTransaction.isActive()) {
            entityTransaction.begin();
        }
        entityTransaction.commit();
    }

    public Object queryByRange(String jpqlStmt, int firstResult, int maxResults) {
        Query query = em.createQuery(jpqlStmt);
        if (firstResult > 0) {
            query = query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query = query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public <T> T persistEntity(T entity) {
        em.persist(entity);
        commitTransaction();
        return entity;
    }

    public <T> T mergeEntity(T entity) {
        entity = em.merge(entity);
        commitTransaction();
        return entity;
    }

    public SalesOrderHeader persistSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        em.persist(salesOrderHeader);
        commitTransaction();
        return salesOrderHeader;
    }

    public SalesOrderHeader mergeSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        SalesOrderHeader entity = null;
        entity = em.merge(salesOrderHeader);
        commitTransaction();
        return entity;
    }

    public void removeSalesOrderHeader(SalesOrderHeader salesOrderHeader) {
        salesOrderHeader = em.find(SalesOrderHeader.class, salesOrderHeader.getSalesOrderId());
        em.remove(salesOrderHeader);
        commitTransaction();
    }

    /** <code>select o from SalesOrderHeader o</code> */
    public List<SalesOrderHeader> getSalesOrderHeaderFindAll() {
        return em.createNamedQuery("SalesOrderHeader.findAll", SalesOrderHeader.class).getResultList();
    }

    public ProductCategories persistProductCategories(ProductCategories productCategories) {
        em.persist(productCategories);
        commitTransaction();
        return productCategories;
    }

    public ProductCategories mergeProductCategories(ProductCategories productCategories) {
        ProductCategories entity = null;
        entity = em.merge(productCategories);
        commitTransaction();
        return entity;
    }

    public void removeProductCategories(ProductCategories productCategories) {
        productCategories = em.find(ProductCategories.class, productCategories.getCategoryId());
        em.remove(productCategories);
        commitTransaction();
    }

    /** <code>select o from ProductCategories o</code> */
    public List<ProductCategories> getProductCategoriesFindAll() {
        return em.createNamedQuery("ProductCategories.findAll", ProductCategories.class).getResultList();
    }

    public CustomerIdentifications persistCustomerIdentifications(CustomerIdentifications customerIdentifications) {
        em.persist(customerIdentifications);
        commitTransaction();
        return customerIdentifications;
    }

    public CustomerIdentifications mergeCustomerIdentifications(CustomerIdentifications customerIdentifications) {
        CustomerIdentifications entity = null;
        entity = em.merge(customerIdentifications);
        commitTransaction();
        return entity;
    }

    public void removeCustomerIdentifications(CustomerIdentifications customerIdentifications) {
        customerIdentifications = em.find(CustomerIdentifications.class, customerIdentifications.getCustomerId());
        em.remove(customerIdentifications);
        commitTransaction();
    }

    /** <code>select o from CustomerIdentifications o</code> */
    public List<CustomerIdentifications> getCustomerIdentificationsFindAll() {
        return em.createNamedQuery("CustomerIdentifications.findAll", CustomerIdentifications.class).getResultList();
    }

    public Customers persistCustomers(Customers customers) {
        em.persist(customers);
        commitTransaction();
        return customers;
    }

    public Customers mergeCustomers(Customers customers) {
        Customers entity = null;
        entity = em.merge(customers);
        commitTransaction();
        return entity;
    }

    public void removeCustomers(Customers customers) {
        customers = em.find(Customers.class, customers.getCustomerId());
        em.remove(customers);
        commitTransaction();
    }

    /** <code>select o from Customers o</code> */
    public List<Customers> getCustomersFindAll() {
        return em.createNamedQuery("Customers.findAll", Customers.class).getResultList();
    }

    public OrderItems persistOrderItems(OrderItems orderItems) {
        em.persist(orderItems);
        commitTransaction();
        return orderItems;
    }

    public OrderItems mergeOrderItems(OrderItems orderItems) {
        OrderItems entity = null;
        entity = em.merge(orderItems);
        commitTransaction();
        return entity;
    }

    public void removeOrderItems(OrderItems orderItems) {
        orderItems =
            em.find(OrderItems.class,
                    new OrderItemsPK(orderItems.getLineItemId(), orderItems.getOrders().getOrderId()));
        em.remove(orderItems);
        commitTransaction();
    }

    /** <code>select o from OrderItems o</code> */
    public List<OrderItems> getOrderItemsFindAll() {
        return em.createNamedQuery("OrderItems.findAll", OrderItems.class).getResultList();
    }

    public CustomerInterests persistCustomerInterests(CustomerInterests customerInterests) {
        em.persist(customerInterests);
        commitTransaction();
        return customerInterests;
    }

    public CustomerInterests mergeCustomerInterests(CustomerInterests customerInterests) {
        CustomerInterests entity = null;
        entity = em.merge(customerInterests);
        commitTransaction();
        return entity;
    }

    public void removeCustomerInterests(CustomerInterests customerInterests) {
        customerInterests = em.find(CustomerInterests.class, customerInterests.getCustomerInterestsId());
        em.remove(customerInterests);
        commitTransaction();
    }

    /** <code>select o from CustomerInterests o</code> */
    public List<CustomerInterests> getCustomerInterestsFindAll() {
        return em.createNamedQuery("CustomerInterests.findAll", CustomerInterests.class).getResultList();
    }

    public Addresses persistAddresses(Addresses addresses) {
        em.persist(addresses);
        commitTransaction();
        return addresses;
    }

    public Addresses mergeAddresses(Addresses addresses) {
        Addresses entity = null;
        entity = em.merge(addresses);
        commitTransaction();
        return entity;
    }

    public void removeAddresses(Addresses addresses) {
        addresses = em.find(Addresses.class, addresses.getAddressId());
        em.remove(addresses);
        commitTransaction();
    }

    /** <code>select o from Addresses o</code> */
    public List<Addresses> getAddressesFindAll() {
        return em.createNamedQuery("Addresses.findAll", Addresses.class).getResultList();
    }

    public Discounts persistDiscounts(Discounts discounts) {
        em.persist(discounts);
        commitTransaction();
        return discounts;
    }

    public Discounts mergeDiscounts(Discounts discounts) {
        Discounts entity = null;
        entity = em.merge(discounts);
        commitTransaction();
        return entity;
    }

    public void removeDiscounts(Discounts discounts) {
        discounts = em.find(Discounts.class, discounts.getDiscountId());
        em.remove(discounts);
        commitTransaction();
    }

    /** <code>select o from Discounts o</code> */
    public List<Discounts> getDiscountsFindAll() {
        return em.createNamedQuery("Discounts.findAll", Discounts.class).getResultList();
    }

    public CouponUsages persistCouponUsages(CouponUsages couponUsages) {
        em.persist(couponUsages);
        commitTransaction();
        return couponUsages;
    }

    public CouponUsages mergeCouponUsages(CouponUsages couponUsages) {
        CouponUsages entity = null;
        entity = em.merge(couponUsages);
        commitTransaction();
        return entity;
    }

    public void removeCouponUsages(CouponUsages couponUsages) {
        couponUsages =
            em.find(CouponUsages.class,
                    new CouponUsagesPK(couponUsages.getAppliedDate(), couponUsages.getDiscounts().getDiscountId(),
                                       couponUsages.getCustomers2().getCustomerId()));
        em.remove(couponUsages);
        commitTransaction();
    }

    /** <code>select o from CouponUsages o</code> */
    public List<CouponUsages> getCouponUsagesFindAll() {
        return em.createNamedQuery("CouponUsages.findAll", CouponUsages.class).getResultList();
    }

    public AddressUsages persistAddressUsages(AddressUsages addressUsages) {
        em.persist(addressUsages);
        commitTransaction();
        return addressUsages;
    }

    public AddressUsages mergeAddressUsages(AddressUsages addressUsages) {
        AddressUsages entity = null;
        entity = em.merge(addressUsages);
        commitTransaction();
        return entity;
    }

    public void removeAddressUsages(AddressUsages addressUsages) {
        addressUsages = em.find(AddressUsages.class, addressUsages.getAddressUsagesId());
        em.remove(addressUsages);
        commitTransaction();
    }

    /** <code>select o from AddressUsages o</code> */
    public List<AddressUsages> getAddressUsagesFindAll() {
        return em.createNamedQuery("AddressUsages.findAll", AddressUsages.class).getResultList();
    }

    public Orders persistOrders(Orders orders) {
        em.persist(orders);
        commitTransaction();
        return orders;
    }

    public Orders mergeOrders(Orders orders) {
        Orders entity = null;
        entity = em.merge(orders);
        commitTransaction();
        return entity;
    }

    public void removeOrders(Orders orders) {
        orders = em.find(Orders.class, orders.getOrderId());
        em.remove(orders);
        commitTransaction();
    }

    /** <code>select o from Orders o</code> */
    public List<Orders> getOrdersFindAll() {
        return em.createNamedQuery("Orders.findAll", Orders.class).getResultList();
    }

    public ProductStockLevels persistProductStockLevels(ProductStockLevels productStockLevels) {
        em.persist(productStockLevels);
        commitTransaction();
        return productStockLevels;
    }

    public ProductStockLevels mergeProductStockLevels(ProductStockLevels productStockLevels) {
        ProductStockLevels entity = null;
        entity = em.merge(productStockLevels);
        commitTransaction();
        return entity;
    }

    public void removeProductStockLevels(ProductStockLevels productStockLevels) {
        productStockLevels = em.find(ProductStockLevels.class, productStockLevels.getProductId());
        em.remove(productStockLevels);
        commitTransaction();
    }

    /** <code>select o from ProductStockLevels o</code> */
    public List<ProductStockLevels> getProductStockLevelsFindAll() {
        return em.createNamedQuery("ProductStockLevels.findAll", ProductStockLevels.class).getResultList();
    }

    public SalesOrderLineItem persistSalesOrderLineItem(SalesOrderLineItem salesOrderLineItem) {
        em.persist(salesOrderLineItem);
        commitTransaction();
        return salesOrderLineItem;
    }

    public SalesOrderLineItem mergeSalesOrderLineItem(SalesOrderLineItem salesOrderLineItem) {
        SalesOrderLineItem entity = null;
        entity = em.merge(salesOrderLineItem);
        commitTransaction();
        return entity;
    }

    public void removeSalesOrderLineItem(SalesOrderLineItem salesOrderLineItem) {
        salesOrderLineItem = em.find(SalesOrderLineItem.class, salesOrderLineItem.getSalesOrderLineItemId());
        em.remove(salesOrderLineItem);
        commitTransaction();
    }

    /** <code>select o from SalesOrderLineItem o</code> */
    public List<SalesOrderLineItem> getSalesOrderLineItemFindAll() {
        return em.createNamedQuery("SalesOrderLineItem.findAll", SalesOrderLineItem.class).getResultList();
    }

    public PaymentOptions persistPaymentOptions(PaymentOptions paymentOptions) {
        em.persist(paymentOptions);
        commitTransaction();
        return paymentOptions;
    }

    public PaymentOptions mergePaymentOptions(PaymentOptions paymentOptions) {
        PaymentOptions entity = null;
        entity = em.merge(paymentOptions);
        commitTransaction();
        return entity;
    }

    public void removePaymentOptions(PaymentOptions paymentOptions) {
        paymentOptions = em.find(PaymentOptions.class, paymentOptions.getPaymentOptionId());
        em.remove(paymentOptions);
        commitTransaction();
    }

    /** <code>select o from PaymentOptions o</code> */
    public List<PaymentOptions> getPaymentOptionsFindAll() {
        return em.createNamedQuery("PaymentOptions.findAll", PaymentOptions.class).getResultList();
    }

    public Products persistProducts(Products products) {
        em.persist(products);
        commitTransaction();
        return products;
    }

    public Products mergeProducts(Products products) {
        Products entity = null;
        entity = em.merge(products);
        commitTransaction();
        return entity;
    }

    public void removeProducts(Products products) {
        products = em.find(Products.class, products.getProductId());
        em.remove(products);
        commitTransaction();
    }

    /** <code>select o from Products o</code> */
    public List<Products> getProductsFindAll() {
        return em.createNamedQuery("Products.findAll", Products.class).getResultList();
    }

    /** <code>select o from Products o WHERE o.productId = :prodId</code> */
    public List<Products> getProductsGetProductById(Long prodId) {
        return em.createNamedQuery("Products.getProductById", Products.class).setParameter("prodId",
                                                                                           prodId).getResultList();
    }
}
