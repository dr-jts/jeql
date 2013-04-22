package jeql.syntax.operation;

import jeql.api.error.ImplementationException;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;
import jeql.util.TypeUtil;

public class ComparisonOperation 
  extends Operation
{

  public ComparisonOperation(ParseTreeNode e1, ParseTreeNode e2, String opStr, int opCode) {
    super(e1, e2, opStr, opCode);
  }

  public Class getType(Scope scope)
  {
    return Boolean.class;
  }

  public Object compute(Object o1, Object o2)
  {
    Class exprType = getMostGeneralType(o1, o2);
    Object v1 = coerce(o1, exprType);
    Object v2 = coerce(o2, exprType);
    
    return new Boolean(computeComparison(TypeUtil.compareValue(v1, v2)));
  }
  
  private boolean computeComparison(int compSign)
  {
    switch(opCode) {
    case Operation.GT: return compSign > 0;
    case Operation.GE: return compSign >= 0;
    case  Operation.LT: return compSign < 0;
    case  Operation.LE: return compSign <= 0;
    case  Operation.EQ: return compSign == 0;
    case  Operation.NE: return compSign != 0;
    }
    throw new ImplementationException("Unknown op code for comparison");
  }

}
