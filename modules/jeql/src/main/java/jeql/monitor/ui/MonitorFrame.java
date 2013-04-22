package jeql.monitor.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import jeql.monitor.MonitorModel;
import jeql.util.SwingUtil;

public class MonitorFrame 
  extends JFrame
{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  JTextArea text = new JTextArea();
  JLabel lblRowStat = new JLabel();
  JLabel lblTime = new JLabel();
  JLabel lblMem = new JLabel();
  JLabel lblScript = new JLabel();
  JPanel titlePanel = new JPanel();
  JPanel statusBarPanel = new JPanel();
  JSplitPane jSplitPane1 = new JSplitPane();

  MonitorItemsPanel itemsPanel = new MonitorItemsPanel();
  MonitorStatusBar statusBar;
  
  private String currStmt = null;
  int rowCount = 0;
  
  private static final String MON_TITLE = "JEQL Monitor";
  
  public MonitorFrame() {
    //super((Frame) null, MON_TITLE, false);
    super(MON_TITLE);
   try {
        init();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
  }

  public void setVisible(boolean visible) {
    pack();
    pack();
    SwingUtil.centerOnScreen(this);
    super.setVisible(visible);
  }

  private void init()
  throws Exception 
  {    
    statusBar = new MonitorStatusBar();
    //setAlwaysOnTop(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    /*
    text.setMinimumSize(new Dimension(400, 50));
    text.setPreferredSize(new Dimension(900, 600));
    text.setBackground(SystemColor.control);
    
    lblTime.setBorder(BorderFactory.createLoweredBevelBorder());
    lblTime.setPreferredSize(new Dimension(21, 21));
    lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
    lblTime.setBackground(SystemColor.control);
    
    lblMem.setBorder(BorderFactory.createLoweredBevelBorder());
    lblMem.setPreferredSize(new Dimension(21, 21));
    lblMem.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMem.setBackground(SystemColor.control);
    
    lblRowStat.setBackground(SystemColor.control);
    lblRowStat.setBorder(BorderFactory.createLoweredBevelBorder());
    lblRowStat.setBackground(SystemColor.control);
    lblRowStat.setHorizontalAlignment(SwingConstants.RIGHT);
    
    lblScript.setBackground(SystemColor.text);
    //lblScript.setBorder(BorderFactory.createLoweredBevelBorder());
    lblScript.setBackground(SystemColor.control);
    lblScript.setHorizontalAlignment(SwingConstants.LEFT);
    
    statusBarPanel.setLayout(new GridLayout(1,3));
    //statusBarPanel.add(lblScript);
    statusBarPanel.add(lblRowStat);
    statusBarPanel.add(lblMem);
    statusBarPanel.add(lblTime);
    statusBarPanel.setBackground(SystemColor.control);
*/
    titlePanel.setLayout(new GridLayout(1,1));
    titlePanel.add(lblScript);
    titlePanel.setBackground(SystemColor.control);
    //titlePanel.setBorder(BorderFactory.createRaisedBevelBorder());
    
    /*
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.add(itemsPanel, JSplitPane.TOP);
    jSplitPane1.add(dataPanel, JSplitPane.BOTTOM);
    jSplitPane1.setBorder(new EmptyBorder(2,2,2,2));
    jSplitPane1.setResizeWeight(0.5);
    */
    
    mainPanel.setLayout(new BorderLayout());
    //controlPanel.add(text, BorderLayout.CENTER);
    //mainPanel.add(jSplitPane1, BorderLayout.CENTER);
    mainPanel.add(itemsPanel, BorderLayout.CENTER);
    mainPanel.add(statusBar, BorderLayout.SOUTH);
    
    mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    mainPanel.setBackground(SystemColor.control);
    

    this.setResizable(true);
    this.getContentPane().setLayout(new BorderLayout());
    //this.getContentPane().add(titlePanel, BorderLayout.NORTH);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
//    this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
//    this.getContentPane().add(new MonitorToolBar(), BorderLayout.NORTH);
  }
  
  public void setScript(String s)
  {
    if (s == null) return;
    lblScript.setText(s);
    setTitle(MON_TITLE + " - " + s);
  }
  
  public void update()
  {
    itemsPanel.update();
    statusBar.update();
  }

  public void end()
  {
    itemsPanel.end();
    statusBar.update();
  }

  public void setModel(MonitorModel model)
  {
    itemsPanel.setModel(model);
    statusBar.setModel(model);
  }
  
  public void setText(String s)
  {
    text.setText(s);
  }
  
  public void setTime(String s)
  {
    lblTime.setText(s);
  }
  
  public void setMemory(String s)
  {
    lblMem.setText(s);
  }
  
  private DecimalFormat format = new DecimalFormat("#,###");

  public void setRowsPerSec(int rowsPerSec)
  {
    lblRowStat.setText(format.format(rowsPerSec) + " rows / sec");
  }
  
  public static void main(String[] args) 
  {
    // put up a test dialog
    MonitorFrame d = new MonitorFrame();
    d.setVisible(true);
    System.exit(0);
 }

}
