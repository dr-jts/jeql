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
  private XMLStreamReader rdr;
  private NameStack pathStk;
  private String value;
private Object prevStart;
private String prevAttr;
  
  public XMLStackReader(Reader inRdr)
  throws Exception
  {
    WstxInputFactory fact = new WstxInputFactory();
    this.rdr =  fact.createXMLStreamReader(inRdr);
    //model = new DocumentModel();
    pathStk = new NameStack("/");
  }
  
  /**
   * Returns next XML Atom
   * @return
   * @throws XMLStreamException
   */
  public Atom next()
  throws XMLStreamException
  {
	if (rdr == null) return null;
    //String name = null;
    //prevStart = null;
	/*
    if (endPath != null) {
    	String save = endPath;
    	endPath = null;
    	return new Atom("/" + save);
    }
    */
    while (rdr.hasNext()) {
      int event = rdr.getEventType();
      //System.out.println(event);
      if (event == XMLStreamConstants.START_ELEMENT) {
        String name = rdr.getLocalName();
        String attr = XMLParseUtil.readAttributes(rdr);
        boolean isNewElement = prevStart != null && ! name.equals(prevStart);
        Atom atom = null;
        if (isNewElement) {
        	atom = new Atom(pathStk.getNameList(), prevAttr, null);
        }
        prevStart = name;
        prevAttr = attr;
        value = null;
        pathStk.push(name);
        rdr.next();
        if (atom != null) return atom;
      }
      else if (event == XMLStreamConstants.CHARACTERS) {
        value = rdr.getText();
        rdr.next();
        continue;
      }
      else if (event == XMLStreamConstants.END_ELEMENT) {
        String name = rdr.getLocalName();
//        System.out.println("/" + locName);
//        value = "***" + locName;
        String startPath = pathStk.getNameList();
        pathStk.pop();
        rdr.next();
        boolean isLeaf = name.equals(prevStart);
        if (isLeaf) {
        	prevStart = null;
        	// can return value now
        	return new Atom(startPath, prevAttr, value);
        }
        else {
        	return new Atom("/" + startPath, null, null);
        }
      }
      else if (event == XMLStreamConstants.END_DOCUMENT) {
    	rdr.close();
    	rdr = null;
        return null;
      }
      else {
    	  rdr.next();
      }
    }
    return null;
  }

//public String path() { return pathStk.getNameList(); }
  
  //public String attr() { return attr; }
  //public String value() { return value; }

  public void close()
  {
    try {
      rdr.close();
    }
    catch (XMLStreamException e) {
    }
  }

  public static class Atom
  {
	  String path;
	  String attr;
	  String value;

	public Atom(String path) {
		  this(path, "", null);
	  }
	public Atom(String path, String attr, String value)
	{
		this.path = path;
		this.attr = attr;
		this.value = value;
	}
  }

}
