package jeql.api.row;

import java.util.Comparator;


public class RowIteratorComparator 
  implements Comparator
{
  RowComparator rowComp = new RowComparator();
  
  public RowIteratorComparator() {
  }
  
  public RowIteratorComparator(Comparator valueComparator) {
    rowComp = new RowComparator(valueComparator);
  }
  
  public int compare(Object o1, Object o2)
  {
    RowIterator r1 = (RowIterator) o1;
    RowIterator r2 = (RowIterator) o2;
    
    while (true) {
      Row row1 = r1.next();
      Row row2 = r2.next();
      
      
      if (row1 == null && row2 != null)
        return -1;
      if (row2 == null && row1 != null)
        return 1;
      if (row2 == null && row1 == null)
        return 0;
      
      // neither null = compare rows
      int compRow = rowComp.compare(row1, row2);
      
      if (compRow != 0)
        return compRow;
    }
  }

  
}
