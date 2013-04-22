package jeql.command.io.kml;

import java.io.PrintWriter;

import jeql.api.error.IllegalValueException;

class KMLStyleMap
{
  private String normalStyleUrl;
  private String highlightStyleUrl;
  
  public KMLStyleMap()
  {
    
  }
  
  public void addStyle(String key, String id)
  {
    if (key.equalsIgnoreCase(KMLCol.KML_NORMAL)) {
      normalStyleUrl = id;
      return;
    }
    if (key.equalsIgnoreCase(KMLCol.KML_HIGHLIGHT)) {
      highlightStyleUrl = id;
      return;  
    }
    throw new IllegalValueException("KML style map key", key);
  }
  
  public void write(PrintWriter writer, String mapId)
  {
    writer.println("<StyleMap id='" + mapId + "'>");
    
    if (normalStyleUrl != null) {
      writer.println("  <Pair>");
      writer.println("    <key>normal</key>");
      writer.println("    <styleUrl>#" + normalStyleUrl + "</styleUrl>");
      writer.println("  </Pair>");
    }
    if (highlightStyleUrl != null) {
      writer.println("  <Pair>");
      writer.println("    <key>highlight</key>");
      writer.println("    <styleUrl>#" + highlightStyleUrl + "</styleUrl>");
      writer.println("  </Pair>");
    }
    writer.println("</StyleMap>");
    writer.println();
  }
}
