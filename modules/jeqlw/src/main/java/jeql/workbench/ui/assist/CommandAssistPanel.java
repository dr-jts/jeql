package jeql.workbench.ui.assist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import jeql.engine.CommandInvoker;
import jeql.engine.CommandRegistry;
import jeql.engine.EngineContext;
import jeql.man.CommandParamMethod;
import jeql.man.CommandUtil;
import jeql.workbench.Workbench;

public class CommandAssistPanel extends JPanel
{  
  private DefaultListModel cmdListModel = new DefaultListModel();
  private JList cmdList = new JList(cmdListModel);
  private DefaultListModel paramListModel = new DefaultListModel();
  private JList paramList = new JList(paramListModel);

  public CommandAssistPanel()
  {
    try {
      initUI();
      populateCmdList();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    //setSize(100, 250);
    cmdList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    cmdList.setSelectionBackground(Color.GRAY);
    cmdList.setSize(100, 250);
    cmdList.setBorder(new EmptyBorder(2, 2, 2, 2));

    cmdList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        populateParamList();
        if (e.getClickCount() == 2) {
          Workbench.controller().insertCodeSnippet((CodeSnippet) cmdList.getSelectedValue());
        }
      }
    });
    
    JScrollPane cmdScrollPane1 = new JScrollPane();
    cmdScrollPane1.getViewport().add(cmdList, null);
    
    //---------  Function list
    
    paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    paramList.setSelectionBackground(Color.GRAY);
    paramList.setSize(100, 250);
    paramList.setBorder(new EmptyBorder(2, 2, 2, 2));

    paramList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          Workbench.controller().insertCodeSnippet((CodeSnippet) paramList.getSelectedValue());
        }
      }
    });
    
    JScrollPane paramScrollPane = new JScrollPane();
    paramScrollPane.getViewport().add(paramList, null);

    setLayout(new GridLayout(1,2));
    setPreferredSize(new Dimension(150, 250));
    add(cmdScrollPane1);
    add(paramScrollPane);

  }
  
  private String getCode()
  {
    CodeSnippet snip = (CodeSnippet) paramList.getSelectedValue();
    return snip.getCode();
  }
  
  public void populateCmdList()
  {
    cmdListModel.clear();
    
    CommandRegistry reg = EngineContext.getInstance().geCommandRegistry();
    Collection cmdNames = reg.getCommandNames();
    
    for (Iterator i = cmdNames.iterator(); i.hasNext(); ) {
      String name = (String) i.next();
      cmdListModel.addElement(new CodeSnippet(name, name + " ", ";"));
      //cmdListModel.addElement(new CodeSnippet(name, name+ " "));
      //writer.writeCommand(name, ManUtil.manDescription(invoker.getCommandClass()));
    }

  }

  private void populateParamList()
  {
    String moduleName = cmdList.getSelectedValue().toString();
    populateParamList(moduleName);
  }
  
  private void populateParamList(String cmdName)
  {
    paramListModel.clear();

    CommandInvoker invoker = EngineContext.getInstance().geCommandRegistry().getCommand(cmdName);

    Map params = CommandUtil.getParameters(invoker.getCommandClass());
    
    // output default first
    if (params.containsKey(CommandInvoker.DEFAULT_METHOD_NAME)) {
      //System.out.println(PARAM_INDENT + params.get(CommandInvoker.DEFAULT_METHOD_NAME));
      CommandParamMethod param = (CommandParamMethod) params.get(CommandInvoker.DEFAULT_METHOD_NAME);
      paramListModel.addElement(new CodeSnippet("<default>", ""));
      /*
      writer.writeCommandParam(param.getIOTag(), param.getDisplayName(), 
          param.getArgTypeList(), param.getDescription());
          */
    }
    for (Iterator i = params.keySet().iterator(); i.hasNext(); ) {
      String paramName = (String) i.next();
      if (paramName.equals(CommandInvoker.DEFAULT_METHOD_NAME))
        continue;
      CommandParamMethod param = (CommandParamMethod) params.get(paramName);
      String name = param.getDisplayName() + ": ";
      paramListModel.addElement(new CodeSnippet(name, name));

      /*
      writer.writeCommandParam(param.getIOTag(), param.getDisplayName(), 
          param.getArgTypeList(), param.getDescription());
          */  
    }
  }


}
