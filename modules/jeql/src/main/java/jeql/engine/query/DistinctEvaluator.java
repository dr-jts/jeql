package jeql.engine.query;

import java.util.Set;
import java.util.TreeSet;

import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;
import jeql.api.row.RowComparator;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;


public class DistinctEvaluator 
  implements QueryOp
{

  private RowList rowStr;
  private Set distinctRows; 
    
  public DistinctEvaluator(RowList rowStr) {
    this.rowStr = rowStr;
  }
  
  public RowList eval()
  {
    distinctRows = new TreeSet(new RowComparator());
    RowIterator it = rowStr.iterator();
    while (true) {
      Row row = it.next();
      if (row == null) break;
      distinctRows.add(row);      
    }
    return new ArrayRowList(rowStr.getSchema(), distinctRows);
  }


}
