package jeql.std.function;

import jeql.api.error.ExitException;
import jeql.api.function.FunctionClass;
import jeql.engine.EngineContext;

public class ScriptFunction 
  implements FunctionClass
{
  public static String exit(String msg)
  {
    throw new ExitException(msg);
  }
  
  public static String exit()
  {
    throw new ExitException();
  }
  
  public static String name(EngineContext context)
  {
    return context.getScriptName();
  }
  
  public static String arg(EngineContext context, int i)
  {
    return context.getArg(i);
  }
  
  public static String arg(EngineContext context, int i, String defaultVal)
  {
    String argVal = context.getArg(i);
    if (argVal != null) return argVal;
    return defaultVal;
  }

  public static int argInt(EngineContext context, int i, int defaultVal)
  {
    String argVal = context.getArg(i);
    if (argVal == null) return defaultVal;
    return ValFunction.toInt(argVal, defaultVal);
  }

  public static int sleep(int millis)
  {
      try {
        Thread.sleep(millis);
      }
      catch (InterruptedException e) {
        // nothing to do here
      }
    return millis;
  }
}
