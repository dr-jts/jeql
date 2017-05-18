package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;

public class ConjoinAggFunction 
  implements AggregateFunction
{

  
  public ConjoinAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "Conjoin"; }
  
  public Class getType() { return String.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class, String.class };
  }

  public Aggregator createAggregator()
  {
    return new ConjoinAggregator();
  }

  private static class ConjoinAggregator 
    implements Aggregator
  {
    private StringBuffer concat = new StringBuffer();

    public void addValue(Object[] args)
    {
    	if (concat.length() != 0) {
    		concat.append(args[1]);
    	}
      concat.append(args[0]);
    }
    
    public Object getResult() 
    {
      return concat.toString();
    }
  }
}
