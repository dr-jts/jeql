package jeql.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class PrintLogger 
implements Logger
{ 
  private static final String filename = "jeql.log";
  
  private PrintWriter writer;
  private boolean isInit = false;
  
  public PrintLogger()
  {
    init();
  }
  
  public void log(Object o)
  {
    if (writer != null) writer.println(o);
  }
  
  private void init()
  {
    if (isInit) return;
    isInit = true;
    try {
      writer = new PrintWriter(new FileWriter(new File(filename)));
    }
    catch (IOException ex) {
      // TODO: handle this gracefully
    }
  }
  
  public void close()
  {
    if (writer != null) writer.close();
  }
}
