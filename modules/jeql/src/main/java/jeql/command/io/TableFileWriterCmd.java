package jeql.command.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jeql.api.table.Table;
import jeql.engine.EngineContext;
import jeql.engine.Scope;

public abstract class TableFileWriterCmd 
implements TableWriterCmd
{
  protected String filename = null;
  protected Table tbl = null;
  protected PrintWriter writer = null;

  public TableFileWriterCmd() {
  }

  public void setFile(String filename)
  {
    this.filename = filename;
  }
  
  public void setDefault(Table tbl)
  {
    this.tbl = tbl;
  }

  public void execute(Scope scope)
    throws Exception
  {
    write(tbl);
  }
   
  protected PrintWriter getWriter() throws IOException {
    if (filename != null) {
      writer = new PrintWriter(new FileWriter(new File(filename)));
    } else {
      writer = getSystemOutWriter();
    }
    return writer;
  }

  private static PrintWriter getSystemOutWriter()
  {
    return EngineContext.OUTPUT_WRITER;
  }
  
  protected abstract void write(Table tbl) throws Exception;
}