package jeql.command.io.kml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jeql.command.io.XMLParseUtil;

public class StyleParser {

  private XMLStreamReader xmlRdr;
  
  public StyleParser(XMLStreamReader xmlRdr) {
    this.xmlRdr = xmlRdr;
  }
  
  public void parse(DocumentModel model)
  throws XMLStreamException
  {
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isStartElement(xmlRdr, "Style")) {      
          parseStyle();
          continue;
      }
      return;
    }
  }
  
  private void parseStyle()
  throws XMLStreamException
  {
    // for now just skip over style contents
    while (xmlRdr.hasNext()) {
      if (XMLParseUtil.isEndElement(xmlRdr, "Style")) {      
          xmlRdr.next();
          return;
        }
//      System.out.println("---- in style: " + xmlRdr.); {
      xmlRdr.next();
    }
  }

}
