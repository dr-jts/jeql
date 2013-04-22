package jeql.std.function;

import jeql.api.function.FunctionClass;

public class MathFunction 
implements FunctionClass
{
  public static double  abs(double a) { return Math.abs(a); }
  public static int     iabs(int a) { return Math.abs(a); }
  public static double  acos(double a) { return Math.acos(a); }
  public static double  asin(double a) { return Math.asin(a); }
  public static double  atan(double a) { return Math.atan(a); }
  public static double  atan2(double y, double x) { return Math.atan2(y, x); }
  public static double  ceil(double a) { return Math.ceil(a); }
  public static double  cos(double a) { return Math.cos(a); }
  public static double  exp(double a) { return Math.exp(a); }
  public static double  floor(double a) { return Math.floor(a); }
  public static double  IEEEremainder(double f1, double f2) { return Math.IEEEremainder(f1, f2); }
  public static double  log(double a) { return Math.log(a); }
  public static double  log10(double a) { return Math.log(a) / Math.log(10); }
  public static double  max(double a, double b) { return Math.max(a, b); }
  public static int     imax(int a, int b) { return Math.max(a, b); }
  public static double  min(double a, double b) { return Math.min(a, b); }
  public static int     imin(int a, int b) { return Math.min(a, b); }
  public static double  pow(double a, double b) { return Math.pow(a, b); }
  public static double  random() { return Math.random(); }
  public static double  random(double n) { return n * Math.random(); }
  public static double  random(double lo, double hi) { return lo + (hi - lo) * Math.random(); }
  public static double  rint(double a) { return Math.rint(a); }
  public static int     round(double a) { return (int) Math.round(a); }
  public static double  sin(double a) { return Math.sin(a); }
  public static double  sqrt(double a) { return Math.sqrt(a); }
  public static double  tan(double a) { return Math.tan(a); }
  public static double  toDegrees(double a) { return Math.toDegrees(a); }
  public static double  toRadians(double a) { return Math.toRadians(a); }
  
  public static double pi() { return Math.PI; }
  public static double e() { return Math.E; }
 
  public static double square(double x) { return x*x; }
  public static double cube(double x) { return x*x*x; }
  

  public static int irandom(int min, int max)
  {
    return (int) Math.floor(Math.random() * (max - min + 2)) + min;
  }
  
  /**
   * Rounds a double to a given number of decimal places.
   * If the number of decimal places is negative, 
   * the number will be rounded to that number 
   * of places <b>left</b> of the decimal point.
   * 
   * @param a
   * @param places
   * @return the rounded number
   */
  public static double roundTo(double a, int places) 
  {
    if (a < 0) 
      return -roundPositiveToPow10(-a, places);
    else 
      return roundPositiveToPow10(a, places);
  }

  private static double roundPositiveToPow10(double a, int places)
  {
    double pow10 = pow10Pos(Math.abs(places));
    if (places < 0)
      return Math.round(a / pow10) * pow10;
    else
      return Math.round(a * pow10) / pow10;
  }
  
  /**
   * Returns a positive power of 10.
   * 
   * @param n the positive, integral exponent
   * @return the positive power of 10
   */
  public static double pow10Pos(int n)
  {
    return Math.floor(Math.pow(10, n) + 0.5);
  }
  
  /**
   * Truncates towards zero.
   * 
   * @param a
   * @return
   */
  public static double truncate(double a)
  {
    if (a < 0.0)
      return ceil(a);
    else 
      return floor(a);
  }
  
  public static double truncate(double a, int places)
  {
    double aPos = Math.abs(a);
    double pow10 = pow10Pos(Math.abs(places));
    
    double truncPos;
    if (places < 0) {
      truncPos = Math.floor(aPos / pow10) * pow10;  
    }
    else {
      truncPos = Math.floor(aPos * pow10) / pow10;
    }
    
    if (a < 0.0)
      return -truncPos;
    return truncPos;
  }
  
  public static double clamp(double x, double min, double max)
  {
    if (x < min) return min;
    if (x > max) return max;
    return x;
  }

  /**
   * Maps a value in a range to another range.
   * 
   * @param v
   * @param inLo
   * @param inHi
   * @param outLo
   * @param outHi
   * @return
   */
  public static double map(double v, double inLo, double inHi,
        double outLo, double outHi)
  {
    double v2 = (v - inLo)/(inHi - inLo);
    if (v2 < 0) v2 = 0;
    if (v2 > 1) v2 = 1;
    return v2 * (outHi - outLo) + outLo;
  }

  /**
   * Maps a value in a range into the range [0,1].
   *  
   * @param v
   * @param inLo
   * @param inHi
   * @return
   */
  public static double mapToUnit(double v, double inLo, double inHi)
  {
    return map(v, inLo, inHi, 0.0, 1.0);
  }
}
