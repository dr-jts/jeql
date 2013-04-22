package jeql.command.io;

import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.io.InputSource;

public class XmlRowList 
implements RowList
{

  public XmlRowList(InputSource src) 
  {
    super();
  }

  public RowSchema getSchema() { return null; }
  
  public RowIterator iterator() { return null; }
  
}
