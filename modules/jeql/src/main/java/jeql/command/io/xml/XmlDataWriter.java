package jeql.command.io.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import jeql.util.SystemUtil;

/**
 * An adapter which supports 
 * writing data-structured XML documents.
 * Data-structured means that elements are not interleaved
 * with text; 
 * or equivalently that text entities
 * occur only at leaves in the XML DOM tree;
 * or equivalently that no text occurs between two start tags or two end tags.
 * <p>
 * Features of the writer include:
 * <ul>
 * <li>emission of correct end tags
 * <li>indenting
 * <li>generation of CDATA escaping, with CDATA end tags escaped also
 * <li>comments
 * </ul>
 * 
 * @author Martin Davis
 *
 */
public class XmlDataWriter 
{
  private Writer writer;
  private boolean documentStarted = false;
  private Stack elementStack = new Stack();
  private int indentLevel = 0;
  private static final String INDENT_STR = "  ";
  
  public XmlDataWriter(Writer writer) {
    this.writer = writer;
  }

  public Writer getWriter()
  {
    return writer;
  }
  
  private void indentPush() { indentLevel++; }
  private void indentPop() 
  { 
    if (indentLevel > 0)
      indentLevel -= 1;
  }
  
  private void writeIndent()
    throws IOException
  {
    for (int i = 0; i < indentLevel; i++) {
      writer.write(INDENT_STR);
    }
  }
  
  public int getIndentLevel() { return indentLevel; }
  
  public void prolog()
    throws IOException
  {
    if (documentStarted)
      throw new IllegalStateException("XML declaration must occur at start of document");
    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.write(SystemUtil.LINE_TERM); 
    documentStarted = true;
  }
  
  public void comment(String comment) throws IOException {
    comment(comment, true);
  }

  public void comment(String comment, boolean indent) throws IOException {
    if (indent)
      writeIndent();
    writer.write("<!-- ");
    writer.write(comment);
    writer.write(" -->");
    writer.write(SystemUtil.LINE_TERM);
  }

  public void elementStart(String tagName) throws IOException 
  {
    elementStart(tagName, null);
  }

  public void elementStart(String tagName, String attributes) throws IOException 
  {
    writeIndent();
    writeStartTag(tagName, attributes);
    writer.write(SystemUtil.LINE_TERM);
    elementStack.push(tagName);
    indentPush();
  }

  /**
   * Writes end tag for current element
   * 
   * @throws IOException
   */
  public void elementEnd() throws IOException 
  {
    indentPop();
    writeIndent();
    writeEndTag((String) elementStack.pop());
  }
  
  /**
   * Writes end tags for elements nested n deep.
   * 
   * @param n
   * @throws IOException
   */
  public void elementEnd(int n) throws IOException 
  {
    for (int i = 0; i < n; i++) {
      elementEnd();
    }
  }
  
  /**
   * Writes an element containing a data string, without XML-encoding it.
   * The data should be known to not contain any XML reserved characters.
   * This is more efficient, since it avoids the translation of these characters.
   * 
   * @param tagName
   * @param value
   * @throws IOException
   */
  public void elementWithDataRaw(String tagName, String data) throws IOException 
  {
    elementWithDataRaw(tagName, null, data);
  }

  /**
   * Writes an element containing a data string, without XML-encoding it.
   * The data should be known to not contain any XML reserved characters.
   * This is more efficient, since it avoids the translation of these characters.
   * 
   * @param tagName
   * @param value
   * @throws IOException
   */
  public void elementWithDataRaw(String tagName, String attributes, String data) throws IOException 
  {
    writeIndent();
    
    writeStartTag(tagName, attributes);
    writer.write(data);
    writeEndTag(tagName);
  }

  /**
   * Writes an element containing a data string, 
   * XML-encoding the string if necessary.
   * 
   * @param tagName
   * @param value
   * @throws IOException
   */
  public void elementWithData(String tagName, String value) throws IOException 
  {
    elementWithData(tagName, null, value);
  }

  /**
   * Writes an element with the given attributes and containing a data string, 
   * XML-encoding the string if necessary.
   * 
   * @param tagName
   * @param attributes
   * @param value
   * @throws IOException
   */
  public void elementWithData(String tagName, String attributes, String value) throws IOException 
  {
    writeIndent();
    writeStartTag(tagName, attributes);    
    writeEncoded(value);
    writeEndTag(tagName);
  }

  /**
   * Writes markup, with optional indenting.
   * Markup is assumed to be correctly XML-encoded.
   * 
   * @param markup
   * @param indent
   * @throws IOException
   */
  public void markup(String markup, boolean indent)
  throws IOException 
  {
    int start = 0;
    int len = markup.length();
    while (start < len) {
      int end = start;
      // write indent, if chars remaining
      if (end < len) {
        writeIndent();
      }
      // find next EOL, or EOS
      while (end < len && markup.charAt(end) != '\n') {
        end++;
      }
      // include EOL in written block
      if (end < len && markup.charAt(end) == '\n')
        end++;
      // write the text block (if any)
      if (end > start) {
        writer.write(markup, start, end - start);
      }
      start = end;
    }
  }
  
  private void writeStartTag(String tagName, String attributes) 
  throws IOException 
  {
    writer.write("<");
    writer.write(tagName);
    if (attributes != null) {
      writer.write(" ");
      writer.write(attributes);
    }
    writer.write(">");
  }
  
  private void writeEndTag(String tagName) 
  throws IOException 
  {
    writer.write("</");
    writer.write(tagName);
    writer.write(">");
    writer.write(SystemUtil.LINE_TERM);
  }
  
  private void writeEncoded(String text)
  throws IOException 
  {
    if (hasReservedTextChars(text)) {
      writeCdata(text);
    }
    else 
      writer.write(text);
  }
  
  private void writeCdata(String text)
  throws IOException 
  {
    // if text contains CDATA terminator, it must be encoded
    String encText = text.replaceAll("]]>", "]]&gt;");
    writer.write("<![CDATA[");
    writer.write(encText);
    writer.write("]]>");
  }
  
  /**
   * Tests whether a string contains any characters which
   * are reserved characters in XML text content.
   * These include '&amp;', '&lt;' and '&gt;'
   * These characters must be escaped to be represented correctly
   * in a valid XML document.
   * 
   * @param text
   * @return true if the input contains any reserved characters
   */
  public static boolean hasReservedTextChars(String text)
  {
    return text.indexOf('&') >= 0 
      || text.indexOf('<') >= 0
      || text.indexOf('>') >= 0;
  }
  
  /**
   * Closes this writer and outputs any non-ended element tags.
   * Does NOT close the underlying Writer.
   * 
   * @throws IOException
   */
  public void close()
    throws IOException
  {
    while (! elementStack.empty()) {
      elementEnd();
    }
  }
}
