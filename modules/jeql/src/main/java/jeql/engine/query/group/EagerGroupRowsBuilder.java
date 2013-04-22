package jeql.engine.query.group;

import jeql.api.function.Aggregator;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.engine.query.SelectEvaluator;
import jeql.engine.query.Tuple;

import com.vividsolutions.jts.util.Assert;

/**
 * An abstract base class for builders which 
 * evaluates the grouped row list eagerly.
 * It provides the eager strategy 
 * which scans the entire the input RowList and
 * computes the grouped expressions over all rows,
 * keeping the results in memory.
 * This is required if the input is not in sorted order,
 * but has the disadvantage of requiring enough memory
 * to store the entire result set.
 */
abstract class EagerGroupRowsBuilder
extends GroupRowsBuilder
{
  public RowList eval(RowList baseRS)
  {
    RowIterator rowIt = baseRS.iterator();
    while (true) {
      Row row = rowIt.next();
      if (row == null) break;
      
     evalRow(row);
    }
    return createRows();
  }
  
  private void evalRow(Row row)
  {
    // get the aggregators for the group key tuple
    Tuple keyTuple = groupEval.extractGroupKey(row);
    //System.out.println("Processing row with GROUP BY key: " + keyTuple);
    Aggregator[] aggs = getAggregatorsForGroup(keyTuple);
    
    Assert.isTrue(aggs.length == groupEval.aggFunArgList.size());
    
    groupEval.baseScope.setRow(row);
    SelectEvaluator.evalAssignments(groupEval.withList, groupEval.baseScope);
    groupEval.evalAggregators(aggs, groupEval.baseScope);
  }
}
