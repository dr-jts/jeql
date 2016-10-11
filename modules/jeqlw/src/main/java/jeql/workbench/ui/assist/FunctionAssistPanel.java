package jeql.workbench.ui.assist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.man.ManUtil;
import jeql.workbench.Workbench;

public class FunctionAssistPanel extends JPanel
{  
  private DefaultListModel modListModel = new DefaultListModel();
  private JList modList = new JList(modListModel);
  private DefaultListModel funcListModel = new DefaultListModel();
  private JList funcList = new JList(funcListModel);
  private CodeAssistPanel codeAssistPanel;

  public FunctionAssistPanel(CodeAssistPanel codeAssistPanel)
  {
    this.codeAssistPanel = codeAssistPanel;
    try {
      initUI();
      populateModuleList();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    //--------- Module list
    modList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    modList.setSelectionBackground(Color.GRAY);
    modList.setSize(100, 250);
    modList.setBorder(new EmptyBorder(2, 2, 2, 2));

    modList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        populateFunctionList();
        codeAssistPanel.showDoc("");
        if (e.getClickCount() == 2) {
          // TODO: display doc
          //Workbench.controller().insertCode(getCode());
        }
        
      }
    });
    
    JScrollPane modScrollPane1 = new JScrollPane();
    modScrollPane1.getViewport().add(modList, null);
    
    //---------  Function list
    
    funcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    funcList.setSelectionBackground(Color.GRAY);
    funcList.setSize(100, 250);
    funcList.setBorder(new EmptyBorder(2, 2, 2, 2));

    funcList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        CodeSnippet snip = CommandAssistPanel.getCodeSnip(funcList);
        codeAssistPanel.showDoc(snip.getDoc());
        if (e.getClickCount() == 2) {
          Workbench.controller().insertCodeSnippet(snip);
        }
      }
    });
    
    JScrollPane funcScrollPane = new JScrollPane();
    funcScrollPane.getViewport().add(funcList, null);
    
    setLayout(new GridLayout(1,2));
    setPreferredSize(new Dimension(150, 250));
    add(modScrollPane1);
    add(funcScrollPane);
  }
  
  private void populateFunctionList()
  {
    String moduleName = (String) modList.getSelectedValue();
    if (moduleName == AGG_FUNS) {
      populateAggFunctionList();
    }
    else if (moduleName == STREAM_FUNS) {
      populateStreamFunctionList();
    }
    else {
      populateFunctionList(moduleName);
    }
  }
  
  private String getCode()
  {
    CodeSnippet snip = (CodeSnippet) funcList.getSelectedValue();
    return snip.getCode();
  }
    
  private static final String AGG_FUNS = "Aggregate Functions";
  private static final String STREAM_FUNS = "Stream Functions";
  
  /**
   * List is built dynamically to allow for imports
   */
  public void populateModuleList()
  {
    modListModel.clear();
    
    // add entry for aggregate functions
    modListModel.addElement(AGG_FUNS);
    
    // add entry for aggregate functions
    modListModel.addElement(STREAM_FUNS);
    
    FunctionRegistry reg = EngineContext.getInstance().getFunctionRegistry();
    Collection funcNames = reg.getFunctionNames();
    String currModule = "";
    for (Iterator i = funcNames.iterator(); i.hasNext(); ) {
      String name = (String) i.next();
      
      // add module names
      String module = FunctionRegistry.moduleName(name);
      // only add module name once
      if (! module.equals(currModule)) {
        currModule = module;
        modListModel.addElement(module);
      }
    }
  }
  
  public void populateFunctionList(String moduleName)
  {
    funcListModel.clear();

    // TODO: make this more efficient
    FunctionRegistry reg = EngineContext.getInstance().getFunctionRegistry();
    for (String fname : reg.getFunctionNames() ) {
      // add module names
      String module = FunctionRegistry.moduleName(fname);
      // only add module name once
      if (! module.equals(moduleName)) continue;
        
      // add functions
      for (Method meth :  reg.getFunctionMethods(fname) ) {
        funcListModel.addElement( CodeSnippet.code2(
        		
            FunctionRegistry.functionName(fname) 
            + " ( " + ManUtil.paramTypeList(meth) + " )"
            + " -> " + ManUtil.resultType(meth), 
            
            FunctionRegistry.functionName(fname) + "\n\n" + ManUtil.functionDoc(meth),
            
            fname + "( ", " )"
            ));
      }
    }
  }
  public void populateAggFunctionList()
  {
    funcListModel.clear();

    // TODO: make this more efficient
    FunctionRegistry reg = EngineContext.getInstance().getFunctionRegistry();
    for (String fname : reg.getFunctionNames() ) {
      // add module names
      String module = FunctionRegistry.moduleName(fname);
      // only add aggregate functions
      if (module != "") continue;
        
      // add functions
      for (Method meth :  reg.getFunctionMethods(fname) ) {
        funcListModel.addElement(CodeSnippet.code2(
            FunctionRegistry.functionName(fname) +" ( " + ManUtil.paramTypeList(meth) + " )", 
            fname + "( ", " )"));
      }
    }
  }
  public void populateStreamFunctionList()
  {
    funcListModel.clear();

    funcListModel.addElement(CodeSnippet.code2(
        "counter ( boolean, ... )", 
        "Returns a count value, which increments when all condition values are true.", 
        "counter( ", " )"));
    funcListModel.addElement(CodeSnippet.code2(
        "index ( value )", 
        "Returns an index value, which increments when the input value changes.", 
        "index( ", " )"));
    funcListModel.addElement(CodeSnippet.code2(
        "keep ( value, boolean [ , init-value ] )", 
        "Returns the input value from the last call where condition was true.  First call returns init-value or null.",
        "keep (", " )"));
    funcListModel.addElement(CodeSnippet.code2(
        "prev ( value [ , init-value ] )", 
        "Returns the input value from the previous row.  First call returns init-value or null",
        "prev( ", " )" ));
    funcListModel.addElement(CodeSnippet.code2(
        "rownum ()", 
        "Returns the current row number in the stream.",
        "rownm( "," )" ));

  }
  /*
  public void OLDpopulateAggFunctionList()
  {
    funcListModel.clear();
    funcListModel.addElement(aggSnippet("first"));
  }

  public static CodeSnippet aggSnippet(String aggFunName)
  {
    return new CodeSnippet(
        aggFunName +" ( )"
        + " -> ", 
        aggFunName+ "( ", " )");
  }
  */

}
