package jeql.command.io;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.xml.stream.XMLStreamException;

import jeql.api.error.JeqlException;
import jeql.api.row.BasicRow;
import jeql.api.row.BasicRowList;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.command.io.XMLStackReader.Atom;
import jeql.io.InputSource;

public class XmlRowList 
extends BasicRowList
{
  public static final String COL_PATH = "path";
  public static final String COL_ATTR = "attr";
  public static final String COL_VAL = "value";
  
  private InputSource src;
  private boolean includeAllEndElements;
  
  public XmlRowList(InputSource src, boolean includeAllEndElements) 
  {
    this.src = src;
    this.includeAllEndElements = includeAllEndElements;
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
    private XMLStackReader xmlRdr;
    
    public XmlRowIterator(RowSchema schema, InputSource src) throws Exception
    {
      LineNumberReader rdr = new LineNumberReader(new InputStreamReader(src.createStream()));
      xmlRdr = new XMLStackReader(rdr, includeAllEndElements);
    }
    
    public RowSchema getSchema()
    {
      return schema;
    }
    
    public Row next()
    {
      Atom atom = null;
      try {
        atom = xmlRdr.next();
      }
      catch (XMLStreamException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // if at end, can close input
      if (atom == null) {
        close();
        return null;
      }
	return createRow(atom);
    }
      
    private void close()
    {
      if (xmlRdr != null) {  xmlRdr.close(); }
      xmlRdr = null;
    }
    
    private Row createRow(Atom atom)
    {
      BasicRow row = new BasicRow(schema.size());
      row.setValue(0, atom.path);
      row.setValue(1, atom.attr);
      row.setValue(2, atom.value);
      return row;
    }
    

  }
}
