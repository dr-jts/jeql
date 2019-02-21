package jeql.std.geom;

import java.util.ArrayList;
import java.util.List;

import jeql.api.function.FunctionClass;

import org.locationtech.jts.dissolve.LineDissolver;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.noding.snapround.GeometryNoder;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

public class GeomProcFunction implements FunctionClass {

	public static Geometry dissolve(Geometry geom) {
		return LineDissolver.dissolve(geom);
	}

	public static Geometry snapRound(
			Geometry geom, double scaleFactor) {
		PrecisionModel pm = new PrecisionModel(scaleFactor);

		Geometry roundedGeom = GeometryPrecisionReducer.reducePointwise(geom,
				pm);

		List geomList = new ArrayList();
		geomList.add(roundedGeom);

		GeometryNoder noder = new GeometryNoder(pm);
		List lines = noder.node(geomList);

		return geom.getFactory().buildGeometry(lines);
	}

	public static Geometry polygonize(Geometry geom) {
		List lines = LineStringExtracter.getLines(geom);
		Polygonizer polygonizer = new Polygonizer();
		polygonizer.add(lines);
		return geom.getFactory().buildGeometry(polygonizer.getPolygons());
	}

}
