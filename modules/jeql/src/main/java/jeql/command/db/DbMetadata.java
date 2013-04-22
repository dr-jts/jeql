package jeql.command.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import jeql.api.command.Command;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.command.db.driver.ConnectionOperation;
import jeql.command.db.driver.JdbcRowMapper;
import jeql.command.db.driver.JdbcTemplate;
import jeql.command.db.driver.RowMapper;
import jeql.engine.Scope;

public class DbMetadata extends DbCommandBase 
{
  private static final String CATALOG = "catalog";
  private static final String SCHEMA = "schema";
  private static final String TABLE = "table";
  private static final String COLUMN = "column";
  private static final String INDEX = "index";
  private static final String PROC = "proc";
  
  protected String sql;
  protected Table result;
  
  private String objectName = null;
  
  private String catalog = null;
  private String schemaPattern = null;
  private String tableNamePattern = null;
  private String columnNamePattern = null;
  private String procNamePattern = null;

  private static boolean isMatch(String object, String objKey)
  {
    return object.toLowerCase().startsWith(objKey);
  }
  
  public DbMetadata() {

  }

  public void setTables(boolean foo) {
    objectName = TABLE;
  }

  public void setColumns(boolean foo) {
    objectName = COLUMN;
  }

  public void setIndexes(boolean foo) {
    objectName = INDEX;
  }

  public void setProcs(boolean foo) {
    objectName = PROC;
  }

  public void setObject(String objectName) {
    this.objectName = objectName;
  }

  public void setSchemaPattern(String schemaPattern)
  {
    this.schemaPattern = schemaPattern;
  }
  
  public void setTablePattern(String tableNamePattern)
  {
    this.tableNamePattern = tableNamePattern;
  }
  
  public void setColumnPattern(String columnNamePattern)
  {
    this.columnNamePattern = columnNamePattern;
  }
  
  public Table getDefault() {
    return result;
  }

  public void execute(Scope scope) throws Exception {
    ConnectionOperation connOp = null;

    connOp = new ObjectConnectionOperation();
    
    JdbcTemplate template = new JdbcTemplate(jdbcDriver, url, user, password);
    RowMapper rowMapper = new JdbcRowMapper();
    RowList rl = template.execute(connOp, rowMapper);
    result = new Table(rl);
    return;
  }
  
  class ObjectConnectionOperation 
    implements ConnectionOperation 
  {
    public ObjectConnectionOperation() {
    }

    public ResultSet execute(Connection conn) throws SQLException 
    {
      if (isMatch(objectName, CATALOG))
        return conn.getMetaData().getCatalogs();
      if (isMatch(objectName, SCHEMA))
        return conn.getMetaData().getSchemas();
      if (isMatch(objectName, TABLE))
        return conn.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, null);
      if (isMatch(objectName, COLUMN))
        return conn.getMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
      if (isMatch(objectName, INDEX))
        return conn.getMetaData().getIndexInfo(catalog, schemaPattern, tableNamePattern, false, true);
      if (isMatch(objectName, PROC))
        return conn.getMetaData().getProcedures(catalog, schemaPattern, procNamePattern);
      return null;
    }

    public void close() throws SQLException {

    }
  }
  

}

