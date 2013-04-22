package jeql.engine.query.group;

import jeql.api.function.Aggregator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.engine.query.Tuple;

/**
 * An abstract class which creates the {@link RowList}
 * containing the aggregated result 
 * of queries which are aggregated (contain a GROUP BY clause).
 * The rowlist contains one column for each occurrence of 
 * an aggregate function, and one row for each grouped row.
 * The value of each column is the result of the aggregate function for 
 * the rows in the corresponding group.
 */
abstract class GroupRowsBuilder
{
  protected GroupByEvaluator groupEval;
  protected GroupScope groupScope;
  protected RowSchema aggSchema;

  public void init(GroupByEvaluator groupEval)
  {
    this.groupEval = groupEval;
    this.groupScope = groupEval.groupScope;
    
    Class[] aggRowType = groupScope.getAggRowTypes();
    // it doesn't matter what the col names are, since they will be renamed
    // during final select list processing
    aggSchema = RowSchema.getDefaultNamedSchema(aggRowType);
  }
  
  abstract RowList eval(RowList baseRS);
  
  /**
   * Gets the array of {@link Aggregator}s for a specific
   * group key tuple
   * 
   * @param tuple group key for the aggregator
   * @param groupScope scope of this grouped query
   * @return the array of aggregators
   */
  abstract Aggregator[] getAggregatorsForGroup(Tuple tuple);
  
  /**
   * Creates the result rows for this grouped query
   * 
   * @return the RowList containing the aggregated result
   */
  abstract RowList createRows();
}
  
