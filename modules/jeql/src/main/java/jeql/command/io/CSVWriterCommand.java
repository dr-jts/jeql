package jeql.command.io;

import java.io.IOException;
import java.io.Writer;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.util.NumberUtil;
import jeql.util.SystemUtil;
import jeql.util.TypeUtil;

import org.locationtech.jts.geom.Geometry;

public class CSVWriterCommand 
extends TableFileWriterCmd 
{
  public static final String CSV_QUOTE = "\"";

  private String colSep = ",";
  private String quote = CSV_QUOTE;
  private boolean showColNames = true; 
  private boolean isTrim = false;
  private boolean isNoquoteNumbers = false;
  
  public static String csvEncode(String s)
  {
    // double all quotes
    return s.replaceAll(CSV_QUOTE, "\"\"");
  }
  
  public CSVWriterCommand() {
  }

  /**
   * Normally should not be overridden
   * 
   * @param sep
   */
  public void setColSep(String sep) {
    this.colSep = sep;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public void setColNames(boolean showColNames)
  {
   this.showColNames = showColNames;  
  }
  
  public void setTrim(boolean isTrim)
  {
    this.isTrim = isTrim;
  }
  public void setNoquoteNumbers(boolean isNoquoteNumbers)
  {
    this.isNoquoteNumbers = isNoquoteNumbers;
  }
  protected void write(Table tbl) throws IOException 
  {
    RowIterator rs = tbl.getRows().iterator();
    writer = getWriter();
    
    if (showColNames) {
      writeColNames(writer, rs.getSchema());
    }
    
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      writeRow(writer, row);
    }
    writer.close();
  }

  private void writeColNames(Writer writer, RowSchema schema)
  throws IOException
  {
    for (int i = 0; i < schema.size(); i++ ) {
      if (i > 0) writer.write(colSep);
      writer.write(schema.getName(i));
    }
    writer.write(SystemUtil.LINE_TERM);
  }
  
  private void writeRow(Writer writer, Row row) throws IOException {
    for (int i = 0; i < row.size(); i++) {
      if (i > 0) writer.write(colSep);
      writeValue(writer, row.getValue(i));
    }
    writer.write(SystemUtil.LINE_TERM);
  }

  private void writeValue(Writer writer, Object val)
  throws IOException {
    if (val == null) {
      // don't write out anything for null values
    }
    else if (val instanceof String) {
      writeString(writer, (String) val);
    }
    else if (val instanceof Geometry) {
      writer.write(CSV_QUOTE);
      writer.write(val.toString());
      writer.write(CSV_QUOTE);
    }
    else {
      writer.write(TypeUtil.toString(val));
    }
  }
  
  private void writeString(Writer writer, String val) throws IOException
  {
    String s = (String) val;
    if (isTrim) {
      s = s.trim();
    }
    if (isNoquoteNumbers) {
      if (NumberUtil.isNumberConvertible(s)) {
        writer.write(s);
        return;
      }
    }
    writer.write(CSV_QUOTE);
    writer.write(csvEncode(s));
    writer.write(CSV_QUOTE);      
  }

}
