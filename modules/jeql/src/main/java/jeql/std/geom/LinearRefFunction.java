package jeql.std.geom;

import jeql.api.annotation.ManDoc;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

@ManDoc (
    description = "Functions for Linear Referencing"
  )
public class LinearRefFunction {

	static GeometryFactory geomFact = new GeometryFactory();
	
	@ManDoc (
	    description = "Projects a point onto a line"
	  )
	public static double project(
	    @ManDoc ( name="line" )
	    Geometry line, 
	    @ManDoc ( name="point" )
	    Geometry pt)
	{
		LengthIndexedLine lil = new LengthIndexedLine(line);
		return lil.project(pt.getCoordinate());
	}
	
	public static Geometry projectPt(Geometry line, Geometry pt)
	{
		LengthIndexedLine lil = new LengthIndexedLine(line);
		double len = lil.project(pt.getCoordinate());
		Coordinate projPt = lil.extractPoint(len);
		return geomFact.createPoint(projPt);
	}
	
	public static Geometry offsetPt(Geometry line, double lenIndex, double offsetDistance)
	{
		LengthIndexedLine lil = new LengthIndexedLine(line);
		Coordinate projPt = lil.extractPoint(lenIndex, offsetDistance);
		return geomFact.createPoint(projPt);
	}
	
	public static Geometry offsetProjectedPt(Geometry line, Geometry pt, double offsetDistance)
	{
		LengthIndexedLine lil = new LengthIndexedLine(line);
		double len = lil.project(pt.getCoordinate());
		Coordinate projPt = lil.extractPoint(len, offsetDistance);
		return geomFact.createPoint(projPt);
	}
}
