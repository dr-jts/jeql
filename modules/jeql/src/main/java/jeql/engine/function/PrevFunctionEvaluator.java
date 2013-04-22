package jeql.engine.function;

import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.query.QueryScope;
import jeql.syntax.ParseTreeNode;

/**
 * Implements the PREV pseudo-function semantics.
 * <p>
 * Syntax:
 * <pre>
 *    PREV(valueExpr [, initValueExpr] )
 * </pre>
 * PREV returns the value of an expression evaluated in the context 
 * of the previous row appearing in the result.
 * An optional initial value may be supplied for the value of the expression
 * for the first row evaluated.
 * Otherwise, the value of PREV(...) for the first row in the result is <code>null</code>
 * 
 * @author Martin Davis
 *
 */
public class PrevFunctionEvaluator
  implements FunctionEvaluator
{
  private int argCount = 0;
  private ParseTreeNode valueExpr;
  private ParseTreeNode initExpr = null;
  public static final String FN_PREV = "prev";
    
  public PrevFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    argCount = args.size();
    valueExpr = (ParseTreeNode) args.get(0);
    if (args.size() >= 2)
      initExpr = (ParseTreeNode) args.get(1);
    
    if (argCount < 1 || argCount > 2)
      throw new CompilationException("PREV() function must have 1 or 2 arguments");
  }
  
   public Object eval(Scope scope)
   {
     // if this fails, function is being called outside of a SELECT => error
     QueryScope qScope = (QueryScope) scope;
     
     Object prevVal = null;
     if (qScope.getRowNum() <= 1) {
       if (initExpr != null)
         prevVal = initExpr.eval(scope);
     }
     else {
       prevVal = qScope.getValue(this);
     }
     Object result = valueExpr.eval(scope);
     qScope.setValue(this, result);

     return prevVal;
   }
   
   public Class getType(Scope scope)
   {
     return valueExpr.getType(scope);
   }
}
