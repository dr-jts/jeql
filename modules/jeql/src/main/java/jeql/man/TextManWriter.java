package jeql.man;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import jeql.util.StringUtil;

public class TextManWriter
{
  private static final int MAX_RET_TYPE_WIDTH = 20;
  private static final String PARAM_INDENT = "  ";

  private PrintWriter writer;
  
  public TextManWriter(Writer writer)
  {
    this.writer = new PrintWriter(writer);
  }
  
  public TextManWriter()
  {
    this.writer = new PrintWriter(System.out);
  }
  
  void writeTitle(String title)
  {
    writer.println();
    writer.println("========== " + title + " ==========");
  }

  private String funcDescription;
  private int paramIndex;
  
  public void writeFunctionModule(String moduleName)
  {
    writer.println();
  }
  public void writeFunction(String retType, String name, String description)
  {
    writer.print(
        StringUtil.padLeft(retType, " ", MAX_RET_TYPE_WIDTH) 
            + " "); 
    writer.print(name + "( ");
    paramIndex = 0;
    funcDescription = description;
  }
  
  public void writeFunctionParam(String type, String name)
  {
    if (paramIndex > 0) {
      writer.print(", ");
    }
    paramIndex++;
    writer.print(type);
    if (name != null && name.length() >0) writer.print(" " + name);

  }
  
  public void writeFunctionClose()
  {
    writer.print( " )" );
    if (funcDescription.length() > 0)
      writer.print(" - " + funcDescription);
    writer.println();
  }

  public void writeCommand(String name, String description)
  {
    writer.println();
    writer.print(name);
    if (description.length() > 0)
      writer.print(" - " + description);
    writer.println();

  }
  
  public void writeCommandParam(CommandParamMethod param)
  {
    writer.print(PARAM_INDENT);
    writer.println(param);
  }

}
