package jeql.util;

import java.util.HashMap;
import java.util.Map;

public class CmdArgParser 
{
  public static final String ARG_DEBUG = "debug";
  public static final String ARG_HELP = "help";
  public static final String ARG_MAN = "man";
  public static final String ARG_MON = "mon";
  public static final String ARG_QUIET = "quiet";
  public static final String ARG_SCRIPT = "script";
  public static final String ARG_STDIN = "stdin";
  public static final String ARG_VERBOSE = "verbose";
  
  public static final String ENGINE_ARG_PREFIX = "-";
  
  private String[] args;
  private int scriptIndex = -1;
  private Map engineArgMap = new HashMap();
  
  public CmdArgParser(String[] args) {
    this.args = args;
    init();
  }
  
  private void init()
  {
    if (args.length <= 0)
      return;
    
    scriptIndex = 0;
    // find script index
    for (int i = 0; i < args.length && isEngineArg(args[i]); i++) 
    { 
      addEngineArg(args[i]);
      scriptIndex = i + 1;
    }
    if (scriptIndex >= args.length)
      scriptIndex = -1;
  }

  private boolean isEngineArg(String arg)
  {
    return arg.startsWith(ENGINE_ARG_PREFIX);
  }
  
  private void addEngineArg(String arg)
  {
    String argName = arg.substring(1);
    engineArgMap.put(argName, null);
  }
  
  public boolean hasEngineArg(String arg)
  {
    return engineArgMap.containsKey(arg);
  }
  
  public int getScriptIndex() { return scriptIndex; }
  
  public String getScriptName() 
  { 
    if (scriptIndex < 0)
      return null;
    return args[scriptIndex]; 
  }
  
  public String[] getScriptArgs()
  {
     int scrArgsLen = args.length - scriptIndex - 1;
     String[] scrArgs = new String[scrArgsLen];
     if (scrArgsLen > 0)
       System.arraycopy(args, scriptIndex + 1, scrArgs, 0, scrArgsLen);
     return scrArgs;
  }
}
