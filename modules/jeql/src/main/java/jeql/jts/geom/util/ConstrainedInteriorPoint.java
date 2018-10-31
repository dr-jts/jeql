package jeql.jts.geom.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;


public class ConstrainedInteriorPoint {
  
  public static Coordinate getPoint(Polygon poly) {
    ConstrainedInteriorPoint lbl = new ConstrainedInteriorPoint(poly);
    return lbl.getPoint();
  }
  
  public static Coordinate getPoint(Polygon poly, Envelope constraint) {
    ConstrainedInteriorPoint lbl = new ConstrainedInteriorPoint(poly, constraint);
    return lbl.getPoint();
  }
  
  private Polygon poly;
  private double scanY;
  private List<Double> crossings = new ArrayList<Double>();
  private Envelope constraint;

  public ConstrainedInteriorPoint(Polygon poly) {
    this.poly = poly;
  }
  
  public ConstrainedInteriorPoint(Polygon poly, Envelope constraint) {
    this.poly = poly;
    this.constraint = constraint; 
  }
  
  public Coordinate getPoint() {
    // TODO: check if constraint does not overlap poly - return empty if so
    scanY = findScanY(poly, constraint);
    scan(poly);
    crossings.sort(new DoubleComparator());
    Coordinate pt = findBestMidpoint();
    return pt;
  }

  private void scan(Polygon poly) {
    scanRing((LinearRing) poly.getExteriorRing());
    for (int i = 0; i < poly.getNumInteriorRing(); i++) {
      scanRing((LinearRing) poly.getInteriorRingN(i));
    }
  }
  
  private void scanRing(LinearRing ring) {
    CoordinateSequence seq = ring.getCoordinateSequence();
    for (int i = 1; i < seq.size(); i++) {
      Coordinate ptPrev = seq.getCoordinate(i - 1);
      Coordinate pt = seq.getCoordinate(i);
      scanSegment(ptPrev, pt);
    }
  }
  
  private double findScanY(Polygon poly, Envelope constraint) {
    // TODO: use centroid as better Y?
    Envelope env = poly.getEnvelopeInternal();
    double ymin = env.getMinY();
    double ymax = env.getMaxY();
    // Assumes constraint overlaps polygon envelope
    if (constraint != null) {
      ymin = Math.max(ymin, constraint.getMinY());
      ymax = Math.min(ymax, constraint.getMaxY());
    }
    return (ymin + ymax) / 2;
  }

  private void scanSegment(Coordinate p0, Coordinate p1) {
    // skip non-crossing segments
    if (! crosses(p0, p1, scanY)) return;
    // skip horizontal lines
    if (p0.y ==  p1.y) return;
    // handle vertices on scan-line
    // downward segment does not include start point
    if (p0.y == scanY && p1.y < scanY) return;
    // upward segment does not include endpoint
    if (p1.y == scanY && p0.y < scanY) return;
   
    // add a crossing
    double xInt = intersection(p0, p1, scanY);
    crossings.add(xInt);
  }

  private Coordinate findBestMidpoint() {
    Envelope env = poly.getEnvelopeInternal();
    double xCon1 = env.getMinX();
    double xCon2 = env.getMaxX();
    if (constraint != null) {
      xCon1 = Math.max(xCon1, constraint.getMinX());
      xCon2 = Math.min(xCon2, constraint.getMaxX());
    }
    /*
     * Entries in crossings list should occur in pairs 
     * representing a section of the scan line interior to the polygon
     * (which may be zero-length)
     */
    double xBest1 = 0;
    double xBest2 = 0;
    double maxDist = -1;
    for (int i = 0; i < crossings.size(); i += 2) {
      double x1 = crossings.get(i);
      // TODO: check for i+1 out of range
      double x2 = crossings.get(i + 1);
      
      // skip if outside constraint region
      if (x2 < xCon1) continue;
      if (x1 > xCon2) continue;

      // clip to constraint 
      double xClip1 = Math.max(x1,  xCon1);
      double xClip2 = Math.min(x2,  xCon2);
      
      double dist = xClip2 - xClip1;
      if (dist > maxDist) {
        xBest1 = xClip1;
        xBest2 = xClip2;
        maxDist = dist;
      }
    }
    double xMid = (xBest1 + xBest2) / 2;
    return new Coordinate(xMid, scanY);
  }
  
  /**
   * Non-robust intersection of a segment with a horizontal line.
   * Inputs are not expected to have high precision, so
   * this computation should be adequate.
   * 
   * @param p0
   * @param p1
   * @param scanY2
   * @return
   */
  private static double intersection(Coordinate p0, Coordinate p1, double Y) {
    double x0 = p0.x;
    double x1 = p1.x;
  
    if (x0 == x1) return x0;
    double m = (p1.x - p0.y) / (x1 - x0);
    double x = ((Y - p0.y) / m) + x0;
    return x;
  }

  private boolean crosses(Coordinate p0, Coordinate p1, double Y) {
    if (p0.y > Y && p1.y > Y) return false;
    if (p0.y < Y && p1.y < Y) return false;
    return true;
  }
  
  private static class DoubleComparator implements Comparator<Double> {
    public int compare(Double v1, Double v2) {
        return v1 < v2 ? -1 : v1 > v2 ? +1 : 0;
    }
  }
}
