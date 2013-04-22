package jeql.workbench.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jeql.monitor.MonitorItem;
import jeql.monitor.MonitorModel;

public class DataPanel extends JPanel
{
  private MonitorModel model;

  JTabbedPane tabPane = new JTabbedPane();
  DataToolBar tools = new DataToolBar(this);
  
  public DataPanel()
  {
    super();
    try {
      initUI();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    tabPane.setTabPlacement(JTabbedPane.LEFT);
    
    setMinimumSize(new Dimension(400, 50));
    setPreferredSize(new Dimension(900, 300));
    this.setLayout(new BorderLayout());
    this.add(tools, BorderLayout.WEST);
    this.add(tabPane, BorderLayout.CENTER);
  }

  /**
   * Clears the panel, by removing all existing tabs (if any)
   */
  public void clear()
  {
    tabPane.removeAll();
  }

  public void setModel(MonitorModel model)
  {
    // table.setModel(new MonitorItemsTableModel());
    // table.initTable();
    this.model = model;
    
  }
  
  public void update()
  {
    if (model == null) return;
    
    List<MonitorItem> items = model.getItems();
    try {
    int i = 0;
    for (MonitorItem item : items) {
      if (i < tabPane.getTabCount()) {
        RowListDataPanel rldp = (RowListDataPanel) tabPane.getComponentAt(i);
        if (rldp.getMonitorItem() != item) {
          insertItem(item, i);
        }
        else {
          rldp.update();
        }
      }
      else {
        addItem(item);
      }
      i++;
    }
    }
    catch (ConcurrentModificationException e) {
      // do nothing - this doesn't happen very often, and it's safe just to continue
    }
  }
  
  public void showSpaces(boolean isShowSpaces)
  {
    for (int i = 0; i < tabPane.getTabCount(); i++) {
      RowListDataPanel rldp = (RowListDataPanel) tabPane.getComponentAt(i);
      rldp.showSpaces(isShowSpaces);
    }
  }

  public void showMonospaced(boolean isShowMonospaced)
  {
    for (int i = 0; i < tabPane.getTabCount(); i++) {
      RowListDataPanel rldp = (RowListDataPanel) tabPane.getComponentAt(i);
      rldp.showMonospaced(isShowMonospaced);
    }
  }

  public void addItem(MonitorItem item)
  {
    tabPane.add(new RowListDataPanel(item), item.getName());
    // make last added tab visible
    tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
  }
  
  private void insertItem(MonitorItem item, int i)
  {
    tabPane.insertTab(item.getName(), null, new RowListDataPanel(item), null, i);
    // make last added tab visible
    tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
  }

  public MonitorItem getCurrentItem()
  {
    int index = tabPane.getSelectedIndex();
    RowListDataPanel panel = (RowListDataPanel) tabPane.getSelectedComponent();
    if (panel == null) return null;
    return panel.getMonitorItem();
  }
}
