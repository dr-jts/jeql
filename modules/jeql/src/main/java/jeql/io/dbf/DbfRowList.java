package jeql.io.dbf;

import jeql.api.error.ExecutionException;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

public class DbfRowList 
implements RowList
{
  private String filename;
  private RowSchema schema;
  
  public DbfRowList(String filename) 
  {
    this.filename = filename;
    loadSchema();
  }

  public RowSchema getSchema() { return schema; }
  
  public RowIterator iterator()
  {
    DbfRowIterator sri = new DbfRowIterator();
    try {
      sri.open(filename);
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }

    return sri;
  }
  
  public void loadSchema()
  {
    DbfRowIterator sri = new DbfRowIterator();
    try {
      sri.open(filename);
      schema = sri.getSchema();
      sri.close();
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }
  }

}
