package jeql.syntax;

import jeql.engine.Scope;

public abstract class ParseTreeNode 
{
  protected int line = 0;
  
  public abstract void bind(Scope scope);
  public abstract Class getType(Scope scope);
  public abstract Object eval(Scope scope);
  
  public void setLoc(int line)
  {
    this.line = line;
  }
  
  public void setLoc(ParseTreeNode node)
  {
    line = node.line;
  }
  
  public int getLine() { return line; }
  
  public boolean hasLocation() { return line > 0; }
}
