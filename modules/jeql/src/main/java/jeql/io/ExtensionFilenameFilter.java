package jeql.io;

import java.io.File;
import java.io.FilenameFilter;

import jeql.std.function.FileFunction;

public class ExtensionFilenameFilter implements FilenameFilter
{
  private String extension = null;
  
  public ExtensionFilenameFilter(String extension)
  {
    this.extension = extension;
  }
  
  public boolean accept(File dir, String name)
  {
    String fileExt = FileFunction.ext(name);
    if (fileExt == null) return false;
    if (fileExt.equals(extension))
      return true;
    return false;
  }
}

