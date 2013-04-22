package jeql.std.geom;

import jeql.jts.geodetic.GeodeticMetrics;

import com.vividsolutions.jts.geom.Coordinate;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class GeodeticMetricsTest extends TestCase {

  public static void main(String args[]) {
    TestRunner.run(GeodeticMetricsTest.class);
  }

    public GeodeticMetricsTest(String name) {
        super(name);
    }

    public void test1()
    {
      Coordinate p1 = new Coordinate(-120, 40);  Coordinate p2 = new Coordinate(-120, 49);
      //Coordinate p1 = new Coordinate(-120, 40);  Coordinate p2 = new Coordinate(60, -41);
      
      double distSphere = GeodeticMetrics.distanceSphere(p1, p2);
      System.out.println("Sphere: " + distSphere);
      
      double distLam = GeodeticMetrics.distanceLambert(p1, p2);
      System.out.println("Lambert: " + distLam);
      
      double distVin = GeodeticMetrics.distanceVincenty(p1.y, p1.x, p2.y, p2.x); 
      System.out.println("Vincenty: " + distVin);
    }
    
    public void testApproachingAntipodal()
    {
      System.out.println(GeodeticMetrics.distanceVincenty(40, -120, -41, 60));
      System.out.println(GeodeticMetrics.distanceVincenty(40, -120, -40, 61));
      System.out.println(GeodeticMetrics.distanceVincenty(40, -120, -40, 59));
      
      System.out.println(GeodeticMetrics.distanceVincenty(40, -120, -39, 60));
    }
    

}
