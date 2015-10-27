package jeql.awt.geom;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Converts JTS Geometry objects into Java 2D Shape objects
 */
public class Java2DConverter {
	private static double POINT_MARKER_SIZE = 3.0;
	private PointConverter pointConverter;

	public Java2DConverter(PointConverter pointConverter) {
		this.pointConverter = pointConverter;
	}

	public Java2DConverter() {
		this(new DefaultPointConverter());
	}

	private Shape toShape(Polygon p) {
		ArrayList holeVertexCollection = new ArrayList();

		for (int j = 0; j < p.getNumInteriorRing(); j++) {
			holeVertexCollection.add(
				toViewCoordinates(p.getInteriorRingN(j).getCoordinates()));
		}

		return new PolygonShape(
			toViewCoordinates(p.getExteriorRing().getCoordinates()),
			holeVertexCollection);
	}

	private Coordinate[] toViewCoordinates(Coordinate[] modelCoordinates)
		{
		Coordinate[] viewCoordinates = new Coordinate[modelCoordinates.length];

		for (int i = 0; i < modelCoordinates.length; i++) {
			Point2D point2D = toViewPoint(modelCoordinates[i]);
			viewCoordinates[i] = new Coordinate(point2D.getX(), point2D.getY());
		}

		return viewCoordinates;
	}

	private Shape toShape(GeometryCollection gc)
		{
		GeometryCollectionShape shape = new GeometryCollectionShape();

		for (int i = 0; i < gc.getNumGeometries(); i++) {
			Geometry g = (Geometry) gc.getGeometryN(i);
			shape.add(toShape(g));
		}

		return shape;
	}

	private GeneralPath toShape(MultiLineString mls)
		{
		GeneralPath path = new GeneralPath();

		for (int i = 0; i < mls.getNumGeometries(); i++) {
			LineString lineString = (LineString) mls.getGeometryN(i);
			path.append(toShape(lineString), false);
		}

		//BasicFeatureRenderer expects LineStrings and MultiLineStrings to be
		//converted to GeneralPaths. [Jon Aquino]
		return path;
	}

	private GeneralPath toShape(LineString lineString)
		{
		GeneralPath shape = new GeneralPath();
		Point2D viewPoint = toViewPoint(lineString.getCoordinateN(0));
		shape.moveTo((float) viewPoint.getX(), (float) viewPoint.getY());

		for (int i = 1; i < lineString.getNumPoints(); i++) {
			viewPoint = toViewPoint(lineString.getCoordinateN(i));
			shape.lineTo((float) viewPoint.getX(), (float) viewPoint.getY());
		}

		//BasicFeatureRenderer expects LineStrings and MultiLineStrings to be
		//converted to GeneralPaths. [Jon Aquino]
		return shape;
	}

	private Shape toShape(Point point){
		Rectangle2D.Double pointMarker =
			new Rectangle2D.Double(
				0.0,
				0.0,
				POINT_MARKER_SIZE,
				POINT_MARKER_SIZE);
		Point2D viewPoint = toViewPoint(point.getCoordinate());
		pointMarker.x = (double) (viewPoint.getX() - (POINT_MARKER_SIZE / 2));
		pointMarker.y = (double) (viewPoint.getY() - (POINT_MARKER_SIZE / 2));

		return pointMarker;
	}

  private Point2D toViewPoint(Coordinate modelCoordinate)
  {
    // Do the rounding now; don't rely on Java 2D rounding, because it
    // seems to do it differently for drawing and filling, resulting in the draw
    // being a pixel off from the fill sometimes. [Jon Aquino]
    Point2D viewPoint = pointConverter.toView(modelCoordinate);
    viewPoint.setLocation(Math.round(viewPoint.getX()), Math.round(viewPoint
        .getY()));
    return viewPoint;
  }

	/**
	 * If you pass in a general GeometryCollection, note that a Shape cannot
	 * preserve information about which elements are 1D and which are 2D.
	 * For example, if you pass in a GeometryCollection containing a ring and a
	 * disk, you cannot render them as such: if you use Graphics.fill, you'll get
	 * two disks, and if you use Graphics.draw, you'll get two rings. Solution:
	 * create Shapes for each element.
	 */
	public Shape toShape(Geometry geometry)
  {
		if (geometry.isEmpty()) {
			return new GeneralPath();
		}

		if (geometry instanceof Polygon) {
			return toShape((Polygon) geometry);
		}

		if (geometry instanceof MultiPolygon) {
			return toShape((MultiPolygon) geometry);
		}

		if (geometry instanceof LineString) {
			return toShape((LineString) geometry);
		}

		if (geometry instanceof MultiLineString) {
			return toShape((MultiLineString) geometry);
		}

		if (geometry instanceof Point) {
			return toShape((Point) geometry);
		}

		if (geometry instanceof GeometryCollection) {
			return toShape((GeometryCollection) geometry);
		}

		throw new IllegalArgumentException(
			"Unrecognized Geometry class: " + geometry.getClass());
	}
}
