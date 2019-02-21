package jeql.command.io.kml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.row.SchemaUtil;

public class KMLUtil
{

  static String intToDateString(int i)
  {
    String s = Integer.toString(i);
    if (i <= 9999) {
      return s;
    }
    if (s.length() < 8)
      return s.substring(0, 4);
    // return yyyy-mm-dd
    return s.substring(0, 4) + "-" + s.substring(4, 2) + "-" + s.substring(6, 2);
  }

  public static String KML_DATE_PATTERN = "yyyy-MM-dd";
  public static String KML_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static DateFormat KML_DATETIME_FORMAT = new SimpleDateFormat(KML_DATETIME_PATTERN);
  static String formatDate(Object o)
  {
    if (o instanceof Date) {
      String dateStr = KML_DATETIME_FORMAT.format((Date) o);
      // return only date if HMS is zero
      if (dateStr.endsWith("T00:00:00Z"))
        return dateStr.substring(0, 10);
      return dateStr;
    }
  
    // TODO: allow numeric dates of form 2008 or 20080101
    else if (o instanceof Number) {
      int idate = ((Number) o).intValue();
      return intToDateString(idate);
    }
  
    return o.toString();
  }
  public static boolean toBooleanLoose(RowSchema schema, String colName, Row row, boolean defaultVal)
  {
    int index = schema.getColIndex(colName);
    if (index < 0)
      return defaultVal;
    Object oval = row.getValue(index);
  
    boolean val = false;
    if (oval instanceof Boolean) {
      boolean bval = ((Boolean) oval).booleanValue();
      val = bval;
    }
    if (oval instanceof String) {
      String sval = (String) oval;
      val = false;
      if (sval.equalsIgnoreCase("y"))
        val = true;
      if (sval.equalsIgnoreCase("true"))
        val = true;
      if (sval.equalsIgnoreCase("1"))
        val = true;
    }
    if (oval instanceof Integer) {
      int ival = ((Integer) oval).intValue();
      val = ival > 0;
    }
    return val;
  }
  static Geometry geomWithLabelPoint(Geometry g)
  {
    GeometryFactory fact = g.getFactory();
    Geometry labelPoint = g.getInteriorPoint();
    Geometry[] gs = new Geometry[] { labelPoint, g };
    return fact.createGeometryCollection(gs);
  }
  static Geometry getGeometry(RowSchema rowSchema, Row row)
  {
    int index = rowSchema.getColIndexIgnoreCase(KMLCol.KML_GEOMETRY);
    // if no named geometry, use any geometry column
    if (index < 0) {
      index = SchemaUtil.getColumnWithType(rowSchema, Geometry.class);
    }
    //---- allow no geometry, for descriptive placemarks
    if (index < 0)
        return null;
      //throw new ExecutionException("no geometry column in KML data table");
    Geometry val = (Geometry) row.getValue(index);
    return val;
  }

}
