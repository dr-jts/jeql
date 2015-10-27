package jeql.std.geom;

import java.util.ArrayList;
import java.util.List;

import jeql.api.function.FunctionClass;

import com.vividsolutions.jts.dissolve.LineDissolver;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.util.LineStringExtracter;
import com.vividsolutions.jts.noding.snapround.GeometryNoder;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

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
