package jeql.io;

import java.io.IOException;

import jeql.api.row.Row;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

/**
 * A stream of features from an external source
 */
public interface RowInputStream 
  extends RowList
{
  public RowSchema getSchema();
  public Row next() throws IOException;
  public void close() throws IOException;
}