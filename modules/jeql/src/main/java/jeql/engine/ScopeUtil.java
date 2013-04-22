package jeql.engine;

import jeql.api.error.ExecutionException;
import jeql.api.table.Table;
import jeql.syntax.ParseTreeNode;

public class ScopeUtil
{

  /**
   * Resolves a table, checking that it is non-null.
   * 
   * @param scope
   * @param tblName
   * @param node
   * @return
   */
  public static Table resolveTableNonNull(Scope scope, String tblName,
      ParseTreeNode node)
  {
    Table tbl;
    try {
      tbl = scope.resolveTable(tblName);
    }
    catch (ExecutionException e) {
      e.setLocation(node.getLine());
      throw e;
    }
    if (tbl == null) {
      throw new ExecutionException(node, "Table " + tblName + " is null");
    }
    return tbl;
  }

}
