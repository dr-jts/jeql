package jeql.command.io.kml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.stax.WstxInputFactory;


public class KMLObjectReader 
{
  static final int STATE_DOC = 1;
  static final int STATE_STYLE = 2;
  static final int STATE_FOLDER = 3;
  static final int STATE_PLACEMARK = 4;
  
  private WstxInputFactory fact;
  private XMLStreamReader xmlRdr;
  private PlacemarkParser placemarkParser;
  private int parseState = STATE_DOC;
  private String currentComposite = null;
  private NameStack folderStack = new NameStack("/");
  
  private DocumentModel model;
  
  public KMLObjectReader() {
    super();
  }
  
  public void open(Reader inStr)
  throws Exception
  {
    fact = new WstxInputFactory();
    xmlRdr =  fact.createXMLStreamReader(inStr);
    model = new DocumentModel();
    placemarkParser = new PlacemarkParser(xmlRdr);
    
    parsePrologue();
  }
  
  private void parsePrologue()
  throws XMLStreamException
  {
    DocumentParser docParser = new DocumentParser(xmlRdr);
    docParser.parse(model);
//    System.out.println("---- name: " + model.getName());
//    System.out.println("---- desc: " + model.getDescription()); 
    
    StyleParser styleParser = new StyleParser(xmlRdr);
    styleParser.parse(model);
  }

  public DocumentModel getModel()
  { 
    return model;
  }
  
  private static boolean isComposite(String tag)
  {
    if (tag.length() <= 0) return false;
    return Character.isUpperCase(tag.charAt(0));
  }
  
  /**
   * Returns next Placemark record
   * @return
   * @throws XMLStreamException
   */
  public Placemark next()
  throws XMLStreamException
  {
    String locName = null;
    while (xmlRdr.hasNext()) {
      int event = xmlRdr.getEventType();
      if (event == XMLStreamConstants.START_ELEMENT) {
        locName = xmlRdr.getLocalName();
        if (isComposite(locName))
          currentComposite = locName;
        
        if (locName.equals(KMLConstants.FOLDER)) {
          parseState = STATE_FOLDER;
        }
        else if (locName.equals(KMLConstants.DOCUMENT)) {
          parseState = STATE_DOC;
        }
        // this is a bit cheesy, but at least is fairly robust to schema change
        else if (currentComposite.equals(KMLConstants.FOLDER) 
            && locName.equals(KMLConstants.NAME)) {
          String name = XMLParseUtil.parseValue(xmlRdr);
//          System.out.println(">>>> Folder name = " + name);
          folderStack.push(name);
        }
        else if (locName.equals(KMLConstants.PLACEMARK)) {
          parseState = STATE_PLACEMARK;
          // at this point we can parse an entire Placemark
          Placemark pm = placemarkParser.parse(model);
          pm.setFolder(folderStack.getNameList());
          return pm;
        }
        
        // otherwise just print tag
//        System.out.println(">>>> " + locName);
      }
      else if (event == XMLStreamConstants.END_ELEMENT) {
        locName = xmlRdr.getLocalName();
//        System.out.println("/" + locName);
        if (locName.equals(KMLConstants.FOLDER)) {
          folderStack.pop();;
        }
        xmlRdr.next();
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
  
  public void close()
  {
    try {
      xmlRdr.close();
    }
    catch (XMLStreamException e) {
      // eat it - nothing we can do
    }
  }
}
