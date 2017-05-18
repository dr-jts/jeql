package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

public class AvgAggFunction 
  implements AggregateFunction
{

  
  public AvgAggFunction() {
  }

  public void bind(Scope scope, List args)
  {
    
  }
  
  public String getName() { return "Avg"; }
  
  public Class getType() { return Double.class; } 

  /**
   * Currently only used for determining number of parameters
   */
  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new AvgAggregator();
  }

  public static class AvgAggregator 
    implements Aggregator
  {
    private int count = 0;
    private double sum = 0;

    public void addValue(Object[] arg)
    {
      count++;
      sum += TypeUtil.toDouble(arg[0]);
    }
    
    public Object getResult() 
    {
      return new Double(sum / count);
    }
  }
}