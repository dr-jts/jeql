package jeql.std.aggfunction;
import java.util.ArrayList;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

/**
 * Aggregate Union using Cascaded Union
 * 
 * @author Martin Davis
 *
 */
public class GeomUnionMemAggFunction 
  implements AggregateFunction
{

  public GeomUnionMemAggFunction() {
  }

  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomUnionMem"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomUnionMemAggregator();
  }

  public static class GeomUnionMemAggregator 
    implements Aggregator
  {
    private List geoms = new ArrayList();

    public void addValue(Object[] arg)
    {
      Geometry geom = (Geometry) arg[0];
      geoms.add(geom);
    }
    
    public Object getResult() 
    {
      return UnaryUnionOp.union(geoms);
    }
  }
}
