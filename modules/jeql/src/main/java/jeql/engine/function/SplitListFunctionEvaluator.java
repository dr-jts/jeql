package jeql.engine.function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.FunctionRegistry;
import jeql.engine.Scope;


public class SplitListFunctionEvaluator
  implements FunctionEvaluator
{
  private String className = null;
  private String name;
  private List args;
  private MethodFunctionInvoker funcInvoker;
  
  public SplitListFunctionEvaluator(String className, String name, List args) 
  {
    this.className = className;
    this.name = name;
    if (args == null)
      this.args = new ArrayList();
    else
      this.args = args;
  }

//  public List getArgs() { return args; }
  
  public void bind(Scope scope, List args)
  {
    String fullName = FunctionRegistry.functionName(className, name);
    Method meth = scope.getContext().getFunction(fullName, args.size());
    if (meth == null)
      throw new CompilationException("Unknown function - " + fullName);
    funcInvoker = new MethodFunctionInvoker(meth);
    
    if (args.size() != funcInvoker.getUserArgNum()) {
      throw new CompilationException("Wrong number of arguments in function call");
    }
  }
  
  public Object eval(Scope scope)
  {
    return funcInvoker.eval(scope, args);
  }
  
   public Class getType(Scope scope)
   {
     return funcInvoker.getParameterizedListType(scope);
   }
}
