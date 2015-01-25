package jeql.engine.query;

import java.util.List;

import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.syntax.ParseTreeNode;
import jeql.syntax.SelectItemList;
import jeql.syntax.SelectItemNode;
import jeql.syntax.StatementListNode;



/**
 * A {@link RowList} of the computed selected items in a select expression
 * 
 * @author Martin Davis
 *
 */
public class SelectedItemsRowList 
  implements RowList
{
  private RowSchema schema;
  private RowList baseRowStr;
  private List selectedItems;
  private StatementListNode aliasList;
  private QueryScope scope;
  
  public SelectedItemsRowList(RowList baseRowStr, 
      SelectItemList selectedItems, 
      StatementListNode aliasList,
      QueryScope scope) 
  {
    this.baseRowStr = baseRowStr;
    this.selectedItems = selectedItems.getFinalItems();
    this.aliasList = aliasList;
    this.scope = scope;
    schema = selectedItems.computeRowSchema(scope);
  }

  public RowSchema getSchema() { return schema; }

  public RowIterator iterator()
  {
    return new SelectedItemsRowIterator(schema, baseRowStr, selectedItems, aliasList, scope);
  }

  private static class SelectedItemsRowIterator
  implements RowIterator
{
    private RowSchema schema;
    private RowList rowList;
    private List selectedItems;
    private StatementListNode aliasList;
    private QueryScope scope;
    private RowIterator it;
    private int rowNum = 0;

  public SelectedItemsRowIterator(RowSchema schema, RowList rowList, 
      List selectedItems,
      StatementListNode aliasList,
      QueryScope scope)
  {
    this.schema = schema;
    this.rowList = rowList;
    this.selectedItems = selectedItems;
    this.aliasList = aliasList;
    this.scope = scope;
    rowNum = 0;
  }
  
  public RowSchema getSchema() { return schema; }

  public Row next() 
  {
    if (it == null)
      it = rowList.iterator();
    
    Row baseRow = it.next();
    if (baseRow == null) return null;
    
    rowNum++;
    scope.setRowNum(rowNum);
   
    // post the current base row in the scope so the select expressions can access it
    scope.setRow(baseRow);
    
    // evaluate alias assignments for this row
    SelectEvaluator.evalAliases(aliasList, scope);
    
    Row outRow = createRow();
    return outRow;
  }
  
  private Row createRow()
  {
    // build a new row containing the values of the selected items (expressions)
    BasicRow row = new BasicRow(selectedItems.size());
    for (int i = 0; i < selectedItems.size(); i++) {
      SelectItemNode item = (SelectItemNode) selectedItems.get(i);
      ParseTreeNode expr = item.getItem();
      Object value = expr.eval(scope);
      row.setValue(i, value);
    }
    return row;
  }
  
}
}
