package jeql.std.function;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import jeql.api.function.FunctionClass;
import jeql.util.URLParamEncoder;

public class NetFunction 
  implements FunctionClass
{

  /**
   * Doesn't work - encodes = signs, and maybe other things as well
   * @param url
   * @return
   */
  private static String encodeURL(String url)
  {
    // split into base and params
    String[] part = url.split("[\\?\\&]");
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < part.length; i++) {
      switch (i) {
      case 0:
        buf.append(part[0]);
        break;
      case 1:
        buf.append("?");
        buf.append(URLParamEncoder.encode(part[1]));
        break;
      default:
        buf.append("&");
        buf.append(URLParamEncoder.encode(part[i]));
        break;
      }
    }
    return buf.toString();
  }
  
  public static String encodeURLParameter(String param)
  {
    return URLParamEncoder.encode(param);
  }
  
  public static String readURLnoEOL(String urlStr) {
    return readURL(urlStr, false);
  }
  
  public static String readURL(String urlStr) {
    return readURL(urlStr, true);
  }
  
  public static String readURL(String urlStr, boolean addEOL) {
      return readURL(urlStr, addEOL, -1);
  }
  public static String readURL(String urlStr, boolean addEOL, int timeout) {
    StringBuffer buf = new StringBuffer();
    BufferedReader bufReader = null;
    try {
      try {
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        // add timeout if specified
        if (timeout > 0) conn.setReadTimeout(timeout);
        bufReader = new BufferedReader(new InputStreamReader(conn
            .getInputStream()));
        String line = "";
        while ((line = bufReader.readLine()) != null) {
          buf.append(line);
          // add EOL char to each line if requested
          if (addEOL) buf.append('\n');
        }

        // System.out.println(buf);
      } finally {
        if (bufReader != null) bufReader.close();
      }
    } catch (Exception ex) {
      // eat any exception - just return an empty string
      //ex.printStackTrace();
    }
    // may be empty string if an error occurred
    return buf.toString();
  }

  
  /*
   * Alternative reading code
   * 
           InputStream responseBodyStream = connection.getInputStream();
        StringBuffer responseBody = new StringBuffer();
        while ((read = responseBodyStream.read(buffer)) != -1)
        {
            responseBody.append(new String(buffer, 0, read));
        }
        connection.disconnect();
*/
  
}
