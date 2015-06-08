package jeql;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;

public class JeqlServlet extends HttpServlet
{
  public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
  
  private ServletContext servletContext;
        
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    this.servletContext = config.getServletContext();
    //System.out.println("init");
  }

  public void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    String uri = request.getServletPath();
    //String info = request.getPathInfo();
    System.out.println("script request: " + uri);
     
    //String currdir = request.getSession().getServletContext().getRealPath("/");
    //System.out.println(currdir);
    
    String scriptPathname = getScriptPathname(request);
    run(scriptPathname, request.getParameterMap());
    
    //testResponse(response);

  }

  /**
   * Parses the http request for the real script or template source file.
   * 
   * @param request
   *          the http request to analyze
   * @return a file object using an absolute file path name
   */
  protected String getScriptPathname(HttpServletRequest request)
  {
    String uri = getScriptUri(request);
    String real = servletContext.getRealPath(uri);
    return real;
  }
         
  private String getScriptUri(HttpServletRequest request)
  {
    return request.getServletPath();
  }

  public void run(String scriptFile, Map parameters)
  {
    JeqlRunner runner = new JeqlRunner();
    JeqlOptions options = new JeqlOptions();
    runner.init(options);
    //TODO: fix exception handling
    boolean returnCode = false;
    try {
      returnCode = runner.execScriptFile(scriptFile, null);
      //returnCode = runScript(runner);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void testResponse(HttpServletResponse response) throws IOException
  {
    response.setContentType(CONTENT_TYPE_TEXT_HTML);
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println("<h1>Hello</h1>");
  }

  public void destroy()
  {
    // TODO Auto-generated method stub

  }

  public ServletConfig getServletConfig()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String getServletInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
