package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vividsolutions.jts.util.Memory;

import jeql.engine.EngineContext;
import jeql.monitor.MonitorModel;
import jeql.monitor.ui.MonitorItemsPanel;
import jeql.monitor.ui.MonitorStatusBar;
import jeql.util.SwingUtil;
import jeql.workbench.images.IconLoader;
import jeql.workbench.model.ScriptSource;
import jeql.workbench.model.WorkbenchModel;
import jeql.workbench.ui.assist.CodeAssistPanel;
import jeql.workbench.ui.assist.CodeSnippet;
import jeql.workbench.ui.data.DataPanel;
import jeql.workbench.ui.data.RowListDataPanel;
import jeql.workbench.util.TextAreaWriter;

public class WorkbenchFrame extends JFrame
{
  SettingsDialog settingsDlg = new SettingsDialog();

  // --------- UI components
  WorkbenchToolBar toolbar = new WorkbenchToolBar();

  JPanel mainPanel = new JPanel();
  ScriptListPanel scriptListPanel = new ScriptListPanel();
   
  JTabbedPane topTabPane = new JTabbedPane();
  JTabbedPane dataTabPane = new JTabbedPane();
  
  //JPanel scriptPanel = new JPanel();
  //JEditorPane scriptText = new JEditorPane();

  JPanel outputPanel = new JPanel();
  JTextArea outputText = new JTextArea();

  JPanel errorPanel = new JPanel();
  JTextArea errorText = new JTextArea();

  JPanel monitorPanel = new JPanel();

  JSplitPane mainSplitPane = new JSplitPane();
  JSplitPane filesSplitPane = new JSplitPane();

  MonitorItemsPanel monitorItemsPanel = new MonitorItemsPanel();

  DataPanel monitorDataPanel = new DataPanel();
  MonitorStatusBar monitorStatusBar = new MonitorStatusBar();

  HelpFrame helpDlg = new HelpFrame(this);
  GeometryViewFrame geomView = new GeometryViewFrame(this);

  private JFileChooser scriptFileChooser;

  // --------- Models

  MonitorModel monitorModel;


  public WorkbenchFrame()
  {
    // super((Frame) null, MON_TITLE, false);
    super(WorkbenchConstants.FRAME_TITLE);
    try {
      initUI();
      
      // capture output streams
      EngineContext.OUTPUT_WRITER = new PrintWriter(new TextAreaWriter(outputText));
      EngineContext.ERROR_WRITER = new PrintWriter(new TextAreaWriter(errorText));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setVisible(boolean visible)
  {
    pack();
    pack();
    SwingUtil.centerOnScreen(this);
    super.setVisible(visible);
  }
  
  private void initUI() throws Exception
  {        
    //-----------  Output panel
    outputText.setFont(new java.awt.Font("Monospaced", 0, 12));
    outputText.setEditable(false);

    outputPanel.setLayout(new BorderLayout());
    outputPanel.add(new JScrollPane(outputText), BorderLayout.CENTER);
    
    //-----------  Errors panel
    errorText.setFont(new java.awt.Font("Monospaced", 0, 12));
    errorText.setEditable(false);

    errorPanel.setLayout(new BorderLayout());
    errorPanel.add(new JScrollPane(errorText), BorderLayout.CENTER);
    
    CodeAssistPanel codeAssistPanel = new CodeAssistPanel();
    //-----------  Main panel

    //topTabPane.add(scriptPanel, "Script");
    scriptAdd();
    
    monitorPanel = new JPanel();
    monitorPanel.setLayout(new BorderLayout());
    monitorPanel.add(monitorItemsPanel, BorderLayout.CENTER);
    monitorPanel.add(monitorStatusBar, BorderLayout.SOUTH);
    
    dataTabPane.add(codeAssistPanel, "Code Assist");
    dataTabPane.add(outputPanel, "Output");
    dataTabPane.add(errorPanel, "Errors");
    dataTabPane.add(monitorPanel, "Monitor");
    dataTabPane.add(monitorDataPanel, "Inspect");

    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    mainSplitPane.add(topTabPane, JSplitPane.TOP);
    mainSplitPane.add(dataTabPane, JSplitPane.BOTTOM);
    mainSplitPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    mainSplitPane.setResizeWeight(0.5);
    //mainSplitPane.setDividerLocation(100);
    
    filesSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    filesSplitPane.add(scriptListPanel, JSplitPane.LEFT);
    filesSplitPane.add(mainSplitPane, JSplitPane.RIGHT);
    filesSplitPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    filesSplitPane.setResizeWeight(0);

    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(filesSplitPane, BorderLayout.CENTER);
//    mainPanel.add(monitorStatusBar, BorderLayout.SOUTH);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(toolbar, BorderLayout.NORTH);
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    
    //===============================================
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(true);
    
    addWindowFocusListener(new WindowFocusListener() {
      public void windowGainedFocus(WindowEvent arg0)
      {
        // refresh file list
        Workbench.controller().updateFileList();
      }

      public void windowLostFocus(WindowEvent arg0)
      {
        // do nothing
      }
    }); 
 }

  private void initFileChooser() {
    if (scriptFileChooser == null) {
      scriptFileChooser = new JFileChooser();
      scriptFileChooser.addChoosableFileFilter(SwingUtil.createFileFilter("JEQL script (*.jql)", "jql"));
      scriptFileChooser.setDialogTitle("Save Script");
      //pngFileChooser.setSelectedFile(new File("geoms.png"));
      scriptFileChooser.setCurrentDirectory(new File("."));
    }
  }

  public GeometryViewFrame geomView()
  {
    return geomView;
  }

  private void setViewTitle(String scriptFile)
  {
    // don't bother for now
    //setTitle(FRAME_TITLE + " - " + scriptFile);
  }
  
  private void setScriptFile(String scriptFile)
  {
    String tabTitle = "Script";
    if (scriptFile.length() > 0)
      tabTitle += " - " + scriptFile;
    /*
    topTabPane.setTitleAt(topTabPane.indexOfComponent(scriptPanel), 
        tabTitle);
        */
  }
  
  public synchronized void updateMonitor()
  {
    monitorItemsPanel.update();
    monitorDataPanel.update();
    monitorStatusBar.update();
  }

  public synchronized void updateScript()
  {
    String scriptFile = Workbench.model().getScriptFile();
    setViewTitle(scriptFile);
    setScriptFile(scriptFile);
    scriptListPanel.populateList();
  }

  public void scriptAdd()
  {
    scriptAdd(null);
  }
  
  /**
   * 
   * @param text the text of the new script (may be null)
   */
  public void scriptAdd(String text)
  {
    ScriptSource src = ScriptSource.createInternal(WorkbenchModel.getNewScriptName());
    if (text != null) {
      src.setText(text);
      src.setModified(true);
    }

    ScriptPanel scriptPanel = new ScriptPanel(true, src);
    scriptPanel.setText(text);
    
    scriptPanelAdd(scriptPanel);
    topTabPane.setSelectedIndex(topTabPane.getTabCount()-1);
    scriptUpdateModified();
  }
  
  public void scriptLoad(ScriptSource src)
  {
    ScriptPanel scriptPanel = new ScriptPanel(true, src);
    scriptPanel.loadText();
    
    scriptPanelAdd(scriptPanel);
    topTabPane.setSelectedIndex(topTabPane.getTabCount() - 1);
  }
  
  private void scriptPanelAdd(ScriptPanel scriptPanel)
  {
    topTabPane.addTab(scriptPanel.getTitle(), scriptPanel.getIcon(),  scriptPanel);
  }
  
  public void scriptClose()
  {
    if (getCurrentScriptPanel().getSource().isModified()) {
      if (! SwingUtil.confirmAction(this, "Confirm Close", "Script has been modified.  Abandon changes?"))
        return;
    }
    topTabPane.remove(getCurrentScriptPanel());
  }

  public void scriptSave()
  {
    if (!getCurrentScriptSource().hasFilename()) {
      initFileChooser();
      String file = SwingUtil.chooseFilenameWithConfirm(this, scriptFileChooser);
      // aborted for some reason
      if (file == null)
        return;
      getCurrentScriptSource().setFilename(file);
    }

    getCurrentScriptPanel().save();
    scriptUpdateModified();
  }

  public void scriptUpdateModified()
  {
    // add modified indicator to tab
    topTabPane.setTitleAt(topTabPane.getSelectedIndex(), 
        getCurrentScriptPanel().getSource().getTitle());
    // enable saving
    toolbar.saveScriptBtn.setEnabled( 
        getCurrentScriptPanel().getSource().isModified()); 
  }
  
  public void inspectText(String text)
  {
    ScriptSource src = ScriptSource.createInternal(WorkbenchConstants.INSPECT_TAB_NAME);
    if (text != null) {
      src.setText(text);
    }

    ScriptPanel scriptPanel = new ScriptPanel(false, src);
    scriptPanel.setText(text);
    
    addOrUpdateScript(scriptPanel);
  }
  
  private void addOrUpdateScript(ScriptPanel panel)
  {
    String title = panel.getSource().getTitle();
    int index = topTabPane.indexOfTab(title);
    if (index == -1) {
      scriptPanelAdd(panel);
      topTabPane.setSelectedIndex(topTabPane.getTabCount()-1);
    }
    else {
      topTabPane.setComponentAt(index, panel);
      topTabPane.setSelectedIndex(index);
    }
  }
  
  public void showTab(int index)
  {
    topTabPane.setSelectedIndex(index);
  }
  
  public int scriptTabIndex(String title)
  {
    int nTab = topTabPane.getTabCount();
    for (int i = 0; i < nTab; i++) {
      String tabTitle = stripStar(topTabPane.getTitleAt(i));
      if (tabTitle.equals(title)) {
        return i;
      }
    }
    return -1;
  }
  
  public static String stripStar(String s)
  {
    if (s.charAt(s.length() - 1) != '*') return s;
    return s.substring(0, s.length() -1);
  }
  public void reportScriptError(String errMsg)
  {
    //topTabPane.setSelectedComponent(scriptPanel);
    dataTabPane.setSelectedComponent(errorPanel);
    setTabColor(errorPanel, WorkbenchConstants.TAB_CLR_ERROR);
    errorText.setForeground(WorkbenchConstants.TAB_CLR_ERROR);
    errorText.setText(errMsg);
  }
  
  public void reportOutput()
  {
    if (outputText.getText().length() > 0) {
      setTabColor(outputPanel, WorkbenchConstants.TAB_CLR_OUTPUT);
    }
  }
  
  public void clearOutput()
  {
    outputText.setText("");
    errorText.setText("");
    setTabColor(errorPanel, null);
    setTabColor(outputPanel, null);
  }
  
  /**
   * 
   * @param clr color to set to; may be null to revert to std color
   */
  private void setTabColor(Component panel, Color clr)
  {
    int index = dataTabPane.indexOfComponent(panel);
    dataTabPane.setForegroundAt(index, clr);
  }
  
  public String getSelectedScriptFile()
  {
    return scriptListPanel.getSelected().toString();
  }
  
  public boolean isInternalScript()
  {
    return getCurrentScriptPanel().isInternal();
  }
  
  public ScriptPanel getCurrentScriptPanel()
  {
    return (ScriptPanel) topTabPane.getSelectedComponent();
  }
  
  public ScriptSource getCurrentScriptSource()
  {
    return getCurrentScriptPanel().getSource();
  }
  
  public String getCurrentScriptText()
  {
    return getCurrentScriptPanel().getText();
  }
  
  public void run(MonitorModel model)
  {
    this.monitorModel = model;
    
    String scriptFile = Workbench.model().getScriptFile();
    updateScript();
    
    dataTabPane.setSelectedComponent(monitorPanel);
    setTabColor(monitorPanel, WorkbenchConstants.TAB_CLR_RUN);
    
    monitorDataPanel.clear();
    monitorItemsPanel.setModel(model);
    monitorDataPanel.setModel(model);
    monitorStatusBar.setModel(model);
    monitorStatusBar.setScript(scriptFile);
    
    toolbar.runBtn.setEnabled(false);
    toolbar.pauseBtn.setEnabled(true);
    toolbar.stopBtn.setEnabled(true);
  }

  public void pause()
  {
    toolbar.runBtn.setEnabled(true);
    toolbar.pauseBtn.setEnabled(false);
    toolbar.stopBtn.setEnabled(true);
  }

  public void stop()
  {
    toolbar.runBtn.setEnabled(true);
    toolbar.pauseBtn.setEnabled(false);
    toolbar.stopBtn.setEnabled(false);
    monitorItemsPanel.end();
    setTabColor(monitorPanel, null);

  }

  public static void main(String[] args)
  {
    // put up a test dialog
    WorkbenchFrame d = new WorkbenchFrame();
    d.setVisible(true);
    System.exit(0);
  }

  public void insertCodeSnippet(CodeSnippet code)
  {
    getCurrentScriptPanel().insertCodeSnippet(code);
  }

  public void scriptCaretUpdate(int line, int col)
  {
    //System.out.println(line + ", " + col);
  }

}
