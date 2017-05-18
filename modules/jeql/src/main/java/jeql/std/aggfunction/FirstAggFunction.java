package jeql.std.aggfunction;

import jeql.api.function.Aggregator;
import jeql.engine.function.BaseAggregateFunction;

/**
 * Returns the first non-null value from an aggregated column.
 * Useful for performing the "Pivot-By-Aggregate" pattern,
 * and the "Extract-Column-For-Group" pattern.
 * <p>
 * The Pivot-By-Aggregate pattern
 * computes a pivot table from an input
 * table where each row represents a column of a row in the output.
 * A tag value which determines the output column
 * is also present in each input row.
 * <p>
 * The Extract-Column-For-Group pattern
 * allows creating a column in the output table ????  - needs work!
 * 
 * 
 * @author Martin Davis
 *
 */
public class FirstAggFunction 
  extends BaseAggregateFunction
{
  Class typeClass = null;
  
  public FirstAggFunction() {
  }

  public String getName() { return "First"; }
  
  
  public Aggregator createAggregator()
  {
    return new FirstAggregator();
  }

  public static class FirstAggregator 
    implements Aggregator
  {
    private Object result = null;

    public void addValue(Object[] arg)
    {
      if (result == null)
        result = arg[0];
    }
    
    public Object getResult() 
    {
      return result;
    }
  }
}
