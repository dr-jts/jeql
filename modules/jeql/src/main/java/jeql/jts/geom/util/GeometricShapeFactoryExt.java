package jeql.jts.geom.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class GeometricShapeFactoryExt
  extends GeometricShapeFactory
{
  protected int numArms = 8;
  protected double armLengthRatio = 0.5;
  
  /**
   * Creates a factory which will create sine stars using the default
   * {@link GeometryFactory}.
   *
   * @param geomFact the factory to use
   */
  public GeometricShapeFactoryExt()
  {
    super();
  }
  
  /**
   * Creates a factory which will create sine stars using the given
   * {@link GeometryFactory}.
   *
   * @param geomFact the factory to use
   */
  public GeometricShapeFactoryExt(GeometryFactory geomFact)
  {
    super(geomFact);
  }

  
  /**
   * Sets the number of arms in the star
   * 
   * @param numArms the number of arms to generate
   */
  public void setNumArms(int numArms)
  {
    this.numArms = numArms;
  }
  
  /**
   * Sets the ration of the length of each arm to the distance from the tip
   * of the arm to the centre of the star.
   * Value should be between 0.0 and 1.0
   * 
   * @param armLengthRatio
   */
  public void setArmLengthRatio(double armLengthRatio)
  {
    this.armLengthRatio = armLengthRatio;
  }
  
  /**
   * Generates the geometry for the star
   * 
   * @return the geometry representing the star
   */
  public Geometry createStar()
  {
    Envelope env = dim.getEnvelope();
    double radius = env.getWidth() / 2.0;

    double armRatio = armLengthRatio;
    if (armRatio < 0.0)
      armRatio = 0.0;
    if (armRatio > 1.0)
      armRatio = 1.0;

    double armMaxLen = armRatio * radius;
    double insideRadius = (1 - armRatio) * radius;

    double centreX = env.getMinX() + radius;
    double centreY = env.getMinY() + radius;

    Coordinate[] pts = new Coordinate[nPts + 1];
    int iPt = 0;
    for (int i = 0; i < nPts; i++) {
      // the fraction of the way thru the current arm - in [0,1]
      double ptArcFrac = (i / (double) nPts) * numArms;
      double armAngFrac = ptArcFrac - Math.floor(ptArcFrac);
      
      // the current length of the arm as a fraction of the maximum length
      double armLenFrac = 1.0 - Math.abs(2.0 * armAngFrac - 1.0);
      
      // the current radius of the curve (core + arm)
      double curveRadius = insideRadius + armMaxLen * armLenFrac;

      // the current angle of the curve
      double ang = i * (2 * Math.PI / nPts);
      double x = curveRadius * Math.cos(ang) + centreX;
      double y = curveRadius * Math.sin(ang) + centreY;
      pts[iPt++] = coord(x, y);
    }
    pts[iPt] = new Coordinate(pts[0]);

    LinearRing ring = geomFact.createLinearRing(pts);
    Polygon poly = geomFact.createPolygon(ring, null);
    return poly;
  }
}

