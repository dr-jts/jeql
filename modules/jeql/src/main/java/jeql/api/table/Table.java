package jeql.api.table;

import jeql.api.row.AliasedRowList;
import jeql.api.row.RowIteratorComparator;
import jeql.api.row.RowList;

/**
 * A structure that allows attaching
 * a name to a {@link RowList}.
 * 
 * @author Martin Davis
 *
 */
public class Table 
{
  public static Table replace(Table t, RowList r)
  {
    Table t2 = new Table(r);
    t2.setName(t.tblName);
    return t2;
  }
  
  private String tblName = null;  // this is important - required in TableColumnNode.bind
  private RowList rowList;
  
  public Table(RowList rowList) {
    this.rowList = rowList;
  }

  public void setName(String name) { this.tblName = name; }
  
  public void setRowList(RowList rowList)
  {
    this.rowList = rowList;
  }
  
  public void changeColumnNames(String[] colName)
  {
    rowList = new AliasedRowList(rowList, colName);
  }
    
  public int size() { return rowList.getSchema().size(); }
  
  public String getName() { return tblName; }
  
  public String getColumnName(int i) { return rowList.getSchema().getName(i); }
  
  public RowList getRows() { return rowList; }
  
  public boolean hasColumn(String name)
  {
    return rowList.getSchema().hasCol(name);
  }
  
  public int getColumnIndex(String name)
  {
    return rowList.getSchema().getColIndex(name);
  }
  
  /**
   * Tests if two tables are equal.
   * Does not check table names for equality.
   */
  public boolean equalsExceptName(Table tbl)
  {
    if (size() != tbl.size()) return false;
    //TODO - replace with RowSchema.equalsWithNames
    
    /*
    for (int i = 0; i < size(); i++) {
      if (! getColumnName(i).equals(tbl.getColumnName(i)))
        return false;
    }*/
    if (! getRows().getSchema().equalsWithNames(tbl.getRows().getSchema()))
      return false;
    
    RowIteratorComparator rowStrComp = new RowIteratorComparator();
    return rowStrComp.compare(getRows(), tbl.getRows()) == 0;
  }
  
  public String toString()
  {
    return tblName + "(" + rowList.getSchema() + ")";
  }
  
  private static String toString(String[] str)
  {
    if (str == null) return "";
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length; i++) {
      if (i > 0)
        buf.append(",");
      buf.append(str[i]);
    }
    return buf.toString();
  }
}
