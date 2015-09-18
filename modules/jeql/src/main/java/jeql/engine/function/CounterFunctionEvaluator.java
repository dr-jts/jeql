package jeql.engine.function;

import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.query.QueryScope;
import jeql.syntax.ParseTreeNode;
import jeql.util.TypeUtil;

/**
 * Implements the COUNTER pseudo-function semantics.
 * <p>
 * Syntax:
 * <pre>
 *    COUNTER(incCondExpr [, resetCondExpr] )
 * </pre>
 * COUNTER returns a integer value
 * which is incremented whenever incCondExpr is true. 
 * The counter value is reset whenever resetCondExpr is true.
 * The initial value of COUNTER(...) is 1.
 * 
 * This allows assigning sequence numbers to streams
 * which contian tag values that are fixed and known in advance.
 * An example is a stream of table cells, where the row and cell delimiters are known.
 * 
 * @author Martin Davis
 *
 */
public class CounterFunctionEvaluator
  implements FunctionEvaluator
{
  private int argCount = 0;
  private ParseTreeNode incCondExpr;
  private ParseTreeNode resetCondExpr = null;
  private int initialValue = 0;
  public static final String FN_NAME = "counter";
  
  public CounterFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    argCount = args.size();
    incCondExpr = (ParseTreeNode) args.get(0);
    if (args.size() >= 2)
      resetCondExpr = (ParseTreeNode) args.get(1);
    
    if (argCount < 1 || argCount > 2)
      throw new CompilationException("COUNTER() function must have 1 or 2 arguments");
  }
  
   public Object eval(Scope scope)
   {
     // if this fails, function is being called outside of a SELECT => error
     QueryScope qScope = (QueryScope) scope;
     
     int counterValue = initialValue;

     if (qScope.getRowNum() <= 1) {
       counterValue = initialValue;
       // TODO: add initial value optional parameter
       //if (initExpr != null)
       //  prevVal = initExpr.eval(scope);
     }
     else {
       counterValue = (Integer) qScope.getValue(this);
     }
     Object resetCondObj = null;
     if (resetCondExpr != null) 
       resetCondObj = resetCondExpr.eval(scope);
     
     boolean resetVal = TypeUtil.toBoolean(resetCondObj);
     Object incCondObj = incCondExpr.eval(scope);
     boolean incVal = TypeUtil.toBoolean(incCondObj);
     
     if (resetVal) {
       // TODO: set this from optional value
       counterValue = initialValue;
     }
     if (incVal) {
       counterValue++;
     }
      
     qScope.setValue(this, counterValue);

     return counterValue;
   }
   
   public Class getType(Scope scope)
   {
     return Integer.class;
   }
   
}
