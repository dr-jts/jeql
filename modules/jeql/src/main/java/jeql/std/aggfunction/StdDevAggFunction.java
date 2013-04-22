package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

public class StdDevAggFunction 
  implements AggregateFunction
{
  public StdDevAggFunction() {
  }

  public void bind(Scope scope, List args)
  {
    
  }
  
  public String getName() { return "StdDev"; }
  
  public Class getType() { return Double.class; } 
  
  public Aggregator createAggregator()
  {
    return new StdDevAggregator();
  }

  public static class StdDevAggregator 
    implements Aggregator
  {
    private int count = 0;
    private double sum = 0.0;
    private double vpn = 0.0;

    public void addValue(Object obj)
    {
      count++;
      double x = TypeUtil.toDouble(obj);
      if (count > 1) {
        double xs = sum - (x * (count - 1));
        vpn += (xs * xs) / count / (count - 1);
      }
      sum += x;
    }
    
    public Object getResult() 
    {
      // computes the sample Std Dev.
      // for population std dev, divide by count instead
      
      // guard against too few data items
      double var = 0.0;
      if (count >= 2)
        var = vpn / (count - 1);
      double sd = Math.sqrt(var);
      
      return new Double(sd);
    }
  }
}