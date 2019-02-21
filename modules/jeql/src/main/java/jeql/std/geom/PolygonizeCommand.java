package jeql.std.geom;

import java.util.Collection;
import java.util.Iterator;

import jeql.api.command.Command;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;
import jeql.engine.Scope;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.polygonize.Polygonizer;

public class PolygonizeCommand 
  implements Command
{
  public static String GEOMETRY_COL = "GEOMETRY";
  
  private Table inputLines = null;
  private Table result = null;

  public PolygonizeCommand() {
    super();
  }

  public void setDefault(Table inputLines)
  {
    this.inputLines = inputLines;
  }
  
  public Table getResult()
  {
    return result;
  }
  
  public void execute(Scope scope)
  throws Exception
{
    Polygonizer polygonizer = new Polygonizer();
    int geomIndex = SchemaUtil.getColumnWithType(inputLines.getRows().getSchema(), Geometry.class);
    //TODO: handle no geometry case (return empty table)
    RowIterator i = inputLines.getRows().iterator();
    while (true) {
      Row row = i.next();
      if (row == null)
        break;
      Geometry g = (Geometry) row.getValue(geomIndex);
      polygonizer.add(g);
    }
    Collection polys = polygonizer.getPolygons();
    result = createGeometryTable(polys);
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
