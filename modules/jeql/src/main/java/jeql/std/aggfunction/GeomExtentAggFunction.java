package jeql.std.aggfunction;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.std.geom.GeomFunction;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class GeomExtentAggFunction 
  implements AggregateFunction
{

  public GeomExtentAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomExtent"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomExtentAggregator();
  }

  public static class GeomExtentAggregator 
    implements Aggregator
  {
    private GeometryFactory fact = null;
    private Envelope env = null;

    public void addValue(Object[] arg)
    {
      Geometry geom = (Geometry) arg[0];
      if (geom != null && fact == null) {
        fact = geom.getFactory();
      }
      if (env == null)
        env = geom.getEnvelopeInternal();
      else {
        env.expandToInclude(geom.getEnvelopeInternal());
      }
    }
    
    public Object getResult() 
    {
      if (fact == null)
        fact = GeomFunction.geomFactory;
      if (env == null)
        env = new Envelope();
      return fact.toGeometry(env);
    }
  }
}
