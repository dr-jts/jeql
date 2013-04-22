package jeql.command.db;

import jeql.api.table.Table;
import jeql.command.db.driver.JdbcRowMapper;
import jeql.command.db.driver.RowMapper;
import jeql.engine.Scope;

public class DbReader 
extends DbCommandBase
{
	protected String sql;
  protected int fetchSize = DbRowList.DEFAULT_FETCH_SIZE;
	protected Table result;
	
	public DbReader()
	{
		
	}
	
  public void setSql(String sql)
  {
    this.sql = sql;
  }
  
  public void setTable(String tbl)
  {
    this.sql = "SELECT * FROM " + tbl + " ;";
  }
  
  public void setFetchSize(int fetchSize)
  {
    this.fetchSize = fetchSize;
  }
  
	public Table getDefault()
	{
		return result;
	}
	
	public void execute(Scope scope) throws Exception 
	{
		executeQuery(new JdbcRowMapper());
	}
	
	protected void executeQuery(RowMapper rowMapper)
	{
		result = new Table(new DbRowList(jdbcDriver, url, user, password, sql, fetchSize, rowMapper));
	}
}
