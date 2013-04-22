package jeql.syntax;


/**
 * An item in an ORDER BY list.
 * Currently only supports named columns.
 * Column indexes can be easily added.
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class OrderItem
{
  private String colName;
  private boolean isAscending;

  public OrderItem(String colName, boolean isAscending) {
    this.colName = colName;
    this.isAscending = isAscending;
  }
  
  public String getColName()
  {
    return colName;
  }
  
  public boolean isAscending() { return isAscending; }
}