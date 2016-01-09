package jeql.workbench.ui.assist;


public class OperatorAssistPanel extends Column2AssistPanel
{  

  public OperatorAssistPanel(CodeAssistPanel codeAssistPanel)
  {
    super(codeAssistPanel);
  }
  
  public void populateList()
  {
    
    add(CodeSnippet.code("+"));
    add(CodeSnippet.code("-"));
    add(CodeSnippet.code("*"));
    add(CodeSnippet.code("/"));
    add(CodeSnippet.code("&"));
    add(CodeSnippet.code("? _ : _"));
    add(CodeSnippet.code("~"));
    add(CodeSnippet.code("~="));
    add(CodeSnippet.code(">"));
    add(CodeSnippet.code(">="));
    add(CodeSnippet.code("<"));
    add(CodeSnippet.code("<="));
    add(CodeSnippet.code("!="));
    add(CodeSnippet.code("<>"));
    add(CodeSnippet.code("and"));
    add(CodeSnippet.code("or"));
    add(CodeSnippet.code("xor"));
    add(CodeSnippet.code("not"));
    add(CodeSnippet.code("exists"));
    add(CodeSnippet.code("not exists"));
    add(CodeSnippet.code("in ( ... )"));
    add(CodeSnippet.code("not in ( ... )"));
    add(CodeSnippet.code("case _ \nwhen _ then _ \nelse _ end"));
    add(CodeSnippet.code("case \nwhen _ then _ \nelse _ end"));

  }

}
