package jeql.syntax;

import jeql.engine.Scope;

public class CommandParameterNode 
  extends ParseTreeNode
{
  private String name;
  private ParseTreeNode e;
  
  /**
   * 
   * @param name
   * @param e the expression for the argument (may be null)
   */
  public CommandParameterNode(String name, ParseTreeNode e) {
    // strip trailing ':'
    this.name = name.substring(0, name.length() - 1);
    this.e = e;
  }

  /**
   * Creates an anonymous (default) parameter
   * @param e
   */
  public CommandParameterNode(ParseTreeNode e) {
    this.name = null;
    this.e = e;
    // location is location of expression
    setLoc(e.getLine());
  }

  /**
   * Tests whether this argument is the default (no-name) argument
   * @return
   */
  public boolean isDefault() { return name == null; }
  
  public String getName() { return name; }
  
  public Class getType(Scope scope)
  {
    // an empty argument represents a boolean value
    if (e == null)
      return Boolean.class;

    return e.getType(scope);
  }
  
  public void bind(Scope scope)
  {
    if (e == null) return;
    // don't error on undefined variables, since they may be defined by this command
    if (e instanceof TableColumnNode) {
      ((TableColumnNode) e).setCheckUndefinedVariables(false);
    }
    e.bind(scope);
  }

  public Object eval(Scope scope)
  {
    // an empty argument evaluates to true
    if (e == null)
      return new Boolean(true);
    
    Object v = e.eval(scope);
    return v;
  }
  
  public boolean isAssignable()
  {
    return e instanceof TableColumnNode;
  }
  
  public String getArgName()
  {
    return ((TableColumnNode) e).getColName();
  }

}
