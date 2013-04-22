package jeql.api.row;

import java.util.Comparator;

import jeql.util.TypeUtil;

public class RowComparator 
  implements Comparator
{
  private Comparator valueComparator = null;
  
  public RowComparator() {
  }
  
  public RowComparator(Comparator valueComparator) 
  {
    this.valueComparator = valueComparator;
  }
  
  public int compare(Object o1, Object o2)
  {
    Row r1 = (Row) o1;
    Row r2 = (Row) o2;
    
    if (r1.size() != r2.size())
      throw new IllegalArgumentException("Rows are different sizes");
    
    for (int i = 0; i < r1.size(); i++) {
      Object v1 = r1.getValue(i);
      Object v2 = r2.getValue(i);
      
      // have to assume that null values are the same, since we don't have further type information
      if (v1 == null && v2 == null)
        return 0;
      
      int comp = -999;
      if (valueComparator != null)
        comp = valueComparator.compare(v1, v2);
      else
        comp = TypeUtil.compareValue(v1, v2);
      
      if (comp != 0)
        return comp;
    }
    return 0;
  }

}
