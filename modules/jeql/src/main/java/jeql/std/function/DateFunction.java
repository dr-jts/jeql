package jeql.std.function;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeql.api.function.FunctionClass;
import jeql.util.TypeUtil;

/**
 * Parsing invalid dates returns null, 
 * which allows table processing to continue.
 * 
 * @author Martin Davis
 *
 */
public class DateFunction implements FunctionClass
{
  public static boolean isValid(String date)
  {
    return parse(date) != null;
  }
  
  public static Date parse(String date)
  {
    // if o is a string and contains '-', assume parse with standard JEQL date
    // format
    String s = date;
    if (s.indexOf('.') >= 0) {
      // trim off any decimal places more than 3 digits
      String s2 = fixMilliseconds(s);
      return parse(s2, TypeUtil.DATE_PATTERN);
    }
    // if string has '-' assume yyyy-mm-dd format
    if (s.indexOf('-') >= 0) {
      // test for hh:mm:ss as well
      if (s.indexOf(':') >= 0)
        return parse(s, TypeUtil.DATE_PATTERN_YMD_HMS);

      return parse(s, TypeUtil.DATE_PATTERN_YMD);
    }
    // otherwise, use the default format
    try {
      return DateFormat.getDateInstance().parse(date);
    }
    catch (ParseException e) {
      // eat it
    }
    return null;
  }

  private static String fixMilliseconds(String s)
  {
    int dotIndex = s.indexOf('.');
    if (dotIndex < 0)
      return s + ".000";

    String padded = s + "000";
    return padded.substring(0, dotIndex + 4);
  }

  public static boolean isValid(String date, String format)
  {
    return parse(date, format) != null;
  }
  
  public static Date parse(String date, String format) 
  {
    DateFormat df = new SimpleDateFormat(format);
    try {
      return df.parse(date);
    }
    catch (ParseException e) {
      // eat it
    }
    return null;
  }

  public static String format(Date date, String format)
  {
    DateFormat df = new SimpleDateFormat(format);
    return df.format(date);
  }

  public static Date now()
  {
    return new Date();
  }

  public static double inMillis(Date date)
  {
    return (double) date.getTime();
  }

  /**
   * Computes duration between two dates in seconds
   * 
   * @param d1
   * @param d2
   * @return
   */
  public static double diff(Date d1, Date d2)
  {
    double t1 = (double) d1.getTime();
    double t2 = (double) d2.getTime();
    return (t1 - t2) / 1000;
  }

  public static Date add(Date d, double secs)
  {
    long millis = (long) (secs * 1000);
    long result = d.getTime() + millis;
    return new Date(result);
  }

  public static Date sub(Date d, double secs)
  {
    long millis = (long) (secs * 1000);
    long result = d.getTime() - millis;
    return new Date(result);
  }

  public static double secsSince1970(Date d)
  {
    return ((double) d.getTime()) / 1000.0;
  }

  public static double daysToSeconds(double days)
  {
    return 86400.0 * days;
  }

  public static double hoursToSeconds(double hrs)
  {
    return 3600.0 * hrs;
  }

  public static int month(Date d)
  {
    return d.getMonth();
  }

  public static int dayOfMonth(Date d)
  {
    return d.getDate();
  }

  public static int dayOfWeek(Date d)
  {
    return d.getDay();
  }

  public static int hours(Date d)
  {
    return d.getHours();
  }

  public static int minutes(Date d)
  {
    return d.getMinutes();
  }

  public static int seconds(Date d)
  {
    return d.getSeconds();
  }
  
  public static String monthName(Date d)
  {
    return new DateFormatSymbols().getMonths()[month(d)];
  }
  
  public static String monthShortName(Date d)
  {
    return new DateFormatSymbols().getShortMonths()[month(d)];
  }
  
  public static int monthIndex(String name)
  {
    if (name.length() >= 4) {
      String[] names = new DateFormatSymbols().getMonths();
      for (int i = 0; i < names.length; i++) {
        if (names[i].equalsIgnoreCase(name))
          return i;
      }
    }
    else {
      String[] names = new DateFormatSymbols().getShortMonths();
      for (int i = 0; i < names.length; i++) {
        if (names[i].equalsIgnoreCase(name))
          return i;
      }      
    }
    return -1;
  }
  
  public static String dayName(Date d)
  {
    return new DateFormatSymbols().getWeekdays()[dayOfWeek(d)];
  }
  
  public static String dayShortName(Date d)
  {
    return new DateFormatSymbols().getShortWeekdays()[dayOfWeek(d)];
  }

  public static int dayIndex(String name)
  {
    if (name.length() >= 4) {
      String[] names = new DateFormatSymbols().getWeekdays();
      for (int i = 0; i < names.length; i++) {
        if (names[i].equalsIgnoreCase(name))
          return i;
      }
    }
    else {
      String[] names = new DateFormatSymbols().getShortWeekdays();
      for (int i = 0; i < names.length; i++) {
        if (names[i].equalsIgnoreCase(name))
          return i;
      }      
    }
    return -1;
  }



}
