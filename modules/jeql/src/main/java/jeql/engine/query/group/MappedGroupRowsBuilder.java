package jeql.engine.query.group;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import jeql.api.function.Aggregator;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowList;
import jeql.engine.query.Tuple;

/**
 * A GroupRowsBuilder for the case where there are one or more
 * GROUP BY expressions present.
 * This will also work for zero expressions (no GROUP BY specified),
 * but this case is more optimally evaluated using SingleGroupResultBuilder
 * 
 * @author Martin Davis
 *
 */
class MappedGroupRowsBuilder
extends EagerGroupRowsBuilder
{
  private Map tupleAggMap = new TreeMap(); 
  
  public MappedGroupRowsBuilder()
  {
  }
  
  public Aggregator[] getAggregatorsForGroup(Tuple tuple)
  {
    Aggregator[] agg = (Aggregator[]) tupleAggMap.get(tuple);
    if (agg != null) return agg;
    
    // not yet present - insert it
    agg = groupScope.createAggregatorVector();
    tupleAggMap.put(tuple, agg);
    return agg;
  }
  
  public RowList createRows()
  {
    ArrayRowList memRL = new ArrayRowList(aggSchema);
    int rowSize = memRL.getSchema().size();
    
    Collection entries = tupleAggMap.entrySet();
    for (Iterator i = entries.iterator(); i.hasNext(); ) {
      Map.Entry entry = (Map.Entry) i.next();
      Tuple key = (Tuple) entry.getKey();
      Aggregator[] agg = (Aggregator[]) entry.getValue();
      
      BasicRow row = new BasicRow(rowSize);
      
      int col = 0;
      for (int iKey = 0; iKey < key.size(); iKey++) {
        row.setValue(col++, key.getValue(iKey));
      }
      for (int iAgg = 0; iAgg < agg.length; iAgg++) {
        row.setValue(col++, agg[iAgg].getResult());
      }
      memRL.add(row);
    }
    return memRL;
  }
  

}
