package jeql.command.io;

import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.Scope;
import jeql.util.TypeUtil;

public class PrintCommand 
 extends TableTextFileWriterCmd
{
  private Object input;
  private boolean showHeader = true;
  private int limit = -1;
  private boolean isCode = false;
  //private String sep = null;
  
  public PrintCommand() {
  }

  public void setDefault(Object input)
  {
   this.input = input;  
  }
  
  public void setHeader(boolean showHeader)
  {
   this.showHeader = showHeader;  
  }
  
  public void setLimit(int limit)
  {
   this.limit = limit;  
  }
  
  public void setCode(boolean isCode)
  {
   this.isCode = isCode;  
  }
  
  public void setSep(String sep) {
    this.colSep = sep;
  }

  public void execute(Scope scope) throws Exception
  {
    if (input instanceof Table) {
      write((Table) input);
    }
    else
      EngineContext.OUTPUT_WRITER.println(TypeUtil.toString(input));
  }

  protected void write(Table tbl) throws Exception {
      TableTextWriter writer = new TableTextWriter();
      writer.setShowHeader(showHeader);
      writer.setLimit(limit);
      if (colSep != null) writer.setColSep(colSep);
      if (rowSep != null) writer.setRowSep(rowSep);
      writer.setCode(isCode);
      writer.write((Table) input);
  }
}
