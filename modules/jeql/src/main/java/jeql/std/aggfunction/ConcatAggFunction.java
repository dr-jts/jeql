package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;

public class ConcatAggFunction 
  implements AggregateFunction
{

  
  public ConcatAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "Concat"; }
  
  public Class getType() { return String.class; } 
  
  public Aggregator createAggregator()
  {
    return new ConcatAggregator();
  }

  private static class ConcatAggregator 
    implements Aggregator
  {
    private StringBuffer concat = new StringBuffer();

    public void addValue(Object obj)
    {
      concat.append(obj);
    }
    
    public Object getResult() 
    {
      return concat.toString();
    }
  }
}
