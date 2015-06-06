package jeql.command.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Stack;

import javax.xml.stream.XMLStreamException;

import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.BasicRowList;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.io.InputSource;

public class XmlRowList 
extends BasicRowList
{
  public static final String COL_PATH = "path";
  public static final String COL_ATTR = "attr";
  public static final String COL_VAL = "value";
  
  private InputSource src;
  
  public XmlRowList(InputSource src) 
  {
    this.src = src;
    schema = new RowSchema(
        new String[] { COL_PATH, COL_ATTR, COL_VAL }, 
        new Class[] { String.class, String.class, String.class } );
  }

  public RowIterator iterator()
  {
    try {
      return new XmlRowIterator(schema, src);
    }
    catch (Exception ex) {
        throw new JeqlException(ex);
      }      
  }
  
  private class XmlRowIterator implements RowIterator
  {
    //private String lineBuffer = null;
    private boolean isClosed = false;
    private Stack tagStack;
    private XMLStackReader xmlRdr;
    //private LineNumberReader rdr;
    
    public XmlRowIterator(RowSchema schema, InputSource src) throws Exception
    {
      LineNumberReader rdr = new LineNumberReader(new InputStreamReader(src.createStream()));
      xmlRdr = new XMLStackReader(rdr);
    }
    
    public RowSchema getSchema()
    {
      return schema;
    }
    
    public Row next()
    {
      String path = null;
      String attr = null;
      String value = null;
      try {
        path = xmlRdr.next();
        attr = xmlRdr.attr();
        value = xmlRdr.value();
      }
      catch (XMLStreamException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // if at end, can close input
      if (path == null) {
        close();
        return null;
      }
      return createRow(path, attr, value);
    }
      
    private void close()
    {
      if (xmlRdr != null) {  xmlRdr.close(); }
      xmlRdr = null;
      isClosed = true;
    }
    
    private Row createRow(String path, String attr, String value)
    {
      BasicRow row = new BasicRow(schema.size());
      row.setValue(0, path);
      row.setValue(1, attr);
      row.setValue(2, value);
      return row;
    }
    

  }
}
