package jeql.engine;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import jeql.api.command.Command;
import jeql.api.function.FunctionClass;
import jeql.command.io.NonClosingPrintWriter;
import jeql.log.Logger;
import jeql.log.PrintLogger;

public class EngineContext 
{
  public static PrintWriter OUTPUT_WRITER = new NonClosingPrintWriter(System.out);
  public static PrintWriter ERROR_WRITER = new NonClosingPrintWriter(System.err);
  
  public static void flush()
  {
    OUTPUT_WRITER.flush();
    ERROR_WRITER.flush();
  }
  
  private static EngineContext context = new EngineContext();
  
  public static EngineContext getInstance() { return context; }

  private String scriptName;
  private String[] scriptArgs = null;
  private FunctionRegistry functionReg = new FunctionRegistry();
  private CommandRegistry cmdReg = new CommandRegistry();
  private boolean isDebug = false;
  
  public EngineContext() {
  }

  public void setDebug(boolean isDebug)
  {
    this.isDebug = isDebug;
  }
  
  public boolean isDebug() { return isDebug; }
  
  public void setScriptName(String scriptName)
  {
    this.scriptName = scriptName;
  }
  
  public String getScriptName() { return scriptName; }
  
  public void setArgs(String[] scriptArgs)
  {
    this.scriptArgs = scriptArgs;
  }
  
  public String[] getArgs() { return scriptArgs; }
  
  public String getArg(int i) 
  {
    if (i < scriptArgs.length)
      return scriptArgs[i];
    return null;
  }
  
  public void register(Class clazz)
  {
    if (FunctionClass.class.isAssignableFrom(clazz))
      functionReg.register(clazz, true);
    else if (Command.class.isAssignableFrom(clazz))
      cmdReg.register(clazz, true);
    else
      throw new ConfigurationException("Class " + clazz.getName() + " is not a function or command");
  }
  
  public FunctionRegistry getFunctionRegistry()
  {
    return functionReg;
  }
  
  public CommandRegistry geCommandRegistry()
  {
    return cmdReg;
  }
  
  public Method getFunction(String name, int nArgs)
  {
    return functionReg.getMethod(name, nArgs);
  }

  public CommandInvoker getCommand(String name)
  {
    return cmdReg.getCommand(name);
  }

}
