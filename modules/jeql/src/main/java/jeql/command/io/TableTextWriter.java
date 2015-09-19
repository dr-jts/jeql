package jeql.command.io;

import java.io.PrintWriter;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.util.ClassUtil;
import jeql.util.TypeUtil;

/**
 * Writes out a table with specified delimiters.
 * 
 * @author mdavis
 *
 */
public class TableTextWriter 
{
  public static void writeTbl(Table t, boolean showHeader, int limit) {
    TableTextWriter writer = new TableTextWriter();
    writer.setShowHeader(showHeader);
    writer.setLimit(limit);
    writer.write(t);
  }
  public static void writeTbl(Table t) {
    TableTextWriter writer = new TableTextWriter();
    writer.write(t);
  }

  protected PrintWriter writer = null;
  
  protected String tableStart = null;
  protected String tableEnd = null;
  protected String rowStart = null;
  protected String rowEnd = null;
  protected String rowSep = null;
  protected String colStart = null;
  protected String colEnd = null;
  protected String colSep = " ";
  protected boolean outputValueAsCode = false;
  
  protected boolean showHeader = false;
  protected int limit = -1;
  protected RowSchema schema;
  
  public TableTextWriter() {
  }

  public void setShowHeader(boolean showHeader)
  {
    this.showHeader = showHeader;
  }
  
  public void setCode(boolean isCode)
  {
    if (! isCode)
      return;
    
    rowStart = "( ";
    rowEnd = " )";
    rowSep = "";
    colSep = ", ";
    outputValueAsCode = true;
  }
  
  public void setColSep(String sep) {
    this.colSep = sep;
  }

  public void setRowSep(String sep) {
    this.rowSep = sep;
  }

  public void setLimit(int limit)
  {
    this.limit = limit;
  }
  
  public void write(Table t)
  {
    write(EngineContext.OUTPUT_WRITER, t);
  }
  
  public void write(PrintWriter printWriter, Table t) {
    this.writer = printWriter;
    RowIterator rs = t.getRows().iterator();
    schema = rs.getSchema();
    
    if (tableStart != null) writer.print(tableStart);
    
    if (showHeader) {
      writeHeader(t, schema);
    }
    
    int count = 0;
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      
      // enforce limit
      if (limit >= 0 && count >= limit)
        break;
      
      // output row separator and EOL
      if (count > 0) {
        if (rowSep != null)
          writer.print(rowSep);
        writer.println();
      }
      
      count++;
      writeRow(row);
      // flush row, so it is seen as soon as possible
      writer.flush();
    }
    if (tableEnd != null) writer.print(tableEnd);

    // output final EOL
    writer.println();
    writer.flush();
  }

  protected void writeHeader(Table tbl, RowSchema rs) {
    for (int i = 0; i < rs.size(); i++) {
      if (i > 0)
        writer.print(", ");
      writer.print(tbl.getColumnName(i) + ":" + ClassUtil.classname(rs.getType(i)));
    }
    writer.println(" ");
  }

  protected void writeRow(Row row) 
  {
    if (rowStart != null)
      writer.print(rowStart);
    
    for (int i = 0; i < row.size(); i++) {
      
      if (i > 0 && colSep != null)
        writer.print(colSep);
      
      writeCol(row, i);
    }
    if (rowEnd != null)
      writer.print(rowEnd);
  }
  
  protected void writeColStart(Row row, int i)
  {
    if (colStart != null)
      writer.print(colStart);
  }
  
  protected void writeColEnd(Row row, int i)
  {
    if (colEnd != null)
      writer.print(colEnd);
  }
  
  protected void writeCol(Row row, int i) {
    writeColStart(row, i);
    Object value = row.getValue(i);
    if (outputValueAsCode)
      writeValueAsCode(value);
    else
      writer.print(TypeUtil.toString(value));
    writeColEnd(row, i);
  }

  private void writeValueAsCode(Object value) {
    if (value instanceof String)
      writer.print("\"" + TypeUtil.toString(value) + "\"");
    else
      writer.print(TypeUtil.toString(value));
  }


}
