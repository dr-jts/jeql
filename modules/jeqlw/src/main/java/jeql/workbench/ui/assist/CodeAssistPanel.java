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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import jeql.engine.EngineContext;
import jeql.engine.FunctionRegistry;
import jeql.workbench.WorkbenchSettings;
import jeql.workbench.model.ScriptFile;

public class CodeAssistPanel extends JPanel
{  
  JTabbedPane tabPane = new JTabbedPane();
  JTextArea docText;
  
  public CodeAssistPanel()
  {
    try {
      initUI();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI() throws Exception
  {
    //-----------  Doc panel
    JPanel docPanel = new JPanel();
    JTextArea docText = new JTextArea();
    docText.setFont(new java.awt.Font("Monospaced", 0, 12));
    docText.setEditable(false);
    docText.setBackground(WorkbenchSettings.HELP_BACKGROUND_CLR);

    docPanel.setLayout(new BorderLayout());
    docPanel.add(new JScrollPane(docText), BorderLayout.CENTER);

    tabPane.setTabPlacement(JTabbedPane.LEFT);
    
    CommandAssistPanel cmdPanel = new CommandAssistPanel();
    tabPane.add(cmdPanel, "Commands");
    FunctionAssistPanel funcPanel = new FunctionAssistPanel();
    tabPane.add(funcPanel, "Functions");
    SelectAssistPanel selectPanel = new SelectAssistPanel();
    tabPane.add(selectPanel, "Select");
    OperatorAssistPanel opPanel = new OperatorAssistPanel();
    tabPane.add(opPanel, "Operators");
    RegExAssistPanel rePanel = new RegExAssistPanel();
    tabPane.add(rePanel, "RegEx");
    
    // Main split pane
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.add(tabPane, JSplitPane.LEFT);
    splitPane.add(docPanel, JSplitPane.RIGHT);
    splitPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    splitPane.setResizeWeight(0.6);

    //----------  Main panel
    setMinimumSize(new Dimension(400, 50));
    setPreferredSize(new Dimension(900, 300));
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }
  




}
