package jeql.syntax;

import java.util.Iterator;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.error.ImplementationException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.CompilationException;
import jeql.engine.ConfigurationException;
import jeql.engine.Scope;
import jeql.engine.ScopeUtil;
import jeql.engine.TableConstants;

/**
 * A table-valued expression, optionally with an alias
 * and renamed columns.
 * 
 * @author Martin Davis
 *
 */
public class TableExpressionNode 
  extends ParseTreeNode
{
  private ParseTreeNode expr = null;
  private String alias = null;
  private List colNames = null;
  private int colListLine = 0;
  
  public TableExpressionNode(ParseTreeNode expr, String alias, List colNames, int colListLine) {
    this.expr = expr;
    this.alias = alias;
    this.colNames = colNames;
    this.colListLine = colListLine;
    setLoc(expr);
  }

  public String getAlias() { return alias; }
  
  public boolean hasAlias() { return alias != null; }
  
  public List getAliasColumnNames() { return colNames; }
  
  public ParseTreeNode getExpression() { return expr; }
  
  public String getTableName()
  {
    if (expr instanceof TableRefNode) {
      return ((TableRefNode) expr).getTableName();
    }
    return null;
  }
  
  public boolean isSubquery()
  {
    return (expr instanceof SelectNode);
  }
  
  public void bind(Scope scope) 
  {
    // check that subqueries have an alias
    if (isSubquery()) {
      if (! hasAlias()) {
        throw new CompilationException(this, "Subquery must have alias specified");
      }
    }
    
    expr.bind(scope);
  }
  
  public Object eval(Scope scope) 
  {
    // TableExpressions are never evaled
    throw new UnsupportedOperationException();
  }
  
  public Class getType(Scope scope) 
  {
    // TableExpressions do not have a type
    throw new UnsupportedOperationException();
  }
  
  public Table getTable(Scope scope)
  {
    if (expr instanceof TableRefNode) {
      return getTableRef(scope);
    }
    else if (expr instanceof TableValueNode) {
      return getTableValue(scope);
    }
    else if (expr instanceof FunctionNode) {
      return getFunctionValue(scope);
    }
    else if (expr instanceof SelectNode) {
      return getSelectValue(scope);
    }
    throw new ImplementationException("Unexpected TableExpression node type: " + expr);
//    return null;
  }
  
  private Table getTableRef(Scope scope)
  {
    Table srcTbl = ScopeUtil.resolveTableNonNull(scope, ((TableRefNode) expr).getTableName(), this);
    RowList rows = (RowList) srcTbl.getRows();
    Table tbl = new Table(rows);
    assignAlias(tbl);
    return tbl;    
  }
  
  private Table getTableValue(Scope scope)
  {
    Table tbl = (Table) ((TableValueNode) expr).eval(scope);
    assignAlias(tbl);
    return tbl;    
  }
  
  private Table getFunctionValue(Scope scope)
  {  
    Object val = expr.eval(scope);
    Table tbl = null;
    if (isTableValued(val)) {
      tbl = convertCollectionToTable(val);
    }
    else { 
      tbl = convertScalarToTable(val);
    }
    assignAlias(tbl);
    return tbl;    
  }
  
  private Table getSelectValue(Scope scope)
  {
    Table tbl = (Table) expr.eval(scope);
    // since this table was created by the select, it can be updated with new names
    assignAlias(tbl);
    return tbl;
  }
  
  private static Table convertScalarToTable(Object val)
  {
    RowSchema schema = new RowSchema(TableConstants.FUNCTION_COL_NAME, val.getClass());
    ArrayRowList rs = new ArrayRowList(schema);
    BasicRow row = new BasicRow(1);
    row.setValue(0, val);
    rs.add(row);
    Table tbl = new Table(rs);
    return tbl;
  }
  
  private static boolean isTableValued(Object o)
  {
    if (o instanceof List) return true;
    if (o instanceof Table) return true;
    return false;
  }
  
  private Table convertCollectionToTable(Object o)
  {
    if (o instanceof List) {
      List l = (List) o;
      Class type = l.get(0).getClass();
      
      RowSchema schema = new RowSchema(TableConstants.FUNCTION_COL_NAME, type);
      ArrayRowList rs = new ArrayRowList(schema);
      for (Iterator i = l.iterator(); i.hasNext(); ) {
        Object val = i.next();
        BasicRow row = new BasicRow(1);
        row.setValue(0, val);
        rs.add(row);
      }
      Table tbl = new Table(rs);
      return tbl;
    }
    else if (o instanceof Table) {
      return (Table) o;
    }
    throw new ConfigurationException("Can't convert to table: " + o.getClass());
  }
  
  private void assignAlias(Table tbl)
  {
    // use alias as table name, if present
    if (alias != null)
      tbl.setName(alias);
    
    /**
     * Set column names if they are specified.
     * Otherwise, table keeps column names from underlying rowlist.
     */
    if (colNames != null) {
      if (colNames.size() != tbl.size())
        throw new CompilationException(colListLine, "Alias column name list length does not match table width");
      String[] colNameArray = (String[]) colNames.toArray(new String[0]);
      tbl.changeColumnNames(colNameArray);
    }
  }
}
