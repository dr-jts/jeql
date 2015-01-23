package jeql.syntax;

import jeql.engine.Scope;
import jeql.engine.TypeConversionException;
import jeql.syntax.operation.Operation;

public class UnaryExpressionNode 
  extends ParseTreeNode
{
  
  private String opStr;
  private int op;
  private ParseTreeNode e; 
  
  public UnaryExpressionNode(String opStr, 
      ParseTreeNode e) {
    this.opStr = opStr;
    this.op = Operation.toOpcode(opStr);
    this.e = e;
  }

  public Class getType(Scope scope)
  {
    return e.getType(scope);
  }
  
  public void bind(Scope scope)
  {
    e.bind(scope);
  }

  public Object eval(Scope scope)
  {
    Object v = e.eval(scope);
    return compute(v);
  }
  
  private Object compute(Object v)
  {
	  if (v == null) return null;
	  
    Class exprType = v.getClass();
    
    if (op == Operation.SUB) {
      if (exprType == Double.class)  
        return new Double(- ((Double) v).doubleValue()); 
      if (exprType == Integer.class)  
        return new Integer(- ((Integer) v).intValue()); 
    }
    else if (op == Operation.NOT) {
      if (exprType == Boolean.class)  
        return new Boolean(! ((Boolean) v).booleanValue()); 
    }
    throw new TypeConversionException(this, "Invalid type for operator " + opStr);
    
  }
  

}
