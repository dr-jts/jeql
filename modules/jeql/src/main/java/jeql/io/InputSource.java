package jeql.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URL;

import jeql.api.error.ExecutionException;
import jeql.util.StringUtil;

/**
 * A input source describes various kinds of sources
 * such as HTTP, file, or string
 * and creates {@link InputStream}s from them.
 * 
 * @author Martin Davis
 *
 */
public class InputSource 
{
  public static final String PROTOCOL_HTTP = "http:";
  public static final String PROTOCOL_HTTPS = "https:";
  public static final String PROTOCOL_FILE = "file:";
  public static final String PROTOCOL_STRING = "string:";
  
 // TODO: add other protocols such as stdin
  
  private String srcName;
  private String data;
  
  public InputSource(String srcName) {
    this.srcName = srcName;
  }
  
  public InputSource(String protocol, String data) {
    this.srcName = protocol;
    this.data = data;
  }
  
  public String getSourceName()
  {
    return srcName;
  }
  
  public InputStream createStream()
  {
    try {
      return createStreamUnchecked();
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }
  }
  
  private InputStream createStreamUnchecked()
    throws Exception
  {
    if (data != null) {
      return new StringBufferInputStream(data);
    }
    // TODO: handle protocols in case-insensitive way
    // TODO: add more protocol checks (or pattern?)
    if (isHTTP()) {
      URL url = new URL(srcName);
      return url.openStream();
    }
    // default: assume srcName refers to file
    return new FileInputStream(srcName);

  }

  private boolean isHTTP()
  {
    if (StringUtil.startsWithIgnoreCase(srcName, PROTOCOL_HTTP))
      return true;
    if (StringUtil.startsWithIgnoreCase(srcName, PROTOCOL_HTTPS))
      return true;
    return false;
  }
}
