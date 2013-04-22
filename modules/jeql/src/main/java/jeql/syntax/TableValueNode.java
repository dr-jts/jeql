package jeql.syntax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.error.ExecutionException;
import jeql.api.error.JeqlException;
import jeql.api.row.ArrayRowList;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.util.TableUtil;
import jeql.util.TypeUtil;

public class TableValueNode 
  extends ParseTreeNode
{
  // a List<List<ParseTreeNode>>
  private List rows = new ArrayList();
  private TableSchemaNode schemaNode = null;
  
  public TableValueNode() {
  }
  
  public void setSchema(TableSchemaNode schemaNode)
  {
    this.schemaNode = schemaNode;
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
    RowSchema schema = extractSchema(scope);
    ArrayRowList rs = new ArrayRowList(schema);
    Row firstRow = null;
    for (Iterator i = rows.iterator(); i.hasNext(); ) {
      List rowValues = (List) i.next();
      Row row = evalRow(rowValues, schema, scope);
      rs.add(row);
      if (firstRow == null) 
        firstRow = row;
      else {
        // check consistency of subsequent rows
        if (row.size() != firstRow.size()) {
          // get row parse node (if possible) to determine src location
          ParseTreeNode rowFirstNode = null;
          if (rowValues.size() >= 1)
            rowFirstNode = (ParseTreeNode) rowValues.get(0);
          
          throw new ExecutionException(rowFirstNode, "Table rows must have same number of columns");
        }
      }
    }
    Table tbl = new Table(rs);
    if (schemaNode != null)
      tbl.setName(schemaNode.getName());
    
    return tbl;
  }
  
  private Row evalRow(List rowValues, RowSchema schema, Scope scope)
  {
    BasicRow row = new BasicRow(rowValues.size());
    int i = 0;
    for (Iterator iRow = rowValues.iterator(); iRow.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) iRow.next();
      Object val = node.eval(scope);
      
      // check that value is of correct type for schema column
      Class reqClass = schema.getType(i);
      try {
        TypeUtil.checkCorrectType(val, reqClass);
      }
      catch (JeqlException ex) {
        ex.setLocation(node);
        throw ex;
      }
      
      row.setValue(i++, val);
    }
    return row;
  }
  
  private RowSchema extractSchema(Scope scope)
  {
    if (rows.size() == 0)
      return TableUtil.emptySchema();
    return extractRowSchema((List) rows.get(0), scope);
  }
 
  private RowSchema extractRowSchema(List rowValues, Scope scope)
  {
    if (schemaNode != null 
        && schemaNode.getColumnNames().size() != rowValues.size())
    {
      throw new ExecutionException(this, "Schema and row data must have same number of columns");
    }
    
    RowSchema schema = new RowSchema(rowValues.size());
    int i = 0;
    for (Iterator iRow = rowValues.iterator(); iRow.hasNext(); ) {
      ParseTreeNode node = (ParseTreeNode) iRow.next();
      Class type = node.getType(scope);
      schema.setColumnDef(i, getColumnName(i), type);
      i++;
    }
    return schema;
  }
  
  private String getColumnName(int i)
  {
    if (schemaNode != null)
      return (String) schemaNode.getColumnNames().get(i);
    return RowSchema.getDefaultColumnName(i);
  }
}
