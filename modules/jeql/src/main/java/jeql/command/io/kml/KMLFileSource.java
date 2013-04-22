package jeql.command.io.kml;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jeql.api.error.ExecutionException;
import jeql.io.InputSource;
import jeql.util.StringUtil;

public class KMLFileSource {

  private String srcName;

  public KMLFileSource(String srcName) {
    this.srcName = srcName;
  }
  
  public String getSourceName()
  {
    return srcName;
  }
  
  public Reader createReader()
  {
    InputStream stream;
    try {
      if (StringUtil.endsWithIgnoreCase(srcName, ".kmz")) {
        stream = createKMZStreamUnchecked();
      }
      else {
        stream = createStreamUnchecked();
      }
    }
    catch (Exception ex) {
      throw new ExecutionException(ex);
    }
    return new InputStreamReader(stream);
  }
  
  private InputStream createStreamUnchecked() throws Exception {
    // TODO: handle protocols in case-insensitive way
    // TODO: add more protocol checks (or pattern?)
    if (StringUtil.startsWithIgnoreCase(srcName, InputSource.PROTOCOL_HTTP)) {
      URL url = new URL(srcName);
      return url.openStream();
    }
    // default: assume srcName refers to file
    return new FileInputStream(srcName);
  }

  private InputStream createKMZStreamUnchecked() throws Exception 
  {
    InputStream baseStream = createStreamUnchecked();
    
    ZipInputStream zipStream = new ZipInputStream(baseStream);
    ZipEntry entry = null;
    while (true)
    {
      entry = zipStream.getNextEntry();
      if (entry == null)
        break;
      return zipStream;
    }
    return null;
  }

}
