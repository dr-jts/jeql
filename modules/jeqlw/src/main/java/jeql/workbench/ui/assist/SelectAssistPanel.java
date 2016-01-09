package jeql.workbench.ui.assist;


public class SelectAssistPanel extends Column2AssistPanel
{  

  public SelectAssistPanel(CodeAssistPanel codeAssistPanel)
  {
    super(codeAssistPanel);
}

  public void populateList()
  {
    add("select * ");
    add("select distinct * ");
    add("select * from _");
    add("select * from _ where _");
    add("select * from _ join _ on _");
    add("select * from _ join _ on _ where _");
    add("select * from _ join _ on _ where _ group by _");
    add("select * from _ join _ on _ where _ order by _");
    add("select * let <assign1>, <assign2> from _ where _");
    add("select splitIndex, splitValue from _ split by _ where _");
    add("let _ ");
    add("from _ ");
    add("join _ on _ ");
    add("split by _ ");
    add("where _ ");
    add("group by _ ");
    add("order by _ ");
    add("limit 1");
    add("limit 1 offset 1");

  }

}
