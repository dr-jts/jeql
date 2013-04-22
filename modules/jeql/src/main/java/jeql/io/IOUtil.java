package jeql.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {

  public static String readTextFile(String srcName, boolean addEOL) 
    throws IOException
  {
    InputSource inputSrc = new InputSource(srcName);
    
    StringBuffer buf = new StringBuffer();
    BufferedReader bufReader = null;
      try {
        InputStream inStream = inputSrc.createStream();
        bufReader = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        while ((line = bufReader.readLine()) != null) {
          buf.append(line);
          // possibly add EOL char to each line
          if (addEOL)
            buf.append('\n');
        }

        // System.out.println(buf);
      } finally {
        if (bufReader != null) bufReader.close();
      }
    // may be empty string if an error occurred
    return buf.toString();
  }


}
