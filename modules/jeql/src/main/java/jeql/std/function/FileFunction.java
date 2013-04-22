package jeql.std.function;

import java.io.File;

import jeql.api.function.FunctionClass;

public class FileFunction 
  implements FunctionClass
{
  private static final String EXT_SEP = ".";
 
  public static String name(String path)
  {
    return (new File(path)).getName();
  }

  public static String pathNoExt(String path)
  {
    int extIndex = path.lastIndexOf(EXT_SEP.charAt(0));
    if (extIndex < 0) return path;
    return path.substring(0, extIndex);
  }

  public static String nameNoExt(String path)
  {
    String name = name(path);
    int extIndex = name.indexOf(EXT_SEP.charAt(0));
    if (extIndex < 0) return name;
    return name.substring(0, extIndex);
  }

  public static String dir(String path)
  {
    String parent = (new File(path)).getParent();
    return parent == null ? "" : parent;
  }

  public static String ext(String path)
  {
    String name = name(path);
    int extIndex = name.lastIndexOf(EXT_SEP.charAt(0));
    if (extIndex < 0) return "";
    return name.substring(extIndex + 1, name.length());
  }
  
  public static String parent(String path)
  {
    return (new File(path)).getParent();
  }
  


}
