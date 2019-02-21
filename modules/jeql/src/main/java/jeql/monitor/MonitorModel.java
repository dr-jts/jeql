package jeql.monitor;

import java.util.ArrayList;
import java.util.List;

import jeql.monitor.MonitorRowList.MonitorRowIterator;

import org.locationtech.jts.util.Stopwatch;

public class MonitorModel
{  
  public static String lineDisplay(int line)
  {
    //return "(line " + line + ")   ";
    return line + " :   ";
  }

  private Stopwatch sw = new Stopwatch();

  private List<MonitorItem> items = new ArrayList<MonitorItem>();
  
  public MonitorModel() {

  }

  public List<MonitorItem> getItems()
  {
    return items;
  }
  
  public MonitorItem getItem(int i)
  {
    return items.get(i);
  }
  
  public void add(MonitorRowIterator mri) 
  {
    MonitorRowList mrl = mri.getRowList();
    
    // find location of mrl, or insert new mrl at appropriate location
    int i = findIndex(mrl);
    if (i < 0) {
      MonitorItem mi = new MonitorItem(mrl, mri);
      items.add(mi);
    }
    else {
      // i is index of existing mrl, or location to insert new one
      MonitorItem mi = items.get(i);
      if (mi.isEqual(mrl)) {
        mi.setIterator(mri);
      }
      else {
        mi = new MonitorItem(mrl, mri);
        items.add(i, mi);    
      }
    }
  }
  
  private int findIndex(MonitorRowList mrl)
  {
    for (int i = 0; i < items.size(); i++) {
      MonitorItem mi = items.get(i);
      if (mi.isEqual(mrl)) return i;
      // found location in script line order
      if (mi.getLine() > mrl.getLine()) return i;
    }
    return -1;
  }

  public void update()
  {
    rowTotal = 0;
    for (int i = 0; i < items.size(); i++) {
      MonitorItem mi = items.get(i);
      mi.updateActive();
      rowTotal += mi.getTotalRowCount();
    }
  }

  public void stop()
  {
    sw.stop();
  }
  
  private long rowTotal = 0;
  
  public long getRowTotal()
  {
    return rowTotal;
  }
  
  public long getTime()
  {
    return sw.getTime();
  }
  
  public String getTimeString()
  {
    return sw.getTimeString();
  }
  
  public String scriptView()
  {
    rowTotal = 0;
    try {
      StringBuilder sb = new StringBuilder();
      for (MonitorItem mi : items) {
          sb.append(mi.toString());
          sb.append("\n");
          rowTotal += mi.getTotalRowCount();
      }
      return sb.toString();
    }
    catch (Exception ex) {
      return ex.toString();
    }
  }
  
}
