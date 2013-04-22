package jeql.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeql.api.error.ExecutionException;
import jeql.std.function.StringFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

public class TypeUtil 
{
  private static WKTWriter wktWriter = new WKTWriter(3);
  
  public static int compareValue(Object v1, Object v2) 
  {
    if (v1 instanceof Comparable) {
      if (v2 == null)
        return -1;
      return ((Comparable) v1).compareTo(v2);
    }
    if (v1 instanceof Boolean)
      return compareValue( ((Boolean) v1).booleanValue(),
                      ((Boolean) v2).booleanValue());
    /*
     // all of these types implement Comparable
    if (v1 instanceof Double)
      return ((Double) v1).compareTo((Double) v2);
    if (v1 instanceof Integer)
      return ((Integer) v1).compareTo((Integer) v2);
    if (v1 instanceof String)
      return ((String) v1).compareTo((String) v2);
      */
    if (v1 instanceof Geometry)
      return ((Geometry) v1).compareTo((Geometry) v2);

    // handle null values
    if (v1 == v2)
      return -1;
    
    // TODO: MD - this needs work...
    throw new IllegalArgumentException("Unknown value type: "
        + (v1 != null ? v1.getClass().getName() : "null"));
  }

  public static boolean isEqual(Object v1, Object v2) {
    return compareValue(v1, v2) == 0;
  }
  
  /**
   * Compares two boolean values, with <tt>false</tt> being less than <tt>true</tt>.
   * 
   * @param b0
   * @param b1
   * @return
   */
  public static int compareValue(boolean b0, boolean b1)
  {
    if (b0 == b1) return 0;
    if (b0) return 1;
    return -1;
  }
  
  public static boolean isSameType(Object o1, Object o2)
  {
    return o1.getClass() == o2.getClass();
  }
  
  public static boolean isSameType(Class c1, Class c2)
  {
    return c1 == c2;
  }
  
  public static void checkSameType(Object o1, Object o2)
  {
    if (o1.getClass() != o2.getClass())
      throw new ExecutionException("Objects are of different types");
  }
  
  public static void checkCorrectType(Object o1, Class reqType)
  {
    // can't check type of null
    if (o1 == null) return;
    
    if (! reqType.isAssignableFrom(o1.getClass()))
      throw new ExecutionException("Expected type " + nameForType(reqType)
           + " but found type " + nameForType(o1.getClass()));
  }
  
  public static double toDouble(Object o)
  {
    if (o instanceof Integer) 
      return new Double( ((Integer) o).intValue());
    if (o instanceof Double) 
      return ((Double) o);
    if (o instanceof String) {
      try {
        return new Double(Double.parseDouble((String) o));
      } 
      catch (NumberFormatException ex) {
        // eat it and drop thru to default return
      }
    }
    return 0.0;
  }
  
  public static boolean toBoolean(Object o)
  {
    if (o == null) return false;
    if (o instanceof Boolean)
      return (Boolean) o;
    return false;
  }
  
  public static Class typeForName(String name)
  {
    if (name.equalsIgnoreCase("string")) return String.class;
    if (name.equalsIgnoreCase("int")) return Integer.class;
    if (name.equalsIgnoreCase("integer")) return Integer.class;
    if (name.equalsIgnoreCase("boolean")) return Boolean.class;
    if (name.equalsIgnoreCase("geometry")) return Geometry.class;
    if (name.equalsIgnoreCase("double")) return Double.class;
    if (name.equalsIgnoreCase("date")) return Date.class;
    return null;
  }
  
  public static String nameForType(Class clz)
  {
    // TODO: check for valid JEQL type
    return ClassUtil.classname(clz);
  }
  
  public static double MAX_DECIMAL = 1.0e15;
  public static double MIN_DECIMAL = 1.0e-15;
  
  public static String DECIMAL_FORMAT_PATTERN = "0.0###############";
  public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
  
  public static String SCI_FORMAT_PATTERN = "0.0###############E0";
  public static DecimalFormat SCI_FORMAT = new DecimalFormat(SCI_FORMAT_PATTERN);
  
  public static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
  public static DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
  
  public static String DATE_PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
  public static DateFormat DATE_FORMAT_YMD_HMS = new SimpleDateFormat(DATE_PATTERN_YMD_HMS);
  
  public static String DATE_PATTERN_YMD = "yyyy-MM-dd";
  public static DateFormat DATE_FORMAT_YMD = new SimpleDateFormat(DATE_PATTERN_YMD);
  
  public static String toString(Object o)
  {
    if (o == null)
      return "null";
    if (o instanceof Date) {
      return DATE_FORMAT.format((Date) o);
    }
    if (o instanceof Double) {
      double d = ((Double) o).doubleValue();
      double abs = Math.abs(d);
      if (abs > MAX_DECIMAL || abs < MIN_DECIMAL)
        return SCI_FORMAT.format(d);
      return DECIMAL_FORMAT.format(d);
    }
    if (o instanceof Geometry) {
      return wktWriter.writeFormatted((Geometry) o);
    }
    return o.toString();
  }
  
  public static String toCodeString(Object o)
  {
    if (o instanceof String) {
      return "\"" + o + "\"";
    }
    return toString(o);
  }
  
  public static int MAX_LEN = 20;
  public static int MAX_GEOM_LEN = 40;
  
  public static String toCodeStringLimited(Object o)
  {
    if (o instanceof String) {
      String s = (String) o;
      if (s.length() <= MAX_LEN) return "\"" + s + "\"";
      return "\"" + StringFunction.leftStr((String) o, MAX_LEN) + " ...\"";
    }
    if (o instanceof Geometry) {
      Geometry g = (Geometry) o;
      if (g.getNumPoints() <= 5)
        return g.toString();
      
      //TODO: do this better - display just a single coordinate if long
      return StringFunction.leftStr(g.toString(), MAX_GEOM_LEN)+" ...)";
    }
    return toString(o);
  }
  

}
