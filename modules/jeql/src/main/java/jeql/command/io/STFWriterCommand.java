package jeql.command.io;

import java.io.IOException;
import java.io.Writer;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.engine.Scope;

// hack... not really a type of TableFileWriterCommand
public class STFWriterCommand 
  extends TableFileWriterCmd 
{
  
  public STFWriterCommand() {
  }

  public void execute(Scope scope) throws Exception {
    writer = getWriter();
    writeObj(tbl);
    writer.close();
  }

  private void writeObj(Object obj)
    throws Exception 
  {
    if (obj instanceof Table)
      write((Table) obj);
    else {
      writer.write(obj.toString());
      writer.write("\n");
    }
  }
  
  protected void write(Table tbl) throws Exception {
    
    RowIterator rs = tbl.getRows().iterator();
    RowSchema schema = rs.getSchema();
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      writeRow(writer, schema, row);
    }
  }

  private void writeRow(Writer writer, RowSchema schema, Row row) 
  throws IOException 
  {
    for (int i = 0; i < row.size(); i++) {
      writer.write("<");
      writer.write(schema.getName(i));
      writer.write("> ");
      writer.write(row.getValue(i).toString());
      writer.write("\n");
    }
    // record separator
    writer.write("\n");
  }

}
