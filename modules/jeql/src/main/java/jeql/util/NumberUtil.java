package jeql.util;

public class NumberUtil
{
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

}
