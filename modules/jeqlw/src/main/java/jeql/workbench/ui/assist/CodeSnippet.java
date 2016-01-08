package jeql.workbench.ui.assist;

public class CodeSnippet
{
  public static CodeSnippet doc(String displayText) {
    return new CodeSnippet(displayText, null, null, null);
  }
  
  public static CodeSnippet doc(String displayText, String doc) {
    return new CodeSnippet(displayText, doc, null, null);
  }
  
  public static CodeSnippet code(String code) {
    return new CodeSnippet(code, code, code, null);
  }
  
  public static CodeSnippet code(String displayText, String code) {
    return new CodeSnippet(displayText, null, code, null);
  }
  
  public static CodeSnippet code(String displayText, String doc, String code) {
    return new CodeSnippet(displayText, doc, code, null);
  }
  
  public static CodeSnippet code2(String displayText, String code1, String code2) {
    return new CodeSnippet(displayText, null, code1, code2);
  }
  
  public static CodeSnippet code2(String displayText, String doc, String code1, String code2) {
    return new CodeSnippet(displayText, doc, code1, code2);
  }
  
  String displayText;
  String codeText1;
  String codeText2 = "";
  private String doc;
  
  /*
  public CodeSnippet(String displayText, String codeText)
  {
    this.displayText = displayText;
    this.codeText1 = codeText;
  }
  
  
  public CodeSnippet(String text)
  {
    this.displayText = text;
    this.codeText1 = text;
  }
  */
  public CodeSnippet(String displayText, String doc, String codeText1, String codeText2)
  {
    this.displayText = displayText;
    this.doc = doc;
    this.codeText1 = codeText1 != null ? codeText1 : "";
    this.codeText2 = codeText2 != null ? codeText2 : "";
  }

  
  public String getDisplayText()
  {
    return displayText;
  }
  
  public String getDoc()
  {
    return doc;
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
    return codeText2 != null && codeText2.length() > 0;
  }
  
  public String toString()
  {
    return displayText;
  }
  
}
