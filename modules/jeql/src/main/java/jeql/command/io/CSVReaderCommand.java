package jeql.command.io;

import java.io.IOException;

import jeql.api.table.Table;
import jeql.engine.Scope;
import jeql.io.InputSource;

public class CSVReaderCommand 
 extends TextFileReaderCmd
{
  private int colNameStrategy = CSVRowList.USE_COL_NAMES;
  private int colCount = -1;
  private String colSepStr = null;
  
  public CSVReaderCommand() 
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public void setHasColNames(boolean readColNames)
  {
    colNameStrategy = readColNames ? CSVRowList.USE_COL_NAMES : CSVRowList.NO_COL_NAMES;  
  }
  
  public void setSkipColNames(boolean skipColNames)
  {
    colNameStrategy = skipColNames ? CSVRowList.SKIP_COL_NAMES : CSVRowList.NO_COL_NAMES;  
  }
  
  public void setUseColNames(boolean useColNames)
  {
    colNameStrategy = useColNames ? CSVRowList.USE_COL_NAMES : CSVRowList.NO_COL_NAMES;  
  }
  
  public void setColSep(String sepStr)
  {
    colSepStr = sepStr;  
  }
  
  /**
   * Reads at most this number of columns.
   * 
   * @param colCount
   */
  public void setColumnCount(int colCount)
  {
   this.colCount = colCount;  
  }
  
  protected Table read(Scope scope, InputSource src)
  throws IOException
  {
    CSVRowList rs = new CSVRowList(src.getSourceName(), colNameStrategy, colCount, colSepChar(colSepStr));
    tbl = new Table(rs);
    return tbl;
  }
  
  private static char colSepChar(String fieldSepStr)
  {
    if (fieldSepStr != null && fieldSepStr.length() >= 1) {
      return fieldSepStr.charAt(0);
    }
    return 0;
  }
}
