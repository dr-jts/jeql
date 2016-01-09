package jeql.man;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import jeql.engine.CommandInvoker;

public class CommandUtil {

  public static Map<String, CommandParamMethod> getParameters(Class cmdClass)
  {
    Map<String, CommandParamMethod> paramMap = new TreeMap<String, CommandParamMethod>();
    Method[] methods = cmdClass.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      String methName = method.getName();
      
      // filter out Java methods which are not command params
      if (methName.equalsIgnoreCase("getClass"))
        continue;
      
      if (isInputMethod(methName)) {
        addMethod(paramMap, method, true);
      }
      else if (isOutputMethod(methName)) {
        addMethod(paramMap, method, false);
      }
    }
    return paramMap;
  }

  public static boolean isInputMethod(String methName)
  {
    return methName.startsWith(CommandInvoker.SET_PREFIX);
  }
  
  public static boolean isOutputMethod(String methName)
  {
    return methName.startsWith(CommandInvoker.GET_PREFIX);
  }
  
  private static void addMethod(Map<String, CommandParamMethod> paramMap, Method method, boolean isInput)
  {
    String rawName = method.getName().substring(3);
    String name = rawName.substring(0, 1).toLowerCase()
      + rawName.substring(1);
    CommandParamMethod cpm = (CommandParamMethod) paramMap.get(name);
    if (cpm == null) {
      cpm = new CommandParamMethod(name, method);
      paramMap.put(name, cpm);
    }
    if (isInput)
      cpm.setInput(true);
    else 
      cpm.setOutput(true);
  }
  

}
