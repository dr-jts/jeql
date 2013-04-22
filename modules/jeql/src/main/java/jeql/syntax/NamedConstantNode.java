package jeql.syntax;

import jeql.engine.Scope;


/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Martin Davis
 * @version 1.0
 */
public class NamedConstantNode
  extends ParseTreeNode
{
  private Object value = null;
  
  public NamedConstantNode(String literal) 
  {
    this.value = parse(literal);
  }

  private Object parse(String literal)
  {
    if (literal.equalsIgnoreCase("true"))
      return new Boolean(true);
    if (literal.equalsIgnoreCase("false"))
      return new Boolean(false);
    return null;
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
  
}