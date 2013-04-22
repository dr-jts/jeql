package jeql.workbench.ui.data;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import jeql.workbench.BaseToolBar;
import jeql.workbench.Workbench;

public class DataToolBar extends BaseToolBar
{
  private DataPanel dataPanel;
  
  public DataToolBar(DataPanel dataPanel)
  {
    super(VERTICAL);
    init();
    this.dataPanel = dataPanel;
  }

  private void init()
  {
    setFloatable(false);

    //Component strut1 = Box.createHorizontalStrut(8);
    addToggleButton("show_chars.png", "Show non-printable chars", new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean showSpaces = e.getStateChange() == ItemEvent.SELECTED;
        dataPanel.showSpaces(showSpaces);
        dataPanel.update();
      }
    });
    
    addButton("World.png", "Show Geometry", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().viewGeoms(dataPanel.getCurrentItem());
      }
    });
    
    /*
    // removed, since always showing text in monospace seems like a simpler strategy
    addToggleButton("change_font.png", "Change font", new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean showMonospaced = e.getStateChange() != ItemEvent.SELECTED;
        dataPanel.showMonospaced(showMonospaced);
        dataPanel.update();
      }
    });
    */
  }

}
