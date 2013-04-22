package jeql.std.aggfunction;
import jeql.api.function.Aggregator;
import jeql.engine.function.BaseAggregateFunction;

public class MaxAggFunction 
  extends BaseAggregateFunction
{

  public MaxAggFunction() {
  }

  public String getName() { return "Max"; }
  
  
  public Aggregator createAggregator()
  {
    return new MaxAggregator();
  }

  public static class MaxAggregator 
    implements Aggregator
  {
    private Comparable max = null;
    private boolean seenRow = false;
    
    public void addValue(Object obj)
    {
      seenRow = true;
      Comparable val = (Comparable) obj;
      if (max == null)
        max = val;
      else if (max.compareTo(val) < 0)
        max = val;
    }
    
    public Object getResult() 
    {
      if (! seenRow)
        return null;
      return max;
    }
  }
}