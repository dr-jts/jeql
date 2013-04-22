package jeql.workbench.ui.assist;

public class CodeSnippet
{
  String displayText;
  String codeText1;
  String codeText2 = "";
  
  public CodeSnippet(String displayText, String codeText)
  {
    this.displayText = displayText;
    this.codeText1 = codeText;
  }
  
  public CodeSnippet(String displayText, String codeText1, String codeText2)
  {
    this.displayText = displayText;
    this.codeText1 = codeText1;
    this.codeText2 = codeText2;
  }
  
  public CodeSnippet(String text)
  {
    this.displayText = text;
    this.codeText1 = text;
  }
  
  public String getDisplayText()
  {
    return displayText;
  }
  
  public String getCode()
  {
    return codeText1 + codeText2;
  }
  
  public String getCode1()
  {
    return codeText1;
  }
  
  public String getCode2()
  {
    return codeText2;
  }
  
  public boolean isSplitCode()
  {
    return codeText2.length() > 0;
  }
  
  public String toString()
  {
    return displayText;
  }
  
}
