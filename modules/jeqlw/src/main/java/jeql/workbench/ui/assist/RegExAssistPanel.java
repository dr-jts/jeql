package jeql.workbench.ui.assist;


public class RegExAssistPanel extends Column2AssistPanel
{  

  public RegExAssistPanel()
  {
    super();
  }

  public void populateList()
  {
    add(new CodeSnippet("Characters", ""));
    add(new CodeSnippet("x -  The character x", "x"));
    add(new CodeSnippet("\\\\ - The backslash character", "\\\\"));
    add(new CodeSnippet("\\0n -  The character with octal value 0n (0 <= n <= 7)", "\\0n"));
    add(new CodeSnippet("\\0nn - The character with octal value 0nn (0 <= n <= 7)", "\\0nn"));
    add(new CodeSnippet("\\0mnn - The character with octal value 0mnn (0 <= m <= 3, 0 <= n <= 7)", "\\0mnn"));
  }
}
