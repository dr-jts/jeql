package jeql.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jeql.api.command.Command;
import jeql.api.error.ExecutionException;
import jeql.api.error.JeqlException;
import jeql.monitor.Monitor;
import jeql.syntax.CommandParameterNode;

public class CommandInvoker 
{
  public static final String DEFAULT_METHOD_NAME = "default";
  
  private static final String DEFAULT_SETTER = "setDefault";
  private static final String DEFAULT_GETTER = "getDefault";
  
  public static final String SET_PREFIX = "set";
  public static final String GET_PREFIX = "get";
  
  private String cmdName;
  private Class cmdClass;
  private Method[] methods;
  //temp local storage - not thread-safe
  private Scope scope; 
  private boolean[] argUsed;
  
  public CommandInvoker(String cmdName, Class cmdClass) 
  {
    this.cmdName = cmdName;
    this.cmdClass = cmdClass;
    methods = cmdClass.getMethods();
  }

  public String getName() { return cmdName; }
  
  public Class getCommandClass()
  {
    return cmdClass;
  }
  
  public void invoke(List args, Scope scope)
  {
    this.scope = scope;
    argUsed = new boolean[args.size()];
    Command cmdObject = null;
    try {
      cmdObject = (Command) cmdClass.newInstance();
      setParameters(cmdObject, args);
      cmdObject.execute(scope);
      getParameters(cmdObject, args);
    }
    catch (JeqlException ex) {
      throw ex;
    }
    catch (Exception e) {
      Throwable ex = e;
      if (e instanceof InvocationTargetException) {
        ex = ((InvocationTargetException) e).getTargetException();
        if (ex instanceof JeqlException)
          throw (JeqlException) ex;
      }
      if (scope.getContext().isDebug()) {
        System.out.println("------- originating exception -------");
        ex.printStackTrace();
      }
      ExceptionHandler.handleExternal(ex, "Error in command " + cmdName);
    }  
    checkAllArgsUsed(cmdObject, argUsed, args);
  }
  
  private static String paramDisplayName(String argName)
  {
    String paramDisplayString = "default";
    if (argName != null) {
      paramDisplayString = argName + ":";
    }
    return paramDisplayString;
  }
  
  private void checkAllArgsUsed(Command cmdObject, boolean[] argUsed, List args)
  {
    for (int i = 0; i < argUsed.length; i++) {
      if (! argUsed[i]) {
        CommandParameterNode cmdArg = (CommandParameterNode) args.get(i);
        throw new ExecutionException(cmdArg, cmdName + " command does not accept the '" 
            + paramDisplayName(cmdArg.getName()) + "' parameter"); 
      }
    }
  }
  
  private void setParameters(Object cmdObject, List args)
    throws IllegalAccessException, InvocationTargetException
  {
    for (int i = 0; i < args.size(); i++ ) {
      CommandParameterNode cmdArg = (CommandParameterNode) args.get(i);
      setArg(i, cmdObject, cmdArg);
    }
  }
  
  /**
   * Calls a setter method with the value of a cmd argument,
   * if such a method exists.
   * 
   * @return true if arg was set
   */
  private void setArg(int i, Object cmdObject, CommandParameterNode cmdParam)
    throws IllegalAccessException, InvocationTargetException
  {
    Method setMethod = null;
    if (cmdParam.isDefault()) {
      setMethod = findSetMethod(DEFAULT_SETTER);
    }
    else {
      String argName = cmdParam.getName();
      setMethod = findSetMethod(getMethodName(SET_PREFIX, argName));
    }
    if (setMethod == null)
      return;
    argUsed[i] = true;
    Object argVal = cmdParam.eval(scope);
    
    setMethod.invoke(cmdObject, new Object[] { argVal });
  }
  
  private void getParameters(Object cmdObject, List args)
    throws IllegalAccessException, InvocationTargetException
  {
    for (int i = 0; i < args.size(); i++ ) {
      CommandParameterNode procArg = (CommandParameterNode) args.get(i);
      getArg(i, cmdObject, procArg);
    }
  }

  /**
   * Calls a getter method to get a value,
   * and sets the value of the parameter (which must be a variable name)
   */
  private void getArg(int i, Object cmdObject, CommandParameterNode cmdParam)
    throws IllegalAccessException, InvocationTargetException
  {
    if (! cmdParam.isAssignable()) {
      return;
      /*
      throw new ExecutionException(cmdName + " command parameter '" 
          + paramDisplayName(cmdArg.getName()) + "' has argument which is not assignable");
          */
    }
    
    Method getMethod = null;
    if (cmdParam.isDefault()) {
      getMethod = findGetMethod(DEFAULT_GETTER);
    }
    else {
      String argName = cmdParam.getName();
      getMethod = findGetMethod(getMethodName(GET_PREFIX, argName));
    }
    if (getMethod == null)
      return;
    
    argUsed[i] = true;
    Object result = getMethod.invoke(cmdObject, new Object[0]);
    
    String tag = cmdName + " " + cmdParam.getArgName();
    result = Monitor.wrap(cmdParam.getLine(), tag, tag, result);
    
    ((BasicScope) scope).setVariable(cmdParam.getArgName(), result);
  }
  

  private static String getMethodName(String prefix, String name)
  {
    return prefix + name;
  }
  
  private Method findSetMethod(String name)
  {
    for (int i = 0; i < methods.length; i++) {
      Method m = methods[i];
      if (! m.getName().equalsIgnoreCase(name))
        continue;
        
      Class[] paramType = m.getParameterTypes();
      if (paramType.length > 1)
        throw new ConfigurationException("Command " + cmdName + " set method " + name + " takes more than 1 argument");
      
      // assert: m param is assignable 
      return m;
    }
    return null;
  }
  
  private Method findGetMethod(String name)
  {
    for (int i = 0; i < methods.length; i++) {
      Method m = methods[i];
      if (! m.getName().equalsIgnoreCase(name))
        continue;
        
      Class[] paramType = m.getParameterTypes();
      if (paramType.length > 0)
        throw new ConfigurationException("Command " + cmdName + " get method " + name + " requires arguments");
      return m;
    }
    return null;
  }
  
  private boolean isAssignable(Class dest, Class src)
  {
    if (dest.isAssignableFrom(src)) return true;
    // also need to handle Double->double, Int->double etc
    return false;
  }

  public void checkParametersExist(List args) {

    for (int i = 0; i < args.size(); i++) {
      CommandParameterNode cmdArg = (CommandParameterNode) args.get(i);
      checkParameterExists(cmdArg);
    }
  }
  
  private void checkParameterExists(CommandParameterNode cmdParam)
  {
    Method method = null;
    if (cmdParam.isDefault()) {
      method = findSetMethod(DEFAULT_SETTER);
      if (method != null) return;
      method = findSetMethod(DEFAULT_GETTER);
      if (method != null) return;
    } 
    String argName = cmdParam.getName();
    method = findSetMethod(getMethodName(SET_PREFIX, argName));
    if (method != null) return;
    method = findGetMethod(getMethodName(GET_PREFIX, argName));
    if (method != null) return;

    // can't find method - error
    throw new CompilationException(cmdParam, "Command " + cmdName + " does not accept parameter '" + argName + "'");
  }
  
}
