package helloworld;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "HelloWorld", urlPatterns = { "/helloworld" })
public class HelloWorld extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=US-ASCII";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>HelloWorld</title></head>");
        out.println("<body>");
        out.println("<p><br>This simple Hello World is running on my <b>Laptop</b>.</p>");
        out.println("</body></html>");
        out.close();
    }
}
