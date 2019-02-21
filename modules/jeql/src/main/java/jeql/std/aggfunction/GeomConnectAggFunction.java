package jeql.std.aggfunction;

import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.std.geom.GeomFunction;

import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * An aggregate function which connects
 * input points into a LineString.
 * 
 * @author mbdavis
 *
 */
public class GeomConnectAggFunction 
  implements AggregateFunction
{

  public GeomConnectAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomConnect"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomConnectAggregator();
  }

  private static class GeomConnectAggregator 
    implements Aggregator
  {
    private GeometryFactory fact = null;
    private CoordinateList pts = new CoordinateList();

    public void addValue(Object[] arg)
    {
      Geometry geom = (Geometry) arg[0];
      if (geom != null && fact == null) {
        fact = geom.getFactory();
      }
      pts.add(geom.getCoordinates(), true);
    }
    
    
    public Object getResult() 
    {
      if (fact == null)
        fact = GeomFunction.geomFactory;
      return fact.createLineString(
          CoordinateArrays.toCoordinateArray(pts));
    }
  }
}