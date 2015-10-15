package jeql.awt.geom;

import java.awt.geom.Point2D;

import com.vividsolutions.jts.geom.Coordinate;

public class DefaultPointConverter
implements PointConverter
{
	public Point2D toView(Coordinate modelCoordinate)
	{
		Point2D p = new Point2D.Double(Math.round(modelCoordinate.x), Math.round(modelCoordinate.y));
		return p;
	}
}