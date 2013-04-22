package jeql.syntax;

import java.util.ArrayList;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.engine.query.SelectEvaluator;
import jeql.std.function.ValFunction;

/**
 * A node representing a select expression
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class SelectNode
  extends ParseTreeNode
{
  public static final int NOT_SPECIFIED = -1;
  
  private SelectItemList selectList;
  private boolean isDistinct = false;
  private StatementListNode withList;
  private FromList fromList;
  private ParseTreeNode whereExpr;
  private ParseTreeNode splitByExpr;
  private ParseTreeNode limitExpr = null;
  private int limitVal = NOT_SPECIFIED;
  private ParseTreeNode offsetExpr = null;
  private int offsetVal = NOT_SPECIFIED;
 
  // list<TableColumnNode>
  private List groupByList = null;
  
  // list<OrderItem>
  private List orderList = null;

  public SelectNode(SelectItemList selectList) {
    this.selectList = selectList;
  }

  public SelectNode(SelectItemList selectList, 
      boolean isDistinct, 
      StatementListNode withList, 
      FromList fromList, 
      ParseTreeNode whereExpr, 
      ParseTreeNode splitByExpr, 
      ParseTreeNode limitVal,
      ParseTreeNode offsetVal,
      List groupByList,
      List orderList) {
    this.selectList = selectList;
    this.isDistinct = isDistinct;
    this.withList = withList;
    this.fromList = fromList;
    this.whereExpr = whereExpr;
    this.splitByExpr = splitByExpr;
    this.limitExpr = limitVal;
    this.offsetExpr = offsetVal;
    if (groupByList == null)
      this.groupByList = new ArrayList();
    else
      this.groupByList = groupByList;
    this.orderList = orderList;
  }

  public SelectItemList getSelectList() { return selectList; }
  public boolean hasDistinct() { return isDistinct; }
  public StatementListNode getWithList() { return withList; }
  public FromList getFromList() { return fromList; }
  public ParseTreeNode getWhere() { return whereExpr; }
  public ParseTreeNode getSplitBy() { return splitByExpr; }
  
  public boolean hasLimit() { return ! (limitExpr == null && offsetExpr == null); }

  public int getLimitValue() 
  { 
    return limitVal; 
  }
  
  public int getOffsetValue() 
  { 
    if (offsetExpr == null) return 0;
    return offsetVal; 
  }
  
  public boolean isGrouped() { return groupByList != null && groupByList.size() > 0; }
  public List getGroupByList() { return groupByList; }

  public boolean hasOrderBy() { return orderList != null && orderList.size() > 0; }
  public List getOrderList() { return orderList; }

  public boolean hasFromList() 
  {
    return fromList != null && fromList.size() > 0; 
  }
  
  public void bind(Scope scope) 
  {
    // most clause of SelectNodes are bound when they are eval'ed
    
    // LIMIT and OFFSET expressions are bound at bind time
    if (limitExpr != null) limitExpr.bind(scope);
    if (offsetExpr != null) offsetExpr.bind(scope);
    
    return;
  }
  
  /**
   * @return a Table
   */
  public Object eval(Scope scope) 
  {
    limitVal = evalInt(limitExpr, scope);
    offsetVal = evalInt(offsetExpr, scope);
    if (limitVal < -1) throw new ExecutionException("Invalid Limit value: " + limitVal);
    if (offsetVal < -1) throw new ExecutionException("Invalid Offset value: " + offsetVal);
    
    SelectEvaluator queryEval = new SelectEvaluator(this);
    return queryEval.eval(scope);
  }
  
  private int evalInt(ParseTreeNode expr, Scope scope)
  {
    if (expr == null) return -1;
    Object val = expr.eval(scope);
    return ValFunction.toInt(val).intValue();
  }
  
  public Class getType(Scope scope) 
  {
    return Table.class;
  }
  
  public String monitorTag()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT FROM ");
    List froms = fromList.getItems();
    boolean isJoin = false;
    for (Object o : froms) {
      FromItem from = (FromItem) o;
      if (isJoin)
        sb.append(" JOIN ");
      sb.append(from.getTableNameOrAlias());
      isJoin = true;
    }
    return sb.toString();
  }
}