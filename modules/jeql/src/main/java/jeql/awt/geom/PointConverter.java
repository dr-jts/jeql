package jeql.awt.geom;

import java.awt.geom.Point2D;

import com.vividsolutions.jts.geom.Coordinate;

public interface PointConverter {
	public Point2D toView(Coordinate modelCoordinate);
}