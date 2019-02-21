package jeql.std.aggfunction;
import java.util.ArrayList;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.std.geom.GeomFunction;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class GeomCollectAggFunction 
  implements AggregateFunction
{

  public GeomCollectAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomCollect"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomCollectAggregator();
  }

  private static class GeomCollectAggregator 
    implements Aggregator
  {
    private GeometryFactory fact = null;
    private List geoms = new ArrayList();

    public void addValue(Object[] arg)
    {
      Geometry geom = (Geometry) arg[0];
      if (geom != null && fact == null) {
        fact = geom.getFactory();
      }
      geoms.add(geom);
    }
    
    
    public Object getResult() 
    {
      if (fact == null)
        fact = GeomFunction.geomFactory;
      return fact.createGeometryCollection(
          GeometryFactory.toGeometryArray(geoms));
    }
  }
}
