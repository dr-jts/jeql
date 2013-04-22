
package jeql.io.dbf;

import java.io.IOException;

import jeql.api.error.ExecutionException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.util.TypeUtil;

import org.geotools.dbffile.DbfFile;

/**
 * A RowIterator for DBF files
 */
public class DbfRowIterator 
implements RowIterator 
{
  private DbfFile dbf;
  private int count = 0;
  private int numfields;
  private RowSchema fs;

  public DbfRowIterator() {
  }

  public RowSchema getSchema() { return fs; }
  
  public void open(String filename)
    throws Exception
  {
    dbf = new DbfFile(filename);

    numfields = dbf.getNumFields();
    fs = new RowSchema(numfields);
    // fill in schema
    for (int j = 0; j < numfields; j++) {
      String typename = dbf.getFieldType(j);
      fs.setColumnDef(j, dbf.getFieldName(j), TypeUtil.typeForName(typename));
    }
  }

  public Row next()
  {
    try {
      if (count >= dbf.getLastRec())
        return null;
      return nextRaw();
    }
    catch (IOException ex) {
      throw new ExecutionException(ex);
    }
  }
  
  public Row nextRaw() throws IOException 
  {
    BasicRow row = new BasicRow(fs.size());
    StringBuffer s = dbf.getNextDbfRec();
    count++;

    for (int y = 0; y < numfields; y++) {
      try {
        row.setValue(y, dbf.ParseRecordColumn(s, y));
      } catch (Exception ex) {
        /**
         * don't propagate exceptions from datatype problems
         * what *should* happen is that ParseRecordColumn does not throw any exceptions
         * but simply returns a null value of the right type
         *
         */
      }

      // System.out.println(  mydbf.getFieldName(y)+"="+ info.get(y).toString() );
    }

    return row;
    //System.out.println(s.toString() );
  }

  public RowSchema getFeatureSchema() {
    return fs;
  }

  public void close() throws IOException 
  {
    dbf.close(); 
  }

}
