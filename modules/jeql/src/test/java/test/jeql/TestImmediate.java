package test.jeql;

import jeql.JeqlStrings;
import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;
import jeql.engine.*;
import jeql.util.*;
import com.vividsolutions.jts.util.*;

/**
 * Reads a JQL program from a string and runs it
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class TestImmediate {

  public static void main(String args[])
  {
    TestImmediate test = new TestImmediate();
    try {
      test.run();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  JeqlOptions options = new JeqlOptions();

  public TestImmediate() {
  }

  void run()
      throws Exception
  {
//    execScript("i = Geom.createPoint(\"2.2\", Val.toInt(\"3.3\"));");
//    execScript("i = Geom.createPoint(Val.toInt(\"2.2\"), Val.toInt(\"3.3\"));");
    String s = "t = select i from Generate.sequence(1, 3000); \n"
        + " t2 = select t1.i, t2.i j from t t1 join t t2; \n"
        + " Print t2;";
    String s2 = "t = select Geom.buffer(Geom.createPoint(i, i), 10) i from Generate.sequence(1, 300); \n"
        + " t2 = select t1.i, t2.i j from t t1 join t t2; \n"
        + " Print t2;";
    options.setWorkbench(true);
    execScript(s);
  }

  void execScript(String script)
  {
    JeqlRunner runner = new JeqlRunner();
//    runner.setVerbose(true);
//    runner.setDebug(true);
    
    boolean ok = false;
    try {
      runner.init(options);
      ok = runner.execScript(script, new String[0]);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }


}