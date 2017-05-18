package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;

public class CountAggFunction 
  implements AggregateFunction
{

  
  public CountAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "Count"; }
  
  public Class getType() { return Integer.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new CountAggregator();
  }

  public static class CountAggregator 
    implements Aggregator
  {
    private int count = 0;

    public void addValue(Object[] arg)
    {
      count++;
    }
    
    public Object getResult() 
    {
      return new Integer(count);
    }
  }
}
