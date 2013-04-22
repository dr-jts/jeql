package jeql.monitor;

import java.util.ArrayList;
import java.util.List;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.std.function.StringFunction;

import com.vividsolutions.jts.geom.Geometry;

public class MonitorRowList 
implements RowList
{
  private int line;
  private String name = "";
  private String src = "";
  private RowList rowList;
  private long scanCount = 0;
  private long totalRowCount = 0;
  /**
   * Controls whether all rows are saved.
   * When running in Monitor mode this is disabled,
   * to avoid filling up memory.
   */
  private boolean keepRows = true;
  private List rows = new ArrayList();

  /*
  public MonitorRowList(String src, RowList rowList)
  {
    this(0, src, src, rowList);
  }
*/
  public MonitorRowList(int line, String name, String src, RowList rowList)
  {
    this(line, name, src, rowList, true);
  }

  public MonitorRowList(int line, String name, String src, RowList rowList, boolean keepRows)
  {
    this.line = line;
    this.name = name;
    this.src = src;
    this.rowList = rowList;
    this.keepRows = keepRows;
  }

  public int getLine() { return line; }
  public String getName() { return name; }
  public String getSource() { return src; }
  public List getRows() { return rows; }
  
  public long getTotalRowCount() { return totalRowCount; }
  
  public RowSchema getSchema() {
    return rowList.getSchema();
  }

  public RowIterator iterator() {
    scanCount++;
    return new MonitorRowIterator(this, rowList.iterator());
  }
  
  public class MonitorRowIterator
  implements RowIterator
  {
    private MonitorRowList monitorRowList;
    private RowIterator rowIt;
    private long rowCount = 0;
    private Row currRow = null;
    private Row lastValidRow = null;
    
    public MonitorRowIterator(MonitorRowList monitorRowList, RowIterator rowIt)
    {
      this.monitorRowList = monitorRowList;
      this.rowIt = rowIt;
      Monitor.add(this);
    }
    
    public RowSchema getSchema() {
      return rowIt.getSchema();
    }

    public Row next() {
      Monitor.checkStatus();
      currRow = rowIt.next();
      if (currRow != null) {
        lastValidRow = currRow;
        if (keepRows && rowCount >= rows.size()) 
          rows.add(currRow);
      }
      
      rowCount++;
      totalRowCount++;
      /*
      if (currRow == null)
        Monitor.pop(this);
      //Monitor.update(this);
       */
      return currRow;
    }
    
    public MonitorRowList getRowList() { return monitorRowList; }
    
    public long getScanCount()
    {
      return scanCount;
    }
    
    public long getRowCount()
    {
      return rowCount;
    }
    
    public long getTotalRowCount()
    {
      return totalRowCount;
    }
    
    public int getLine() { return line; }
    
    public String toString()
    {
      return 
      MonitorModel.lineDisplay(line)
      + "  " + scanRow() + "  "
      + src
      + "\n"
      + "                                " + rowDesc()
      ;
    }
    
    private String scanRow() {
      return "["
      + "scan " + scanCount + " - "
      + "row " + rowCount + "]";
    }
    
    private static final int MAX_DISPLAY_COLS = 5;
    
    public String rowDesc()
    {
      return rowDesc(lastValidRow);
    }
    public String rowDesc(Row row)
    {
      if (row == null) return "";
      
      boolean showMore = row.size() > MAX_DISPLAY_COLS;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < MAX_DISPLAY_COLS; i++) {
        // row might change while we're reading it!
        if (row == null) return "";
        if (row.size() <= i) break;
        Object val = row.getValue(i);
        if (i > 0)
          sb.append(", ");
        sb.append(rowIt.getSchema().getName(i) + " = " + 
            toStringLim(val));
      }
      if (showMore)
        sb.append(", ...");
      return sb.toString();
    }
  }
  
  private static String toStringLim(Object o)
  {
    if (o == null) {
      return "null";
    }
    else if (o instanceof String) {
      return StringFunction.leftStr((String) o, 10);
    }
    else if (o instanceof Geometry) {
      return ((Geometry) o).getGeometryType().toUpperCase();
    }
    return o.toString();
  }

}
