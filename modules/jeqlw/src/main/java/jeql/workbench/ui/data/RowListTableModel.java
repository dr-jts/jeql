package jeql.workbench.ui.data;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vividsolutions.jts.geom.Geometry;

import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.monitor.MonitorRowList;
import jeql.std.geom.GeomUtil;
import jeql.util.Unicode;

public class RowListTableModel
extends AbstractTableModel
{
  private List rows;
  private RowSchema schema;

  private boolean isShowSpaces = false;
  
  public RowListTableModel(MonitorRowList rowList)
  {
    setRowList(rowList);
  }
  
  public RowSchema getSchema() { return schema; }
  
  public void update()
  {
    fireTableDataChanged();
  }
  
  public void setShowSpaces(boolean isShowSpaces)
  {
    this.isShowSpaces = isShowSpaces;
  }

  public boolean isShowSpaces() { return isShowSpaces; }
  
  public void setRowList(MonitorRowList rowList)
  {
    this.rows = rowList.getRows();
    schema = rowList.getSchema();
  }
  
  public int getRowCount()
  {
    return rows.size();
  }
  
  public int getColumnCount()
  {
    return schema.size() + 1;
  }
  
  public String getColumnName(int column)
  {
    if (column == 0) return "";
    return schema.getName(column - 1);
  }
  
  public Class<?> getColumnClass(int column)
  {
    //  Row number
    if (column == 0) return Integer.class; 
    
    Class type = schema.getType(column - 1);
    
    // display booleans as strings
    if (type == Boolean.class) return String.class;
    return type;
  }
  
  public Row getRow(int row)
  {
    return ((Row) rows.get(row));
  } 
  
  public Object getRawValueAt(int row, int column)
  {
    return ((Row) rows.get(row)).getValue(column-1);
  }
  
  public Object getValueAt(int row, int column)
  {
    // first column shown is the row number
    if (column == 0) return row;
    
    int tblCol = column - 1;
    
    Object val = ((Row) rows.get(row)).getValue(tblCol);
    
    if (schema.getType(tblCol) == Geometry.class) 
      return GeomUtil.shortString((Geometry) val);
    
    if (val instanceof Boolean) return val.toString();
    
    if (isShowSpaces && schema.getType(tblCol) == String.class) {
      String valstr = (String) val;
      String rep = null;
      if (valstr != null) {
        rep = valstr.replace(' ', Unicode.DEGREE_SIGN);
      }
      else {
        // show nulls 
        rep = Unicode.CURRENCY_SIGN + "";
      }
      return rep;
    }
    return val;
  }
  

}
