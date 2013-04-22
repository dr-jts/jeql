package jeql.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jeql.io.InputSource;

public class IOUtil
{

  /**
   * Writes a string to a file. If an IOException occurs it is not thrown but is
   * returned (thus allowing caller to ignore it if desired).
   * 
   * @param filename
   * @param str
   * @return
   */
  public static IOException writeToFileNoThrow(String filename, String str)
  {
    try {
      FileWriter writer = new FileWriter(filename);
      writer.write(str);
      writer.close();
    }
    catch (IOException ex) {
      return ex;
    }
    return null;
  }

  public static String readFile(String filename, boolean addEOL)
      throws IOException
  {

    StringBuffer buf = new StringBuffer();
    BufferedReader bufReader = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      bufReader = new BufferedReader(new InputStreamReader(fis));
      String line = "";
      while ((line = bufReader.readLine()) != null) {
        buf.append(line);
        // possibly add EOL char to each line
        if (addEOL)
          buf.append('\n');
      }

      // System.out.println(buf);
    }
    finally {
      if (bufReader != null)
        bufReader.close();
    }
    // may be empty string if an error occurred
    return buf.toString();
  }

}
