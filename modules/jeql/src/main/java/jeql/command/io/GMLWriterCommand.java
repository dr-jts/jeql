package jeql.command.io;


import java.io.IOException;
import java.io.Writer;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.row.RowUtil;
import jeql.api.table.Table;
import jeql.command.io.TableFileWriterCmd;
import jeql.command.io.xml.XmlDataWriter;
import jeql.engine.Scope;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.gml2.GMLWriter;

public class GMLWriterCommand 
extends TableFileWriterCmd
{  
  private static final String TBL_ROW_TAGS_SEPARATOR = "_";
  
  public static final String GML_FEATURECOLLECTION_TAG = "gml:FeatureCollection";
  public static final String GML_FEATUREMEMBER_TAG = "gml:featureMember";
  public static final String GML_FEATURE_TAG = "gml:Feature";
  public static final String GML_NAMESPACE_ATTR = "xmlns:gml=\"http://www.opengis.net/gml\"";
  
  private String comment = null;
  
  private static String featureMemberTag = GML_FEATUREMEMBER_TAG;
  private String featureCollectionTag = GML_FEATURECOLLECTION_TAG;
  private String featureTag = GML_FEATURE_TAG;
  private String userNamespacePrefix = null;
  private String userNamespaceURI = null;
  private String gmlIdColumn = "gml_id";
  
  private RowSchema schema = null;
  private XmlDataWriter xmlWriter;
  private GMLWriter gmlWriter = new GMLWriter();

  
  public GMLWriterCommand() {
    super();
  }

  /**
   * Sets the name to use as the table tag name.
   * Name string may contain multiple tags separated by TBL_ROW_TAGS_SEPARATOR.
   * Multiple tags will be written as nested elements.
   * 
   * @param tableTag
   */
  public void setFeatureCollectionTag(String tableTag)
  {
    this.featureCollectionTag = tableTag;
  }
  
  public void setNamespacePrefix(String userNamespacePrefix)
  {
    this.userNamespacePrefix = userNamespacePrefix;
  }
  
  public void setNamespaceURI(String userNamespaceURI)
  {
    this.userNamespaceURI = userNamespaceURI;
  }
  

  /**
   * Sets the name to use as the row tag name.
   * Name string may contain multiple tags separated by TBL_ROW_TAGS_SEPARATOR.
   * Multiple tags will be written as nested elements.
   * 
   * @param rowTag
   */
  public void setFeatureTag(String rowTag)
  {
    this.featureTag = rowTag;
  }
  
  public void setIdColumn(String gmlIdColumn)
  {
    this.gmlIdColumn = gmlIdColumn;
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
  /*
  public void setTableAttr(String tableAttr)
  {
    this.tableAttr = tableAttr;
  }
    */
  
  public void execute(Scope scope) throws Exception {
    writer = getWriter();
    xmlWriter = new XmlDataWriter(writer);
    
    xmlWriter.prolog();
    if (comment != null)
      xmlWriter.comment(comment);
    
    String tableAttr = GML_NAMESPACE_ATTR;
    if (userNamespacePrefix != null && userNamespaceURI != null) 
    	tableAttr += " " + "xmlns:" + userNamespacePrefix +"=" + "\"" + userNamespaceURI + "\"";
    
    // write table
    int n = writeStartElements(featureCollectionTag, tableAttr);
    write(tbl);
    writeEndElements(n);
    writer.close();
  }

  private int writeStartElements(String tag, String attr)
  throws IOException
  {
    String[] tags = tag.split(TBL_ROW_TAGS_SEPARATOR);
    for (int i = 0; i < tags.length; i++) {
      if (i == 0)
        xmlWriter.elementStart(tags[0], attr);
      else
        xmlWriter.elementStart(tags[i]);
    }
    return tags.length;
  }
  
  private void writeEndElements(int n)
  throws IOException
  {
    for (int i = 0; i < n; i++) {
      xmlWriter.elementEnd();
    }
  }
  protected void write(Table tbl) throws Exception 
  {
    schema = tbl.getRows().getSchema();
    RowIterator rs = tbl.getRows().iterator();
    while (true) {
      Row row = rs.next();
      if (row == null)
        break;
      
      String fidAttr = null;
      Object fidVal = getFidValue(row);
      if (fidVal != null) fidAttr = "fid=\"" + fidVal + "\"";
      
      //int n = writeStartElements(featureTag, null);
      xmlWriter.elementStart(featureMemberTag);
      xmlWriter.elementStart(featureTag, fidAttr);
      writeRow(writer, row);
      //writeEndElements(n);
      xmlWriter.elementEnd();
      xmlWriter.elementEnd();
    }
  }

  private String getFidValue(Row row)
  {
  	return RowUtil.getString(schema, gmlIdColumn, row);
  }
  
  private void writeRow(Writer writer, Row row) throws IOException {
    for (int i = 0; i < row.size(); i++) {
      String itemTag = schema.getName(i);
      
      // skip id value
      if (itemTag.equals(gmlIdColumn)) continue;
      
      String nsItemTag = addNamespace(itemTag, userNamespacePrefix);
      
      Object val = row.getValue(i);
      if (val instanceof String) {
        xmlWriter.elementWithData(nsItemTag, (String) val);
      }
      else if (val instanceof Geometry) {
        // indent geometry output
        xmlWriter.elementStart(nsItemTag);
        xmlWriter.markup(geometryRep((Geometry) val), true);
        xmlWriter.elementEnd();
        
//        xmlWriter.elementWithDataRaw(itemTag, geometryRep((Geometry) val)));        
      }
      else {
        // no need to encode non-string values -> faster
        xmlWriter.elementWithDataRaw(nsItemTag, val.toString());
      }
      // TODO: better formatting for data items (eg Geometry, dates) ??
    }
  }

  private String geometryRep(Geometry g)
  {
      return gmlWriter.write(g);
  }
  
  private String addNamespace(String tag, String ns)
  {
  	if (tag.contains(":") || ns == null) return tag;
  	return ns + ":" + tag;
  }
}