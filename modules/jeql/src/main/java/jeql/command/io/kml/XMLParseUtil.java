package jeql.command.io.kml;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jeql.util.StringUtil;

public class XMLParseUtil 
{

  public static void skipToStart(XMLStreamReader xmlRdr, String elementName)
  throws XMLStreamException 
  {
    while (xmlRdr.hasNext()) {
        if (xmlRdr.isStartElement()
          && xmlRdr.getLocalName().equals(elementName)) {
          break;
        }
     xmlRdr.next(); 
    }
  }
  
  public static void consumeStart(XMLStreamReader xmlRdr, String elementName)
  throws XMLStreamException 
  {
    while (xmlRdr.hasNext()) {
      if (xmlRdr.isStartElement() && xmlRdr.getLocalName().equals(elementName)) {
        xmlRdr.next();        
        return;
      }
      xmlRdr.next();
    }
    throw new IllegalStateException("expected start tag <" + elementName
         + ">, found '" + xmlRdr.getText() + "'");
  }
  
  public static void consumeEnd(XMLStreamReader xmlRdr, String elementName)
  throws XMLStreamException 
  {
    while (xmlRdr.hasNext()) {
      if (xmlRdr.isEndElement() && xmlRdr.getLocalName().equals(elementName)) {
        xmlRdr.next();        
        return;
      }
      xmlRdr.next();
    }
    throw new IllegalStateException("expected start tag <" + elementName
         + ">, found '" + xmlRdr.getText() + "'");
  }
  
  public static String readElement(XMLStreamReader xmlRdr)
      throws XMLStreamException {
    StringBuffer buf = new StringBuffer();
    String elementName = null;
    if (xmlRdr.hasNext() && xmlRdr.isStartElement()) {
      elementName = xmlRdr.getLocalName();
    }
    // not a start elt - skip it
    if (elementName == null)
      return "";

    while (xmlRdr.hasNext()) {
      String val = "";
      if (xmlRdr.isStartElement()) {
        buf.append("<" + xmlRdr.getLocalName());
        buf.append(readAttributes(xmlRdr));
        buf.append(">");
      }
      if (xmlRdr.getEventType() == XMLStreamConstants.CHARACTERS) {
        String txt = xmlRdr.getText();
        boolean isWhite = txt == null || StringUtil.isWhitespace(txt);
        if (! isWhite)
          buf.append(txt);
      } 
      if (xmlRdr.isEndElement()) {
        buf.append("</" + xmlRdr.getLocalName() + ">");
      }
      if (xmlRdr.isEndElement() && xmlRdr.getLocalName().equals(elementName)) {
        xmlRdr.next();
        break;
      }
      xmlRdr.next();
    }
    return buf.toString();
  }
  
  private static String readAttributes(XMLStreamReader xmlRdr) {
    StringBuffer buf = new StringBuffer();
    int attrCnt = xmlRdr.getAttributeCount();
    for (int i = 0; i < attrCnt; i++) {
      String name = xmlRdr.getAttributeLocalName(i);
      String value = xmlRdr.getAttributeValue(i);
      buf.append(" " + name + '=' + "'" + value + "'");
    }
    return buf.toString();
  }

  /*
  public static void OLDconsumeStart(XMLStreamReader xmlRdr, String elementName)
  throws XMLStreamException 
  {
    String locName = null;
    if (xmlRdr.isStartElement()) {
      locName = xmlRdr.getLocalName();
      if (locName.equals(elementName)) {
        xmlRdr.next();
        return;
      }
    }
    throw new IllegalStateException("expected start tag <" + elementName
         + ">, found '" + xmlRdr.getText() + "'");
  }
  
  public static void OLDconsumeEnd(XMLStreamReader xmlRdr, String elementName)
  throws XMLStreamException 
  {
    String locName = null;
    if (xmlRdr.isEndElement()) {
      locName = xmlRdr.getLocalName();
      if (locName.equals(elementName)) {
        xmlRdr.next();
        return;
      }
    }
    throw new IllegalStateException("expected end tag: " + elementName
        + ", found " + locName);
  }
  */
  
  public static String parseValue(XMLStreamReader xmlRdr, String elementName) 
  throws XMLStreamException 
  {
    String val = null;
    while (true) {
      xmlRdr.next();
      int event2 = xmlRdr.getEventType();
      if (event2 == XMLStreamConstants.CHARACTERS) {
        val = xmlRdr.getText();
      } else if (event2 == XMLStreamConstants.END_ELEMENT
          && xmlRdr.getLocalName().equals(elementName)) {
        return val;
      }
    }
  }

  public static String parseValue(XMLStreamReader xmlRdr) 
  throws XMLStreamException 
  {
    // consume start tag
    xmlRdr.next();
    String val = null;

    if (xmlRdr.getEventType() == XMLStreamConstants.CHARACTERS) {
        val = xmlRdr.getText();
        xmlRdr.next();
    } 
    skipToEndElement(xmlRdr);
    return val;
  }

  public static void skipToEndElement(XMLStreamReader xmlRdr)
  throws XMLStreamException 
  {
    do {
      if (xmlRdr.getEventType() == XMLStreamConstants.END_ELEMENT) {
        break;
      }
      xmlRdr.next();
    } while (xmlRdr.hasNext());
  }
  
  public static boolean isStartElement(XMLStreamReader xmlRdr, String tagName)
  {
    //return isElement(xmlRdr, XMLStreamConstants.START_ELEMENT, tagName);

    return xmlRdr.isStartElement() 
      && xmlRdr.getLocalName().equals(tagName);
  }
  
  public static boolean isEndElement(XMLStreamReader xmlRdr, String tagName)
  {
    return isElement(xmlRdr, XMLStreamConstants.END_ELEMENT, tagName);
  }
  
  public static boolean isElement(XMLStreamReader xmlRdr, int eventType, String tagName)
  {
    int event = xmlRdr.getEventType();
    if (event == eventType) {
      String locName = xmlRdr.getLocalName();
      if (locName.equals(tagName)) 
        return true;
    }
    return false;
  }
  
  public static boolean matches(String s, String[] pattern)
  {
    for (int i = 0; i < pattern.length; i++) {
      if (s.equalsIgnoreCase(pattern[i]))
        return true;
    }
    return false;
  }
}
