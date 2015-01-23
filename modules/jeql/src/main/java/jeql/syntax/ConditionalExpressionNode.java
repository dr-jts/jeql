package jeql.syntax;

import jeql.engine.Scope;

public class ConditionalExpressionNode 
  extends ParseTreeNode
{

  private ParseTreeNode condExpr; 
  private ParseTreeNode expr1;
  private ParseTreeNode expr2;
  
  public ConditionalExpressionNode(ParseTreeNode condExpr, 
      ParseTreeNode expr1,
      ParseTreeNode expr2
      ) 
  {
    this.condExpr = condExpr;
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  public Class getType(Scope scope)
  {
    return expr1.getType(scope);
  }
  
  public void bind(Scope scope)
  {
    condExpr.bind(scope);
    expr1.bind(scope);
    expr2.bind(scope);
  }

  public Object eval(Scope scope)
  {
    Object condVal = condExpr.eval(scope);
    boolean cond = condVal == null ? false : ((Boolean) condVal).booleanValue();
    if (cond)
      return expr1.eval(scope);
    else
      return expr2.eval(scope);
  }
  

}
