package jeql.syntax.operation;

import java.util.Date;

import jeql.api.error.ExecutionException;
import jeql.std.function.StringFunction;
import jeql.syntax.ParseTreeNode;
import jeql.util.ClassUtil;
import jeql.util.TypeUtil;

public class ArithmeticOperation 
  extends Operation
{

  public ArithmeticOperation(ParseTreeNode e1, ParseTreeNode e2, String opStr, int opCode) {
    super(e1, e2, opStr, opCode);
  }

  public Object compute(Object o1, Object o2)
  {
    Class exprType = getMostGeneralType(o1, o2);
    Object v1 = coerce(o1, exprType);
    Object v2 = coerce(o2, exprType);
    
    if (exprType == Double.class)  
      return computeDouble(v1, v2);
    if (exprType == Integer.class)  
      return computeInteger(v1, v2);
    if (exprType == String.class)
      return computeString(v1, v2);
    if (exprType == Date.class)
      return computeDate(v1, v2);
    
    throwOpTypeError(opStr, o1.getClass());
    return null;
  }
  
  private Object computeDouble(Object v1, Object v2)
  {
	  // null safety
	if (v1 == null || v2 == null) return null;
    double o1 = ((Double) v1).doubleValue(); 
    double o2 = ((Double) v2).doubleValue(); 
    switch (opCode) {
    case Operation.MUL: return new Double(o1 * o2);
    case Operation.DIV: return new Double(o1 / o2);
    case Operation.ADD: return new Double(o1 + o2);
    case Operation.SUB: return new Double(o1 - o2);
    case Operation.MOD: return new Double(o1 % o2);
    }
    throwOpTypeError(opStr, Double.class);
    return null;
  }
  
  private Object computeInteger(Object v1, Object v2)
  {
	  // null safety
	if (v1 == null || v2 == null) return null;
    int o1 = ((Integer) v1).intValue(); 
    int o2 = ((Integer) v2).intValue(); 
    switch (opCode) {
    case Operation.MUL: return new Integer(o1 * o2);
    case Operation.DIV: return new Integer(o1 / o2);
    case Operation.ADD: return new Integer(o1 + o2);
    case Operation.SUB: return new Integer(o1 - o2);
    case Operation.MOD: return new Integer(o1 % o2);
    }
    throwOpTypeError(opStr, Integer.class);
    return null;
  }
  
  private Object computeString(Object v1, Object v2)
  {
    String s1 = (String) v1; 
   
    switch (opCode) {
    case Operation.ADD: {
        String s2 = (String) v2; 
    	if (s1 == null) return s2;
    	if (s2 == null) return s1;
    	return s1 + s2;
    }
    case Operation.MUL: {
    	if (s1 == null) return null;
    	if (v2 == null) return "";
        int count = (int) TypeUtil.toDouble(v2); // a bit of a hack - need toInt()
    	return StringFunction.repeat(s1, count);
    }
    // TODO: provide string * integer
    }
    throwOpTypeError(opStr, s1.getClass());
    return null;
  }
  
  private Object computeDate(Object v1, Object v2)
  {
	  // null safety
	if (v1 == null || v2 == null) return null;
    Date o1 = (Date) v1; 
    Date o2 = (Date) v2; 

    // TODO: +, - for Dates and numbers
    
    throwOpTypeError(opStr, Date.class);
    return null;
  }
  
  private void throwOpTypeError(String opStr, Class expectedClass) {
    throw new ExecutionException("Operator '" + opStr 
        + "' is undefined for type " 
        + ClassUtil.classname(expectedClass));

  }
}
