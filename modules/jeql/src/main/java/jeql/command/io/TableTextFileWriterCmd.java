package jeql.command.io;


public abstract class TableTextFileWriterCmd 
extends TableFileWriterCmd
{
  protected String colSep = " ";
  protected String rowSep = null;

  public TableTextFileWriterCmd() {
  }

  public void setColSep(String colSep)
  {
    this.colSep = colSep;
  }
  
  public void setRowSep(String rowSep)
  {
    this.rowSep = rowSep;
  }
  
}
