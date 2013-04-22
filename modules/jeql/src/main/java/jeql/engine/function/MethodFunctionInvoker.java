package jeql.engine.function;

import java.lang.reflect.*;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.error.JeqlException;
import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;
import jeql.util.ClassUtil;
import jeql.util.ExceptionUtil;
import jeql.util.TypeUtil;

public class MethodFunctionInvoker
{
  private Method method;

  private boolean isContextSupplied;

  private int argOffset = 0;

  public MethodFunctionInvoker(Method method)
  {
    this.method = method;
    isContextSupplied = isContextSupplied(method);
    if (isContextSupplied)
      argOffset = 1;
  }

  public Class getType(Scope scope)
  {
    Class rtnType = method.getReturnType();
    return ClassUtil.objectClass(rtnType);
  }

  /**
   * Gets the type of a parameterized List 
   * @param scope
   * @return
   */
  public Class getParameterizedListType(Scope scope)
  {
    Type returnType = method.getGenericReturnType();
    ParameterizedType type = (ParameterizedType) returnType;
    Type[] typeArguments = type.getActualTypeArguments();
    Class typeArgClass = (Class) typeArguments[0];
    // System.out.println("typeArgClass = " + typeArgClass);
    return typeArgClass;

    /*
     * Class rtnType = method.getReturnType(); TypeVariable[] typeParam =
     * rtnType.getTypeParameters(); Class listType = typeParam[0].getClass();
     * return ClassUtil.getObjectClass(listType);
     */
  }

  public int getUserArgNum()
  {
    int methodArgNum = method.getParameterTypes().length;
    if (isContextSupplied) {
      return methodArgNum - 1;
    }
    return methodArgNum;
  }

  /**
   * Tests whether the current {@link EngineContext} must be passed to this
   * function.
   * 
   * @return true if the current context should be supplied to this function
   */
  private static boolean isContextSupplied(Method method)
  {
    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes.length <= 0)
      return false;
    if (paramTypes[0] == EngineContext.class)
      return true;
    return false;
  }

  public int getArgOffset()
  {
    return argOffset;
  }

  /**
   * Creates an argument array of the required size for this function.
   * 
   * @return
   */
  public Object[] createArgArray()
  {
    int count = getUserArgNum();
    if (isContextSupplied) {
      count++;
    }
    Object[] argVal = new Object[count];
    return argVal;
  }

  public Object eval(Scope scope, List args)
  {
    return eval(scope, null, args);
  }

  public Object eval(Scope scope, Object target, List args)
  {
    Object[] argVal = createArgArray();
    evalArgs(scope, args, argVal, getArgOffset());
    Object result = invoke(scope, target, argVal);
    return result;
  }

  private Object[] evalArgs(Scope scope, List args, Object[] argVal, int offset)
  {
    for (int i = 0; i < args.size(); i++) {
      argVal[i + offset] = ((ParseTreeNode) args.get(i)).eval(scope);
    }
    return argVal;
  }

  private Object invoke(Scope scope, Object target, Object[] args)
  {
    if (isContextSupplied) {
      args[0] = scope.getContext();
    }
    return invoke(method, target, args);
  }

  /**
   * Invokes a static method, and maps any exceptions to JEQL standard exception
   * classes.
   * 
   * @param method
   * @param arg0
   * @param args
   * @return
   * @throws ExecutionException
   * 
   */
  public static Object invoke(Method method, Object[] args)
  {
    return invoke(method, null, args);
  }

  /**
   * Invokes a method on a target object, and maps any exceptions to JEQL
   * standard exception classes.
   * 
   * @param method
   * @param arg0
   * @param args
   * @return
   * @throws ExecutionException
   * 
   */
  public static Object invoke(Method method, Object target, Object[] args)
  {
    Object result = null;
    try {
      result = method.invoke(target, args);
    }
    catch (InvocationTargetException ex) {
      Throwable t = ex.getCause();
      if (t instanceof JeqlException)
        throw (JeqlException) t;
      throwExecutionException(method, args, t);
    }
    catch (Exception ex) {
      // System.out.println(ex.getMessage());
      throwExecutionException(method, args, ex);
    }
    return result;
  }

  private static void throwExecutionException(Method method, Object[] args,
      Throwable t)
  {
    throw new ExecutionException(functionSig(method, args) + " : "
        + ExceptionUtil.getMessage(t));
  }

  private static String functionSig(Method method, Object[] args)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      if (i > 0)
        sb.append(",");
      sb.append(TypeUtil.toCodeStringLimited(args[i]));
    }
    return FunctionRegistry.functionName(method.getDeclaringClass().getName(),
        method.getName()) + "(" + sb.toString() + ")";
  }

  private static String invocationErrMsg(InvocationTargetException ex)
  {
    Throwable targetEx = ex.getTargetException();
    String msg = ClassUtil.classname(targetEx.getClass()) + ": "
        + targetEx.getMessage();
    return msg;
  }
}
