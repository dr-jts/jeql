package jeql.workbench.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.monitor.MonitorItem;
import jeql.monitor.MonitorRowList;
import jeql.style.StyleConstants;
import jeql.util.ColorUtil;

public class RowListDataPanel extends JPanel
{
  private MonitorItem monItem;

  private RowListTable table;

  public RowListDataPanel(MonitorItem mi)
  {
    monItem = mi;
    table = new RowListTable(mi.getRowList());

    setBackground(SystemColor.control);

    this.setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.getViewport().setBackground(SystemColor.control);
    scrollPane.getViewport().add(table);
    this.add(scrollPane, BorderLayout.CENTER);

  }

  public MonitorItem getMonitorItem()
  {
    return monItem;
  }

  public Row getSelectedRow() {
    return table.getSelectedRowValue();
  }
  
  public void update()
  {
    ((RowListTableModel) table.getModel()).fireTableDataChanged();
  }
  
  public void showSpaces(boolean isShowSpaces)
  {
    ((RowListTableModel) table.getModel()).setShowSpaces(isShowSpaces);
  }
  
  public void showMonospaced(boolean useMonospacedFont)
  {
    table.setMonospaced(useMonospacedFont);
  }

  public void setSelectedRow(int i) {
    table.setRowSelectionInterval(i, i);
    /*
    table.setHighlightRow(i);
    update();
    */
    table.scrollRectToVisible(table.getCellRect(i, 0, true)); 
  }

  public void clearSelection() {
    table.clearSelection();
    //table.clearHighlightRow();
    update();
  }
}

