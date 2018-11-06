package jeql.workbench;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;

public class Inspector {
  public static String inspectString(Object o)
  {
    if (o == null) return "";
    if (o instanceof Geometry) {
      WKTWriter writer = new WKTWriter();
      writer.setMaxCoordinatesPerLine(2);
      String str = writer.writeFormatted((Geometry) o);
      return str;
    }
    return o.toString();
  }
  
  public static String inspectString(RowSchema schema, Row row) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < schema.size(); i++) {
      sb.append(schema.getName(i) + ": ");
      sb.append(inspectString(row.getValue(i)));
      sb.append("\n");
    }
    return sb.toString();
  }
}
