package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;

import org.locationtech.jts.geom.Geometry;


public class GeomUnionAggFunction 
  implements AggregateFunction
{

  public GeomUnionAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomUnion"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomUnionAggregator();
  }

  public static class GeomUnionAggregator 
    implements Aggregator
  {
    private Geometry unionGeom = null;

    public void addValue(Object[] arg)
    {
      Geometry geom = (Geometry) arg[0];
      if (unionGeom == null)
        unionGeom = geom;
      else {
        unionGeom = unionGeom.union(geom);
      }
    }
    
    public Object getResult() 
    {
      return unionGeom;
    }
  }
}
