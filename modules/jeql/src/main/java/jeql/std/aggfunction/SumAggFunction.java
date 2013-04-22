package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

public class SumAggFunction 
  implements AggregateFunction
{

  
  public SumAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  
  public String getName() { return "Sum"; }
  
  public Class getType() { return Double.class; } 
  
  public Aggregator createAggregator()
  {
    return new SumAggregator();
  }

  public static class SumAggregator 
    implements Aggregator
  {
    private double sum = 0;

    public void addValue(Object obj)
    {
      sum += TypeUtil.toDouble(obj);
    }
    
    public Object getResult() 
    {
      return new Double(sum);
    }
  }
}
