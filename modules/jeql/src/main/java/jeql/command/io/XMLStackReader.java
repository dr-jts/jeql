package jeql.command.io;

import java.io.Reader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ctc.wstx.stax.WstxInputFactory;

public class XMLStackReader
{
  private XMLStreamReader rdr;
  private NameStack pathStk;
  private String value;
private Object prevStart;
private String prevAttr;
private boolean includeAllEndElements;
  private Atom atomBuffer = null;
  
  public XMLStackReader(Reader inRdr, boolean includeAllEndElements)
  throws Exception
  {
    WstxInputFactory fact = new WstxInputFactory();
    this.rdr =  fact.createXMLStreamReader(inRdr);
    //model = new DocumentModel();
    pathStk = new NameStack("/");
    this.includeAllEndElements = includeAllEndElements;
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
      
      if (atomBuffer != null) {
        Atom atom = atomBuffer;
        atomBuffer = null;
        return atom;
      }
      
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
        Atom leaf = null;
        if (isLeaf) {
        	prevStart = null;
        	// can return value now
        	leaf = new Atom(startPath, prevAttr, value);
        }
        if (! isLeaf || includeAllEndElements) {
          Atom endElt = new Atom("/" + startPath, null, null);
          if (leaf != null) {
            atomBuffer = endElt;
            return leaf;
          }
          else {
            atomBuffer = null;
            return endElt;
          }
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
