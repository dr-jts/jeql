package jeql.engine.function;

import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.query.QueryScope;

/**
 * Implements the ROWNUM pseudo-function semantics.
 * 
 * @author Martin Davis
 *
 */
public class RowNumFunctionEvaluator
  implements FunctionEvaluator
{
  private int argCount;  // only needed for syntax checking
  public static final String FN_ROWNUM = "rownum";
    
  public RowNumFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    argCount = args.size();
    if (argCount != 0)
      throw new CompilationException("ROWNUM() function must have 0 arguments");
  }
  
   public Object eval(Scope scope)
   {
     // if this fails, function is being called outside of a SELECT => error
     QueryScope qScope = (QueryScope) scope;
     return new Integer(qScope.getRowNum());
   }
   
   public Class getType(Scope scope)
   {
     return Integer.class;
   }
}