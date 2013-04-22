package jeql.engine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;

public class EqualityRowIndex
{
  private Map map = new HashMap();
  private int keyColIndex = -1;
  private RowSchema schema;
  
  public EqualityRowIndex(RowList rs, String keyCol)
  {
    schema = rs.getSchema();
    keyColIndex = rs.getSchema().getColIndex(keyCol);
    build(rs, keyColIndex);
  }
  
  private void build(RowList rs, int keyColIndex)
  {
    RowIterator rowIt = rs.iterator();
    while (true) {
      Row row = rowIt.next();
      if (row == null) return;
      Object val = row.getValue(keyColIndex);
      add(val, row);
    }
  }
  
  private void add(Object key, Row row)
  {
    Object curr = map.get(key);
    if (curr == null) {
      map.put(key, row);
    }
    else if (curr instanceof Row) {
      List list = new ArrayList();
      list.add(curr);
      list.add(row);
      map.put(key, list);
    }
    else {
      List list = (List) curr;
      list.add(row);
    }
  }
  
  public int size(Object key)
  {
    Object curr = map.get(key);
    if (curr == null) {
      return 0;
    }
    else if (curr instanceof Row) {
      return 1;
    }
    else {
      return ((List)curr).size();
    }
  }
  
  public Row get(Object key, int i)
  {
    Object curr = map.get(key);
    if (curr == null) {
      return null;
    }
    else if (curr instanceof Row) {
      return (Row) curr;
    }
    else {
      return (Row) ((List)curr).get(i);
    }
  }
  
  public RowIterator iterator(Object key)
  {
    Object curr = map.get(key);
    if (curr == null) {
      return null;
    }
    else if (curr instanceof Row) {
      return new SingleRowIterator( (Row) curr) ;
    }
    else {
      return new ListRowIterator((List)curr);
    }
  }
  
  private class SingleRowIterator implements RowIterator
  {
    private Row row;
    
    SingleRowIterator(Row row)
    {
      this.row = row;
    }
    
    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      Row saveRow = row;
      row = null;
      return saveRow;
    }
  }
  
  private class ListRowIterator implements RowIterator
  {
    private List rowList;
    int i = 0;
    
    ListRowIterator(List rowList)
    {
      this.rowList = rowList;
    }
    
    public RowSchema getSchema() {
      return schema;
    }

    public Row next() {
      if (i < rowList.size()) {
        Row row = (Row) rowList.get(i);
        i++;
        return row;
      }
      return null;
    }
  }
}