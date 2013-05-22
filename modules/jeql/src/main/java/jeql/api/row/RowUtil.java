package jeql.api.row;

import com.vividsolutions.jts.geom.Geometry;

import jeql.engine.TypeConversionException;

public class RowUtil 
{
  /**
   * Gets a string from the given column
   * 
   * @param schema
   * @param colName
   * @param row
   * @return the string value of the column
   * @return null if the column does not exist
   */
  public static String getString(RowSchema schema, String colName, Row row)
  {
    return getString(schema, colName, row, null);
  }
  
  /**
   * Gets a string from the given column, or a default value
   * 
   * @param schema
   * @param colName
   * @param row
   * @return the string value of the column
   * @return defaultValue if the column does not exist
   */
  public static String getString(RowSchema schema, String colName, Row row, String defaultValue)
  {
    int index = schema.getColIndex(colName);
    return getString(index, row, defaultValue);
  }
  
  /**
   * Gets a string from the given column, or a default value
   * 
   * @param index
   * @param row
   * @return the string value of the column
   * @return defaultValue if the column does not exist
   */
  public static String getString(int index, Row row, String defaultValue)
  {
    if (index < 0) return defaultValue;
    Object v = row.getValue(index);
    if (v == null) return null;
    String val = v.toString();
    return val;
  }
  
  public static int getInt(RowSchema schema, String colName, Row row, int defaultVal)
  {
    return getInt(schema.getColIndex(colName), row, defaultVal);
  }

  public static int getInt(int index, Row row, int defaultVal)
  {
    if (index < 0) return defaultVal;
    Object oval = row.getValue(index);
    try {
      int val = ((Number) oval).intValue();
      return val;
    }
    catch (Exception ex) {
      throw new TypeConversionException(oval, Integer.class);
    }
  }

  public static Geometry getGeometry(int index, Row row)
  {
    if (index < 0) return null;
    Object oval = row.getValue(index);
    try {
      Geometry val = (Geometry) oval;
      return val;
    }
    catch (Exception ex) {
      throw new TypeConversionException(oval, Geometry.class);
    }
  }

  public static double getDouble(RowSchema schema, String colName, Row row,
      double defaultVal) {
    int index = schema.getColIndex(colName);
    if (index < 0)
      return defaultVal;
    Object oval = row.getValue(index);
    try {
      double val = ((Number) oval).doubleValue();
      return val;
    } catch (Exception ex) {
      throw new TypeConversionException(oval, Double.class);
    }
  }

  public static boolean getBoolean(RowSchema schema, String colName, Row row,
      boolean defaultVal) {
    int index = schema.getColIndex(colName);
    if (index < 0)
      return defaultVal;
    Object oval = row.getValue(index);
    try {
      boolean val = ((Boolean) oval).booleanValue();
      return val;
    } catch (Exception ex) {
      throw new TypeConversionException(oval, Boolean.class);
    }
  }

}
