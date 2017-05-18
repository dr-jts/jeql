package jeql.engine.function;

import java.util.List;

import jeql.api.function.AggregateFunction;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;

public abstract class BaseAggregateFunction 
implements AggregateFunction
{
  Class typeClass = null;
  
  public void bind(Scope scope, List args)
  {
    typeClass = ((ParseTreeNode) args.get(0)).getType(scope);
  }
  
  public Class getType() { return typeClass; } 

  public Class[] getParamTypes() {
	  return new Class[] { Object.class };
  }
}
