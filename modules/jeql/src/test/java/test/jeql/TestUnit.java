package test.jeql;

import java.util.*;
import java.io.*;
import jeql.*;
import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;
import test.jeql.util.*;
import com.vividsolutions.jts.util.*;

public class TestUnit 
{
  static final String BASE = "C:\\data\\martin\\proj\\jeql\\trunk";
  
  String[] dirs = {
      BASE + "\\script\\unitTest",
      BASE + "\\script\\unitTest\\io",
      BASE + "\\script\\unitTest\\geom"
  };
    
  public static void main(String args[])
  {
    TestUnit test = new TestUnit();
    try {
      test.run();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private int successCount = 0;
  
  public TestUnit() {
  }

  void run()
      throws Exception
  {
    List filenames = listAllJQLFiles(dirs);
    execAll(filenames);
  }

  private static List listAllJQLFiles(String[] dirs)
  {
    List filenames = new ArrayList();
    for (int i = 0; i < dirs.length; i++) {
      File[] files = FileUtil.listFilesWithExtension(dirs[i], ".jql");
      filenames.addAll(listOfNames(files));
    }
    return filenames;
  }
  
  private static List listOfNames(File[] files)
  {
    List filenames = new ArrayList();
    for (int i = 0; i < files.length; i++) {
      filenames.add(files[i].getPath());
    }  
    return filenames;
  }
  
  private static String SCRIPT_SEP = "================ ";
  
  void execAll(List filenames)
  {
    Stopwatch sw = new Stopwatch();
    successCount = 0;
    int n = filenames.size();
    
    for (int i = 0; i < n; i++) {
      String filename = (String) filenames.get(i);
      System.out.println(SCRIPT_SEP + filename);
      exec(filename);
//      System.out.println();
    }
    System.out.flush();
    System.out.println();
    int errCount = n - successCount;
    if (errCount > 0)
      System.out.println("#######  ERRORS ENCOUNTERED  #######");
    System.out.println("Files: " + n 
        + "   Success: " + successCount
        + "   Errors: " + errCount
        );
    System.out.print(SCRIPT_SEP);
    System.out.println("Unit test run finished in " + sw.getTimeString());
    System.out.flush();
  }
  
  void exec(String scriptFile)
  {
    JeqlRunner runner = new JeqlRunner();
    JeqlOptions options = new JeqlOptions();
    
    boolean ok = false;
    try {
      runner.init(options);
      ok = runner.execScriptFile(scriptFile, new String[0]);
    }
    catch (Throwable ex) {
      // this should probably never happen
      ex.printStackTrace();
    }
    if (ok)
      successCount++;      
  }
}
