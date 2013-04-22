package jeql.engine;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jeql.api.function.AggregateFunction;
import jeql.api.function.SplittingFunction;
import jeql.std.function.AggFunction;
import jeql.std.function.AggregateFunctions;
import jeql.std.function.ColorFunction;
import jeql.std.function.ConsoleFunction;
import jeql.std.function.DateFunction;
import jeql.std.function.DebugFunction;
import jeql.std.function.FileFunction;
import jeql.std.function.FileSysFunction;
import jeql.std.function.GenerateFunction;
import jeql.std.function.HtmlFunction;
import jeql.std.function.IOFunction;
import jeql.std.function.MathFunction;
import jeql.std.function.NetFunction;
import jeql.std.function.RegExFunction;
import jeql.std.function.ScriptFunction;
import jeql.std.function.SplitByFunction;
import jeql.std.function.StrMatchFunction;
import jeql.std.function.StringFunction;
import jeql.std.function.SystemFunction;
import jeql.std.function.ValFunction;
import jeql.std.function.XmlStrFunction;
import jeql.std.geom.CRSFunction;
import jeql.std.geom.GeodeticFunction;
import jeql.std.geom.GeomFunction;
import jeql.std.geom.GeomPrepAllFunction;
import jeql.std.geom.GeomPrepFunction;
import jeql.std.geom.GridFunction;
import jeql.std.geom.LinearRefFunction;
import jeql.util.ClassUtil;
import jeql.util.MultiMap;

/**
 * A registry of the functions which are available
 * in the JQL execution context. 
 * 
 * @author Martin Davis
 *
 */
public class FunctionRegistry 
{
  public static final String FUNCTION_SUFFIX = "Function";
  public static final String FUNCTION_CONNECTOR = ".";
  
  public static String moduleName(String funcName)
  {
    int dotIndex = funcName.indexOf(".");
    if (dotIndex < 0)
      return "";
    return funcName.substring(0, dotIndex);
  }
  
  public static String functionName(String funcName)
  {
    int lastDotIndex = funcName.lastIndexOf(".");
    if (lastDotIndex < 0)
      return funcName;
    return funcName.substring(lastDotIndex + 1, funcName.length());
  }
  
  public static String functionClassName(Class functionClass)
  {
    String name = ClassUtil.classname(functionClass);
    // strip Function suffix, if any
    String jqlClassName = stripSuffix(name, FUNCTION_SUFFIX);
    return jqlClassName;
  }
  
  public static String functionClassName(String functionClassName)
  {
    String name = ClassUtil.classname(functionClassName);
    // strip Function suffix, if any
    String jqlClassName = stripSuffix(name, FUNCTION_SUFFIX);
    return jqlClassName;
  }
  
  /**
   * Compute registration name of function.
   * Classname may be null, in which case function name
   * is simply value of <tt>name</tt>.
   * 
   * @param className
   * @param name
   * @return
   */
  public static String functionName(String className, String funcName)
  {
    if (className != null)
      return functionClassName(className) + FUNCTION_CONNECTOR + funcName;
    return funcName;
  }
  
  
  public static String resultType(Method meth)
  {
    Class retType = meth.getReturnType();
    return ClassUtil.classname(retType);
  }
  /*
  public static String basicClassName(Class cl)
  {
    String fullName = cl.getName();
    int dotPos = fullName.lastIndexOf('.');
    String name = fullName;
    if (dotPos > 0) {
      name = fullName.substring(dotPos + 1);
    }
    return name;
  }
*/
  
  public static String stripSuffix(String str, String suffix)
  {
    if (str.endsWith(suffix)) {
      String stripStr = str.substring(0, str.length() - suffix.length());
      return stripStr;
    }
    return str;
  }

  /**
   * Gets the number of arguments actual visible to the user.
   * 
   * @param method
   * @return
   */
  public static int userArgCount(Method method)
  {
    int nArgs = method.getParameterTypes().length;
    if (nArgs == 0) return 0;
    if (method.getParameterTypes()[0] == EngineContext.class)
      nArgs--;
    return nArgs;
  }
  
  private Set functionClasses = new HashSet();
  private Map functionMap = new MultiMap(new TreeMap());
  
  public FunctionRegistry() {
    init();
  }

  private void init()
  {
    register(null, AggregateFunctions.class, false);
    register(AggFunction.class);
    register(ColorFunction.class);
    register(ConsoleFunction.class);
    register(CRSFunction.class);
//    register(ProjFunction.class);
    register(DateFunction.class);
    register(DebugFunction.class);
    register(FileFunction.class);
    register(FileSysFunction.class);
    register(GeodeticFunction.class);
    register(HtmlFunction.class);
    register(IOFunction.class);
    register(MathFunction.class);
    register(NetFunction.class);
    register(RegExFunction.class);
    register(StringFunction.class);
    register(StrMatchFunction.class);
    register(GenerateFunction.class);
    register(ScriptFunction.class);
    register(SplitByFunction.class);
    register(SystemFunction.class);
    register(ValFunction.class);
    register(XmlStrFunction.class);
    
    register(GeomFunction.class);
    register(GridFunction.class);
    register(LinearRefFunction.class);
    register(GeomPrepFunction.class);
    register(GeomPrepAllFunction.class);

    register(jeql.std.function.geommatch.GeomMatchFunction.class);
  }
 
  public void register(Class functionClass)
  {
    register(functionClass, false);
  }
  
  public void register(Class functionClass, boolean allowReplacement)
  {
    String name = ClassUtil.classname(functionClass);
    // strip Function suffix, if any
    String jqlClassName = stripSuffix(name, FUNCTION_SUFFIX);
    register(jqlClassName, functionClass, allowReplacement);
  }
  
  private void register(String className, 
      Class functionClass, 
      boolean allowReplacement)
  {
    // only register once
    if (! allowReplacement && functionClasses.contains(className))
      throw new ConfigurationException("Function class " + className 
          + " is already registered");
    
    functionClasses.add(functionClass);
    
    Method[] method = functionClass.getMethods();
    for (int i = 0; i < method.length; i++) {
      if (Modifier.isStatic(method[i].getModifiers()) ) {
        String funcName = functionName(className, method[i].getName());
        int nArgs = userArgCount(method[i]);
        if (! allowReplacement && getMethod(funcName, nArgs) != null)
          throw new ConfigurationException("Function " + funcName 
              + "(nargs = " +  nArgs + ") already registered");
        functionMap.put(funcName, method[i]);
      }
    }
  }
  
  public Collection getFunctionNames()
  {
    return functionMap.keySet();
  }
 
  public Collection getFunctionMethods(String moduleName)
  {
    Object item = functionMap.get(moduleName);
    if (item == null) 
      return null;
    if (item instanceof Method) {
      List l = new ArrayList();
      l.add(item);
      return l;
    }
    return (Collection) item;
  }
  
  /**
   * Gets the {@link Method} object corresponding to the given
   * name and argument count.
   * 
   * @param name the name of the function to resolve
   * @return the method for this function
   * @return null if no matching function exists
   */
  public Method getMethod(String name, int nArgs)
  {
    Object item = functionMap.get(name);
    if (item == null) return null;
    
    // CASE: uniquely-defined method
    if (item instanceof Method) {
      Method method = (Method) item;
      /**
       * Check for special (function object) methods.
       * 
       * These are always matched without checking if arg count matches,
       * since they actually return an function evaluator object
       * (so they are called with no args)
       */ 
      if (method.getReturnType() == SplittingFunction.class
          || method.getReturnType() == AggregateFunction.class)
        return method;
      
      // regular value method
      return match(name, nArgs, (Method) item);
    }
    // CASE: method name registered to multiple methods
    Collection c = (Collection) item;
    for (Iterator i = c.iterator(); i.hasNext(); ) {
      Method meth = match(name, nArgs, (Method) i.next());
      if (meth != null) return meth;
    }
    return null;
  }
 
  public Class getReturnType(String name, int nArgs)
  {
    Method meth = getMethod(name, nArgs);
    if (meth == null)
      return null;
    return meth.getReturnType();
  }
  
  /**
   * Check if the number of user-supplied args matches the
   * arg count for the given method, and if so return it.
   * 
   * @param name
   * @param nArgs
   * @param method
   * @return null if method does not match
   */
  public Method match(String name, int nArgs, Method method)
  {
    if (nArgs != userArgCount(method))
      return null;
    return method;  
  }


}
