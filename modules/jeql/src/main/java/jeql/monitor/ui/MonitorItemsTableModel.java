package jeql.monitor.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import jeql.monitor.MonitorItem;
import jeql.monitor.MonitorModel;

public class MonitorItemsTableModel
extends AbstractTableModel
{
  private MonitorModel model;
  private List<MonitorItem> items;

  
  public MonitorItemsTableModel()
  {
    //setModel(model);
  }
  
  public void update()
  {
    fireTableDataChanged();
  }
  public void setModel(MonitorModel model)
  {
    this.model = model;
    items = model.getItems();
    fireTableDataChanged();
  }
  
  public MonitorModel getModel()
  {
    return model;
  }
  
  public int getRowCount()
  {
    if (items == null) return 0;
    return items.size();
  }
  
  public int getColumnCount()
  {
    return 5;
  }
  
  public String getColumnName(int column)
  {
    switch (column) {
    case 0: return "Line";
    case 1: return "Scan";
    case 2: return "Row";
    case 3: return "Operation";
    case 4: return "Data";
    }
    return "";
  }
  public Class<?> getColumnClass(int column)
  {
    switch (column) {
    case 0: return Integer.class;
    case 1: return Integer.class;
    case 2: return Long.class;
    case 3: return String.class;
    case 4: return String.class;
    }
    return String.class;
  }
  
  public Object getValueAt(int row, int column)
  {
    MonitorItem mi = items.get(row);
    switch (column) {
    case 0: 
      return mi.getLine();
    case 1:
      return mi.getScanCount();
    case 2:
      return mi.getRowCount();
    case 3:
      return mi.getTag();
    case 4:
      return mi.getRowDesc();
    }
    return "foo";
  }
  

}
