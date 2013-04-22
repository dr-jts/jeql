package jeql.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.io.ExtensionFilenameFilter;

public class FileUtil {

  public static final String EXTENSION_SEPARATOR = ".";

  /**
   * Get name of file (with extension, if any)
   * 
   * @param path
   * @return
   */
  public static String name(String path)
  {
    File file = new File(path);
    return file.getName();
  }

  public static String extension(String path)
  {
    String name = name(path);
    int extIndex = name.lastIndexOf(EXTENSION_SEPARATOR.charAt(0));
    if (extIndex < 0) return "";
    return name.substring(extIndex, name.length());
  }

  public static File[] listFiles(String dirname, String pattern, boolean includeDirs) 
  {
    if (pattern == null)
      return listFiles(dirname, (FilenameFilter) null, includeDirs);
    
    return listFiles(dirname, new ExtensionFilenameFilter(pattern), includeDirs);
  }
  
  public static File[] listFiles(String dirname, FilenameFilter filter, boolean includeDirs) 
  {
    File[] files;
    if (filter == null)
      files = (new File(dirname)).listFiles();
    else
      files = (new File(dirname)).listFiles(filter);
   
    if (files == null) return new File[0];
    if (includeDirs) return files;
    
    List<File> list = new ArrayList<File>();
    for (File f : files) {
      if (f.isFile()) {
        list.add(f);
      }
    }
    return list.toArray(new File[0]);
  }


}
