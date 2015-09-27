package jeql.std.function;

import java.util.Date;
import java.util.UUID;

import jeql.api.function.FunctionClass;
import jeql.util.TypeUtil;

public class ValFunction 
implements FunctionClass
{

  public static boolean isNull(Object o)
  { 
    return o == null;
  }
  
  public static String toString(Object o)
  {
    if (o == null)
      return "";
    return TypeUtil.toString(o);
  }
  
  public static Boolean toBoolean(Object o)
  {
    if (o == null)
      return null;
    if (o instanceof Boolean)
      return (Boolean) o;
    if (o instanceof String) {
      String s = (String) o;
      if (s.length() <= 0) return Boolean.valueOf(false);
      char c = s.charAt(0);
      c = Character.toLowerCase(c);
      return Boolean.valueOf(c == 't' || c == 'y');
    }
    if (o instanceof Number) {
      double n = ((Number) o).doubleValue();
      boolean isFalse = n <= 0;
      return new Boolean(! isFalse);
    }
    return new Boolean(false);
  }
  
  public static boolean isBoolean(Object o)
  {
    if (o == null) return false;
    if (o instanceof Boolean)
      return true;
    return false;
  }

  public static boolean isBooleanConvertible(Object o)
  {
    if (o == null) return false;
    if (o instanceof Boolean)
      return true;
    if (o instanceof String) {
      String s = (String) o;
      if (s.length() <= 0) return false;
      if (s.equalsIgnoreCase("t")
          || s.equalsIgnoreCase("true")
          || s.equalsIgnoreCase("y")
          || s.equalsIgnoreCase("yes")
          || s.equalsIgnoreCase("1")
          || s.equalsIgnoreCase("f")
          || s.equalsIgnoreCase("false")
          || s.equalsIgnoreCase("n")
          || s.equalsIgnoreCase("no")
          || s.equalsIgnoreCase("0")
          ) 
        return true;
    }
    return false;
  }

  public static boolean isNumber(Object o)
  {
    if (o == null) return false;
    if (o instanceof Number) return true;
    return false;
  }

  public static boolean isNumberConvertible(Object o)
  {
    if (o == null) return false;
    if (o instanceof String) {
      try {
        Double.parseDouble((String) o);
      }
      // this is a bit cheezy....
      catch (NumberFormatException e) {
        return false;
      }
      return true;
    }
    if (o instanceof Number) return true;
    return false;
  }

  public static Integer toInt(Object o)
  {
    if (o instanceof Integer) 
      return (Integer) o;
    if (o instanceof Double) 
      return new Integer(((Double) o).intValue());
    if (o instanceof String) {
      try {
        return new Integer(Integer.parseInt((String) o));
      } 
      catch (NumberFormatException ex) {
        // eat it and drop thru to default return
      }
    }
    return null;
  }
  
  public static int toInt(Object o, int defaultVal)
  {
    Integer i = toInt(o);
    if (i == null)
      return defaultVal;
    return i;
  }
  
  public boolean isIntegerCompatible(Object o)
  {
    return toInt(o) != null;
  }
  
  public static Double toDouble(Object o)
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
    return null;
  }
  
  public static double toDouble(Object o, double defaultVal)
  {
    Double d = toDouble(o);
    if (d == null)
      return defaultVal;
    return d;
  }
  
  public static Date toDate(Object o)
  {
    if (o instanceof String) {
      return DateFunction.parse((String) o);
    }
    else if (o instanceof Date)
      return (Date) o;
    return null;
  }
  
  /*
  private static Date toDate(Object o, String formatStr)
  {
    if (o instanceof String) {
      try {
        return DateFunction.parse((String) o, formatStr);
      }
      catch (ParseException e) {
        return null;
      }
    }
    if (o instanceof Date)
      return (Date) o;
    return null;
  }
  */
  
  public static boolean isDate(Object o)
  {
    if (o == null) return false;
    if (o instanceof Date)
      return true;
    return false;
  }

  public static String uuid() {
	  return UUID.randomUUID().toString();
  }
}
