package jeql.engine.function;

import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.engine.Scope;
import jeql.engine.query.group.GroupScope;

/**
 * Evaluates an aggregate function in 
 * the context of its group scope.
 * 
 * @author Martin Davis
 *
 */
public class AggregateFunctionEvaluator
  implements FunctionEvaluator
{
  private AggregateFunction aggFun;
  private int colIndex = 0;
  
  public AggregateFunctionEvaluator(AggregateFunction aggFun) {
    this.aggFun = aggFun;
  }

  public void bind(Scope scope, List args) 
  {
    aggFun.bind(scope, args); 
  }

  public Object eval(Scope scope)
  {
    // if this fails, is being called outside of a group scope => internal error
    GroupScope grScope = (GroupScope) scope;
    Object value = grScope.getRow().getValue(colIndex);
    return value;
  }

  public Class getType(Scope scope) {
    return aggFun.getType();
  }

  public AggregateFunction getFunction() {
    return aggFun;
  }
  
  public void setColumnIndex(int colIndex)
  {
    this.colIndex = colIndex;
  }
  
  public int getColumnIndex()
  {
    return colIndex;
  }
}
