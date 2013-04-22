package jeql.engine.query.group;

import jeql.api.function.Aggregator;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowList;
import jeql.engine.query.Tuple;

/**
 * A GroupResultBuilder for an aggregated query
 * with a single result row (i.e. containing
 * aggregate functions but with no GROUP BY specified.)
 * In this case storage and processing can be optimized. 
 * 
 * @author Martin Davis
 *
 */
class SingleGroupRowsBuilder 
extends EagerGroupRowsBuilder 
{
  private Aggregator[] aggItem;

  public SingleGroupRowsBuilder() 
  {
  }

  /**
   * tuple is not used (and may be null)
   */
  public Aggregator[] getAggregatorsForGroup(Tuple tuple) {
    createAggregators();
    return aggItem;
  }
  
  private void createAggregators()
  {
    if (aggItem == null) {
      // not yet present - insert it
      aggItem = groupScope.createAggregatorVector();
    }
  }
  
  public RowList createRows() 
  {
    ArrayRowList memRL = new ArrayRowList(aggSchema);
    int rowSize = memRL.getSchema().size();
    BasicRow row = new BasicRow(rowSize);
    int col = 0;
    
    // ensure aggregators are created (in case base rowlist was empty)
    createAggregators();
    
    for (int iAgg = 0; iAgg < aggItem.length; iAgg++) {
      row.setValue(col++, aggItem[iAgg].getResult());
    }
    memRL.add(row);
    return memRL;
  }
  
  
}