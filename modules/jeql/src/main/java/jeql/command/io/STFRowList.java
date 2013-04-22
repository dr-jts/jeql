package jeql.command.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;


/**
 * A {@link RowList} for Simple Table Format files
 * @author Martin Davis
 *
 */
public class STFRowList 
  implements RowList
{
  private static String parseColName(String line)
  {
    int sepPos = line.indexOf('>');
    if (sepPos == -1)
      throw new IllegalArgumentException("Invalid column name format in line: " 
          + line);
    String colName = line.substring(1, sepPos).trim();
    return colName;
  }
  
  private static String parseColValue(String line)
  {
    int sepPos = line.indexOf('>');
    if (sepPos == -1)
      return "";
    String val = line.substring(sepPos + 1).trim();
    return val;
  }
  
  private String filename;
  private RowSchema schema;
  
  public STFRowList(String filename) 
    throws IOException
  {
    this.filename = filename;
    schema = readSchema(filename);
  }

  public RowSchema getSchema()
  {
    return schema;
  }

  public RowIterator iterator()
  {
    return new STFRowIterator(schema, filename);
  }
  
  private RowSchema readSchema(String filename)
    throws IOException
  {
    SchemaExtracter se = new SchemaExtracter(filename);
    return se.getSchema();
  }
  
  //=====================================================
  
  private static class SchemaExtracter 
  {
    private String filename = null;

    private LineNumberReader lineReader = null;

    private List colNames = new ArrayList();

    public SchemaExtracter(String filename) {
      this.filename = filename;
    }

    public RowSchema getSchema() 
    throws IOException
    {
      try {
        lineReader = new LineNumberReader(new FileReader(filename));
        readCols();
      }
      finally {
        if (lineReader != null)
          lineReader.close();
      }
      return buildSchema(colNames);
    }

    private RowSchema buildSchema(List colNames)
    {
      String[] names = new String[colNames.size()];
      Class[] types = new Class[colNames.size()];
      
      int index = 0;
      for (Iterator i = colNames.iterator(); i.hasNext(); ) {
        String name = (String) i.next();
        names[index] = name;
        types[index] = String.class;
        index++;
      }
      RowSchema schema = new RowSchema(names, types);
      return schema;
    }
    
    private void readCols() 
    throws IOException
    {
      RowLinesReader linesReader = new RowLinesReader(lineReader);
      List lines = linesReader.readRow();
      
      for (int i = 0; i < lines.size(); i++) {
        // extract a column name
        colNames.add(parseColName((String) lines.get(i)));
        //TODO: check for duplicate col names and throw error if found (prevents very long rows)
      }
    }
  }
  
  /**
   * Reads a block of lines from a STF file which comprise a row.
   * 
   * @author Martin Davis
   *
   */
  private static class RowLinesReader
  {    
    private LineNumberReader rdr;
    
    public RowLinesReader(LineNumberReader rdr)
    {
      this.rdr = rdr;
    }
    
    public List readRow()
    throws IOException
    {
      List lines = new ArrayList();
      readRow(lines);
      return lines;
    }
    
    /**
     * Reads lines from file until maximum is exceeded,
     * or blank line or EOF is found.
     * 
     * @param maxLines
     * @return an array containing the lines read
     * @return null if no lines were read (i.e. EOF)
     */
    public void readRow(List lines)
    throws IOException
    {
      int numLinesRead = 0;
      while (true) {
        String line = readLine();
        // EOF
        if (line == null) break;
        // blank line = end of row
        if (line.trim().length() == 0) break;
        lines.add(line);
      }
    }
    
    private String readLine()
      throws IOException
    {
      String lineBuffer = rdr.readLine();
      // if at end, can close input
      return lineBuffer;
    }

  }
  
  // =============================================
  
  private static class STFRowIterator
    implements RowIterator
  {
    private String filename;

    private RowSchema schema;
    private LineNumberReader lineReader = null;
    private RowLinesReader rowReader = null;
    private List lines = new ArrayList();
    private String lineBuffer = null;
    private boolean isClosed = false;
    
    public STFRowIterator(RowSchema schema, String filename)
    {
      this.schema = schema;
      this.filename = filename;
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
      try {
        lineReader = new LineNumberReader(new FileReader(filename));
        rowReader = new RowLinesReader(lineReader);
      }
      catch (FileNotFoundException ex) {
        // convert to unchecked
        throw new JeqlException(ex);
      }
    }
    
    public Row next()
    {
      // init in case not already started
      init();
      
      lines.clear();
      try {
        rowReader.readRow(lines);
      }
      catch (IOException ex) {
        // convert to unchecked
        throw new JeqlException(ex);
      }
      
      // check if at EOF
      if (lines.size() == 0) {
        close();
        return null;
      }
      
      return createRow(lines);
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
    
    private Row createRow(List lines)
    {
      BasicRow row = new BasicRow(schema.size());
      
      for (int i = 0; i < lines.size(); i++) {

        String line = (String) lines.get(i);
        String col = parseColName(line);
        String val = parseColValue(line);
        
        int colIndex = schema.getColIndex(col);
        row.setValue(colIndex, val);
      }
      
      return row;
    }
    

  }
}
