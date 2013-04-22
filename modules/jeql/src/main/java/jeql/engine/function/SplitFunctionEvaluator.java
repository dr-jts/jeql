package jeql.engine.function;

import java.lang.reflect.Method;
import java.util.List;

import jeql.api.function.SplittingFunction;
import jeql.engine.CompilationException;
import jeql.engine.ConfigurationException;
import jeql.engine.FunctionRegistry;
import jeql.engine.Scope;

/**
 * Evaluates a split function.
 * 
 * @author Martin Davis
 *
 */
public class SplitFunctionEvaluator
  implements FunctionEvaluator
{
  private String className = null;
  private String name;
  private SplittingFunction splitFun;
  private List args;
  private MethodFunctionInvoker funcInvoker;
  
  public SplitFunctionEvaluator(String className, String name, List args) {
    this.className = className;
    this.name = name;
    this.args = args;
  }

  public void bind(Scope scope, List args) 
  {
    String fullName = FunctionRegistry.functionName(className, name);
    Method method = scope.getContext().getFunction(fullName, args.size());
    if (method == null)
      throw new CompilationException("Unknown function - " + fullName);
    
    if (method.getReturnType() != SplittingFunction.class) {
      throw new CompilationException("Function " + fullName 
          + " is not a Split function");      
    }
    splitFun = (SplittingFunction) MethodFunctionInvoker.invoke(method, null);
    // call split function via methodInvoker because # of args is only known at runtime
    funcInvoker = new MethodFunctionInvoker(findExecuteMethod(splitFun.getClass()));  
  }

  private static final String SPLIT_FN_EXECUTE_METHOD = "execute";
  
  private static Method findExecuteMethod(Class functionClass)
  {
    Method[] method = functionClass.getMethods();
    for (int i = 0; i < method.length; i++) {
      if (method[i].getName().equals(SPLIT_FN_EXECUTE_METHOD)) {
        return method[i];
      }
    }
    throw new ConfigurationException("Method 'execute' not found in SplitFunction class " + functionClass.getName());
  }
 
  public Object eval(Scope scope)
  {
    return funcInvoker.eval(scope, splitFun, args);
  }

  
  public Class getType(Scope scope) {
    return splitFun.getType();
  }

}
