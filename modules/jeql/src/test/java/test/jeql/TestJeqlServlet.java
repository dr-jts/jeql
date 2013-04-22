package test.jeql;

import java.net.URL;

import jeql.JeqlServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Example: http://localhost:8080/jeql/foo.jql
 * 
 * @author Martin Davis
 *
 */
public class TestJeqlServlet
{
  private static final int PORT = 8080;
  private static final String WEBAPPDIR = "webapp";
  private static final String CONTEXTPATH = "/jeql";

  // from Jetty example
  public static void main(String args[])
  {
    try {
      final Server server = new Server(PORT);
       
      // for whatever is in the webapp directory
      final URL warUrl = TestJeqlServlet.class.getClassLoader().getResource(WEBAPPDIR);
      final String warUrlString = warUrl.toExternalForm();
      server.setHandler(new WebAppContext(warUrlString, CONTEXTPATH));
       
      server.start();    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main2(String args[])
  {
    try {
      Server server = new Server(PORT);
      Context root = new Context(server,"/",Context.SESSIONS);
      root.addServlet(new ServletHolder(new JeqlServlet()), "/*");
      server.start();    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public static void main3(String args[])
  {
    try {
      final Server jettyServer = new Server();
    
      WebAppContext wah = new WebAppContext();
      wah.setContextPath("/jeql");
      wah.setWar("webapp");
      
      jettyServer.setHandler(wah);
      //wah.setTempDirectory(new File("target/work"));
      //this allows to send large SLD's from the styles form
      wah.getServletContext().getContextHandler().setMaxFormContentSize(1024 * 1024 * 2);


      jettyServer.start();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
}
