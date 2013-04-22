package jeql.command.io;

import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.util.SystemUtil;

public class HtmlWriterCommand extends TableFileWriterCmd {

  private boolean isFragment = false;
  int border = 1;
  int cellSpacing = 2;
  int cellPadding = 2;
  String width = null;
  
  public HtmlWriterCommand() {
  }

  public void setFragment(boolean isFragment)
  {
    this.isFragment = isFragment;
  }
  
  public void setBorder(int border)
  {
    this.border = border;
  }
  
  public void setCellSpacing(int cellSpacing)
  {
    this.cellSpacing = cellSpacing;
  }
  
  public void setCellPadding(int cellPadding)
  {
    this.cellPadding = cellPadding;
  }
  
  public void setWidth(String width)
  {
    this.width = width;
  }
  
  public void execute(Scope scope) throws Exception {
    writeObj(tbl);
  }

  private void writeObj(Object obj)
    throws Exception 
  {
    if (obj instanceof Table)
      write((Table) obj);
    else {
      writer = getWriter();
      writer.write(obj.toString());
      writer.write(SystemUtil.LINE_TERM);
      writer.close();
    }
  }
  
  protected void write(Table tbl) throws Exception {
    HtmlTableTextWriter ttwriter = new HtmlTableTextWriter();
    ttwriter.write(getWriter(), (Table) tbl);
  }

  class HtmlTableTextWriter extends TableTextWriter
  {
    public HtmlTableTextWriter()
    {
      if (isFragment) {
      tableStart = "<table>";
      tableEnd  = "</table>";      
      }
      else {
        tableStart = "<html>\n<body>\n<table " + tableAttr() + ">";
        tableEnd  = "</table>\n</body>\n</html>\n";      
      }
      rowStart  = "<tr>\n";
      rowEnd    = "</tr>\n";
      colStart  = "  <td>";
      colEnd    = "</td>\n";
      colSep = null;
    }
    
    private String tableAttr()
    {
      StringBuilder sb = new StringBuilder();
      if (border > 0) sb.append("border='" + border + "' ");
      sb.append("cellSpacing='" + cellSpacing + "' ");
      sb.append("cellPadding='" + cellPadding + "' ");
      if (width != null) sb.append("width='" + width + "' ");
      return sb.toString();
    }
  }
}
