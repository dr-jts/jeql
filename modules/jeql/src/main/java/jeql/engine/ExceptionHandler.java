package jeql.engine;

import jeql.api.error.ExecutionException;

public class ExceptionHandler 
{

  public static void handleExternal(Throwable ex, String contextMsg)
  {
    String fullMsg = ex.getClass().getName();
    String msg = ex.getMessage();
    if (msg != null 
        && msg.length() > 5 
        && ! msg.equalsIgnoreCase("null")) {
      fullMsg += ": " + msg;
    }
    throw new ExecutionException(contextMsg 
        + " (" + fullMsg + ")");
  }

}
