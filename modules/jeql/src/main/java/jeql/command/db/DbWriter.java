package jeql.command.db;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import jeql.api.error.DbException;
import jeql.api.error.ExecutionException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.command.db.driver.JdbcUtil;
import jeql.engine.Scope;

/**
 * 
 * @author Martin Davis
 *
 */
public class DbWriter 
  extends DbCommandBase 
{
  protected String tableName;
  protected Table tbl = null;
  protected String values = null;
  protected String sql = null;
  protected int batchSize = 1;
  protected int commitSize = 1;
  private Statement stmt = null;
  private PreparedStatement prepStmt = null;
  private Connection conn = null;
  private boolean isAutoCommit = true;
  
	public DbWriter()
	{
		
	}
	
  public void setSql(String sql)
  {
    this.sql = sql;
  }
  
  public void setValues(String values)
  {
    this.values = values;
  }

  public void setTable(String tableName)
  {
    this.tableName = tableName;
  }

  public void setBatchSize(int batchSize)
  {
    // ignore batch sizes <= 0
    if (batchSize >= 1)
      this.batchSize = batchSize;
  }
  
  /**
   * Sets the commit size.
   * <ul>
   * <li>If commitSize < 0, commit is never performed
   * <li>If commitSize = 0, commit is performed at end of run
   * <li>If commitSize = 1, autoCommit is enabled
   * <li>If commitSize = n and is > 1, commit is performed after every n stmts
   * </ul>
   * The commit size should synch with the batch size.
   * 
   * @param commitSize
   */
  public void setCommitSize(int commitSize)
  {
    this.commitSize = commitSize;
  }
  
  public void setDefault(Table tbl)
  {
    this.tbl = tbl;
  }

	public void execute(Scope scope) throws Exception 
	{
    conn = JdbcUtil.createConnection(jdbcDriver, url, user, password);
    if (commitSize != 1) {
      conn.setAutoCommit(false);
      isAutoCommit = false;
    }
    
    try {
      /*
      stmt = conn.createStatement();
      executeRawInserts();
      */
      
      executePreparedStmts();
    }
    catch (SQLException ex) {
      //close();
      throw new DbException(ex.getMessage(), ex);
    }
    finally {
      if (stmt != null) stmt.close();
      if (prepStmt != null) prepStmt.close();
    }
	}
	
  private void executeRawInserts()
  throws SQLException
  {
    RowIterator rs = tbl.getRows().iterator();
    RowSchema schema = rs.getSchema();
    
    String insertBase = insertStmtPrefix(schema);
    
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      writeRow(insertBase, row);
   }
    stmt.close();
  }
  
  private String insertStmtPrefix(RowSchema schema)
  {
    StringBuffer buf = new StringBuffer(); 
    buf.append("INSERT INTO ");
    buf.append(tableName);
    buf.append(" ( ");
    for (int i = 0; i < schema.size(); i++) {
      if (i > 0) buf.append(",");
      buf.append(schema.getName(i));
    }
    buf.append(" ) VALUES ( ");
    return buf.toString();
  }
  
 
  private void writeRow(String insertBase, Row row)
  throws SQLException
  {
    StringBuffer buf = new StringBuffer(); 
    buf.append(insertBase);
    for (int i = 0; i < row.size(); i++) {
      if (i > 0) buf.append(",");
     writeValue(buf, row.getValue(i));
    }
    buf.append(" )");
    String sql = buf.toString();
    stmt.executeUpdate(sql);
  }
  
  private void writeValue(StringBuffer buf, Object val)
  {
    if (val instanceof String) {
      buf.append("'");
      buf.append(val);
      buf.append("'");
    }
    else {
      buf.append(val);
    }
  }
  
  //===========================================
  //  Prepared INSERTS methods
  //===========================================
  
  private void executePreparedStmts()
  throws SQLException
  {
    RowIterator rs = tbl.getRows().iterator();
    RowSchema schema = rs.getSchema();
    
    // use user-supplied SQL, or else construct an INSERT stmt
    String insertSQL = sql;
    if (sql == null)
      insertSQL = createInsertPreparedSQL(schema);
    
    prepStmt = conn.prepareStatement(insertSQL);
    ParameterMetaData metadata = prepStmt.getParameterMetaData();
    int numParam = metadata.getParameterCount();
    
    /*
    String[] paramClassName = new String[numParam];
    for (int i = 0; i < numParam; i++) {
      paramClassName[i] = metadata.getParameterClassName(i);
    }
    */
    
    int batchCount = 0;
    int commitCount = 0;
    while (true) {
      Row row = rs.next();
      if (row == null) // done writing
        break;
      addRow(prepStmt, numParam, row, schema);
      batchCount += 1;
      commitCount += 1;
      if (batchCount == batchSize) {
        prepStmt.executeBatch();
        batchCount = 0;
        
        /**
         * Only commit after an update.
         * Don't commit in AutoCommit mode (some DBs will error on this)
         */
        if (! isAutoCommit
            && commitSize >= 0 && commitCount > 0 && commitCount >= commitSize) {
          conn.commit();
          commitCount = 0;
        }
      }
    }
    // execute final batch, if any
    if (batchCount != 0) {
      prepStmt.executeBatch();
      batchCount = 0;
    }
    if (! isAutoCommit && commitSize >= 0 && commitCount != 0) {
      conn.commit();
      commitCount = 0;
    }
    if (commitSize < 0) {
      conn.rollback();
    }
  }
  
  private String createInsertPreparedSQL(RowSchema schema)
  {
    StringBuffer buf = new StringBuffer(); 
    buf.append("INSERT INTO ");
    
    if (tableName == null) {
      throw new ExecutionException("table: parameter must be specified");
    }
    buf.append(tableName);
    buf.append(" ( ");
    
    StringBuffer defaultValues = new StringBuffer();
    for (int i = 0; i < schema.size(); i++) {
      if (i > 0) {
        buf.append(",");
        defaultValues.append(",");
      }
      buf.append(schema.getName(i));
      defaultValues.append("?");
      
    }
    buf.append(" ) VALUES ( ");
    
    if (values != null)
      buf.append(values);
    else 
      buf.append(defaultValues);
    
    buf.append(" )");
    return buf.toString();
  }

  private void addRow(PreparedStatement prepStmt, int numParam, Row row, RowSchema schema)
  throws SQLException
  {
    int num = Math.min(numParam, row.size());

    for (int i = 0; i < num; i++) {
      try {
        if (schema.getType(i) == String.class) {
          setString(prepStmt, i, (String) row.getValue(i));
        }
        else {
          prepStmt.setObject(i+1, row.getValue(i));
        }
      }
      catch (SQLException ex) {
        throw new DbException("column " + schema.getName(i) + "[" + i + "] : " 
            + ex.getMessage(), ex);
      }
    }
    prepStmt.addBatch();
  }

  private static final int MAX_SAFE_STRING_SIZE = 2000;

  private void setString(PreparedStatement prepStmt, int i, String s) throws SQLException {
    if (s == null || s.length() < MAX_SAFE_STRING_SIZE) {
      prepStmt.setString(i+1, s);
      return;
    }
    Clob clob = conn.createClob();
    clob.setString(1, s);
    prepStmt.setClob(i+1, clob);
  }

}
