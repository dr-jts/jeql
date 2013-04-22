package jeql.api.row;



public class BasicRow 
  implements Row
{
  public static Row createRow(Row row0, int size0, Row row1, int size1) {
    BasicRow joinRow = new BasicRow(size0 + size1);
    joinRow.copyTo(0, row0);
    joinRow.copyTo(size0, row1);
    return joinRow;
  }
  
  Object[] values;
   
  public BasicRow(int size) {
    values = new Object[size];
  }

  /**
   * Sets the contents of the row to be
   * the given array of values.
   * The array is not copied.
   * 
   * @param values
   */
  public BasicRow(Object[] values) {
    this.values = values;
  }

  public BasicRow(Row row) {
    this(row.size());
    for (int i = 0; i < values.length; i++) {
      values[i] = row.getValue(i);
    }
  }

  public void setValue(int i, Object o) { values[i] = o; }
   
  public Object getValue(int i) { return values[i]; }
   
  public int size() { return values.length; }
  
  /**
   * 
   * @param startIndex
   * @param srcRow row to copy (may be null)
   */
  public void copyTo(int startIndex, Row srcRow) {
    if (srcRow == null) return;
    for (int i = 0; i < srcRow.size(); i++) {
      setValue(i + startIndex, srcRow.getValue(i));
    }
  }

  public void setColumnsToNull(int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
      setValue(i, null);
    }
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (i > 0)
        buf.append(' ');
      buf.append(values[i]);
    }
    return buf.toString();
  }
}
