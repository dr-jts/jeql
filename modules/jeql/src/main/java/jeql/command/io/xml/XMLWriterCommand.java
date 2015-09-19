package jeql.command.io.xml;


import java.io.IOException;
import java.io.Writer;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;
import jeql.command.io.IOConstants;
import jeql.command.io.TableFileWriterCmd;
import jeql.engine.Scope;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.gml2.GMLWriter;

// TODO: allow output of nested column values (using _ as tag sep)

/**
 * Outputs XML with defined tags for table and row tags.
 * Column tags are defined by column names.
 * Also allows outputing custom XML attributes for table element.
 * Geometries are written in GML2 format (to write them as WKT, convert them to a WKT string in JEQL)
 * 
 * @author Martin Davis
 *
 */
public class XMLWriterCommand 
extends TableFileWriterCmd
{
  public static String[] splitTags(String tags, String tagSep)
  {
    String[] tag = tags.split(tagSep);
    return tag;
  }
  
  private static final int GEOM_FORMAT_WKT = 0;
  private static final int GEOM_FORMAT_GML2 = 1;
  
  private String comment = null;
  private String tableAttr = null;
  
  private String tableTag = "table";
  private String rowTag = "row";
  
  private int geometryFormat = GEOM_FORMAT_WKT;
  
  private RowSchema schema = null;
  private XmlDataWriter xmlWriter;
  private GMLWriter gmlWriter = new GMLWriter();

  
  public XMLWriterCommand() {
    super();
  }

  /**
   * Sets the name to use as the table tag name.
   * Name string may contain multiple tags separated by TBL_ROW_TAGS_SEPARATOR.
   * Multiple tags will be written as nested elements.
   * 
   * @param tableTag
   */
  public void setTableTag(String tableTag)
  {
    this.tableTag = tableTag;
  }
  
  /**
   * Sets the name to use as the row tag name.
   * Name string may contain multiple tags separated by TBL_ROW_TAGS_SEPARATOR.
   * Multiple tags will be written as nested elements.
   * 
   * @param rowTag
   */
  public void setRowTag(String rowTag)
  {
    this.rowTag = rowTag;
  }
  
  /**
   * Sets comment text to write at start of document
   * @param comment
   */
  public void setComment(String comment)
  {
    this.comment = comment;
  }
  
  /**
   * Attributes string to write in table tag
   * @param tableAttr
   */
  public void setTableAttr(String tableAttr)
  {
    this.tableAttr = tableAttr;
  }
  
  /**
   * Sets whether to write geometry as GML-style XML
   * 
   * @param asGML
   */
  public void setGml(boolean asGML)
  {
    if (asGML)
      geometryFormat = GEOM_FORMAT_GML2;
  }
  
  public void execute(Scope scope) throws Exception {
    writer = getWriter();
    xmlWriter = new XmlDataWriter(writer);
    
    xmlWriter.prolog();
    if (comment != null)
      xmlWriter.comment(comment);
    
    // write table
    int n = writeStartElements(tableTag, tableAttr);
    write(tbl);
    xmlWriter.elementEnd(n);
    writer.close();
  }

  private int writeStartElements(String tag, String attr1)
  throws IOException
  {
    String[] tags = splitTags(tag, IOConstants.XML_NESTED_TAGS_SEPARATOR);
    for (int i = 0; i < tags.length; i++) {
      if (i == 0)
        xmlWriter.elementStart(tags[0], attr1);
      else
        xmlWriter.elementStart(tags[i]);
    }
    return tags.length;
  }
  
  protected void write(Table tbl) throws Exception 
  {
    schema = tbl.getRows().getSchema();
    RowIterator rs = tbl.getRows().iterator();
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      int n = writeStartElements(rowTag, null);
      writeRow(writer, row);
      xmlWriter.elementEnd(n);
    }
  }

  private void writeRow(Writer writer, Row row) throws IOException {
    for (int i = 0; i < row.size(); i++) {
      String itemTag = schema.getName(i);
      Object val = row.getValue(i);
      if (val instanceof String) {
        xmlWriter.elementWithData(itemTag, (String) val);
      }
      else if (val instanceof Geometry) {
        // indent geometry output
        xmlWriter.elementStart(itemTag);
        xmlWriter.markup(geometryRep((Geometry) val), true);
        xmlWriter.elementEnd();
        
//        xmlWriter.elementWithDataRaw(itemTag, geometryRep((Geometry) val)));        
      }
      else {
        // no need to encode non-string values -> faster
        xmlWriter.elementWithDataRaw(itemTag, val.toString());
      }
      // TODO: better formatting for data items (eg Geometry, dates) ??
    }
  }

  private String geometryRep(Geometry g)
  {
    if (geometryFormat == GEOM_FORMAT_GML2)
      return gmlWriter.write(g);
    return g.toString() + "\n";
  }
}