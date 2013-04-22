package jeql.syntax;

import java.util.List;

/**
 * Node which represents a table name and optional column list.
 * (May include types in the future)
 * 
 * @author Martin Davis
 *
 */
public class TableSchemaNode 
  
{
  private String name = null;
  private List colNames = null;
  private int colListLine = 0;
  
  public TableSchemaNode(String name, List colNames, int colListLine) {
    this.name = name;
    this.colNames = colNames;
    this.colListLine = colListLine;
  }

  public String getName() { return name; }
  
  public List getColumnNames() { return colNames; }

}
