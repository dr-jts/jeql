package jeql.std.function;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import jeql.api.function.FunctionClass;
import jeql.io.IOUtil;

public class IOFunction
implements FunctionClass
{
  public static String readTextFile(String srcName)
  {
    return readTextFile(srcName, true);
  }
  
  public static String readTextFile(String srcName, boolean addEOL)
  {
    try {
      return IOUtil.readTextFile(srcName, addEOL);
    }
    catch (IOException ex) {
      // eat it - not much we can do
    }
    return null;
  }
  
  public static boolean writeTextFile(String filename, String value)
  {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      BufferedWriter bufWriter = new BufferedWriter(fileWriter);
      bufWriter.write(value);
      bufWriter.close();
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
      return false;
    }
    return true;
  }
}
