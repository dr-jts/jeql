package jeql.api.row;

import java.util.Collection;
import java.util.Iterator;


public class CollectionRowIterator
  implements RowIterator
{
  private RowSchema schema;
  private Iterator it = null;
  
  public CollectionRowIterator(RowSchema schema, Collection rows)
  {
    this.schema = schema;
    it = rows.iterator();
  }
  
  public RowSchema getSchema() { return schema; }
  
  /**
   * Gets the next Row from this stream, if any.
   * The Row returned must be a value object 
   * (i.e. its contents must not change once it has been returned).
   * 
   * @return a Row
   * @return null if no Row remains in the stream
   */
  public Row next()
  {
    if (it.hasNext()) return (Row) it.next();
    return null;
  }
}