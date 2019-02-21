package jeql.io.shapefile;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import jeql.api.row.ArrayRowList;
import jeql.api.row.Row;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

class TableShpWriter {
  public TableShpWriter() {
  }

  public void write(ArrayRowList rowList, String shpfilePathNoExt)
      throws Exception {
    int geomIndex = ShapefileWriter.findFirstGeometryIndex(rowList.getSchema());

    // this gc will be a collection of either multi-points, multi-polygons, or
    // multi-linestrings
    // polygons will have the rings in the correct order
    GeometryCollection gc = creatShpGeometryCollection(rowList.getRows(),
        geomIndex);

    int coordDim = Shapefile.XY; // default is XY
    if (gc.getNumGeometries() > 0) {
      coordDim = ShapefileWriter.determineCoordinateDimension(gc
          .getGeometryN(0));
    }

    URL url = new URL("file", "localhost", shpfilePathNoExt + ".shp");
    ShpWriter shpWriter = new ShpWriter(url);
    shpWriter.write(gc, coordDim);

    // write index
    // *
    // don't bother writing index
    // String shxPath = shpfilePathNoExt + ".shx";
    // writer.writeIndex(gc, coordDim, shxPath);
    // */
  }

  public void OLDwrite(ArrayRowList rowList, String shpfilePathNoExt)
      throws Exception {
    int geomIndex = ShapefileWriter.findFirstGeometryIndex(rowList.getSchema());

    // this gc will be a collection of either multi-points, multi-polygons, or
    // multi-linestrings
    // polygons will have the rings in the correct order
    GeometryCollection gc = creatShpGeometryCollection(rowList.getRows(),
        geomIndex);

    int coordDim = Shapefile.XY; // default is XY
    if (gc.getNumGeometries() > 0) {
      coordDim = ShapefileWriter.determineCoordinateDimension(gc
          .getGeometryN(0));
    }

    URL url = new URL("file", "localhost", shpfilePathNoExt + ".shp");
    ShpWriter shpWriter = new ShpWriter(url);
    shpWriter.write(gc, coordDim);

    // write index
    // *
    // don't bother writing index
    // String shxPath = shpfilePathNoExt + ".shx";
    // writer.writeIndex(gc, coordDim, shxPath);
    // */
  }

  private static final int DEFAULT_GEOM_TYPE = Shapefile.POINT;

  /**
   * Find the generic geometry type for the collection of geometries. The
   * collection is expected to be homogeneous. For Polygons and lines,
   * shapefiles handle single and multi geometries uniformly, so no special
   * logic is required. For points, shapefiles treat single points and
   * multipoints as distinct types. Because of this, the type is only returned
   * as POINT if all geometries are single points. Otherwise, MULTIPOINT is
   * returned.
   * 
   * @param rows
   *          rows containing geometries
   * @param geomIndex
   *          index of column containing geometry
   * @return a code indicating the type of geometry to create
   **/
  private static int findBestGeometryType(List rows, int geomIndex) {
    boolean isPoint = false;
    boolean isMultiPoint = false;

    int rowCount = 0;
    for (Iterator i = rows.iterator(); i.hasNext();) {
      Row row = (Row) i.next();
      rowCount++;

      // sanity check on geomIndex, and provide nicer error msg
      if (geomIndex < 0 || geomIndex >= row.size()) {
        throw new IllegalArgumentException(
            "Unable to determine geometry column");
      }

      Geometry geom = (Geometry) row.getValue(geomIndex);

      if (geom instanceof Point) {
        isPoint = true;
      }
      if (geom instanceof MultiPoint) {
        isMultiPoint = true;
      }
      if (geom instanceof LineString) {
        return Shapefile.ARC;
      }
      if (geom instanceof MultiLineString) {
        return Shapefile.ARC;
      }
      if (geom instanceof Polygon) {
        return Shapefile.POLYGON;
      }
      if (geom instanceof MultiPolygon) {
        return Shapefile.POLYGON;
      }
    }
    // occurrence of any multipoint forces entire file to be multipoint
    if (isMultiPoint)
      return Shapefile.MULTIPOINT;
    if (isPoint)
      return Shapefile.POINT;

    if (rowCount == 0)
      return DEFAULT_GEOM_TYPE;

    // unknown type
    return 0;
  }

  /**
   * return a single geometry collection <Br>
   * result.GeometryN(i) = the i-th feature in the FeatureCollection<br>
   * All the geometry types will be the same type (ie. all polygons) - or they
   * will be set to<br>
   * NULL geometries<br>
   * <br>
   * GeometryN(i) = {Multipoint,Multilinestring, or Multipolygon)<br>
   * 
   * @param rows
   *          input rows
   * @param geomIndex
   *          index of geometry in row
   */
  public GeometryCollection creatShpGeometryCollection(List rows, int geomIndex)
      throws Exception {
    Geometry[] allGeoms = new Geometry[rows.size()];

    int geomtype = findBestGeometryType(rows, geomIndex);

    if (geomtype == 0) {
      throw new IllegalArgumentException(
          "Could not determine shapefile geometry type (data is either empty or all GeometryCollections)");
    }

    for (int t = 0; t < rows.size(); t++) {
      Row row = (Row) rows.get(t);
      Geometry geom = (Geometry) row.getValue(geomIndex);

      allGeoms[t] = ShapeGeometryBuilder.buildGeometry(geom, geomtype);
    }
    GeometryCollection result = new GeometryCollection(allGeoms,
        new PrecisionModel(), 0);
    return result;
  }

}