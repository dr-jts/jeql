package jeql.command.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jeql.api.error.ExecutionException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.command.db.driver.JdbcUtil;
import jeql.command.db.driver.RowMapper;

public class DbRowList
 implements RowList
{
	public static final int DEFAULT_FETCH_SIZE = 1000;
	
	private String driverClassName;
	private String connectString;
	private String user;
	private String password;
	private RowMapper rowMapper;
	private String sql;
	private int fetchSize = 1;
  
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private RowSchema schema;
	
	private boolean isRead = false;

	public DbRowList(String driverClassName, String connectString, 
      String user, String password,
			String sql, 
      int fetchSize,
      RowMapper rowMapper)
	{
		this.driverClassName = driverClassName;
		this.connectString = connectString;
		this.user = user;
		this.password = password;
		this.sql = sql;
    this.fetchSize = fetchSize;
		this.rowMapper = rowMapper;
		open();
	}

	private void open()
	{
		conn = JdbcUtil.createConnection(driverClassName, connectString, user, password);
		try {
			conn.setAutoCommit(false);  // this allows cursor-driven fetching (for PostGIS at least)
			stmt = conn.createStatement();
			stmt.setFetchSize(fetchSize);	
			rs = stmt.executeQuery(sql);
			schema = rowMapper.getSchema(rs);
		}
		catch (SQLException ex) {
			close();
			throw new ExecutionException(ex.getMessage());
		}
	}
	
	public RowSchema getSchema() {
		return schema;
	}

	public RowIterator iterator() 
	{
		if (isRead)
			throw new ExecutionException("Attempt to re-read streamed table");
		isRead = true;
		return new DbRowIterator(this);
	}

	//private ResultSet getRS() { return rs; }
	
	private void close() {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			// eat this exception - nothing we can do about it now
		}
		rs = null;
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException ex) {
			// eat this exception - nothing we can do about it now
		}
		stmt = null;
		try {
			if (conn != null)
				conn.close();
			conn = null;
		} catch (SQLException ex) {
			// eat this exception - nothing we can do about it now
		}
	}
	
	//=============================================

	private static class DbRowIterator 
		implements RowIterator
	{
		private DbRowList dbRL;
		private RowSchema schema = null;

		public DbRowIterator(DbRowList dbRL) {
			this.dbRL = dbRL;
			schema = dbRL.getSchema();
		}

		public RowSchema getSchema() {
			return schema;
		}

		public Row next()
		{
			if (dbRL == null) return null;
			
			Row row = null;
			try {
				if (dbRL.rs.next()) {
					row = dbRL.rowMapper.createRow(dbRL.schema, dbRL.rs);
				}
				// else drop through and close the rowlist
			}
			catch (Exception ex) {
				throw new ExecutionException(ex.getMessage());
			}
			finally {
				if (row == null) {
					dbRL.close();
					dbRL = null;
				}
			}
  		return row;
		}

	}
}
