package jeql.std.aggfunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.api.function.Aggregator;
import jeql.engine.Scope;
import jeql.std.geom.GeomFunction;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class GeomConvexHullAggFunction 
  implements AggregateFunction
{

  public GeomConvexHullAggFunction() {
  }
  public void bind(Scope scope, List args)
  {
    
  }
  public String getName() { return "GeomConvexHull"; }
  
  public Class getType() { return Geometry.class; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }

  public Aggregator createAggregator()
  {
    return new GeomConvexHullAggregator();
  }

  private static class GeomConvexHullAggregator 
    implements Aggregator
  {
    private GeometryFactory fact = null;
    private List pts = new ArrayList();
    private CoordinateExtracter coordExtracter = new CoordinateExtracter(pts);

    public void addValue(Object[] arg)
    {
      ((Geometry) arg[0]).apply(coordExtracter);
    }
    
    
    public Object getResult() 
    {
      if (fact == null)
        fact = GeomFunction.geomFactory;
      
      Coordinate[] coords = CoordinateArrays.toCoordinateArray(pts);
      ConvexHull convexHull = new ConvexHull(coords, fact);
      return convexHull.getConvexHull();
    }
  }
  
  private static class CoordinateExtracter
  implements CoordinateFilter
  {
    private Collection coords;
    
    public CoordinateExtracter(Collection coords)
    {
      this.coords = coords;
    }
    
    public void filter(Coordinate coord)
    {
      coords.add(coord);
    }
  }
}
