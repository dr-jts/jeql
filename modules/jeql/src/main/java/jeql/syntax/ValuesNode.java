package jeql.syntax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.error.JeqlException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.engine.Scope;

public class ValuesNode 
  extends ParseTreeNode
{
  List rows = new ArrayList();
  
  public ValuesNode() {
  }
  
  public void add(List row)
  {
    rows.add(row);
  }
  
  public Class getType(Scope scope)
  {
    throw new UnsupportedOperationException();
  }
  
  public void bind(Scope scope)
  {
    for (Iterator i = rows.iterator(); i.hasNext(); ) {
      List rowValues = (List) i.next();
      for (Iterator iRow = rowValues.iterator(); iRow.hasNext(); ) {
        ParseTreeNode node = (ParseTreeNode) iRow.next();
        node.bind(scope);
      }
    }
  }

  public Object eval(Scope scope)
  {
    RowSchema schema = extractRowSchema((List) rows.get(0), scope);
    ArrayRowList rs = new ArrayRowList(schema);
    Row firstRow = null;
    for (Iterator i = rows.iterator(); i.hasNext(); ) {
      List rowValues = (List) i.next();
      Row row = evalRow(rowValues, scope);
      rs.add(row);
      if (firstRow == null) 
        firstRow = row;
      else {
        // check consistency of subsequent rows
        if (row.size() != firstRow.size())
          throw new JeqlException("Values rows must have same size");
        // check schema here too...
      }
    }
    return rs;
  }
  
  public Row evalRow(List rowValues, Scope scope)
  {
    BasicRow row = new BasicRow(rowValues.size());
    int i = 0;
    for (Iterator iRow = rowValues.iterator(); iRow.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) iRow.next();
      Object val = node.eval(scope);
      row.setValue(i++, val);
    }
    return row;
  }
  
  public static RowSchema extractRowSchema(List rowValues, Scope scope)
  {
    RowSchema schema = new RowSchema(rowValues.size());
    int i = 0;
    for (Iterator iRow = rowValues.iterator(); iRow.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) iRow.next();
      Class type = node.getType(scope);
      schema.setColumnDef(i, RowSchema.getDefaultColumnName(i), type);
      i++;
    }
    return schema;
  }
}
