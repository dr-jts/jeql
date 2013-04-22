package jeql.jts.geom.util;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class PathConverter 
{
  public static List convert(PathIterator pathIt)
  {
    PathConverter pc = new PathConverter(pathIt);
    return pc.extractCoordSeqs();
  }
  
  public static Geometry convert(PathIterator pathIt, GeometryFactory geomFact)
  {
    PathConverter pc = new PathConverter(pathIt);
    return pc.convert(geomFact);
  }
  
  private PathIterator pathIt;
  
  public PathConverter(PathIterator pathIt) {
    this.pathIt = pathIt;
  }

  public Geometry convert(GeometryFactory geomFact)
  {
    List pathPtSeq = extractCoordSeqs();
    
    List polys = new ArrayList();
    int seqIndex = 0;
    while (seqIndex < pathPtSeq.size()) {
      // assume next seq is shell 
      // TODO: test this
      Coordinate[] pts = (Coordinate[]) pathPtSeq.get(seqIndex);
      LinearRing shell = geomFact.createLinearRing(pts);
      seqIndex++;
      
      List holes = new ArrayList();
      // add holes as long as rings are CCW
      while (seqIndex < pathPtSeq.size() && isHole((Coordinate[]) pathPtSeq.get(seqIndex))) {
        Coordinate[] holePts = (Coordinate[]) pathPtSeq.get(seqIndex);
        LinearRing hole = geomFact.createLinearRing(holePts);
        holes.add(hole);
        seqIndex++;
      }
      LinearRing[] holeArray = GeometryFactory.toLinearRingArray(holes);
      polys.add(geomFact.createPolygon(shell, holeArray));
    }
    return geomFact.buildGeometry(polys);
  }
  
  private boolean isHole(Coordinate[] pts)
  {
    return CGAlgorithms.isCCW(pts);
  }
  
  private List extractCoordSeqs()
  {
    List coordArrays = new ArrayList();
    while (! pathIt.isDone()) {
      Coordinate[] pts = nextCoordinateArray(pathIt);
      if (pts == null)
        break;
      coordArrays.add(pts);
    }
    return coordArrays;
  }
  
  private double[] pathPt = new double[6];
  
  private Coordinate[] nextCoordinateArray(PathIterator pathIt)
  {
    CoordinateList coordList = null;
    boolean isDone = false;
    while (! pathIt.isDone()) {
      int segType = pathIt.currentSegment(pathPt);
      switch (segType) {
      case PathIterator.SEG_MOVETO:
        if (coordList != null) {
          // don't advance pathIt, to retain start of next path if any
          isDone = true;
        }
        else {
          coordList = new CoordinateList();
          coordList.add(new Coordinate(pathPt[0], pathPt[1]));
          pathIt.next();
        }
        break;
      case PathIterator.SEG_LINETO:
        coordList.add(new Coordinate(pathPt[0], pathPt[1]));
        pathIt.next();
        break;
      case PathIterator.SEG_CLOSE:  
        coordList.closeRing();
        pathIt.next();
        isDone = true;   
        break;
      }
      if (isDone) 
        break;
    }
    return coordList.toCoordinateArray();
  }

}
