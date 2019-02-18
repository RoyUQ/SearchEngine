import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Server file for the web interface
 */
public class JqueryAjaxServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        String result = "111";
        try {
            SearchEngine searchEngine = new SearchEngine();
            result = searchEngine.searchResult(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String personJSON = "{\"result" + "\":\"" + result + "\"}";
        response.getWriter().write(personJSON);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);

    }
}
