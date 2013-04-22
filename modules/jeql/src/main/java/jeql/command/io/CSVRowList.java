package jeql.command.io;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeql.api.error.IllegalValueException;
import jeql.api.error.InvalidInputException;
import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowList;
import jeql.api.row.RowSchema;


/**
 * A {@link RowList} providing rows from a CSV file
 * @author Martin Davis
 *
 */
public class CSVRowList 
  implements RowList
{
  public static final int NO_COL_NAMES = 1;
  public static final int SKIP_COL_NAMES = 2;
  public static final int USE_COL_NAMES = 3;
  
  private String filename;
  private int colNameStrategy = NO_COL_NAMES;
  private RowSchema schema = null;
  private char colSeparator = 0;
  private int numColumns = -1;
  
  public CSVRowList(String filename) 
  throws IOException
  {
    this.filename = filename;
    schema = readSchema(filename);
  }

  public CSVRowList(String filename, int colNameStrategy, int numColumns, char separator) 
  throws IOException
  {
    this.filename = filename;
    this.colNameStrategy = colNameStrategy;
    this.numColumns = numColumns;
    colSeparator = separator;
    schema = readSchema(filename, numColumns);     
  }
  
  public RowSchema getSchema()
  {
    return schema;
  }

  private RowSchema readSchema(String filename) throws IOException {
    SchemaExtracter se = new SchemaExtracter(filename, colNameStrategy == USE_COL_NAMES, colSeparator);
    return se.getSchema();
  }

  private RowSchema readSchema(String filename, int numColumns) throws IOException {
    SchemaExtracter se = new SchemaExtracter(filename, colNameStrategy == USE_COL_NAMES, colSeparator);
    se.setNumColumns(numColumns);
    return se.getSchema();
  }

  public RowIterator iterator()
  {
    CSVRowIterator it = new CSVRowIterator(schema, filename, colNameStrategy != NO_COL_NAMES);
    if (colSeparator > 0) it.setColSep(colSeparator);
    return it;
  }
  
  private static class CSVRowIterator
    implements RowIterator
  {
    private CSVRecordParser csvLineParser = new CSVRecordParser();
    
    private String filename;
    private boolean hasColNames = true;
    private RowSchema schema;
    private LineNumberReader lineReader = null;
    private String line = null;
    private boolean isClosed = false;
    private int rowCount = 0;

    public CSVRowIterator(RowSchema schema, String filename, boolean hasColNames)
    {
      this.schema = schema;
      this.filename = filename;
      this.hasColNames = hasColNames;
      init();
    }
    
    public void setColSep(char separator)
    {
      csvLineParser.setColSep(separator);
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
      }
      catch (FileNotFoundException ex) {
        throw new JeqlException(ex);
      }
      if (hasColNames)
        readLine();
    }
    
    private String readLine()
    {
      try {
        return lineReader.readLine();
      }
      catch (IOException ex) {
        throw new JeqlException(ex);
      }
      //return null;
    }
    
    public Row next()
    {
      init();
      line = readLine();
      rowCount++;
      // if at end, can close input
      if (line == null) {
        close();
        return null;
      }
      Row row;
      try {
        row = createRow(line);
      }
      catch (InvalidInputException e) {
        throw new InvalidInputException(e, rowCount);
      }
      return row;
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
    
    private Row createRow(String line)
    {
      BasicRow row = new BasicRow(schema.size());
      String[] vals = csvLineParser.parse(line);
      int nToCopy = vals.length;
      if (nToCopy > row.size())
        nToCopy = row.size();
      for (int i = 0; i < nToCopy; i++) {
        row.setValue(i, vals[i]);
      }
      return row;
    }
  }
  
  /*
  private static class CheesyCSVLineParser
  {
    public static String[] parse(String line)
    {
      String[] val = line.split(",");
      for (int i = 0; i < val.length; i++) {
        if (val[i].charAt(0) == '"') {
          val[i] = val[i].substring(1, val[i].length() - 1);
        }
      }
      return val;
    }
  }
  */
  
  //=====================================================
  
  private static class SchemaExtracter 
  {
    private String filename = null;
    
    // -1 = use number of cols in CSV file schema or first line
    private int numColumns = -1;
    private boolean useColNames;
    private CSVRecordParser csvLineParser = new CSVRecordParser();
    private LineNumberReader lineReader = null;
    private List colNames = new ArrayList();

    public SchemaExtracter(String filename, boolean useColNames, char fieldSeparator) {
      this.filename = filename;
      this.useColNames = useColNames;
      if (fieldSeparator > 0)
        csvLineParser.setColSep(fieldSeparator);
    }

    /**
     * 
     * @param numColumns -1 if all columns should be read
     */
    public void setNumColumns(int numColumns)
    {
      this.numColumns = numColumns;
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
        // replace blanks with underscores
        names[index] = name.replace(" ", "_");
        types[index] = String.class;
        index++;
      }
      RowSchema schema = new RowSchema(names, types);
      return schema;
    }
    
    private void readCols() 
    throws IOException
    {
      String line = lineReader.readLine();
      String[] cols = csvLineParser.parse(line);
      
      int schemaColSize = cols.length;
      if (numColumns >= 0) {
        schemaColSize = numColumns;
      }
      for (int i = 0; i < schemaColSize; i++) {
        // TODO: if column names are provided read them
        String colName = RowSchema.getDefaultColumnName(i + 1);
        if (useColNames && i < cols.length) {
          colName = cols[i];
          if (! RowSchema.isValidColumnName(colName)) {
            throw new IllegalValueException("column name", colName);
          }
        }
        // create a standard column name
        colNames.add(colName);
        
        //TODO: check for duplicate col names and throw error if found (prevents very long rows)
      }
    }
  }

}
