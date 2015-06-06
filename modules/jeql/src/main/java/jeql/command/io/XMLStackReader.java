package jeql.command.io;

import java.io.Reader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jeql.command.io.kml.DocumentModel;

import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.stax.WstxInputFactory;

public class XMLStackReader
{
  private DocumentModel model;
  private XMLStreamReader xmlRdr;
  private NameStack path;
  private String value;
  private String attr;
  
  public XMLStackReader(Reader rdr)
  throws Exception
  {
    WstxInputFactory fact = new WstxInputFactory();
    xmlRdr =  fact.createXMLStreamReader(rdr);
    model = new DocumentModel();
    path = new NameStack("/");
  }
  
  /**
   * Returns next Placemark record
   * @return
   * @throws XMLStreamException
   */
  public String next()
  throws XMLStreamException
  {
    String name = null;
    String lastStart = null;
    while (xmlRdr.hasNext()) {
      int event = xmlRdr.getEventType();
      if (event == XMLStreamConstants.START_ELEMENT) {
        name = xmlRdr.getLocalName();
        lastStart = name;
        value = null;
        attr = XMLParseUtil.readAttributes(xmlRdr);
        path.push(name);
      }
      else if (event == XMLStreamConstants.CHARACTERS) {
        value = xmlRdr.getText();
      }
      else if (event == XMLStreamConstants.END_ELEMENT) {
        name = xmlRdr.getLocalName();
        boolean isLeaf = name.equals(lastStart);
//        System.out.println("/" + locName);
//        value = "***" + locName;
        String p = path.getNameList();
        path.pop();
        xmlRdr.next();
        if (isLeaf) return p;
      }
      else if (event == XMLStreamConstants.END_DOCUMENT) {
        break;
      }
      
      // consume the event
      try {
        xmlRdr.next();
      }
      catch (WstxIOException e) {
        // can't do much about this, so carry on
        e.printStackTrace();
        throw e;
      }
    }
    return null;
  }
  
  public String path() { return path.getNameList(); }
  
  public String attr() { return attr; }
  public String value() { return value; }

  public void close()
  {
    try {
      xmlRdr.close();
    }
    catch (XMLStreamException e) {
    }
  }

  

}
