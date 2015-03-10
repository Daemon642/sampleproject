package oracle.alpha;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/AlphaOfficeAccess/Student03")
public class Student03{
    public Student03() {
        super();
    }

    @GET
    @Path("/sayHello")
    @Produces(MediaType.APPLICATION_JSON)

    public String sayHello() {
        String message;
        String firstName = "John";
        String lastName = "Smith";

        message = new String (firstName + " " + lastName + " - You have deployed a REST api to the Oracle Public Cloud");
        return message;
    }
}
