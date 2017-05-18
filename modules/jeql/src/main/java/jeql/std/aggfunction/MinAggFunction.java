package jeql.std.aggfunction;

import jeql.api.function.Aggregator;
import jeql.engine.function.BaseAggregateFunction;

public class MinAggFunction 
  extends BaseAggregateFunction
{

  public MinAggFunction() {
  }

  public String getName() { return "Min"; }
  
  
  public Aggregator createAggregator()
  {
    return new MinAggregator();
  }

  public static class MinAggregator 
    implements Aggregator
  {
    private Comparable min = null;
    private boolean seenRow = false;
    
    public void addValue(Object[] arg)
    {
      seenRow = true;
      Comparable val = (Comparable) arg[0];
      if (min == null)
        min = val;
      else if (min.compareTo(val) > 0)
        min = val;
    }
    
    public Object getResult() 
    {
      if (! seenRow)
        return null;
      return min;
    }
  }
}
