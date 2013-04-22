package jeql.command.db.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jeql.api.error.DbException;
import jeql.api.error.ExecutionException;

public class JdbcUtil 
{

  public static Connection createConnection(String driverClassName, String url, String user, String password) {
    Connection conn = null;
    try {
    	Class.forName(driverClassName).newInstance();
    }
    catch (Exception ex){
       throw new IllegalStateException("Cannot load DB driver: " + driverClassName
      		 + ". Possible cause: driver not in classpath." );
    }
    try {
      conn = DriverManager.getConnection(url, user, password);
    }
    catch (SQLException e){
    	throw new DbException("Cannot connect to DB URL: " + url, e);
    }
    return conn;
  }


  
}
