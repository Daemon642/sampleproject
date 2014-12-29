package oracle.alpha;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/AlphaOfficeAccess/Customers")
public class CustomerAccess {
    final JavaServiceFacade javaServiceFacade;

    public CustomerAccess() {
        super();
        javaServiceFacade = new JavaServiceFacade();
    }
    
    @GET
    @Path("/getCustomers")
    @Produces(MediaType.APPLICATION_XML)
    public CustomerAPIList getCustomers() {
        List<Customers> customerList;
        
        CustomerAPIList customerAPIList = new CustomerAPIList ();
        
        customerList = javaServiceFacade.getCustomersFindAll();
        for ( int i = 0; customerList != null && i < customerList.size(); i++ ) {
            CustomerAPI customerAPI = new CustomerAPI ();
            customerAPI.updateWithCustomerBean(customerList.get(i));
            customerAPIList.getCustomerAPI().add(customerAPI);
        }
        
        return customerAPIList;
    }

    @GET
    @Path("/getCustomersJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerAPIList getCustomersJSON() {
        return getCustomers();
    }    

    @GET
    @Path("/getCustomerById/{customerId}")
    @Produces(MediaType.APPLICATION_XML)
    public CustomerAPI getCustomerById (@PathParam("customerId") Long customerId) {
        CustomerAPI customerAPI = null;
        List<Customers> customerList;
        
        customerList = javaServiceFacade.getCustomersFindAll();
        for ( int i = 0; customerList != null && i < customerList.size(); i++ ) {
            if (customerList.get(i).getCustomerId().equals(customerId)) {
                customerAPI = new CustomerAPI ();
                customerAPI.updateWithCustomerBean(customerList.get(i));
                i = customerList.size();
            }
        }
        
        return customerAPI;
    }
    
    @GET
    @Path("/getCustomerByIdJSON/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerAPI getCustomerByIdJSON (@PathParam("customerId") Long customerId) {
        return getCustomerById (customerId);
    }
    
    @GET
    @Path("/createCustomer/{principalName}/{firstName}/{lastName}/{phoneNumber}")
    @Produces(MediaType.APPLICATION_XML)
    public CustomerAPI createCustomer (@PathParam("principalName") String principalName, 
                                       @PathParam("firstName") String firstName, 
                                       @PathParam("lastName") String lastName,
                                       @PathParam("phoneNumber") String phoneNumber) {
        Customers customer;
        CustomerAPI customerAPI;
        
        customer = new Customers ();
        customer.setCustomerId(dbUtilities.getDatabaseId ("CUSTOMER_SEQ"));
        customer.setPrincipalName(principalName);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(firstName + "." + lastName + "@alpha.com");
        customer.setCustomerTypeCode("CUST");
        customer.setMaritalStatusCode("Y");
        customer.setGender("M");
        customer.setContactableFlag("Y");
        customer.setContactByAffilliatesFlag("Y");
        customer.setCreatedBy("alpha");
        customer.setCreationDate(new Date());
        customer.setLastUpdatedBy("alpha");
        customer.setLastUpdateDate(new Date());
        customer.setObjectVersionId(new Long(1));
        
        customer = javaServiceFacade.persistCustomers(customer);
        
        customerAPI = new CustomerAPI ();
        customerAPI.updateWithCustomerBean(customer);
        
        return customerAPI;
    }
    
    @GET
    @Path("/createCustomerJSON/{principalName}/{firstName}/{lastName}/{phoneNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerAPI createCustomerJSON (@PathParam("principalName") String principalName, 
                                           @PathParam("firstName") String firstName, 
                                           @PathParam("lastName") String lastName,
                                           @PathParam("phoneNumber") String phoneNumber) {
        CustomerAPI customerAPI;
        
        customerAPI = createCustomer (principalName, firstName, lastName, phoneNumber);
                
        return customerAPI;
    }
    
    @GET
    @Path("/assignAddress/{customerId}/{addressId}")
    @Produces(MediaType.APPLICATION_XML)
    public CustomerAPI assignAddress (@PathParam("customerId") Long customerId,
                                      @PathParam("addressId") Long addressId) {
        CustomerAPI customerAPI = null;
        List<Customers> customerList;
        Customers customer = null;
        List<Addresses> addressList;
        
        customerList = javaServiceFacade.getCustomersFindAll();
        for ( int i = 0; customerList != null && i < customerList.size(); i++ ) {
            if (customerList.get(i).getCustomerId().equals(customerId)) {
                customer = customerList.get(i);
                customerAPI = new CustomerAPI ();
                i = customerList.size();
            }
        }
        addressList = javaServiceFacade.getAddressesFindAll();
        for ( int i = 0; addressList != null && i < addressList.size(); i++ ) {
            if (addressList.get(i).getAddressId().equals(addressId)) {
                customer.setAddresses2(addressList.get(i));
                i = addressList.size();
            }
        }
        customer = javaServiceFacade.mergeCustomers(customer);
        customerAPI.updateWithCustomerBean(customer);
        
        return customerAPI;
    }
    
    @GET
    @Path("/assignAddressJSON/{customerId}/{addressId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerAPI assignAddressJSON (@PathParam("customerId") Long customerId,
                                          @PathParam("addressId") Long addressId) {
        return assignAddress(customerId, addressId);
    }
    
    @GET
    @Path("/getAddresses")
    @Produces(MediaType.APPLICATION_XML)
    public AddressAPIList getAddresses() {
        List<Addresses> addressList;
        
        AddressAPIList addressAPIList = new AddressAPIList ();
        
        addressList = javaServiceFacade.getAddressesFindAll();
        for ( int i = 0; addressList != null && i < addressList.size(); i++ ) {
            AddressAPI addressAPI = new AddressAPI ();
            addressAPI.updateWithAddressBean(addressList.get(i));
            addressAPIList.getAddressAPI().add(addressAPI);
        }
        
        return addressAPIList;
    }

    @GET
    @Path("/getAddressesJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public AddressAPIList getAddressesJSON() {
        return getAddresses();
    }    

    @GET
    @Path("/getAddressById/{addressId}")
    @Produces(MediaType.APPLICATION_XML)
    public AddressAPI getAddressById (@PathParam("addressId") Long addressId) {
        AddressAPI addressAPI = null;
        List<Addresses> addressList;
        
        AddressAPIList addressAPIList = new AddressAPIList ();
        
        addressList = javaServiceFacade.getAddressesFindAll();
        for ( int i = 0; addressList != null && i < addressList.size(); i++ ) {
            if (addressList.get(i).getAddressId().equals(addressId)) {
                addressAPI = new AddressAPI ();
                addressAPI.updateWithAddressBean(addressList.get(i));
                i = addressList.size();
            }
        }
        
        return addressAPI;
    }

    @GET
    @Path("/getAddressByIdJSON/{addressId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AddressAPI getAddressByIdJSON (@PathParam("addressId") Long addressId) {
        return getAddressById (addressId);
    }
    
    @GET
    @Path("/createAddress/{address1}/{address2}/{city}/{countryId}/{postalCode}/{stateProvince}")
    @Produces(MediaType.APPLICATION_XML)
    public AddressAPI createAddress (@PathParam("address1") String address1, 
                                     @PathParam("address2") String address2, 
                                     @PathParam("city") String city,
                                     @PathParam("countryId") String countryId,
                                     @PathParam("postalCode") String postalCode,
                                     @PathParam("stateProvince") String stateProvince) {
        Addresses address;
        AddressAPI addressAPI;
        
        address = new Addresses ();
        address.setAddressId(dbUtilities.getDatabaseId ("ADDRESS_SEQ"));
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setCountryId(countryId);
        address.setPostalCode(postalCode);
        address.setStateProvince(stateProvince);
        address.setCreatedBy("alpha");
        address.setCreationDate(new Date());
        address.setLastUpdatedBy("alpha");
        address.setLastUpdateDate(new Date());
        address.setObjectVersionId(new Long(1));
        
        address = javaServiceFacade.persistAddresses(address);
        
        addressAPI = new AddressAPI ();
        addressAPI.updateWithAddressBean(address);
        
        return addressAPI;
    }
    
    @GET
    @Path("/createAddressJSON/{address1}/{address2}/{city}/{countryId}/{postalCode}/{stateProvince}")
    @Produces(MediaType.APPLICATION_JSON)
    public AddressAPI createAddressJSON (@PathParam("address1") String address1, 
                                         @PathParam("address2") String address2, 
                                         @PathParam("city") String city,
                                         @PathParam("countryId") String countryId,
                                         @PathParam("postalCode") String postalCode,
                                         @PathParam("stateProvince") String stateProvince) {
        AddressAPI addressAPI;
        
        addressAPI = createAddress (address1, address2, city, countryId, postalCode, stateProvince);
                
        return addressAPI;
    }
}
