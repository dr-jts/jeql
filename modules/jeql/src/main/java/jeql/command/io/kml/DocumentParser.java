package jeql.command.io.kml;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jeql.command.io.xml.XMLParseUtil;

public class DocumentParser {

  private XMLStreamReader xmlRdr;
  
  private String[] stopSet = new String[] { "Style", "Folder", "Placemark" };
  
  public DocumentParser(XMLStreamReader xmlRdr) {
    this.xmlRdr = xmlRdr;
  }

  public void parse(DocumentModel model)
  throws XMLStreamException
  {
    while (xmlRdr.hasNext()) {
      int event = xmlRdr.getEventType();
      if (event == XMLStreamConstants.START_ELEMENT) {
        String locName = xmlRdr.getLocalName();
        if (locName.equals("name")) {
          model.setName(XMLParseUtil.parseValue(xmlRdr)); //, "name"));
        }
        else if (locName.equals("description")) {
          model.setDescription(XMLParseUtil.parseValue(xmlRdr));
        }
        else if (XMLParseUtil.matches(locName, stopSet))
          return;
      }
      if (! xmlRdr.hasNext())
        return;
      xmlRdr.next();
    }
  }
  
  

}
