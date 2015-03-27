package jeql.api;

import jeql.JeqlVersion;
import jeql.api.error.ExitException;
import jeql.api.error.JeqlException;
import jeql.api.table.Table;
import jeql.command.io.TableTextWriter;
import jeql.engine.BasicScope;
import jeql.engine.ConfigurationException;
import jeql.engine.EngineContext;
import jeql.engine.JeqlEngine;
import jeql.man.ManGenerator;
import jeql.monitor.Monitor;
import jeql.syntax.parser.ParseException;
import jeql.util.CmdArgParser;
import jeql.util.ExceptionUtil;
import jeql.util.FileUtil;

import com.vividsolutions.jts.util.Stopwatch;

public class JeqlRunner 
{
  private EngineContext context;
  private JeqlEngine engine;
  private JeqlOptions options = null;
  private String scriptDisplayName = "==Script==";
  private boolean returnCode;
  private String errMsg = null;
  
  public JeqlRunner() {
  }

  public void init(JeqlOptions options)
  {
    this.options = options;
    context = EngineContext.getInstance();
    if (options.isVerbose())  System.out.println(JeqlVersion.COPYRIGHT_JAVAVER);
  }
  
  public EngineContext getContext()
  {
    return context;
  }
  
  public boolean getReturnCode()
  {
    return returnCode;
  }
  
  public String getErrorMessage()
  {
    return errMsg;
  }
  
  public boolean execScriptFile( 
      String scriptFilename,
      String[] scriptArgs) 
  throws Exception 
  {
    return exec(scriptFilename, null, scriptArgs);
  }
  
  public boolean execScript( 
      String script,
      String[] scriptArgs) 
  throws Exception 
  {
    return exec(null, script, scriptArgs);
  }
  
  private boolean exec( 
      String scriptFilename,
      String scriptVal,
      String[] scriptArgs) 
  throws Exception 
  {
    scriptDisplayName = "==Script==";
    if (scriptFilename != null) {
      scriptDisplayName = FileUtil.name(scriptFilename);
    }
   
    if (options.isWorkbench) {
      // keep data in Workbench Debug mode, but not Run mode or MonitorView
      boolean isKeepData = ! options.isMonitorView;
      Monitor.enableWorkbench(isKeepData);      
    }
    else {
      if (options.isMonitorView) Monitor.enableView();
    }
    
    Monitor.setScript(scriptDisplayName);

    context.setDebug(options.isDebugMode);

    context.setScriptName(scriptDisplayName);
    context.setArgs(scriptArgs);

    engine = new JeqlEngine(context);

    Stopwatch sw = new Stopwatch();
    returnCode = true;
    try {
      returnCode = exec(engine, scriptFilename, scriptVal);
    }
    finally {
      Monitor.end();
    }
    
    if (options.isVerbose)
      System.out.println(scriptDisplayName + " - Run completed in " + sw.getTimeString());

    return returnCode;
  }

  /**
   * 
   * @param engine
   * @param filename
   * @param scriptVal
   * @return true if script ended succesfully
   * @return false if script aborted
   */
  private boolean exec(JeqlEngine engine, String filename,
      String scriptVal) {
    try {
      if (filename != null) {
        engine.prepareScriptFile(filename);
      }
      else {
        engine.prepareScript(scriptVal);        
      }
      Object result = engine.evalScript();
      return true;

      /*
      // MD - skip this, could cause unexpected long printouts
      // print final result of script (if any)
      // this allows use of scripts for "immediate" computation of results
      // A null result value indicates not to output anything
      if (result != null)
        printResult(result);
        */
    } 
    catch (ExitException ex) {
      String msg = ex.getMessage();
      if (msg != null && msg.length() > 0)
        reportError(msg);
      return true;
    } 
    catch (ConfigurationException ex) {
      reportError(ex.getLocMessage(scriptDisplayName));
      if (options.isVerbose) ex.printStackTrace();
    } 
    catch (JeqlException ex) {
      reportError(ex.getLocMessage(scriptDisplayName));
      if (options.isDebugMode) ex.printStackTrace();
    } 
    catch (ParseException ex) {
      reportError(
          JeqlException.errorFileLoc(scriptDisplayName, 
                  ex.currentToken.next.beginLine)
                  + "Syntax error at column " + ex.currentToken.next.beginColumn
                  + " - unexpected \"" +  ex.currentToken.next.image + '"' 
                  );
    }
    catch (Throwable ex) {
      String msg = ExceptionUtil.getMessage(ex);
      // print top of stack trace for further info (if present)
      if (options.isDebugMode && ex.getStackTrace().length > 0)
        msg += "\n : " + ex.getStackTrace()[0];
      reportError(msg);
    }
    return false;
  }
  
  private void reportError(String msg)
  {
    // save error msg for display on external console
    errMsg = msg;
    System.err.println(msg);
  }
  
  /*
  private static void printResult(Object result)
  {
    if (result instanceof Table)
      TableTextWriter.writeTbl((Table) result);
    else 
      System.out.println(result);
  }
  
  private static String[] getScriptArgs(String[] args)
  {
    String[] scriptArgs = new String[args.length - 1];
    System.arraycopy(args, 1, scriptArgs, 0, args.length - 1);
    return scriptArgs;
  }
  */
}
