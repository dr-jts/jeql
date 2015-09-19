package jeql.command.io.text;

import jeql.api.table.Table;
import jeql.command.io.TableTextFileWriterCmd;
import jeql.command.io.TableTextWriter;
import jeql.engine.Scope;
import jeql.util.SystemUtil;

public class TextWriterCommand extends TableTextFileWriterCmd {
  private Object before;
  private Object after;

  public TextWriterCommand() {
  }

  public void setBefore(Object obj)
  {
    before = obj;
  }
  
  public void setAfter(Object obj)
  {
    after = obj;
  }
  
  public void execute(Scope scope) throws Exception {
    writer = getWriter();
    if (before != null) write(before);
    write(tbl);
    if (after != null) write(after);
    writer.close();
  }

  private void write(Object obj)
    throws Exception 
  {
    if (obj instanceof Table)
      write((Table) obj);
    else {
      writer.write(obj.toString());
      writer.write(SystemUtil.LINE_TERM);
    }
  }
  
  protected void write(Table tbl) throws Exception {
    TableTextWriter printer = new TableTextWriter();
    if (colSep != null) printer.setColSep(colSep);
    if (rowSep != null) printer.setRowSep(rowSep);
    printer.write(writer, (Table) tbl);
  }

  /*
  protected void OLDwrite(Table tbl) throws Exception {
    
    RowIterator rs = tbl.getRowList().iterator();
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      writeRow(writer, row);
    }
  }

  private void writeRow(Writer writer, Row row) throws IOException {
    for (int i = 0; i < row.size(); i++) {
      if (i > 0 && colSep != null && colSep.length() > 0)
        writer.write(colSep);
      writer.write(row.getValue(i).toString());
    }
    writer.write(SystemUtil.LINE_SEP);
  }
*/
}
