package jeql.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jeql.api.row.RowSchema;
import jeql.engine.CompilationException;
import jeql.engine.Scope;

/**
 * A list of {@link SelectItemNode}s.
 * Maybe be constructed during parse, 
 * or during SELECT transformation.
 * 
 * @author Martin Davis
 * @version 1.0
 */
public class SelectItemList
{
  List items = new ArrayList();
  List finalItems = new ArrayList();
  
  public SelectItemList() {
  }

  public void add(SelectItemNode item)
  {
    items.add(item);
  }

  public List getItems() { return items; }

  public List getFinalItems() { return finalItems; }

  public void bind(Scope scope)
  {
    for (Iterator i = finalItems.iterator(); i.hasNext(); ) {
      SelectItemNode item = (SelectItemNode) i.next();
      item.bind(scope);
    }
  }

  public void expand(Scope scope) {
    finalItems = new ArrayList();
    Set itemNames = new HashSet();
    int colIndex = 0;
    for (Iterator i = items.iterator(); i.hasNext();) {
      SelectItemNode item = (SelectItemNode) i.next();
      if (item.getItem() instanceof TableRefNode) {
        TableRefNode tblStar = (TableRefNode) item.getItem();
        List starCols = tblStar.prepareStarSelectItems(scope);
        //List starCols = tblStar.prepareStarSelectItems(scope, itemNames);
        addColNames(starCols, colIndex, itemNames);
        finalItems.addAll(starCols);
        colIndex += starCols.size();
      } else {
        finalItems.add(item);
        itemNames.add(item.getName(colIndex));
      }
      colIndex++;
    }
  }
  
  private static void addColNames(List selItems, int startIndex, Set names)
  {
    for (Iterator i = selItems.iterator(); i.hasNext(); ) {
      SelectItemNode item = (SelectItemNode) i.next();
      names.add(item.getName(startIndex++));
    }
  }
  
  public String[] getResultColumnNames()
  {
    String[] colName = new String[finalItems.size()];
    for (int i = 0; i < finalItems.size(); i++) {
      SelectItemNode item = (SelectItemNode) finalItems.get(i);
      String name = item.getName(i + 1);
      colName[i] = name;
    }
    return colName;
  }

  public void checkUniqueNames()
  {
    Set namesSet = new TreeSet();
    for (int i = 0; i < finalItems.size(); i++) {
      SelectItemNode item = (SelectItemNode) finalItems.get(i);
      String name = item.getName(i + 1);
      if (namesSet.contains(name))
        throw new CompilationException(item, "Duplicate column names in result table: " + name);
      namesSet.add(name);
    }
  }

  public RowSchema computeRowSchema(Scope scope)
  {
    RowSchema rs = new RowSchema(finalItems.size());
    for (int i = 0; i < finalItems.size(); i++) {
      SelectItemNode item = (SelectItemNode) finalItems.get(i);
      Class colType = item.getType(scope);
      if (colType == null)
        throw new CompilationException(item, "Unable to determine type of select list item (#" + (i + 1)
            + " - " + item 
            + " )");
      rs.setColumnDef(i, item.getName(i), colType);
    }
    return rs;
  }

}