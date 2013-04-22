package jeql.util;

import java.io.FileNotFoundException;

public class ExceptionUtil 
{
  /**
   * Creates a non-null message with as much information as possible.
   * If the Throwable is known to not provide an informative error
   * message, the original message is enhanced to provide more information
   * 
   * @param e
   * @return
   */
  public static String getMessage(Throwable e)
  {
    String eMsg = e.getMessage();
    if (e instanceof FileNotFoundException) {
      eMsg = "File not found: " + eMsg;
    }
    else if (e instanceof NullPointerException) {
      eMsg = "Null Pointer Exception in " + throwLocation(e);
    }
    else if (e instanceof NoClassDefFoundError) {
      eMsg = "Class definition cannot be found:  " + eMsg;
    }
    /*
    // if no message, use class name
    else if (eMsg == null || eMsg.length() == 0) {
      eMsg = ClassUtil.classname(e.getClass());
    }
    // if message is terse (ie no spaces) add classname
    else if (eMsg.indexOf(" ") < 0) {
      eMsg = ClassUtil.classname(e.getClass()) + ": " + eMsg;
    }*/
    // always add exception class
    eMsg = ClassUtil.classname(e.getClass()) + ": " + eMsg;
    return eMsg;
  }

  public static String throwLocation(Throwable e)
  {
    StackTraceElement[] stack = e.getStackTrace();
    if (stack.length == 0)
      return "unknown";
    StackTraceElement location = stack[0];
    int i = 0;
    do {
      location = stack[i++];
    } while (location.getLineNumber() < 0);

    return location.toString();
  }
  
}
