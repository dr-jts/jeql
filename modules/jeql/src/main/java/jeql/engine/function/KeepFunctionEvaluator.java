package jeql.engine.function;

import java.util.List;

import jeql.engine.Scope;
import jeql.engine.query.QueryScope;
import jeql.syntax.ParseTreeNode;

/**
 * Implements the KEEP pseudo-function semantics.
 * Keep returns the value of an expression evaluated
 * on the most recent previous row for which the value 
 * of a condition expression is true.
 * 
 * Syntax:
 * <pre>
 *    KEEP(valueExpr, condExpr [, initValueExpr] )
 * </pre>
 * A KEEP function must always have a value expression and a
 * condition expression.
 * An optional expression for the initial value of the function may be supplied.
 * 
 * @author Martin Davis
 *
 */
public class KeepFunctionEvaluator
  implements FunctionEvaluator
{
  private ParseTreeNode valueExpr;
  private ParseTreeNode condExpr = null;
  private ParseTreeNode initExpr = null;
  // builtins
  public static final String FN_KEEP = "keep";
    
  public KeepFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    valueExpr = (ParseTreeNode) args.get(0);
    if (args.size() >= 2)
      condExpr = (ParseTreeNode) args.get(1);
    if (args.size() >= 3)
      initExpr = (ParseTreeNode) args.get(2);
  }
  
   public Object eval(Scope scope)
   {
     // if this fails, KEEP is being called outside of a SELECT => error
     QueryScope qScope = (QueryScope) scope;
     
     // set initial value if present
     if (! qScope.hasValue(this) && initExpr != null) {
       Object initValue  = initExpr.eval(scope);
       qScope.setValue(this, initValue); 
     }
     
     // compute new possible result, and save if condition is true
     Object result = null;
     if (isCondTrue(scope)) {
       result = valueExpr.eval(scope);
       qScope.setValue(this, result);
     }
     else {
       result = qScope.getValue(this);
     }
     return result;
   }
   
   private boolean isCondTrue(Scope scope)
   {
     if (condExpr == null) return true;
     
     Boolean condVal = (Boolean) condExpr.eval(scope);
     return condVal.booleanValue();
   }
   
   public Class getType(Scope scope)
   {
     return valueExpr.getType(scope);
   }
}
