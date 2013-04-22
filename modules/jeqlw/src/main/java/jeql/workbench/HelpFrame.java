package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import jeql.engine.EngineContext;
import jeql.man.ManGenerator;

public class HelpFrame extends JFrame
{
  JEditorPane helpText = new JEditorPane();

  String manText;
  
  public HelpFrame(Frame frame)
  {
    super("JEQL Help");
    try {
      initUI();
      pack();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private void initUI()
  {
    //setLayout(new BorderLayout());
    
    helpText.setEditable(false);
    helpText.setBackground(WorkbenchSettings.HELP_BACKGROUND_CLR);
    
    JScrollPane scrollPane = new JScrollPane(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    //scrollPane.getViewport().setBackground(SystemColor.control);
    scrollPane.getViewport().add(helpText);
    add(scrollPane);
    getContentPane().setPreferredSize(new Dimension(600, 600));
  }
  
  public void loadContent()
  {
    if (manText == null) {
      manText = ManGenerator.generate();
      helpText.setText(manText);
      helpText.setCaretPosition(0);
    } 
  }
  
}
