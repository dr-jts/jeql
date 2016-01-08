package jeql.workbench.ui.assist;


public class RegExAssistPanel extends Column2AssistPanel
{  

  public RegExAssistPanel(CodeAssistPanel codeAssistPanel)
  {
    super(codeAssistPanel);
  }

  public void populateList()
  {
    add(CodeSnippet.doc("Characters"));
    add(CodeSnippet.code("x", "The character x", "x"));
    add(CodeSnippet.code("\\\\", "The backslash character", "\\\\"));
    add(CodeSnippet.code("\\0n", "The character with octal value 0n (0 <= n <= 7)", "\\0n"));
    add(CodeSnippet.code("\\0nn", "The character with octal value 0nn (0 <= n <= 7)", "\\0nn"));
    add(CodeSnippet.code("\\0mnn", "The character with octal value 0mnn (0 <= m <= 3, 0 <= n <= 7)", "\\0mnn"));
  }
}
