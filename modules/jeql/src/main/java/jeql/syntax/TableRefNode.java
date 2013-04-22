package jeql.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jeql.api.error.ExecutionException;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.engine.query.BaseQueryScope;


/**
 * A reference to a table.
 * An alias can be supplied.  
 * If the ref is a from ref, the alias is a table alias. 
 * If the ref is in a select list, the alias is a column alias.
 * 
 * If the reference is a "*" in a select list, there may be no table name.
 *  
 * @author Martin Davis
 *
 */
public class TableRefNode 
  extends ParseTreeNode
{
  public static boolean isStar(ParseTreeNode node)
  {
    if (node instanceof TableRefNode) {
      return ((TableRefNode) node).isStar();
    }
    return false;
  }
  
  private String tableName = null;
  private String alias = null;
  private List exceptColList = null;
  
  public TableRefNode() {
  }

  public TableRefNode(String table) {
    this.tableName = table;
  }

  public TableRefNode(String table, String alias) {
    this.tableName = table;
    this.alias = alias;
  }

  public void setExceptCols(List exceptColList)
  {
    this.exceptColList = exceptColList;
  }
  public String getTableName() { return tableName; }
  public String getAlias() { return alias; }
  
  public boolean isStar() {
    return tableName == null;
  }

  /**
   * Used only as a select tbl ref.
   * 
   * @param scope
   * @return
   */
  public List prepareStarSelectItems(Scope scope) {
    Table tbl = resolveTable(scope);
    if (tbl == null) {
      throw new ExecutionException(this, "table is null in '" + tableName + ".*'");
    }
    
    // TODO: check that except columns are legal column names
    
    List items = new ArrayList();
    for (int i = 0; i < tbl.size(); i++) {
      String colName = tbl.getColumnName(i);
      
      // skip any excepted columns
      if (exceptColList != null)
        if (isExceptCol(colName))
          continue;
      
      TableColumnNode col = new TableColumnNode(tbl.getName(), tbl.getColumnName(i));
      SelectItemNode item = new SelectItemNode(col);
      // uniquifying disabled for now...
      //SelectItemNode item = new SelectItemNode(col, generateUniqueName(col.getColName(), currentColNames));
      item.bind(scope);
      items.add(item);
    }
    // TODO: check that table column list has at least one item
    return items;
  }

  private static final int MAX_UNIQUE_SUFFIX_NUM = 9999;
  
  private static String generateUniqueName(String baseName, Set excludedNames)
  {
    if (! excludedNames.contains(baseName))
      return baseName;
    int i = 2;
    while (i < MAX_UNIQUE_SUFFIX_NUM) {
      String candidate = baseName + "_" + i;
      if (! excludedNames.contains(candidate))
        return candidate;
    }
    // oh well - risk failure
    return baseName + MAX_UNIQUE_SUFFIX_NUM;
  }
  
  private boolean isExceptCol(String col)
  {
    for (int i = 0; i < exceptColList.size(); i++) {
      if (col.equals(exceptColList.get(i)))
        return true;
    }
    return false;
  }
  
  private Table resolveTable(Scope scope) 
  {
    if (! isStar()) {
      return scope.resolveTable(tableName);
    }
    
    // require single table in scope
    if (! ((BaseQueryScope) scope).hasDefaultTable())
      throw new ExecutionException(this, "Unqualified '*' ambiguous or undefined");
    return ((BaseQueryScope) scope).getDefaultTable();
  }

  public void bind(Scope scope) 
  {
  }
  public Object eval(Scope scope) 
  {
    // TableRefs are never evaled
    throw new UnsupportedOperationException();
  }
  public Class getType(Scope scope) 
  {
    // TableRefs do not have a type
    throw new UnsupportedOperationException();
  }
}
