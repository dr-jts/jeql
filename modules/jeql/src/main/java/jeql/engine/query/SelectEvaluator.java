package jeql.engine.query;

import java.util.Iterator;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.Scope;
import jeql.engine.query.group.GroupBinder;
import jeql.engine.query.group.GroupByEvaluator;
import jeql.engine.query.group.GroupScope;
import jeql.engine.query.join.IndexedJoinRowList;
import jeql.engine.query.join.NestedLoopJoinRowList;
import jeql.engine.query.join.SimpleNestedLoopJoinRowList;
import jeql.monitor.Monitor;
import jeql.syntax.AssignmentNode;
import jeql.syntax.FromItem;
import jeql.syntax.FromList;
import jeql.syntax.FunctionNode;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.SelectItemList;
import jeql.syntax.SelectNode;
import jeql.syntax.StatementListNode;


public class SelectEvaluator 
{
  
  public static Table eval(SelectNode select, Scope scope)
  {
    SelectEvaluator queryEval = new SelectEvaluator(select);
    return queryEval.eval(scope);
  }
  
  public static void evalAliases(StatementListNode stmtList, Scope scope)
  {
    if (stmtList == null)
      return;
    for (Iterator i = stmtList.getStatements().iterator(); i.hasNext(); ) {
      AssignmentNode node = (AssignmentNode) i.next();
      node.eval(scope);
    }
  }
  
  private SelectNode select;
  private BaseQueryScope baseScope = null;

  public SelectEvaluator(SelectNode select) {
    this.select = select;
  }

  public Table eval(Scope scope)
  {
    // each select expression gets its own scope
    baseScope = new BaseQueryScope(scope);
    baseScope.prepareFromItems(select.getFromList());
    
    SelectItemList selectList = select.getSelectList();
    selectList.expand(baseScope);
    selectList.checkUniqueNames();
    bind(select, baseScope);
    
    RowList baseRS = evalFromWhereSplit();

    RowList selectRS = null;
    boolean isGrouped = isGrouped(select, baseScope);    
    if (isGrouped) {
      selectRS = evalGroupBy(select, baseScope, baseRS);
    }
    else {
      selectRS = evalSelect(select, baseScope, baseRS);
    }
    RowList finalRS = evalDistinctOrderLimit(select, selectRS);
    return createTable(finalRS);
  }

  private Table createTable(RowList rs)
  {
    //rs = (RowList) Monitor.wrap(select.getLine(), select.monitorTag(), rs);
    
    // column names are already present in input RL, so just reuse them
    Table tbl = new Table(rs);
    return tbl;
  }

  
  private boolean isGrouped(SelectNode select, BaseQueryScope scope)
  {
    return select.isGrouped() || scope.hasAggregateFunctions();
  }

  private void bind(SelectNode select, QueryScope scope)
  {
    ParseTreeNode whereExpr = select.getWhere();
    if (whereExpr != null)
      whereExpr.bind(baseScope);

    bindSplitBy(select.getSplitBy(), baseScope);
    
    // bind LET block stmts
    bindAliasBlock(select.getAliasList(), baseScope);
    
    // bind select list
    select.getSelectList().bind(baseScope); 
  }
  
  private void bindSplitBy(ParseTreeNode splitByExpr, QueryScope scope)
  {
    if (splitByExpr != null) {
      splitByExpr.bind(baseScope);
      // add SPLIT BY pseudo-column variables to scope
      scope.setVariableType(QueryScope.VAR_SPLITINDEX, Integer.class);
      scope.setVariable(QueryScope.VAR_SPLITINDEX, null);
      scope.setVariableType(QueryScope.VAR_SPLITVALUE, splitByExpr.getType(baseScope));
      scope.setVariable(QueryScope.VAR_SPLITVALUE, null);
    }
  }
  
  /**
   * Enter alias vars into scope, with defined type but undefined value
   * 
   * @param scope
   */
  private void bindAliasBlock(StatementListNode stmtList, QueryScope scope)
  {
    if (stmtList == null) 
      return;
    for (Iterator i = stmtList.getStatements().iterator(); i.hasNext(); ) {
      AssignmentNode node = (AssignmentNode) i.next();
      node.bind(scope);
      String name = node.getVariableName();
      Class nodeType = node.getType(scope);
      /**
       * Set the type of the variable, which is now known.
       * The value is not yet known and so is set to null.
       */
      scope.setVariableType(name, nodeType);
      scope.setVariable(name, null);
    }
  }
  
  private RowList evalSelect(SelectNode select, QueryScope scope, RowList baseRS) 
  {
    RowList selectRS = new SelectedItemsRowList(baseRS, select.getSelectList(), select.getAliasList(), scope);
    return selectRS;
  }

  //*
  private RowList evalGroupBy(SelectNode select, BaseQueryScope scope, RowList baseRS) 
  {
    GroupBinder groupBinder = new GroupBinder(select, scope);
    List aggFunArgList = groupBinder.getAggFunArgs();
    
    GroupScope groupScope = groupBinder.getScope(scope);
    
    SelectItemList selectList = select.getSelectList();
    // now re-bind in group scope (which will not bind below agg functions)
    selectList.bind(groupScope);  
    
    GroupByEvaluator ge = new GroupByEvaluator(
        scope, aggFunArgList, select.getAliasList(),
        groupScope, selectList);
    
    RowList selectRS = ge.eval(baseRS);
    return selectRS;
  }
  
  /**
   * Computes final rowList with DISTINCT/ORDER BY/LIMIT+OFFSET
   * evaluated if specified.
   * 
   * @param selectList
   * @param selectRS
   * @return
   */
  private RowList evalDistinctOrderLimit(SelectNode select, RowList selectRS)
  {
    RowList finalRS = selectRS;
    
    // evaluate DISTINCT
    if (select.hasDistinct()) {
      finalRS = (new DistinctEvaluator(finalRS)).eval();
    }
    
    // evaluate ORDER BY
    if (select.hasOrderBy()) {
      finalRS = (new OrderByEvaluator(select, finalRS)).eval();
    }
    
    // evaluate LIMIT and OFFSET
    if (select.hasLimit()) {
      // eval using a stream
      finalRS = new LimitRowList(finalRS, select.getLimitValue(), select.getOffsetValue());
//      finalRS = (new LimitEvaluator(select, finalRS)).eval();
    }
    
    return finalRS;
  }
  
  private RowList evalFromWhereSplit()
  {
    RowList baseRS = buildFrom();
    
    RowList splitRS = buildSplitRowStream(baseRS, select.getSplitBy());
    RowList whereRS = buildFilteredRowStream(splitRS, select.getWhere());
    return whereRS;
  }
  
  private RowList buildFrom()
  {
    RowList baseRS = null;
    if (! select.hasFromList()) {
      throw new CompilationException(select, "Empty/Missing FROM clause not supported");
    }
    if (select.getFromList().size() == 1) {
      baseRS = buildSingleTableRowStream();
    }
    else {
      baseRS = buildJoin();
    }
    /*
    else 
      throw new CompilationException(select, "JOINing more than 2 tables not currently supported");
    */
    return baseRS;
  }
  
  private RowList buildFilteredRowStream(RowList baseRS,
      ParseTreeNode filterExpr) 
  {
    // short-circuit if there is no WHERE clause
    if (filterExpr == null)
      return baseRS;

    RowList rowStr = new FilterRowList(baseRS, filterExpr, baseScope);
    return rowStr;
  }
  
  private RowList buildSplitRowStream(RowList baseRS,
      ParseTreeNode splitExpr) 
  {
    // short-circuit if there is no SPLIT BY clause
    if (splitExpr == null)
      return baseRS;

    if (! (splitExpr instanceof FunctionNode)
        || ! ((FunctionNode) splitExpr).isSplitFunction()) {
      throw new CompilationException(select, "Invalid Splitting function: " + splitExpr);
    }
    RowList rowStr = new SplitRowList(baseRS, splitExpr, baseScope);
    return rowStr;
  }
  
  /*
  private RowList buildNoTableRowStream()
  {
    Object lastObj = baseScope.getVariable(Scope.LAST_TABLE);
    if (lastObj instanceof Table) {
      Table tbl = (Table) lastObj;
      return tbl.getRows();
    }
    throw new ExecutionException(select, "No default table computed");
  }
  */
  private RowList buildSingleTableRowStream()
  {
    return baseScope.getFromTable(0).getRows();
  }
  
  /*
  private RowList buildSimpleNestedLoopJoin()
  {
    
    RowList rs0 = select.getFromList().getItem(0).eval(baseScope);
    
    FromItem from1 = select.getFromList().getItem(1);
    RowList rs1 = from1.eval(baseScope);
    
    SimpleNestedLoopJoinRowList joinRS = new SimpleNestedLoopJoinRowList();
    joinRS.addRowStream(rs0);
    joinRS.addRowStream(rs1);

    ParseTreeNode joinCondExpr = from1.getJoinConditionExpr();
    return buildFilteredRowStream(joinRS, joinCondExpr);
  }
  
  private RowList buildNestedLoopSingleJoin()
  {
    
    RowList rs0 = select.getFromList().getItem(0).eval(baseScope);
    
    FromItem from1 = select.getFromList().getItem(1);
    RowList rs1 = from1.eval(baseScope);
    ParseTreeNode joinCondExpr = from1.getJoinConditionExpr();
    
    NestedLoopJoinRowList joinRS = new NestedLoopJoinRowList(rs0, rs1, 
        joinCondExpr, from1.getJoinType(), baseScope);
    return joinRS;
  }
  */
  
  private RowList buildJoin()
  {  
    FromList fromList = select.getFromList();
    RowList rs0 = fromList.getItem(0).eval(baseScope);
    
    RowList joinedRS = rs0;
    for (int i = 1; i < fromList.size(); i++) {
      FromItem from = fromList.getItem(i);
      joinedRS = addJoin(joinedRS, from);
    }
    return joinedRS;
  }

  private RowList addJoin(RowList innerRS, FromItem from) {
    RowList joinRS = from.eval(baseScope);
    ParseTreeNode joinCondExpr = from.getJoinConditionExpr();
    
    RowList joinedRS = null;
    // see if join can be indexed
    int indexSide = IndexedJoinRowList.indexableOperandSide(joinCondExpr, from, joinRS.getSchema());
    if (indexSide >= 0) {
      joinedRS = new IndexedJoinRowList(innerRS, joinRS, indexSide,
          joinCondExpr, from.getJoinType(), baseScope);      
    }
    else {
      // default is to do a nested loop join
      joinedRS = new NestedLoopJoinRowList(innerRS, joinRS, 
        joinCondExpr, from.getJoinType(), baseScope);
    }
    return joinedRS;
  }

}
