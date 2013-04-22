package jeql.syntax;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jeql.api.error.JeqlException;
import jeql.api.function.AggregateFunction;
import jeql.api.function.SplittingFunction;
import jeql.engine.CompilationException;
import jeql.engine.FunctionRegistry;
import jeql.engine.Scope;
import jeql.engine.function.AggregateFunctionEvaluator;
import jeql.engine.function.CounterFunctionEvaluator;
import jeql.engine.function.IndexFunctionEvaluator;
import jeql.engine.function.FunctionEvaluator;
import jeql.engine.function.KeepFunctionEvaluator;
import jeql.engine.function.MethodFunctionEvaluator;
import jeql.engine.function.MethodFunctionInvoker;
import jeql.engine.function.PrevFunctionEvaluator;
import jeql.engine.function.RowNumFunctionEvaluator;
import jeql.engine.function.SplitFunctionEvaluator;
import jeql.engine.function.SplitListFunctionEvaluator;
import jeql.engine.function.ValFunctionEvaluator;
import jeql.engine.query.BaseQueryScope;
import jeql.engine.query.QueryScope;
import jeql.engine.query.group.GroupScope;



public class FunctionNode
  extends ParseTreeNode
{
  private FunctionEvaluator funcEval = null;
  private String className = null;
  private String name;
  private List args;
  
  public FunctionNode() 
  {
  }

  public boolean isSplitFunction()
  {
    return funcEval instanceof SplitListFunctionEvaluator 
    || funcEval instanceof SplitFunctionEvaluator ;
  }
  
  public String getName() { return name; }
  
  public void setFunction(String name1, String name2, List args) 
  {
    if (name2 == null) {
      this.name = name1;
    }
    else {
      this.className = name1;
      this.name = name2;
    }
    if (args == null)
      this.args = new ArrayList();
    else
      this.args = args;
  }

  public void bind(Scope scope)
  {
    // skip if already bound
//  if (funcEval != null) return;
    /**
     * No, need to bind always.
     * E.g. for nested subqueries, need to rebind
     * each time subquery is evaled, in order to 
     * create new state for aggregate functions
     * 
     */

    
    /**
     * If in a GroupScope and this is an aggregate function
     * don't bind  
     * (binding has already taken place in the base scope)
     */ 
    if (scope instanceof GroupScope
        && funcEval instanceof AggregateFunctionEvaluator)
      return;

    /**
     * Create evaluator, which is cached and used
     * for all evaluations of this function node.
     */
    funcEval = createFunctionEvaluator(scope);

    boolean isArgStar = args.size() == 1 && TableRefNode.isStar((ParseTreeNode) args.get(0));
    if (isArgStar 
        && ! (funcEval instanceof AggregateFunctionEvaluator)) {
      throw new CompilationException(this, "Only aggregate functions can take * as their argument");
    }
      
    // ensure any exceptions thrown during function binding 
    // are reported at the function call line
    /*
     * MD - OLD I think!
    try {
      if (funcEval != null)
        funcEval.bind(scope, args);
    }
    catch (JeqlException ex) {
      ex.setLocation(this);
      throw ex;
    }
    */
    
    BaseQueryScope aggFunScope = null;
    if (funcEval instanceof AggregateFunctionEvaluator) {
      
      // check for nested agg functions
      if (scope instanceof BaseQueryScope) {
        BaseQueryScope baseScope = (BaseQueryScope) scope;
        if (baseScope.isBindingAggregateFunction()) {
          throw new JeqlException(this, "Nested aggregate function found (" + name + ")");
        }
        aggFunScope = baseScope;
        aggFunScope.addAggregateFunction(this);
      }
      
      // check for arg count = 1
      if (args.size() > 1) {
        throw new CompilationException(this, "Aggregate function arg list must have length 1");
      }
      
    }
    
    if (aggFunScope != null) {
      aggFunScope.setBindingAggregateFunction(true);
    }
 
    // bind args
    for (int i = 0; i < args.size(); i++) {
      ((ParseTreeNode) args.get(i)).bind(scope);
    }

    if (aggFunScope != null) {
      aggFunScope.setBindingAggregateFunction(false);
    }
    
    /*
     * Bind function itself.
     * Any exceptions thrown during function binding 
     * are reported at the function call line
     * 
     */
     try {
      if (funcEval != null)
        funcEval.bind(scope, args);
    }
    catch (JeqlException ex) {
      ex.setLocation(this);
      throw ex;
    }

  }
  
  private FunctionEvaluator createFunctionEvaluator(Scope scope)
  {
    // TODO: match using a registry of name-only functions (i.e. from builtins or imports)
    FunctionEvaluator funcEval = null;
    
    if (className == null) {
      
      // pseudo-functions
      
      if (name.equalsIgnoreCase(KeepFunctionEvaluator.FN_KEEP)) {
        funcEval = new KeepFunctionEvaluator();
      }
      else if (name.equalsIgnoreCase(PrevFunctionEvaluator.FN_PREV)) {
        funcEval = new PrevFunctionEvaluator();
      }
      else if (name.equalsIgnoreCase(IndexFunctionEvaluator.FN_NAME)) {
        funcEval = new IndexFunctionEvaluator();
      }
      else if (name.equalsIgnoreCase(CounterFunctionEvaluator.FN_NAME)) {
        funcEval = new CounterFunctionEvaluator();
      }
      else if (name.equalsIgnoreCase(RowNumFunctionEvaluator.FN_ROWNUM)) {
        funcEval = new RowNumFunctionEvaluator();
      }
      else if (name.equalsIgnoreCase(ValFunctionEvaluator.FN_VAL)) {
        funcEval = new ValFunctionEvaluator();
      }
      
    }
    // if function is not a built-in one, look it up
    if (funcEval == null) {
      funcEval = createFunctionEvaluatorFromRegistry(scope);
    }
    return funcEval;
  }
  
  private FunctionEvaluator createFunctionEvaluatorFromRegistry(Scope scope)
  {
    String fullName = FunctionRegistry.functionName(className, name);
    Class returnType = scope.getContext().getFunctionRegistry()
                    .getReturnType(fullName, args.size());
    /*
    if (returnType == SplittingFunction.class)
      return new SplitFunctionEvaluator(className, name, args);
      */
    // TODO: check for a parameterized List (and maybe also an annotation of "SplitFunction"?
    if (returnType == List.class)
      return new SplitListFunctionEvaluator(className, name, args);
    if (returnType == AggregateFunction.class)
      return new AggregateFunctionEvaluator(createAggFunction(scope, className, name, args));
    // otherwise assume method is a regular function
    return new MethodFunctionEvaluator(className, name, args);
  }
  
  private AggregateFunction createAggFunction(Scope scope, String className, String name, List args)
  {
    String fullName = FunctionRegistry.functionName(className, name);
    Method method = scope.getContext().getFunction(fullName, args.size());
    if (method == null)
      throw new CompilationException("Unknown function - " + fullName);
    
    if (method.getReturnType() != AggregateFunction.class) {
      throw new CompilationException(this, "Function " + fullName 
          + " is not a Aggregate function");      
    }
    return (AggregateFunction) MethodFunctionInvoker.invoke(method, null);
  }
  
  public FunctionEvaluator getFunctionEvaluator() { return funcEval; }
  
  public List getArgs() { return args; }
  
   public Object eval(Scope scope)
   {
     if (funcEval instanceof AggregateFunctionEvaluator) {
       return ((QueryScope) scope).getRow().getValue(
           ((AggregateFunctionEvaluator) funcEval).getColumnIndex());
     }
     
     try {
       return funcEval.eval(scope);
     }
     catch (JeqlException ex) {
       ex.setLocation(this);
       throw ex;
     }
   }
   
   public Class getType(Scope scope)
   {
     return funcEval.getType(scope);
   }
   
   public String toString()
   {
     return name + "()";
   }
}
