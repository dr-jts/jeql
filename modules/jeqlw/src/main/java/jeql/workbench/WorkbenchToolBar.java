package jeql.workbench;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;

public class WorkbenchToolBar extends BaseToolBar
{
  JButton addScriptBtn;
  JButton delScriptBtn;
  JButton saveScriptBtn;
  JButton runBtn;
  JButton stopBtn;
  JButton pauseBtn;
  
  public WorkbenchToolBar()
  {
    super();
    init();
  }

  private void init()
  {
    setFloatable(false);

    //Component strut1 = Box.createHorizontalStrut(8);
    addScriptBtn = addButton("script_add.png", "Add Script", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().scriptAdd();
      }
    });
    addButton("script_copy.png", "Copy Script", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().scriptCopy();
      }
    });
    saveScriptBtn = addButton("script_save.png", "Save Script", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().scriptSave(  );
      }
    });
    //---------------------------------
    add(Box.createHorizontalStrut(16));
    delScriptBtn = addButton("script_delete.png", "Close Script", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().scriptClose();
      }
    });
    //---------------------------------
    add(Box.createHorizontalStrut(16));
    runBtn = addButton("run.png", "Load and Run", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().run();
      }
    });
    runBtn = addButton("debug.png", "Load and Debug", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().debug();
      }
    });
    pauseBtn = addButton("pause.png", "Pause", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().pause();
      }
    });
    pauseBtn.setEnabled(false);
    stopBtn = addButton("stop.png", "Stop", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().stop();
      }
    });
    stopBtn.setEnabled(false);
    saveScriptBtn.setEnabled(false);
    
    
    Component strut1 = Box.createHorizontalStrut(16);
    add(strut1);
    
    addButton("help.png", "Help", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().help();
      }
    });
    /*
    addButton("help.png", "Geometry View", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().geomView();
      }
    });
    */
    
    /*
     // Don't show Settings button until there is something to do
    // right-aligned buttons
    add(Box.createHorizontalGlue());
    
    addButton("settings.png", "Settings", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Workbench.controller().displaySettings();
      }
    });
    */
  }

}
