package jeql.util;

import java.awt.Color;

import com.vividsolutions.jts.math.MathUtil;

public class ColorUtil 
{

  public static int RGBtoInt(String rgb) {
    int r = Integer.parseInt(getR(rgb), 16);
    int g = Integer.parseInt(getG(rgb), 16);
    int b = Integer.parseInt(getB(rgb), 16);

    int clr = r;
    clr = clr << 8 | g;
    clr = clr << 8 | b;
    return clr;
  }

  public static Color RGBtoColor(String rgb) {
    int r = Integer.parseInt(getR(rgb), 16);
    int g = Integer.parseInt(getG(rgb), 16);
    int b = Integer.parseInt(getB(rgb), 16);
    
    return new Color(r, g, b);
  }
  
  public static Color RGBAtoColor(String rgba) {
    if (! isValidColor(rgba)) return null;
    
    int r = Integer.parseInt(getR(rgba), 16);
    int g = Integer.parseInt(getG(rgba), 16);
    int b = Integer.parseInt(getB(rgba), 16);
    // if no A value return an opaque color
    if (rgba.length() >= 8) {
      int a = Integer.parseInt(getA(rgba), 16);
      return new Color(r, g, b, a);
    }
    return new Color(r, g, b);
  }

  public static String getR(String rgb) {
    return rgb.substring(0, 2);
  }

  public static String getG(String rgb) {
    return rgb.substring(2, 4);
  }

  public static String getB(String rgb) {
    return rgb.substring(4, 6);
  }

  public static String getA(String rgba) 
  {
    if (rgba.length() < 8) return "ff";
    return rgba.substring(6, 8);
  }

  public static float[] toHSV(Color clr)
  {
    float[] hsv = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsv);
    return hsv;
  }
  
  public static Color gray(int grayVal)
  {
    return new Color(grayVal, grayVal, grayVal);
  }
  
  public static Color opaque(Color clr)
  {
    return new Color(clr.getRed(), clr.getGreen(), clr.getBlue());
  }
  
  public static Color lighter(Color clr)
  {
    float[] hsb = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
    hsb[1] *= 0.4;
    return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
  }
  
  public static Color lighter(Color clr, double saturationFraction)
  {
    float[] hsb = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
    hsb[1] *= saturationFraction;
    return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
  }
  
  public static Color saturate(Color clr, double saturation)
  {
    float[] hsb = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
    hsb[1] = (float) MathUtil.clamp(saturation, 0, 1);;
    return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
  }

  public static boolean isValidColor(String clrStr)
  {
    return clrStr != null && clrStr.length() > 0;
  }
  

}
