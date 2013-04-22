package jeql.syntax.operation;

import java.util.Date;

import jeql.api.error.ExecutionException;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.syntax.ParseTreeNode;
import jeql.util.ClassUtil;

import com.vividsolutions.jts.geom.Geometry;

public abstract class Operation 
{
  public static final int MUL = 1;
  public static final int DIV = 2;
  public static final int ADD = 3;
  public static final int SUB = 4;
  public static final int MOD = 5;
  
  public static final int GT = 10;
  public static final int GE = 11;
  public static final int LT = 12;
  public static final int LE = 13;
  public static final int EQ = 14;
  public static final int NE = 15;
  public static final int RE_FIND = 16;
  public static final int RE_MATCH = 17;
  
  public static final int AND = 20;
  public static final int OR = 21;
  public static final int XOR = 22;
  public static final int NOT = 23;
  
  public static final int UNION = 23;
  
  public static boolean isRelOp(int op)
  {
    return op == EQ
    || op == NE
    || op == GT
    || op == GE
    || op == LT
    || op == LE
    || op == RE_FIND
    || op == RE_MATCH;
  }
  
  public static boolean isBooleanTypeOp(int op)
  {
    return op == AND
    || op == OR
    || op == NOT
    || isRelOp(op);
  }
  
 public static int toOpcode(String opStr)
 {
   if (opStr.equals("*")) return MUL;
   if (opStr.equals("/")) return DIV;
   if (opStr.equals("+")) return ADD;
   if (opStr.equals("-")) return SUB;
   if (opStr.equals("%")) return MOD;
   
   if (opStr.equals(">"))  return GT;
   if (opStr.equals(">=")) return GE;
   if (opStr.equals("<"))  return LT;
   if (opStr.equals("<=")) return LE;
   if (opStr.equals("==")) return EQ;
   if (opStr.equals("!=")) return NE;
   if (opStr.equals("~"))  return RE_FIND;
   if (opStr.equals("~=")) return RE_MATCH;
   
   if (opStr.equalsIgnoreCase("and")) return AND;
   if (opStr.equalsIgnoreCase("or"))  return OR;
   if (opStr.equalsIgnoreCase("xor")) return XOR;
   if (opStr.equalsIgnoreCase("not")) return NOT;
   
   //if (opStr.equalsIgnoreCase("union")) return UNION;
   
   return -1;
 }

  protected ParseTreeNode e1;

  protected ParseTreeNode e2;

  protected int opCode;

  protected String opStr;

  public Operation(ParseTreeNode e1, ParseTreeNode e2, String opStr, int opCode) {
    this.e1 = e1;
    this.e2 = e2;
    this.opCode = opCode;
    this.opStr = opStr;
  }

  public Class getType(Scope scope) {
    Class t1 = e1.getType(scope);
    Class t2 = e2.getType(scope);
    return getResultType(t1, t2);
  }

  /**
   * Determines the result type of a mixed-type operator
   * 
   * @param t1
   * @param t2
   * @return
   */
  public static Class getResultType(Class t1, Class t2) {
    Class exprType = Integer.class;

    // order is important in the following
    if (t1 == String.class || t2 == String.class) {
      exprType = String.class;
    } else if (t1 == Double.class || t2 == Double.class) {
      exprType = Double.class;
    } else if (t1 == Date.class || t2 == Date.class) {
      exprType = Date.class;
    } else if (t1 == Boolean.class || t2 == Boolean.class) {
      exprType = Boolean.class;
    } else if (t1 == Table.class || t2 == Table.class) {
      exprType = Table.class;
    } else if (Geometry.class.isAssignableFrom(t1)
        || Geometry.class.isAssignableFrom(t2))
      exprType = Geometry.class;

    return exprType;
  }

  protected static Class getMostGeneralType(Object o1, Object o2) {
    if (o1 == null)
      return o2.getClass();
    if (o2 == null)
      return o1.getClass();
    return Operation.getResultType(o1.getClass(), o2.getClass());
  }

  /**
   * A few type coercions are allowed.
   * 
   * @param o
   * @param reqType
   * @return
   */
  protected Object coerce(Object o, Class reqType) {
    if (o == null)
      return null;

    if (o.getClass() == reqType)
      return o;

    if (reqType == String.class) {
      return o.toString();
    }
    if (reqType == Double.class) {
      if (o instanceof Integer)
        return new Double(((Integer) o).intValue());
    }
    if (reqType == Geometry.class) {
      if (o instanceof Geometry)
        return o;
    }
    throw new ExecutionException("Can't convert type "
        + ClassUtil.classname(o.getClass()) + " to "
        + ClassUtil.classname(reqType)
        + " in operation " + opStr);
  }

  protected void require(Object o, Class reqType) {
    if (o == null)
      return;

    if (o.getClass() == reqType)
      return;

    throw new ExecutionException("Type "
        + ClassUtil.classname(o.getClass()) 
        + " is not of the required type "
        + ClassUtil.classname(reqType)
        + " in operation " + opStr);
  }

  public abstract Object compute(Object o1, Object o2);
}
