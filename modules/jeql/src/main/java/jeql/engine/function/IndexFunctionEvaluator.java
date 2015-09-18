package jeql.engine.function;

import java.util.List;

import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.query.QueryScope;
import jeql.syntax.ParseTreeNode;
import jeql.util.TypeUtil;

/**
 * Implements the INDEX pseudo-function semantics.
 * <p>
 * Syntax:
 * <pre>
 *    INDEX( inc-val [, reset-val ]  )
 * </pre>
 * INDEX returns a integer value
 * which is incremented whenever the value of inc-val changes. 
 * The counter value is reset whenever the value of reset-val changes.
 * The initial value of INDEX(...) is 1.
 * 
 * This allows assigning sequence numbers to streams of 
 * changing values where the values are not known in advance.
 * 
 * @author Martin Davis
 *
 */
public class IndexFunctionEvaluator
  implements FunctionEvaluator
{
  private int argCount = 0;
  private ParseTreeNode incCondExpr;
  private ParseTreeNode resetExpr = null;
  public static final String FN_NAME = "index";
  
  public IndexFunctionEvaluator() 
  {
  }

  public void bind(Scope scope, List args)
  {   
    argCount = args.size();
    incCondExpr = (ParseTreeNode) args.get(0);
    if (args.size() >= 2)
      resetExpr = (ParseTreeNode) args.get(1);
    
    if (argCount < 1 || argCount > 2)
      throw new CompilationException("INDEX() function must have 1 or 2 arguments");
  }
  
   public Object eval(Scope scope)
   {
     // if this fails, function is being called outside of a SELECT => error
     QueryScope qScope = (QueryScope) scope;
     
     IndexValueTracker track = (IndexValueTracker) qScope.getValue(this);
     if (track == null || qScope.getRowNum() <= 1) {
       // TODO: add initial value optional parameter
       track = new IndexValueTracker();
       qScope.setValue(this, track);
     }
     
     Object incCondObj = incCondExpr.eval(scope);
     
     Object resetObj = null;
     if (resetExpr != null) 
       resetObj = resetExpr.eval(scope);
     
     track.update(incCondObj, resetObj);
     

     return track.getIndex();
   }
   
   public Class getType(Scope scope)
   {
     return Integer.class;
   }
   
   class IndexValueTracker {
     
     private boolean isFirst = true;
     private int initialValue = 1;
     private int index = 1;
     private Object currIncVal = null;
     private Object currResetVal = null;
     
     public IndexValueTracker()
     {
       // TODO: allow initial value to be set by user
       index = initialValue;
     }
     
     public int getIndex()
     {
       return index;
     }
     
     public void update(Object incVal, Object resetVal)
     {
       if (isFirst) {
         currIncVal = incVal;
         currResetVal = resetVal;
         isFirst = false;
         index = initialValue;
         return;
       }
       if (isFirst || ! eq(resetVal, currResetVal)) {
         index = initialValue;
         currResetVal = resetVal;
         if (! eq(incVal, currIncVal)) {
           index++;
         }
         currIncVal = incVal;
       }
       else if (! eq(incVal, currIncVal)) {
         index++;
         currIncVal = incVal;
       }
       isFirst = false;
     }
     
     public boolean eq(Object o1, Object o2)
     {
       if (o1 == null) {
         return o2 == null;
       }
       return o1.equals(o2);
     }
   }
}
