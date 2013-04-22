package jeql.command.io;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;
import jeql.io.InputSource;


/**
 * A {@link RowList} providing lines from a text file
 * @author Martin Davis
 *
 */
public class TextFileRowList 
  implements RowList
{
  public static final String COL_NAME = "line";
  
  private InputSource src;
  private RowSchema schema;
  
  public TextFileRowList(InputSource src) 
  {
    this.src = src;
    schema = new RowSchema(new String[] { COL_NAME }, new Class[] { String.class });
  }

  public RowSchema getSchema()
  {
    return schema;
  }

  public RowIterator iterator()
  {
    return new TextFileRowIterator(schema, src);
  }
  
  private static class TextFileRowIterator
    implements RowIterator
  {
    private InputSource src;

    private RowSchema schema;
    private LineNumberReader lineReader = null;
    private String lineBuffer = null;
    private boolean isClosed = false;

    public TextFileRowIterator(RowSchema schema, InputSource src)
    {
      this.schema = schema;
      this.src = src;
      init();
    }
    
    public RowSchema getSchema()
    {
      return schema;
    }

    private void init()
    {
      if (isClosed) return;
      if (lineReader != null) return;
      lineReader = new LineNumberReader(new InputStreamReader(src.createStream()));
    }
    
    public Row next()
    {
      init();
      try {
        lineBuffer = lineReader.readLine();
      }
      catch (IOException ex) {
        throw new JeqlException(ex);
      }
      // if at end, can close input
      if (lineBuffer == null) {
        close();
        return null;
      }
      return createRow(lineBuffer);
    }
      
    private void close()
    {
      if (lineReader != null) {
        try {
          lineReader.close();
        }
        catch (IOException ex) {
          // eat this exception - nothing we can do about it anyway
        }
      }
      lineReader = null;
      isClosed = true;
    }
    
    private Row createRow(String val)
    {
      BasicRow row = new BasicRow(1);
      row.setValue(0, val);
      return row;
    }
    

  }
}
