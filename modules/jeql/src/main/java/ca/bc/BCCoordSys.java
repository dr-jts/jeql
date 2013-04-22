package ca.bc;

import jeql.api.function.FunctionClass;
import ca.bc.coordsys.Reprojector;
import ca.bc.coordsys.impl.BCStandardCoordinateSystems;

import com.vividsolutions.jts.geom.Geometry;

public class BCCoordSys 
implements FunctionClass
{

  public static Geometry albersToGeo(Geometry g)
  {
    Geometry g2 = (Geometry) g.clone();
    Reprojector.instance().reproject(g2, 
        BCStandardCoordinateSystems.BC_ALBERS_NAD_83,
        BCStandardCoordinateSystems.GEOGRAPHICS_WGS_84);
    return g2;
  }

  public static Geometry geoToAlbers(Geometry g)
  {
    Geometry g2 = (Geometry) g.clone();
    Reprojector.instance().reproject(g2, 
        BCStandardCoordinateSystems.GEOGRAPHICS_WGS_84,
        BCStandardCoordinateSystems.BC_ALBERS_NAD_83);
    return g2;
  }

}
