package jeql.workbench.ui.assist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import jeql.engine.CommandInvoker;
import jeql.engine.CommandRegistry;
import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.man.ManUtil;
import jeql.workbench.Workbench;
import jeql.workbench.model.ScriptFile;

public class SelectAssistPanel extends Column2AssistPanel
{  

  public SelectAssistPanel()
  {
    super();
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
