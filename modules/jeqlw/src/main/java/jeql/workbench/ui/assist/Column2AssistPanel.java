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

public abstract class Column2AssistPanel extends JPanel
{  
  private DefaultListModel listModel = new DefaultListModel();
  private JList list = new JList(listModel);

  public Column2AssistPanel()
  {
    try {
      initUI();
      listModel.clear();
      populateList();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    //setSize(100, 250);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectionBackground(Color.GRAY);
    list.setSize(100, 250);
    list.setBorder(new EmptyBorder(2, 2, 2, 2));

    MouseListener mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          Workbench.controller().insertCodeSnippet((CodeSnippet) list.getSelectedValue());
        }
      }
    };
    list.addMouseListener(mouseListener);
    
    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.getViewport().add(list, null);
    
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(150, 250));
    add(jScrollPane1, BorderLayout.CENTER);
  }
  
  public abstract void populateList();
  
  protected void add(CodeSnippet codeSnip)
  {
    listModel.addElement(codeSnip);
  }

}
