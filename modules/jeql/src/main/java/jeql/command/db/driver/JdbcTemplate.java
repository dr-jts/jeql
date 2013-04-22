package jeql.command.db.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jeql.api.error.DbException;
import jeql.api.error.ExecutionException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;

public class JdbcTemplate 
{
	private String driverClassName;
	private String connectString;
	private String user;
	private String password;
	
	public JdbcTemplate(String driverClassName, String connectString, String user, String password)
	{
		this.driverClassName = driverClassName;
		this.connectString = connectString;
		this.user = user;
		this.password = password;
	}
	
	private Connection createConnection()
	{
		return JdbcUtil.createConnection(driverClassName, connectString, user, password);
	}
  
  public ArrayRowList OLDquery(String sql, RowMapper rowMapper)
  {
  	Connection conn = createConnection();
  	Statement stmt = null;
  	ResultSet rs = null;
  	ArrayRowList rowList = null;
  	
  	// outer try catches exceptions from close methods 
  	try {
	  	try {
	  		stmt = conn.createStatement();
	  		rs = stmt.executeQuery(sql);
	  		RowSchema schema = rowMapper.getSchema(rs);
	  		rowList = new ArrayRowList(schema);
	  		while (rs.next()) {
	  			Row row = rowMapper.createRow(schema, rs);
	  			rowList.add(row);
	  		}
	  	} 
	  	finally {
	  		if (stmt != null) stmt.close();
	  		if (rs != null) rs.close();
	    	conn.close();
	  	}
  	}
    catch (SQLException ex) {
      throw new DbException(ex.getMessage(), ex);
    }
    catch (Exception ex) {
      throw new ExecutionException(ex.getMessage());
    }
		return rowList;  	
  }
  
  
  public ArrayRowList query(final String sql, RowMapper rowMapper)
  {
  	return execute(new ConnectionOperation() {
  		Statement stmt = null;
  		public ResultSet execute(Connection conn) throws SQLException
  		{
  			stmt = conn.createStatement();
  			return stmt.executeQuery(sql);
  		}
    	public void close() throws SQLException
    	{
    		if (stmt != null) stmt.close();
    	}
  	}, rowMapper);
  }
  
  public ArrayRowList execute(ConnectionOperation connOp, RowMapper rowMapper)
  {
  	Connection conn = createConnection();
  	ResultSet rs = null;
  	ArrayRowList rowList = null;
  	// outer try catches exceptions from close methods 
  	try {
	  	try {
	  		rs = connOp.execute(conn);
	  		RowSchema schema = rowMapper.getSchema(rs);
	  		rowList = new ArrayRowList(schema);
	  		while (rs.next()) {
	  			Row row = rowMapper.createRow(schema, rs);
	  			rowList.add(row);
	  		}
	  	} 
	  	finally {
	  		if (rs != null) rs.close();
	  		connOp.close();
	    	conn.close();
	  	}
  	}
    catch (SQLException ex) {
      throw new DbException(ex.getMessage(), ex);
    }
  	catch (Exception ex) {
  		throw new ExecutionException(ex.getMessage());
  	}
		return rowList;  	
  }
  
  /*
  public MemoryRowList convert(ResultSet rs, RowMapper rowMapper)
			throws Exception {
		MemoryRowList rowList = null;
		RowSchema schema = rowMapper.getSchema(rs);
		rowList = new MemoryRowList(schema);
		while (rs.next()) {
			Row row = rowMapper.createRow(schema, rs);
			rowList.add(row);
		}
		return rowList;
	}
	*/
  
  
}
