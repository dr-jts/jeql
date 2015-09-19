package jeql.command.io.kml;

import java.io.PrintWriter;

import jeql.api.row.Row;
import jeql.command.io.xml.XmlDataWriter;
import jeql.util.StringUtil;

public class XMLWriter
{

  public static void writeElement(PrintWriter writer,
      int indent, String tagName, String val)
  {
    if (val == null) return;
    
    writer.print(StringUtil.stringOf(' ', indent));
    writer.print("<");
    writer.print(tagName);
    writer.print(">");
    writer.print(val.toString());
    
    writer.print("</");
    writer.print(tagName);
    writer.println(">");
  }

  public static void writeElementNonEmpty(PrintWriter writer,
      int indent, String tagName, String val)
  {
    if (val == null) return;
    if (val.length() <= 0) return;
    writeElement(writer, indent, tagName, val);
  }

  public static String xmlEncode(String val)
  {
    if (! XmlDataWriter.hasReservedTextChars(val))
      return val;
  
    // if text contains CDATA terminator, it must be encoded
    String encText = val.replaceAll("]]>", "]]&gt;");
    return "<![CDATA[" + encText + "]]>";
  }

  static String indentStr(int indent)
  {
    return StringUtil.stringOf(' ', indent);
  }

  public static void writeElement(PrintWriter writer,
      int indent, String tagName, String valStr, boolean encode)
  {
    // don't output if null
    if (valStr == null) return;
    
    writer.print(StringUtil.stringOf(' ', indent));
    writer.print("<");
    writer.print(tagName);
    writer.print(">");
  
    if (encode)
      writer.print(xmlEncode(valStr.toString()));
    else 
      writer.print(valStr.toString());
     
    writer.print("</");
    writer.print(tagName);
    writer.println(">");
  }

  public static void writeElement(PrintWriter writer,
      int indent, String tagName, int colIndex, 
      Row row, String defaultVal, boolean encode)
  {
    String valStr;
    
    // use default if column is missing
    if (colIndex < 0) {
      valStr = defaultVal;
    }
    else {
      valStr = row.getValue(colIndex).toString();
    }
    writeElement(writer, indent, tagName, valStr, encode);
  }

}
