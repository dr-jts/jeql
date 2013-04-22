package jeql.io.shapefile;

import jeql.api.error.ExecutionException;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

public class ShapefileRowList 
implements RowList
{
  private String filename;
  private boolean readDBF = true;
  private RowSchema schema;
  
  public ShapefileRowList(String filename) 
  {
    this.filename = filename;
    loadSchema();
  }

  public ShapefileRowList(String filename, boolean readDBF) 
  {
    this.filename = filename;
    this.readDBF = readDBF;
    loadSchema();
  }

  public RowSchema getSchema() { return schema; }
  
  public RowIterator iterator()
  {
    ShapefileRowIterator sri = new ShapefileRowIterator();
    try {
      sri.open(filename, readDBF);
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }

    return sri;
  }
  
  public void loadSchema()
  {
    ShapefileRowIterator sri = new ShapefileRowIterator();
    try {
      sri.open(filename, readDBF);
      schema = sri.getSchema();
      sri.close();
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }
  }

}
