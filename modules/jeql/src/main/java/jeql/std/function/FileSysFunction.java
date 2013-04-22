package jeql.std.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.annotation.ManDoc;
import jeql.api.function.FunctionClass;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.io.ExtensionFilenameFilter;

/**
 * File functions which either interrogate the file system
 * or modify it.
 * 
 * @author Martin Davis
 *
 */
public class FileSysFunction 
implements FunctionClass
{
  public static boolean mkdirs(String path)
  {
    return (new File(path)).mkdirs();
  }

  public static boolean mkdir(String path)
  {
    return (new File(path)).mkdir();
  }
  
  public static boolean isFile(String path)
  {
    return (new File(path)).isFile();
  }

  public static boolean isDir(String path)
  {
    return (new File(path)).isDirectory();
  }

  public static boolean rename(String oldName, String newName)
  {
    return (new File(oldName)).renameTo(new File(newName));
  }

  public static boolean delete(String file)
  {
    return (new File(file)).delete();
  }

  /**
   * Moves a file to a new directory and name, 
   * creating the directory if necessary.
   * 
   * @param file
   * @param newName
   * @return
   */
  public static boolean move(String oldName, String newName)
  {
    // create dest directory if it doesnt' exist
    String newDir = FileFunction.parent(newName);
    (new File(newDir)).mkdirs();
    
    return (new File(oldName)).renameTo(new File(newName));
  }

  public static boolean copy(String sourceName, String destName) 
  {
    try {
      copy(new File(sourceName), new File(destName));
      return true;
    }
    catch (IOException ex) {
      ex.printStackTrace();
      // can't do much with this so eat it
    }
    return false;
  }
  
  private static void copy(File sourceFile, File destFile) 
  throws IOException 
  {
    File destDir = destFile.getParentFile();
    destDir.mkdirs();
    
    if(! destFile.exists()) {
     destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;
    try {
     source = new FileInputStream(sourceFile).getChannel();
     destination = new FileOutputStream(destFile).getChannel();
     destination.transferFrom(source, 0, source.size());
    }
    finally {
     if (source != null) {
      source.close();
     }
     if (destination != null) {
      destination.close();
     }
    }
   }

  private static String COLNAME_FILENAME = "filename";
  private static String COLNAME_ISDIR = "isDir";

  private static RowSchema getFileListSchema()
  {
    RowSchema schema = new RowSchema(2);
    schema.setColumnDef(0, COLNAME_FILENAME, String.class);
    schema.setColumnDef(1, COLNAME_ISDIR, Boolean.class);
    return schema;
  }
  
  public static Table listFiles(String dirname) 
  {
    return listFilesFiltered(dirname, null);
  }

  public static Table listFiles(String dirname, String pattern) 
  {
    if (pattern.startsWith("*."))
      return listFilesFiltered(dirname, new ExtensionFilenameFilter(FileFunction.ext(pattern)));
    throw new IllegalArgumentException("File pattern '" + pattern + "'is not supported");
  }

  private static Table listFilesFiltered(String dirname, FilenameFilter filter) 
  {
    RowSchema schema = new RowSchema(1);
    schema.setColumnDef(0, COLNAME_FILENAME, String.class);
    ArrayRowList rs = new ArrayRowList(schema);
    
    String[] files;
    if (filter == null)
      files = (new File(dirname)).list();
    else
      files = (new File(dirname)).list(filter);
    
    int nfiles = 0;
    if (files != null) nfiles = files.length;
    
    for (int i = 0; i < nfiles; i += 1) {
      BasicRow row = new BasicRow(1);
      row.setValue(0, files[i]);
      rs.add(row);
    }
    Table t = new Table(rs);
    return t;
  }

  
  @ManDoc (
      description = "Lists all files in a directory tree"
    )
  public static Table listAllFiles(String dirname) 
  {
    return listAllFilesFiltered(dirname, null, false);
  }

  @ManDoc (
      description = "Lists all files in a directory tree"
    )
  public static Table listAllDirsAndFiles(String dirname) 
  {
    return listAllFilesFiltered(dirname, null, true);
  }

  @ManDoc (
      description = "Lists all files in a directory tree which match a pattern"
    )
  public static Table listAllFiles(String dirname, String pattern) 
  {
    if (pattern.startsWith("*."))
      return listAllFilesFiltered(dirname, new ExtensionFilenameFilter(FileFunction.ext(pattern)), false);
    throw new IllegalArgumentException("File pattern '" + pattern + "'is not supported");
  }

  private static Table listAllFilesFiltered(String dirname, FilenameFilter filter, boolean includeDirs) 
  {
    ArrayRowList rs = new ArrayRowList(getFileListSchema());
    
    listAllFilesFiltered(dirname, filter, includeDirs, rs);
    
    Table t = new Table(rs);
    return t;
  }

  private static void listAllFilesFiltered(String dirname, FilenameFilter filter, boolean includeDirs,
      ArrayRowList rowList) 
  {
    File[] files;
    
    if (filter == null)
      files = (new File(dirname)).listFiles();
    else
      files = (new File(dirname)).listFiles(filter);
    
    int nfiles = 0;
    if (files != null) nfiles = files.length;
    
    for (int i = 0; i < nfiles; i += 1) {
      boolean isFile = files[i].isFile();
      if (isFile || includeDirs) {
        BasicRow row = new BasicRow(2);
        row.setValue(0, files[i].toString());
        row.setValue(1, new Boolean(! isFile));
        rowList.add(row);
      }
      if (! isFile) {
        listAllFilesFiltered(files[i].toString(), filter, includeDirs, rowList);
      }
    }
  }

}


