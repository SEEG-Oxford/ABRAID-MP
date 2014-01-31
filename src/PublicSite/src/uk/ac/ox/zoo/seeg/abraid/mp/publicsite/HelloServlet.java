package uk.ac.ox.zoo.seeg.abraid.mp.publicsite;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class HelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HashMap<String, NavBarItem> navBarItems = new HashMap<>();
        navBarItems.put("/",new NavBarItem("Home","index"));
        navBarItems.put("/datavalidation", new NavBarItem("Data Validation", "datavalidation"));
        navBarItems.put("/modeloutputs", new NavBarItem("Model Outputs","index"));
        navBarItems.put("/experts", new NavBarItem("Experts","index"));
        navBarItems.put("/publications", new NavBarItem("Publications","index"));

        request.setAttribute("navBarItems", navBarItems);

        String path = request.getRequestURI().substring(11);
        request.setAttribute("activeTab", path);

        PrintWriter out = response.getWriter();
        out.println(request.getRequestURI());

        request.getRequestDispatcher(navBarItems.get(path).getTemplate()).forward(request, response);
    }

}