package jeql.api.row;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * A RowList held in main memory.
 * 
 * @author Martin Davis
 *
 */
public class ArrayRowList 
  implements RowList
{
  RowSchema schema;
  List rows;
  Iterator it = null;
  
  /**
   * Creates an empty rowlist ready for population
   * @param schema
   */
  public ArrayRowList(RowSchema schema) 
  {
    this.schema = schema;
    rows = new ArrayList();
  }

  /**
   * Creates an in-memory rowlist from a row iterator
   * @param rowIt
   */
  public ArrayRowList(RowList rowList) 
  {
    this(rowList.iterator());
  }

  /**
   * Creates an in-memory rowlist from a row iterator
   * @param rowIt
   */
  public ArrayRowList(RowIterator rowIt) 
  {
    this.schema = rowIt.getSchema();
    rows = new ArrayList();
    add(rowIt);
  }

  /**
   * Creates an in-memory rowlist from an iterator
   * which provides {@link Row}s.
   * 
   * @param schema
   * @param it
   */
  public ArrayRowList(RowSchema schema, Iterator it) 
  {
    this.schema = schema;
    rows = new ArrayList();
    add(it);
  }

  /**
   * Creates an in-memory rowlist from a collection
   * of {@link Row}s.
   * 
   * @param schema
   * @param collect
   */
  public ArrayRowList(RowSchema schema, Collection collect) 
  {
    this.schema = schema;
    rows = new ArrayList();
    add(collect.iterator());
  }

  public RowSchema getSchema() { return schema; }
  
  public void add(Row row)
  {
    rows.add(row);
  }
  
  public void add(RowIterator rowStr)
  {
    while (true) {
      Row row = rowStr.next();
      if (row == null) return;
      rows.add(row);
    }
  }
  
  public void add(Iterator it)
  {
    while (it.hasNext()) {
      Row row = (Row) it.next();
      // Assert: row has EXACTLY the same schema as this RowList
      rows.add(row);
    }
  }
  
  public RowIterator iterator() 
  {
    return new CollectionRowIterator(schema, rows);
  }
  
  public List getRows()
  {
    return rows;
  }
}

