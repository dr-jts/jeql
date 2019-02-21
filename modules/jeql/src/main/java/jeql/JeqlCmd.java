package jeql;

import java.io.PrintWriter;

import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;
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
import jeql.util.CmdArgParser;
import jeql.util.ExceptionUtil;
import jeql.util.FileUtil;

import org.locationtech.jts.util.Stopwatch;

public class JeqlCmd 
{
  public static void main(String args[])
  {
    JeqlCmd runner = new JeqlCmd();
    try {
      runner.run(args);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private JeqlOptions options = new JeqlOptions();
  
  public JeqlCmd() {
  }
  
  /**
   * 
   * @param args
   * @return true if script executed successfully
   * @throws Exception
   */
  public boolean run(String[] args)
      throws Exception 
  {
    boolean isScriptRequired = true;
    
    CmdArgParser cmdParser = new CmdArgParser(args);
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_DEBUG)) {
      options.setDebug(true);
    }
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_QUIET)) {
      options.setVerbose(false);
    }
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_VERBOSE)) {
      options.setVerbose(true);
    }
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_HELP)) {
      System.out.println(JeqlVersion.COPYRIGHT_JAVAVER);
      printHelp();
      return true;
    }
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_MON)) {
      options.setMonitorView(true);
      isScriptRequired = false;
    }
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_MAN)) {
      isScriptRequired = false;
    }
        
    String scriptName = null;
    String scriptValue = null;
    if (cmdParser.hasEngineArg(CmdArgParser.ARG_STDIN)) {
      // read script from stdin
      scriptName = JeqlStrings.STDIN;    
    }
    else if (cmdParser.hasEngineArg(CmdArgParser.ARG_SCRIPT)) {
      // read script from cmd arg
      scriptName = JeqlStrings.SCRIPT;  
      scriptValue = cmdParser.getScriptName();
    }
    else {
      String cmdScriptName = cmdParser.getScriptName();
      if (cmdScriptName != null)
        scriptName = cmdScriptName;
    }
    // action if no script provided
    if (isScriptRequired && scriptName == null) {
      System.out.println("**** No script specified ****");
      printHelp();
      return false;
    }
    
    JeqlRunner runner = new JeqlRunner();
    runner.init(options);

    if (cmdParser.hasEngineArg(CmdArgParser.ARG_MAN)) {
      ManGenerator.generate(runner.getContext(), new PrintWriter(System.out));
      return true;
    }
 
    boolean retCode = true;
    if (scriptValue != null)
      retCode = runner.execScript(scriptValue, cmdParser.getScriptArgs());
    else 
      retCode = runner.execScriptFile(scriptName, cmdParser.getScriptArgs());
    return retCode;
  }

  private void printHelp()
  {
    System.out.println(JeqlVersion.COPYRIGHT_JAVAVER);    
    System.out.println("jeql [ options ] [ <scriptname> <script_arg> ...]");    
    System.out.println("");    
    System.out.println("Options:");    
    System.out.println(" -debug    - sets Debug mode");    
    System.out.println(" -help     - prints help message");    
    System.out.println(" -man      - prints listing of functions and commands");    
    System.out.println(" -mon      - displays monitor during script execution");    
    System.out.println(" -stdin    - reads script from standard input");    
    System.out.println(" -script <text>   - reads script from string argument");    
    System.out.println(" -verbose  - runs in verbose mode");    
  }
  
  private static String[] getScriptArgs(String[] args)
  {
    String[] scriptArgs = new String[args.length - 1];
    System.arraycopy(args, 1, scriptArgs, 0, args.length - 1);
    return scriptArgs;
  }
}
