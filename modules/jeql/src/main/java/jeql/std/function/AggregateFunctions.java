package jeql.std.function;

import jeql.api.error.ExecutionException;
import jeql.api.function.AggregateFunction;
import jeql.api.function.FunctionClass;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.std.aggfunction.AvgAggFunction;
import jeql.std.aggfunction.ConcatAggFunction;
import jeql.std.aggfunction.ConjoinAggFunction;
import jeql.std.aggfunction.CountAggFunction;
import jeql.std.aggfunction.FirstAggFunction;
import jeql.std.aggfunction.GeomCollectAggFunction;
import jeql.std.aggfunction.GeomConnectAggFunction;
import jeql.std.aggfunction.GeomConvexHullAggFunction;
import jeql.std.aggfunction.GeomExtentAggFunction;
import jeql.std.aggfunction.GeomUnionAggFunction;
import jeql.std.aggfunction.GeomUnionMemAggFunction;
import jeql.std.aggfunction.LastAggFunction;
import jeql.std.aggfunction.MaxAggFunction;
import jeql.std.aggfunction.MinAggFunction;
import jeql.std.aggfunction.StdDevAggFunction;
import jeql.std.aggfunction.SumAggFunction;
import jeql.util.TypeUtil;

/**
 * Following functions are evaluated at stmt bind time to
 *    produce an AggregateFunction to do aggregation
 *    
 * @author Martin Davis
 *
 */
public class AggregateFunctions 
  implements FunctionClass
{
  
  public static AggregateFunction avg() {   return new AvgAggFunction();  }
  public static AggregateFunction concat() {   return new ConcatAggFunction();  }
  public static AggregateFunction conjoin() {   return new ConjoinAggFunction();  }
  public static AggregateFunction count()  {    return new CountAggFunction();  }
  public static AggregateFunction max() {    return new MaxAggFunction();  }
  public static AggregateFunction min() {    return new MinAggFunction();  }
  public static AggregateFunction sum() {    return new SumAggFunction();  }
  public static AggregateFunction stddev() {    return new StdDevAggFunction();  }
  
  public static AggregateFunction first() {    return new FirstAggFunction();  }
  public static AggregateFunction last() {    return new LastAggFunction();  }
  
  public static AggregateFunction geomExtent() {    return new GeomExtentAggFunction(); }
  public static AggregateFunction geomUnion() {    return new GeomUnionAggFunction(); }
  public static AggregateFunction geomUnionMem() {    return new GeomUnionMemAggFunction(); }
  public static AggregateFunction geomCollect() {    return new GeomCollectAggFunction(); }
  public static AggregateFunction geomConvexHull() {    return new GeomConvexHullAggFunction(); }
  public static AggregateFunction geomConnect()
  {
    return new GeomConnectAggFunction();
  }
}
