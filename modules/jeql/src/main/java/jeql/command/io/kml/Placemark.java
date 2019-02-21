package jeql.command.io.kml;

import org.locationtech.jts.geom.Geometry;

public class Placemark {

  String name;
  String description;
  String styleUrl;
  Geometry geom;
  String folder;
  String data;
  String schemaData;
  
  public Placemark() {
    super();
  }

  public void setName(String name)
  {
    this.name = name;
  }
  public void setDescription(String description)
  {
    this.description = description;
  }
  public void setStyleUrl(String url)
  {
    // strip any leading '#'
    if (url != null && url.startsWith("#"))
      url = url.substring(1);
    
    this.styleUrl = url;
  }
  public void setData(String data) {
    this.data = data;
  }
  public void setSchemaData(String data) {
    this.schemaData = data;
  }
  public void setGeometry(Geometry geom)
  {
    this.geom = geom;
  }
  public void setFolder(String folder)
  {
    this.folder = folder;
  }
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("==Placemark==\n");
    add(buf, "folder", folder);
    add(buf, "name", name);
    add(buf, "description", description);
    add(buf, "styleUrl", styleUrl);
    add(buf, "data", data);
    add(buf, "schemaData", data);
    add(buf, "geometry", geom);
    return buf.toString();
  }
  
  private static void add(StringBuffer buf, String name, Object value)
  {
    if (value != null)
      buf.append(name + "=" + value + "\n");
  }

}
