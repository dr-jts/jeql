package jeql.syntax;

import jeql.engine.CompilationException;
import jeql.engine.Scope;



/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */
public class NumericConstantNode
  extends ParseTreeNode
{
  private Object value = null;
  
  public NumericConstantNode(String numStr) 
  {
    this.value = parse(numStr);
  }

  private Object parse(String numStr)
  {
    // strip "_" - they are irrelevant
    String cleanNumStr = numStr.replaceAll("_", "");
    try {
      if (cleanNumStr.indexOf('.') >= 0)
        return new Double(cleanNumStr);
      return new Integer(cleanNumStr);
    }
    catch (NumberFormatException ex) {
      throw new CompilationException(this, "Invalid number: " + numStr);
    }
  }
  
  public Class getType(Scope scope)
  {
    return value.getClass();
  }
   
  public void bind(Scope scope)
  {
  }

  public Object eval(Scope scope)
  {
    return value;
  }
  
  public int getInteger() { return ((Number) value).intValue(); }
}