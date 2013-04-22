package jeql.monitor;

import jeql.monitor.MonitorRowList.MonitorRowIterator;

public class MonitorItem
{
  private static final String ITEM_NAME_LINE_SEP = " : ";
  
  private int line;
  private String name;
  private String tag;
  private MonitorRowList mrl;
  private MonitorRowIterator mri;
  private String rowDesc;
  private long lastScanCount;
  private long lastRowCount;
  private boolean isActive = false;
  
  public MonitorItem(MonitorRowList mrl, MonitorRowIterator mri)
  {
    this.line = mrl.getLine();
    name = mrl.getName() + ITEM_NAME_LINE_SEP + line;
    this.tag = mrl.getSource();
    this.mrl = mrl;
    setIterator(mri);
  }
  
  public int getLine() { return line; }
  public String getName() { return name; }
  public String getTag() { return tag; }
  public MonitorRowList getRowList() { return mrl; }
  
  public long getScanCount()
  {
    return mri.getScanCount();
  }
  
  public long getRowCount()
  {
    return mri.getRowCount();
  }
  
  public long getTotalRowCount()
  {
    if (mrl != null) {
      return mrl.getTotalRowCount();
    }
    return 0;
  }
  
  public String getRowDesc()
  {
    String newDesc = mri.rowDesc();
    if (rowDesc == null || newDesc.length() > 0)
      rowDesc = newDesc;
    return rowDesc;
  }
  
  public void updateActive()
  {
    isActive = lastScanCount !=  mri.getScanCount() || lastRowCount != mri.getRowCount();
    lastScanCount = mri.getScanCount();
    lastRowCount = mri.getRowCount();
  }

  public boolean isActive()
  {
    return isActive;
  }
  
  public boolean isEqual(MonitorRowList mrl)
  {
    return this.mrl == mrl;
  }
  
  public void setIterator(MonitorRowIterator mri)
  {
    this.mri = mri;
  }
  
  public String toString()
  {
    if (mri != null) return mri.toString();
    return "XXXX";
  }
}
