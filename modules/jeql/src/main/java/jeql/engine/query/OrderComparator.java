package jeql.engine.query;

import java.util.Comparator;

import jeql.api.row.Row;
import jeql.util.TypeUtil;


public class OrderComparator 
  implements Comparator
{
  private int[] colIndex;
  private boolean[] orderDir;
  
  public OrderComparator(int[] orderIndexDir) 
  {
    this.colIndex = extractIndex(orderIndexDir);
    this.orderDir = extractDir(orderIndexDir);
  }

  private static int[] extractIndex(int[] orderIndexDir)
  {
    int[] oi = new int[orderIndexDir.length];
    for (int i = 0; i < orderIndexDir.length; i++) {
      oi[i] = Math.abs(orderIndexDir[i]) - 1;
    }
    return oi;
  }
  
  private static boolean[] extractDir(int[] orderIndexDir)
  {
    boolean[] dir = new boolean[orderIndexDir.length];
    for (int i = 0; i < orderIndexDir.length; i++) {
      dir[i] = orderIndexDir[i] >= 0;
    }
    return dir;
  }
  
  public int compare(Object o1, Object o2)
  {
    Row r1 = (Row) o1;
    Row r2 = (Row) o2;
    
    for (int i = 0; i < colIndex.length; i++) {
      Object val1 = r1.getValue(colIndex[i]);
      Object val2 = r2.getValue(colIndex[i]);
      
      int comp = compareValue(val1, val2);
      if (! orderDir[i])
        comp = -1 * comp;
      
      if (comp != 0) return comp;
    }
    return 0;
  }
  
  public int compareValue(Object val1, Object val2)
  {
    // compare nulls first
    if (val1 == null) {
      if (val2 == null) return 0;
      return -1;
    }
    if (val2 == null) return 1;
    
    if (val1 instanceof Comparable) {
      return ((Comparable) val1).compareTo(val2);
    }
    return TypeUtil.compareValue(val1, val2);

//    throw new IllegalStateException("Column values not comparable");
  }
}
