package jeql.syntax.operation;

import jeql.api.error.ImplementationException;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;

public class BooleanOperation 
  extends Operation
{

  public BooleanOperation(ParseTreeNode e1, ParseTreeNode e2, String opStr, int opCode) {
    super(e1, e2, opStr, opCode);
  }

  public Class getType(Scope scope)
  {
    return Boolean.class;
  }

  public Object compute(Object o1, Object o2)
  {
    Object v1 = coerce(o1, Boolean.class);
    Object v2 = coerce(o2, Boolean.class);
    boolean b1 = ((Boolean) v1).booleanValue();
    boolean b2 = ((Boolean) v2).booleanValue();
    
    switch (opCode) {
    case Operation.AND: return new Boolean(b1 && b2);
    case Operation.OR:  return new Boolean(b1 || b2);
    case Operation.XOR: return new Boolean(b1 != b2);
    }
    throw new ImplementationException("Unknown op code for boolean operation");
  }
}
