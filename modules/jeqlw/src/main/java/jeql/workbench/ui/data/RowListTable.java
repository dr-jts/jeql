package jeql.workbench.ui.data;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import jeql.api.row.RowSchema;
import jeql.monitor.MonitorRowList;
import jeql.style.StyleConstants;
import jeql.util.ClassUtil;
import jeql.util.ColorUtil;
import jeql.util.MethodUtil;
import jeql.workbench.Workbench;
import jeql.workbench.WorkbenchConstants;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

class RowListTable extends JTable
{
  private static Map<String, RowListColumnStyle> colStyleMap = new HashMap<String, RowListColumnStyle>();
  
  private static RowListColumnStyle findColStyle(RowSchema schema)
  {
    String key = RowListColumnStyle.columnKey(schema);
    //System.out.println("Finding style for " + key);
    RowListColumnStyle cs = colStyleMap.get(key);
    if (cs == null) {
      cs = new RowListColumnStyle(key, schema);
      colStyleMap.put(key, cs);
      //System.out.println("Creating style...");
    }
    return cs;
  }
  
  private static final Color LIGHT_GRAY = new Color(220, 220, 220);

  private boolean useMonospacedFont = true;
  
  private boolean[] isColorColumn;
  
  private RowListColumnStyle colStyle;
  
  public RowListTable(MonitorRowList mrl)
  {
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    setModel(new RowListTableModel(mrl));
    int numCols = mrl.getSchema().size();
    isColorColumn = colorColumns(mrl.getSchema());
    init();
    
    // allow sorting if available in Java version
    MethodUtil.invokeVoidSafe(this, "setAutoCreateRowSorter", boolean.class, Boolean.TRUE);
    //setAutoCreateRowSorter(true);
  }

  public void setMonospaced(boolean useMonospacedFont)
  {
    this.useMonospacedFont = useMonospacedFont;
  }
  
  private static final int ROW_NUM_COLUMN_WIDTH = 60;
  
  private void init()
  {
    // getColumnModel().setColumnMargin(4);
    RowListTableModel model = (RowListTableModel) getModel();
    colStyle = findColStyle(model.getSchema());
    
    for (int i = 0; i < model.getColumnCount(); i++) {
      TableColumn col = getColumnModel().getColumn(i);

      // first column is row number
      if (i == 0) {
        col.setMinWidth(ROW_NUM_COLUMN_WIDTH);
        col.setPreferredWidth(ROW_NUM_COLUMN_WIDTH);
        col.setMaxWidth(ROW_NUM_COLUMN_WIDTH);
      }
      else if (i < model.getColumnCount()) {
      // don't set width for last column, to allow it to stretch
        //col.setPreferredWidth(100);
        col.setPreferredWidth(colStyle.width(i - 1));
        //System.out.print("col " + (i-1) + " to width " + colStyle.width(i - 1) + " -style " + colStyle.getKey());
      }
      col.setHeaderRenderer(new RowListTableHeaderRenderer(getTableHeader()
          .getDefaultRenderer()));
      if (model.getColumnClass(i) == Double.class) {
        col.setCellRenderer(new DoubleFormatRenderer());
      }
    }
    getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      public void columnMarginChanged(ChangeEvent arg0) 
      {
        updateColStyle();
      }

      public void columnAdded(TableColumnModelEvent arg0)      { }
      public void columnMoved(TableColumnModelEvent arg0) {}
      public void columnRemoved(TableColumnModelEvent arg0) {}
      public void columnSelectionChanged(ListSelectionEvent arg0){}
    });
    
    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() < 2)
          return;
        
          int row = rowAtPoint(evt.getPoint());
          int col = columnAtPoint(evt.getPoint());
          if (row >= 0 && col >= 0) {
             Object val = ((RowListTableModel) getModel()).getRawValueAt(row, col);
             //System.out.println(val);
             
             Workbench.controller().inspect(inspectString(val));
          }
      }
  });

  }

  private static String inspectString(Object o)
  {
    if (o == null) return "";
    if (o instanceof Geometry) {
      WKTWriter writer = new WKTWriter();
      writer.setMaxCoordinatesPerLine(2);
      String str = writer.writeFormatted((Geometry) o);
      return str;
    }
    return o.toString();
  }
  
  private void updateColStyle()
  {
    for (int i = 0; i < getModel().getColumnCount(); i++) {
      if (i > 0) {
        TableColumn col = getColumnModel().getColumn(i);
        colStyle.setWidth(i - 1, col.getPreferredWidth());
      }
    }
  }

//Implement table header tool tips.
  protected JTableHeader createDefaultTableHeader() {
      return new JTableHeader(columnModel) {
          public String getToolTipText(MouseEvent e) {
              String tip = null;
              java.awt.Point p = e.getPoint();
              int colIndex = columnModel.getColumnIndexAtX(p.x);
              if (colIndex < 0) return "";
              int realIndex = columnModel.getColumn(colIndex).getModelIndex();
              return ClassUtil.classname(getModel().getColumnClass(realIndex));
          }
      };
  }
  
  /**
   * Override <tt>prepareRenderer()</tt> to set the font size in the instances
   * of the cell renderers returned by the Look-And-Feel's renderer.
   */
  public Component prepareRenderer(TableCellRenderer renderer, int row,
      int col)
  {
    Component c = super.prepareRenderer(renderer, row, col);
    if (getModel().getColumnClass(col) == String.class && useMonospacedFont) 
      c.setFont(WorkbenchConstants.FONT_LUCIDA_CONSOLE);
    return c;
  }
  
  public TableCellRenderer getCellRenderer(int row, int col)
  {
    // System.out.println(" getCellRenderer  " + row + ", " + column);
    JLabel renderer = (JLabel) super.getCellRenderer(row, col);
    
    //-------- Row striping
    if (col == 0) {
      // row number column
      renderer.setBackground(SystemColor.control);
    }
    else {
      // data column
      renderer.setBackground(((row % 2) == 0) ? Color.white : LIGHT_GRAY);
    }
    
    //--------- Show repeated column values
    if ( ((RowListTableModel) getModel()).isShowSpaces()
        && isRepeatedColumnValue(row, col)) {
      renderer.setBackground(Color.GRAY);
    }
    
    // show strings in different colour
    if (getModel().getColumnClass(col) == String.class) {
      renderer.setForeground(WorkbenchConstants.CLR_DARK_BLUE);
    }
    else if (getModel().getColumnClass(col) == Geometry.class) {
      renderer.setForeground(WorkbenchConstants.CLR_DARK_GREEN);
    }
    if (col > 0 && isColorColumn[col - 1]) {
      Color clr = ColorUtil.RGBAtoColor((String) getModel().getValueAt(row, col));
      if (clr == null) {
        clr = Color.white;
      }
      renderer.setBackground(clr);
      renderer.setForeground(readableForeground(clr));
    }

    return (TableCellRenderer) renderer;
  }
  
  private boolean isRepeatedColumnValue(int row, int col)
  {
    if (row == 0) return false;
    Object o = getModel().getValueAt(row, col);
    if (o == null) return false;
    
    Object oprev = getModel().getValueAt(row-1, col);
    if (o.equals(oprev))
      return true;
    return false;
  }
  
  private static Color readableForeground(Color background)
  {
    //float[] hsv = ColorUtil.toHSV(background);
    //boolean isDark = hsv[1] > 0.8 && hsv[2] < 0.5;
    boolean isDark = background.getRed() + background.getGreen() + background.getBlue() > 384;  // = 3 * 128
    if (isDark) return Color.BLACK;
    return Color.WHITE;
  }
  
  private static boolean[] colorColumns(RowSchema schema)
  {
    int numCols = schema.size();
    boolean[] isColorColumn = new boolean[numCols];
    for (int i = 0; i < numCols; i++) {
      setColorCol(StyleConstants.COLOR, schema, isColorColumn);
      setColorCol(StyleConstants.STROKE, schema, isColorColumn);
      setColorCol(StyleConstants.FONT_COLOR, schema, isColorColumn);
      setColorCol(StyleConstants.HALO_COLOR, schema, isColorColumn);
      setColorCol(StyleConstants.FILL, schema, isColorColumn);
      setColorCol(StyleConstants.COL_MARKER_COLOR, schema, isColorColumn);
    }
    return isColorColumn;
  }
  
  private static void setColorCol(String clrColName, RowSchema schema, boolean[] isColorColumn)
  {
    int i = schema.getColIndex(clrColName);
    if (i < 0) return;
    isColorColumn[i] = true;

  }
}

class RowListTableHeaderRenderer implements TableCellRenderer
{
  TableCellRenderer originalRenderer;

  RowListTableHeaderRenderer(TableCellRenderer originalRenderer)
  {
    this.originalRenderer = originalRenderer;
  }

  // This method is called each time a column header
  // using this renderer needs to be rendered.

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int col)
  {
    JLabel label = (JLabel) originalRenderer.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, col);

    // Configure the component with the specified value
    label.setText(value.toString());

    // Set tool tip if desired
    // setToolTipText((String)value);
    label.setBackground(SystemColor.control);

    // setForeground(SystemColor.control);
    // Since the renderer is a component, return itself
    return label;
  }

  // The following methods override the defaults for performance reasons
  public void validate()
  {
  }

  public void revalidate()
  {
  }

  
  protected void firePropertyChange(String propertyName, Object oldValue,
      Object newValue)
  {
  }

  public void firePropertyChange(String propertyName, boolean oldValue,
      boolean newValue)
  {
  }
  
}