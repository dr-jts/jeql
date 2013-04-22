package jeql.command.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jeql.api.error.ExecutionException;
import jeql.command.db.driver.JdbcUtil;
import jeql.engine.Scope;

public class DbExec 
extends DbCommandBase 
{
	protected String sql;
  private Connection conn = null;
  
	public DbExec()
	{
		
	}
	
  public void setSql(String sql)
  {
    this.sql = sql;
  }
  
	public void execute(Scope scope) throws Exception 
	{
    conn = JdbcUtil.createConnection(jdbcDriver, url, user, password);
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.execute(sql);
    }
    catch (SQLException ex) {
      throw new ExecutionException(ex.getMessage());
    }
    finally {
      if (stmt != null) stmt.close();
    }
	}
	
}
