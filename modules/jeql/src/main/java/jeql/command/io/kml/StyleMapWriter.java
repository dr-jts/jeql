package jeql.command.io.kml;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jeql.api.error.MissingColumnException;
import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.table.Table;

class StyleMapWriter
{
  private Map styleMapMap = new HashMap(); 
  
  public StyleMapWriter(Table styleTbl)
  {
    init(styleTbl);
  }
  
  private static String STYLE_TABLE_MISSING_COL_SUFFIX = "in style table";
  
  private void init(Table styleTbl)
  {
    // extract style maps, if any
    if (styleTbl == null)
      return;
    
    RowIterator it = styleTbl.getRows().iterator();
    RowSchema schema = it.getSchema();
    
    int styleMapIdIndex = schema.getColIndexIgnoreCase(KMLCol.KML_STYLE_STYLEMAP_ID);
    if (styleMapIdIndex < 0)
      return;     // no stylemaps defined
    
    int styleMapKeyIndex = schema.getColIndexIgnoreCase(KMLCol.KML_STYLE_STYLEMAP_KEY);
    if (styleMapKeyIndex < 0)
      throw new MissingColumnException(KMLCol.KML_STYLE_STYLEMAP_KEY, STYLE_TABLE_MISSING_COL_SUFFIX);
   
    int styleIdIndex = schema.getColIndexIgnoreCase(KMLCol.KML_ID);
    if (styleIdIndex < 0)
      throw new MissingColumnException(KMLCol.KML_ID, STYLE_TABLE_MISSING_COL_SUFFIX);
      
    while (true) {
      Row row = it.next();
      if (row == null)
        break;
      String styleMapId = (String) row.getValue(styleMapIdIndex);
      if (styleMapId == null)
        continue;
      
      String key = (String) row.getValue(styleMapKeyIndex);
      String styleId = (String) row.getValue(styleIdIndex);
      
      add(styleMapId, key, styleId);
    }
  }
  
  private void add(String styleMapId, String key, String id)
  {
    KMLStyleMap sm = (KMLStyleMap) styleMapMap.get(styleMapId);
    if (sm == null) {
      sm = new KMLStyleMap();
      styleMapMap.put(styleMapId, sm);
    }
    sm.addStyle(key, id);
  }
  
  public void write(PrintWriter writer)
  {
    for (Iterator i = styleMapMap.keySet().iterator(); i.hasNext(); ) {
      String styleMapId = (String) i.next();
      KMLStyleMap sm = (KMLStyleMap) styleMapMap.get(styleMapId);
      sm.write(writer, styleMapId);
    }
  }
}
