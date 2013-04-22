package jeql.syntax;

import jeql.api.error.ExecutionException;
import jeql.engine.Scope;
import jeql.syntax.operation.ArithmeticOperation;
import jeql.syntax.operation.BooleanOperation;
import jeql.syntax.operation.ComparisonOperation;
import jeql.syntax.operation.Operation;
import jeql.syntax.operation.RegExOperation;

public class BinaryExpressionNode 
  extends ParseTreeNode
{
  private String opStr;
  private int opCode;
  private ParseTreeNode e1; 
  private ParseTreeNode e2;
  private Operation operation;
  
  public BinaryExpressionNode(String opStr, 
      ParseTreeNode e1, 
      ParseTreeNode e2,
      int line) {
    setLoc(line);
    this.opStr = opStr;
    this.opCode = Operation.toOpcode(opStr);
    this.e1 = e1;
    this.e2 = e2;
    
    switch(opCode) {
    case Operation.RE_FIND:
    case Operation.RE_MATCH:
      operation = new RegExOperation(e1, e2, opStr, opCode);
      break;
    case Operation.GT:
    case Operation.GE:
    case Operation.LT:
    case Operation.LE:
    case Operation.EQ:
    case Operation.NE:
      operation = new ComparisonOperation(e1, e2, opStr, opCode);
      break;
    case Operation.AND:
    case Operation.OR:
    case Operation.XOR:
      operation = new BooleanOperation(e1, e2, opStr, opCode);
      break;
    default:
      operation = new ArithmeticOperation(e1, e2, opStr, opCode);
    }
  }

  public int getOpCode()
  {
    return opCode;
  }
  
  /**
   * Gets the operand from a given side of the expression
   * @param side 0 or 1, for LHS or RHS
   * @return
   */
  public ParseTreeNode getSide(int side)
  {
    if (side == 0) return e1;
    return e2;
  }
  
  public Class getType(Scope scope)
  {
    return operation.getType(scope);
    
    /*
    if (Operation.isRelOp(opCode))
      return Boolean.class;
    
    Class t1 = e1.getType(scope);
    Class t2 = e2.getType(scope);
    
    return Operation.getType(t1, t2);
    */
  }
  
  public void bind(Scope scope)
  {
    e1.bind(scope);
    e2.bind(scope);
  }

  public Object eval(Scope scope)
  {
    Object v1 = e1.eval(scope);
    
    // short circuits for boolean operations
    if (opCode == Operation.AND) {
      if (((Boolean) v1).booleanValue() == false) {
        return Boolean.FALSE;
      }
    }
    if (opCode == Operation.OR) {
      if (((Boolean) v1).booleanValue() == true) {
        return Boolean.TRUE;
      }
    }
        
    Object v2 = e2.eval(scope);
    
    // special case if one value is null - only certain ops can be computed
    if (v1 == null || v2 == null) {
      return computeNullValuesOp(v1, v2);
    }
    
    return operation.compute(v1, v2);
  }
  
  private Object computeNullValuesOp(Object v1, Object v2)
  {
    if (opCode == Operation.EQ) return new Boolean(v1 == v2);
    if (opCode == Operation.NE) return new Boolean(v1 != v2);
    if (opCode == Operation.RE_FIND) return Boolean.FALSE;
    if (opCode == Operation.RE_MATCH) return Boolean.FALSE;
    if (opCode == Operation.ADD 
        && (v1 instanceof String || v2 instanceof String)) 
      return operation.compute(v1, v2);
   
    throw new ExecutionException(this, "Operator " + opStr + " is undefined for null operands");    
  }
  

}
