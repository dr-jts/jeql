package jeql.std.function;

import java.util.Map;
import java.util.TreeMap;

import jeql.api.function.FunctionClass;

public class ColorFunction 
implements FunctionClass
{
  private static String[][] cssColor = {
  { "AliceBlue", "F0F8FF" },
  { "AntiqueWhite", "FAEBD7" },
  { "Aqua", "00FFFF" },
  { "Aquamarine", "7FFFD4" },
  { "Azure", "F0FFFF" },
  { "Beige", "F5F5DC" },
  { "Bisque", "FFE4C4" },
  { "Black", "000000" },
  { "BlanchedAlmond", "FFEBCD" },
  { "Blue", "0000FF" },
  { "BlueViolet", "8A2BE2" },
  { "Brown", "A52A2A" },
  { "BurlyWood", "DEB887" },
  { "CadetBlue", "5F9EA0" },
  { "Chartreuse", "7FFF00" },
  { "Chocolate", "D2691E" },
  { "Coral", "FF7F50" },
  { "CornflowerBlue", "6495ED" },
  { "Cornsilk", "FFF8DC" },
  { "Crimson", "DC143C" },
  { "Cyan", "00FFFF" },
  { "DarkBlue", "00008B" },
  { "DarkCyan", "008B8B" },
  { "DarkGoldenRod", "B8860B" },
  { "DarkGray", "A9A9A9" },
  { "DarkGrey", "A9A9A9" },
  { "DarkGreen", "006400" },
  { "DarkKhaki", "BDB76B" },
  { "DarkMagenta", "8B008B" },
  { "DarkOliveGreen", "556B2F" },
  { "DarkOrange", "FF8C00" },
  { "DarkOrchid", "9932CC" },
  { "DarkRed", "8B0000" },
  { "DarkSalmon", "E9967A" },
  { "DarkSeaGreen", "8FBC8F" },
  { "DarkSlateBlue", "483D8B" },
  { "DarkSlateGray", "2F4F4F" },
  { "DarkSlateGrey", "2F4F4F" },
  { "DarkTurquoise", "00CED1" },
  { "DarkViolet", "9400D3" },
  { "DeepPink", "FF1493" },
  { "DeepSkyBlue", "00BFFF" },
  { "DimGray", "696969" },
  { "DimGrey", "696969" },
  { "DodgerBlue", "1E90FF" },
  { "FireBrick", "B22222" },
  { "FloralWhite", "FFFAF0" },
  { "ForestGreen", "228B22" },
  { "Fuchsia", "FF00FF" },
  { "Gainsboro", "DCDCDC" },
  { "GhostWhite", "F8F8FF" },
  { "Gold", "FFD700" },
  { "GoldenRod", "DAA520" },
  { "Gray", "808080" },
  { "Grey", "808080" },
  { "Green", "008000" },
  { "GreenYellow", "ADFF2F" },
  { "HoneyDew", "F0FFF0" },
  { "HotPink", "FF69B4" },
  { "IndianRed", "CD5C5C" },
  { "Indigo", "4B0082" },
  { "Ivory", "FFFFF0" },
  { "Khaki", "F0E68C" },
  { "Lavender", "E6E6FA" },
  { "LavenderBlush", "FFF0F5" },
  { "LawnGreen", "7CFC00" },
  { "LemonChiffon", "FFFACD" },
  { "LightBlue", "ADD8E6" },
  { "LightCoral", "F08080" },
  { "LightCyan", "E0FFFF" },
  { "LightGoldenRodYellow", "FAFAD2" },
  { "LightGray", "D3D3D3" },
  { "LightGrey", "D3D3D3" },
  { "LightGreen", "90EE90" },
  { "LightPink", "FFB6C1" },
  { "LightSalmon", "FFA07A" },
  { "LightSeaGreen", "20B2AA" },
  { "LightSkyBlue", "87CEFA" },
  { "LightSlateGray", "778899" },
  { "LightSlateGrey", "778899" },
  { "LightSteelBlue", "B0C4DE" },
  { "LightYellow", "FFFFE0" },
  { "Lime", "00FF00" },
  { "LimeGreen", "32CD32" },
  { "Linen", "FAF0E6" },
  { "Magenta", "FF00FF" },
  { "Maroon", "800000" },
  { "MediumAquaMarine", "66CDAA" },
  { "MediumBlue", "0000CD" },
  { "MediumOrchid", "BA55D3" },
  { "MediumPurple", "9370D8" },
  { "MediumSeaGreen", "3CB371" },
  { "MediumSlateBlue", "7B68EE" },
  { "MediumSpringGreen", "00FA9A" },
  { "MediumTurquoise", "48D1CC" },
  { "MediumVioletRed", "C71585" },
  { "MidnightBlue", "191970" },
  { "MintCream", "F5FFFA" },
  { "MistyRose", "FFE4E1" },
  { "Moccasin", "FFE4B5" },
  { "NavajoWhite", "FFDEAD" },
  { "Navy", "000080" },
  { "OldLace", "FDF5E6" },
  { "Olive", "808000" },
  { "OliveDrab", "6B8E23" },
  { "Orange", "FFA500" },
  { "OrangeRed", "FF4500" },
  { "Orchid", "DA70D6" },
  { "PaleGoldenRod", "EEE8AA" },
  { "PaleGreen", "98FB98" },
  { "PaleTurquoise", "AFEEEE" },
  { "PaleVioletRed", "D87093" },
  { "PapayaWhip", "FFEFD5" },
  { "PeachPuff", "FFDAB9" },
  { "Peru", "CD853F" },
  { "Pink", "FFC0CB" },
  { "Plum", "DDA0DD" },
  { "PowderBlue", "B0E0E6" },
  { "Purple", "800080" },
  { "Red", "FF0000" },
  { "RosyBrown", "BC8F8F" },
  { "RoyalBlue", "4169E1" },
  { "SaddleBrown", "8B4513" },
  { "Salmon", "FA8072" },
  { "SandyBrown", "F4A460" },
  { "SeaGreen", "2E8B57" },
  { "SeaShell", "FFF5EE" },
  { "Sienna", "A0522D" },
  { "Silver", "C0C0C0" },
  { "SkyBlue", "87CEEB" },
  { "SlateBlue", "6A5ACD" },
  { "SlateGray", "708090" },
  { "SlateGrey", "708090" },
  { "Snow", "FFFAFA" },
  { "SpringGreen", "00FF7F" },
  { "SteelBlue", "4682B4" },
  { "Tan", "D2B48C" },
  { "Teal", "008080" },
  { "Thistle", "D8BFD8" },
  { "Tomato", "FF6347" },
  { "Turquoise", "40E0D0" },
  { "Violet", "EE82EE" },
  { "Wheat", "F5DEB3" },
  { "White", "FFFFFF" },
  { "WhiteSmoke", "F5F5F5" },
  { "Yellow", "FFFF00" },
  { "YellowGreen", "9ACD32" } };
  
  private static Map cssColorMap = null;
  
  private static Map createCSSColorMap()
  {
    Map map = new TreeMap();
    for (int i = 0; i < cssColor.length; i++ ) {
      map.put(cssColor[i][0].toLowerCase(), cssColor[i][1].toLowerCase());
    }
    return map;
  }
  
  private static Map getCSSColorMap()
  {
    if (cssColorMap == null) 
      cssColorMap = createCSSColorMap();
    return cssColorMap;
  }
  
  private static String DEFAULT_RGB = "ffffff";
  
  public static String cssNameToRGB(String colorName)
  {
    String rgbHex = (String) getCSSColorMap().get(colorName.toLowerCase().trim());
    if (rgbHex == null)
      return DEFAULT_RGB;
    return rgbHex;
  }
  
  public static String RGBtoBGR(String rgbHex)
  {
    String pad = (rgbHex + "000000").substring(0, 6);
    return pad.substring(4, 6)+ pad.substring(2, 4)+ pad.substring(0, 2);
  }
  
  public static String RGBAtoABGR(String rgbaHex)
  {
    String pad = rgbaHex;
    if (pad == null) return "00000000";
    if (rgbaHex.length() < 6) {
      pad = (rgbaHex + "000000").substring(0, 6);
    }
    String pad2 = (pad + "ff").substring(0, 8);
    return pad2.substring(6, 8)+ pad2.substring(4, 6)+ pad2.substring(2, 4)+ pad2.substring(0, 2);
  }
  
  public static String getRed(String rgb)
  {
    if (rgb.length() < 2) return "00";
    return rgb.substring(0, 2);
  }
  
  public static String getGreen(String rgb)
  {
    if (rgb.length() < 4) return "00";
    return rgb.substring(2, 4);
  }
  
  public static String getBlue(String rgb)
  {
    if (rgb.length() < 6) return "00";
    return rgb.substring(4, 6);
  }
  
  public static String getAlpha(String rgba)
  {
    if (rgba.length() < 8) return "FF";
    return rgba.substring(6, 8);
  }
  
  public static String getR(String rgb)  
  {    
    return rgb.substring(0, 2);  
  }
  
  public static String getG(String rgb)  
  {    
    return rgb.substring(2, 4);  
  }
  public static String getB(String rgb)  
  {    
    return rgb.substring(4, 6);  
  }
  
  public static double getH(String rgb)
  {
    float[] hsv = toHSV(rgb);
    return hsv[0];
  }

  public static double getS(String rgb)
  {
    float[] hsv = toHSV(rgb);
    return hsv[1];
  }

  public static double getV(String rgb)
  {
    float[] hsv = toHSV(rgb);
    return hsv[2];
  }

  public static String setH(String rgb, double val)
  {
    float[] hsv = toHSV(rgb);
    hsv[0] = (float) val; 
    return toRGBfromHSV(hsv[0], hsv[1], hsv[2]);
  }

  public static String setS(String rgb, double val)
  {
    float[] hsv = toHSV(rgb);
    hsv[1] = (float) val; 
    return toRGBfromHSV(hsv[0], hsv[1], hsv[2]);
  }
  
  public static String setV(String rgb, double val)
  {
    float[] hsv = toHSV(rgb);
    hsv[2] = (float) val; 
    return toRGBfromHSV(hsv[0], hsv[1], hsv[2]);
  }
  
  
  private static float[] toHSV(String rgb)
  {
    int r = Integer.parseInt(getRed(rgb), 16);
    int g = Integer.parseInt(getGreen(rgb), 16);
    int b = Integer.parseInt(getBlue(rgb), 16);
    float[] hsv = new float[3];
    java.awt.Color.RGBtoHSB(r, g, b, hsv);
    return hsv;
  }

  public static double clampFloat(double value)
  {
    if (value > 1.0) return 1.0;
    if (value < 0.0) return 0.0;
    return value;
  }
  
  public static int clampInt(int value)
  {
    if (value > 255) return 255;
    if (value < 0) return 0;
    return value;
  }
  
  public static String toRGBfromHSV(double h, double s, double v)
  {
    int clrVal = java.awt.Color.HSBtoRGB(
        (float) clampFloat(h), 
        (float) clampFloat(s), 
        (float) clampFloat(v));
    clrVal = clrVal & 0x00ffffff;
    java.awt.Color clr = new java.awt.Color(clrVal);
    return toRGB(clr.getRed(), clr.getGreen(), clr.getBlue());
  }
  
  public static String toRGB(int r, int g, int b)
  {
    return toHexByte(r) + toHexByte(g) + toHexByte(b);
  }
  
  public static String toRGBA(int r, int g, int b, int a)
  {
    return toHexByte(r) + toHexByte(g) + toHexByte(b) + toHexByte(a);
  }
  
  private static final String hexDigits = "0123456789ABCDEF"; 
  
  public static String toHexByte(int x)
  {
    x = Math.abs(x);
    int lowNib = x % 16;
    int hiNib = (x / 16) % 16;
    return hexDigits.substring(hiNib, hiNib + 1) + hexDigits.substring(lowNib, lowNib + 1);
  }
  
  private static int[] toIntArray(String clr)
  {
    int[] rgb;
    if (clr.length() > 6) {
      rgb = new int[4];
    }
    else {
      rgb = new int[3];
    }
    rgb[0] = Integer.parseInt(getRed(clr), 16);
    rgb[1] = Integer.parseInt(getGreen(clr), 16);
    rgb[2] = Integer.parseInt(getBlue(clr), 16);
    if (rgb.length == 4)
      rgb[3] = Integer.parseInt(getAlpha(clr), 16);
    return rgb;
  }
  
  /**
   * 
   * @param clr1 a color string (RGB or RGBA)
   * @param clr2 a color string (RGB or RGBA)
   * @param frac a number between 0 and 1
   * @return the interpolated color string
   */
  public static String interpolate(String clr1, String clr2, double frac)
  {
    if (frac <= 0) return clr1.toUpperCase();
    if (frac >= 1.0) return clr2.toUpperCase();
    
    int[] clrRGB1 = toIntArray(clr1);
    int[] clrRGB2 = toIntArray(clr2);
    boolean hasAlpha = clrRGB1.length > 3 || clrRGB2.length > 3;
    int r = interpolate(clrRGB1[0], clrRGB2[0], frac);
    int g = interpolate(clrRGB1[1], clrRGB2[1], frac);
    int b = interpolate(clrRGB1[2], clrRGB2[2], frac);
    if (! hasAlpha)
      return toRGB(r, g, b);
    
    int a1 = clrRGB1.length > 3 ? clrRGB1[3] : 255;
    int a2 = clrRGB2.length > 3 ? clrRGB2[3] : 255;
    int a = interpolate(a1, a2, frac);
    return toRGBA(r, g, b, a);
  }
  
  public static String interpolate(String clr1, String clr2, String clr3, double frac)
  {
    if (frac <= 0) return clr1.toUpperCase();
    if (frac >= 1.0) return clr3.toUpperCase();
    if (frac == 0.5) return clr2.toUpperCase();
    
    if (frac < 0.5) return interpolate(clr1, clr2, frac * 2.0);
    return interpolate(clr2, clr3, 2 * (frac - 0.5));
  }
  
  public static String interpolate(String clr1, String clr2, String clr3, String clr4, double frac)
  {
    if (frac <= 0) return clr1.toUpperCase();
    if (frac >= 1.0) return clr4.toUpperCase();
    
    if (frac <= 0.333333 ) return interpolate(clr1, clr2, frac * 3.0);
    if (frac <= 0.666666 ) return interpolate(clr2, clr3, (frac - 0.333333333) * 3.0);
    return interpolate(clr3, clr4, (frac - 0.6666666) * 3.0);
  }
  
  private static int interpolate(int i1, int i2, double frac)
  {
    if (frac <= 0.0) return i1;
    if (frac >= 1.0) return i2;
    return (int) (frac * (i2 - i1)) + i1;
  }
  
  
  /*
   // TODO:
  public static String HSVtoRGB(double h, double s, double v)
  {
    int irgb = java.awt.Color.HSBtoRGB((float)h, (float)s, (float)v);
    
  }
  */
}
