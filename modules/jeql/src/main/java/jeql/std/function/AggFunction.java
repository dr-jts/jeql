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

public class AggFunction 
  implements FunctionClass
{
  /**
   * Counts rows in a table.
   * 
   * @param o
   * @return
   */
  public static int countRows(Table tbl)
  {
    RowIterator it = tbl.getRows().iterator();
    int count = 0;
    while (true) {
      Row row = it.next();
      if (row == null) break;
      count++;
    }
    return count;
  }

  public static double sumRows(Table tbl)
  {
    RowList rows = tbl.getRows();
    
    if (rows.getSchema().size() != 1) 
      throw new ExecutionException("argument to sum function must have exactly 1 column");
    
    RowIterator it = rows.iterator();
    double sum = 0;
    while (true) {
      Row row = it.next();
      if (row == null) break;
      
      Object val = row.getValue(0);
      double d = TypeUtil.toDouble(val);
      sum += d;
    }
    return sum;
  }

}
