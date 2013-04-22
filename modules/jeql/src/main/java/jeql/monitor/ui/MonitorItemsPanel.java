package jeql.monitor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import jeql.monitor.MonitorModel;

public class MonitorItemsPanel extends JPanel
{
  MonitorModel monitorModel;
  
  private ItemTable table = new ItemTable();
  
  public MonitorItemsPanel()
  {
    super();
    initUI();
    
    table.setModel(new MonitorItemsTableModel());
    table.initTable();
  }

  private static final Font STATUS_FONT = new Font("SanSerif", Font.BOLD, 12);
  
  public void initUI() 
  {
    setMinimumSize(new Dimension(400, 50));
    setPreferredSize(new Dimension(900, 300));
    setBackground(SystemColor.control);

    this.setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.getViewport().setBackground(SystemColor.control);
    scrollPane.getViewport().add(table);
    this.add(scrollPane, BorderLayout.CENTER);
  }
  
  public void setModel(MonitorModel model)
  {
    //table.setModel(new MonitorItemsTableModel());
    table.initTable();
    this.monitorModel = model;
    ((MonitorItemsTableModel) table.getModel()).setModel(model);
  }
  
  public void update()
  {
    ((MonitorItemsTableModel) table.getModel()).update();
  }

  public void end()
  {
    table.end();
    ((MonitorItemsTableModel) table.getModel()).update();
  }

}

class ItemTable extends JTable
{
  private boolean isEnded = false;
  
  public ItemTable()
  {
    setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
  }

  public void end()
  {
    isEnded = true;
  }
  
  private static final int[] COL_WIDTH = new int[] { 70, 70, 70, 300, 50 };

  public void initTable()
  {
    isEnded = false;
    getColumnModel().setColumnMargin(4);
    for (int i = 0; i < 5; i++) {
      TableColumn col = getColumnModel().getColumn(i);
      
      // don't set width for last column, to allow it to stretch
      if (i < 4) {
        col.setPreferredWidth(COL_WIDTH[i]);
        col.setMaxWidth(COL_WIDTH[i]);
      }
      col.setHeaderRenderer(new MonitorItemTableHeaderRenderer(getTableHeader()
          .getDefaultRenderer()));
    }
    /*
    JComponent hdrRenderer = (JComponent) getTableHeader().getDefaultRenderer();
    hdrRenderer.setBackground(SystemColor.control);
    hdrRenderer.setForeground(SystemColor.control);
    */
    

  }

  private final Color ACTIVE_ROW_CLR = new Color(210, 230, 230);

  public TableCellRenderer getCellRenderer(int row, int column)
  {
    //System.out.println(" getCellRenderer  " + row + ", " + column);
    JComponent renderer = (JComponent) super.getCellRenderer(row, column);
    if (true) {
      boolean isActive = ((MonitorItemsTableModel) getModel()).getModel()
          .getItem(row).isActive();
      Color bkClr = isActive ? ACTIVE_ROW_CLR : Color.white;
      if (isEnded) bkClr = AppColor.BACKGROUND;
      renderer.setBackground(bkClr);
    }
    return (TableCellRenderer) renderer;
  }
}

class MonitorItemTableHeaderRenderer implements TableCellRenderer {
  TableCellRenderer originalRenderer;
  
  MonitorItemTableHeaderRenderer(TableCellRenderer originalRenderer)
  {
    this.originalRenderer = originalRenderer;
  }  
  
  // This method is called each time a column header
  // using this renderer needs to be rendered.

  
  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int col) {
    JLabel label = (JLabel) originalRenderer
    .getTableCellRendererComponent(table, value, isSelected,
            hasFocus, row, col);

      // Configure the component with the specified value
      label.setText(value.toString());

      // Set tool tip if desired
      //setToolTipText((String)value);
      label.setBackground(SystemColor.control);
      //setForeground(SystemColor.control);
      // Since the renderer is a component, return itself
      return label;
  }

  // The following methods override the defaults for performance reasons
  public void validate() {}
  public void revalidate() {}
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}
