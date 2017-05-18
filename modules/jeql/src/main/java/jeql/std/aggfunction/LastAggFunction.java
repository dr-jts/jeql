package jeql.std.aggfunction;

import jeql.api.function.Aggregator;
import jeql.engine.function.BaseAggregateFunction;


/**
 * Returns the last non-null value from an aggregated column.
 * 
 * @author Martin Davis
 *
 */
public class LastAggFunction 
  extends BaseAggregateFunction
{
  Class typeClass = null;
  
  public LastAggFunction() {
  }

  public String getName() { return "Last"; }
  
  
  public Aggregator createAggregator()
  {
    return new LastAggregator();
  }

  public static class LastAggregator 
    implements Aggregator
  {
    private Object result = null;

    public void addValue(Object[] obj)
    {
      if (obj[0] != null)
        result = obj[0];
    }
    
    public Object getResult() 
    {
      return result;
    }
  }
}
