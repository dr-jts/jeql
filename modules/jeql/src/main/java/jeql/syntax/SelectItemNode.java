package jeql.syntax;

import jeql.api.row.RowSchema;
import jeql.engine.Scope;
import jeql.syntax.util.IdentifierUtil;


/**
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class SelectItemNode 
  extends ParseTreeNode
{
  private ParseTreeNode expr;
  private String alias = null;

  public SelectItemNode(ParseTreeNode expr) {
    this.expr = expr;
    setLoc(expr);
  }

  public SelectItemNode(ParseTreeNode expr, String alias) {
    this(expr);
    this.alias = IdentifierUtil.keyIdentifier(alias);
  }

  public String getName(int colNum) 
  {
    if (alias != null)
      return alias;
    if (expr instanceof TableColumnNode)
      return ((TableColumnNode) expr).getColName();
    // no other name assigned, so return default nam
    return RowSchema.getDefaultColumnName(colNum);
  }

  public ParseTreeNode getItem() {
    return expr;
  }

  public Class getType(Scope scope) {
    return expr.getType(scope);
  }

  public void bind(Scope scope) {
    expr.bind(scope);
  }

  public Object eval(Scope scope)
  {
    return expr.eval(scope);
  }

  public String toString()
  {
    String aliasStr = alias == null ? "" : alias; 
    return aliasStr;
  }
}