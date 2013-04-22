package test.jeql.util;

import java.io.*;

public class FileUtil 
{

  public static File[] listFilesWithExtension(String dirName, String ext)
  {
    File dir = new File(dirName);
    return dir.listFiles(new FileExtensionFilter(ext));
  }

}

class FileExtensionFilter
  implements FilenameFilter
{
  private String ext;
  
  public FileExtensionFilter(String ext)
  {
    this.ext = ext;
  }
  
  public boolean accept(File dir, String name)
  {
    if (name.endsWith(ext))
      return true;
    return false;
  }
}