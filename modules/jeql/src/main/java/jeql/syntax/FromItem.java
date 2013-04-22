package jeql.syntax;

import jeql.api.error.ImplementationException;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.Scope;

/**
 * <p> </p>
 * <p> </p>
 * @author Martin Davis
 * @version 1.0
 */
public class FromItem
  extends ParseTreeNode
{
  public static final int JOIN_INNER = 0;
  public static final int JOIN_LEFT_OUTER = 1;
  public static final int JOIN_RIGHT_OUTER = 2;
  public static final int JOIN_OUTER = 4;
  
  private boolean isJoinNode = true;
  private int joinType = JOIN_INNER;
  private TableExpressionNode tableExpr;
  private ParseTreeNode joinConditionExpr = null;

  public FromItem(TableExpressionNode tableExpr) {
    this(0, tableExpr, null);
    isJoinNode = false;
  }
  
  /*
  public FromItem(TableExpressionNode tableExpr, ParseTreeNode joinConditionExpr) {
    this(0, tableExpr, joinConditionExpr);
  }
*/
  
  public FromItem(int joinType, TableExpressionNode tableExpr, ParseTreeNode joinConditionExpr) 
  {
    this.joinType = joinType;
    this.tableExpr = tableExpr;
    this.joinConditionExpr = joinConditionExpr;
    
    if (joinType == JOIN_RIGHT_OUTER || joinType == JOIN_OUTER) {
      throw new CompilationException(this, "RIGHT OUTER joins are not supported");
    }
  }

  public String getTableName()
  {
    return tableExpr.getTableName();
  }
  
  /**
   * Gets the alias name, if present, or else the original table name.
   * Used in situations where the alias name should be used if present.
   * 
   * Sometimes no table name can be determined, e.g. when a table-valued function is used.
   * This is an error - a table alias is required.
   * 
   * @return
   */
  public String getAliasOrTableName()
  {
    String alias = tableExpr.getAlias();
    if (alias != null)
      return alias;
    return tableExpr.getTableName();
  }
  
  /**
   * Gets the original table name, if a single table,
   * or the alias, if an expression
   * @return
   */
  public String getTableNameOrAlias()
  {
    String name = tableExpr.getTableName();
    if (name != null)
      return name;
    return tableExpr.getAlias();
  }
  
  public int getJoinType() { return joinType; }
  
  public String getAlias() { return tableExpr.getAlias(); }
  
  public Table getTable(Scope scope)
  {
    return tableExpr.getTable(scope);
  }
  
  public TableExpressionNode getTableExpression() { return tableExpr; }
  
  public ParseTreeNode getJoinConditionExpr() { return joinConditionExpr; }
  
  public RowList eval(Scope scope)
  {
    String fromTblName = getAliasOrTableName();
    if (fromTblName == null) {
      throw new CompilationException(tableExpr, fromClauseName() + " expression must have table alias");
    }
    Table table = scope.resolveTable(fromTblName);
    return table.getRows();
  }
  
  private String fromClauseName()
  {
    if (isJoinNode) return "JOIN";
    return "FROM";
  }
  
  @Override
  public void bind(Scope scope)
  {
    throw new ImplementationException("FromItem does not support binding");
  }
  @Override
  public Class getType(Scope scope)
  {
    throw new ImplementationException("FromItem does not provide a type");
  }

}