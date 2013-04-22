package jeql.engine.query;

import java.util.Collections;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.RowList;
import jeql.syntax.OrderItem;
import jeql.syntax.SelectNode;


public class OrderByEvaluator 
  implements QueryOp
{
  private RowList baseRowStr = null;
  private int[] orderIndex;
    
  public OrderByEvaluator(SelectNode select, RowList rowStr)
  {
    this.baseRowStr = rowStr;
    String[] resultColName = select.getSelectList().getResultColumnNames();
    orderIndex = computeOrderIndices(resultColName, select.getOrderList());
  }

  private int[] computeOrderIndices(String[] selColName, List orderList)
  {
    int[] index = new int[orderList.size()];
    
    for (int i = 0; i < index.length; i++) {
      index[i] = computeOrderIndexDirection(selColName, (OrderItem) orderList.get(i));
    }
    return index;
  }
  
  /**
   * 
   * Order indices are 1-based (to conform to SQL convention, and to allow
   * sign to indicate direction).
   * 
   * @param selColName
   * @param orderItem
   * @return the directed order index (< 0 if descending)
   */
  private int computeOrderIndexDirection(String[] selColName, OrderItem orderItem)
  {
    String orderColName = orderItem.getColName();
    
    // find index of order col 
    int colNameIndex = colNameIndex(selColName, orderColName);
    
    if (colNameIndex < 0)
      throw new ExecutionException("Unknown ORDER BY select column name: " + orderColName);

    int indexDir = colNameIndex + 1;
    if (! orderItem.isAscending())
      indexDir *= -1;
    
    return indexDir;
  }
  
  /**
   * Finds the index of a column name in an array
   * 
   * @param colName the array of column names
   * @param name the name to look up
   * @return the index of the name (0-based)
   * @return -1 if the name is not in the list
   */
  private int colNameIndex(String[] colName, String name)
  {
    // find index of order col 
    for (int i = 0; i < colName.length; i++) {
      if (name.equals(colName[i])) { 
        return i;
      }
    }
    return -1;
  }

  /*
  public OrderByEvaluator(RowList baseRowStr, int[] orderIndex) 
  {
    this.baseRowStr = baseRowStr;
    this.orderIndex = orderIndex;
  }
  */
  
  public RowList eval()
  {
    // memorize the rowlist in order to allow sorting
    ArrayRowList rowList = new ArrayRowList(baseRowStr.iterator());
    // dismiss baseRowStr to allow GC
    baseRowStr = null;
    
    // sort according to ordering
    OrderComparator comp = new OrderComparator(orderIndex);
    Collections.sort(rowList.getRows(), comp);
    return rowList;
  }
  

}
