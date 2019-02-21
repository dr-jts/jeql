package jeql.std.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jeql.api.command.Command;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;
import jeql.engine.Scope;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

public class VoronoiCommand 
  implements Command
{
  public static String GEOMETRY_COL = "GEOMETRY";
  
  private Table input = null;
  private Table result = null;

  public VoronoiCommand() {
    super();
  }

  public void setDefault(Table input)
  {
    this.input = input;
  }
  
  public Table getResult()
  {
    return result;
  }
  
  public void execute(Scope scope)
  throws Exception
  {
    int geomIndex = SchemaUtil.getColumnWithType(input.getRows().getSchema(), Geometry.class);
    //TODO: handle no geometry case (return empty table)
    List pts = extractCoordinates(input.getRows().iterator(), geomIndex);
    VoronoiDiagramBuilder voronoiBuilder = new VoronoiDiagramBuilder();
    voronoiBuilder.setSites(pts);
    Geometry voronoi = voronoiBuilder.getDiagram(GeomFunction.geomFactory);
    
    List polys = toListOfGeometry(voronoi);
    
    result = createGeometryTable(polys);
  }
  
  private List extractGeometry(RowIterator i, int geomIndex)
  {
    List geoms = new ArrayList();
    while (true) {
      Row row = i.next();
      if (row == null)
        break;
      Geometry g = (Geometry) row.getValue(geomIndex);
      geoms.add(g);
    }
    return geoms;
  }
  
  private List extractCoordinates(RowIterator i, int geomIndex)
  {
    CoordinateList coordList = new CoordinateList();
    while (true) {
      Row row = i.next();
      if (row == null)
        break;
      Geometry g = (Geometry) row.getValue(geomIndex);
      Coordinate[] pts = g.getCoordinates();
      coordList.add(pts, false);
    }
    return coordList;
  }
  
  private static List toListOfGeometry(Geometry g)
  {
    List geoms = new ArrayList();
    for (int i = 0; i < g.getNumGeometries(); i++) {
      geoms.add(g.getGeometryN(i));
    }
    return geoms;
  }
  
  private static Table createGeometryTable(Collection geoms)
  {
    ArrayRowList rl = new ArrayRowList(new RowSchema(GEOMETRY_COL, Geometry.class));
    for (Iterator i = geoms.iterator(); i.hasNext(); ) {
      Geometry g = (Geometry) i.next();
      BasicRow row = new BasicRow(1);
      row.setValue(0, g);
      rl.add(row);
    }
    return new Table(rl);
  }
}
