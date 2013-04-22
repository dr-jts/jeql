package jeql.monitor.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import jeql.monitor.Monitor;
import jeql.util.SwingUtil;


public class MonitorToolBar extends BaseToolBar
{
  public MonitorToolBar()
  {
    init();
  }

  private void init()
  {
    setFloatable(false);

    Component strut1 = Box.createHorizontalStrut(8);
    //add(createButton("go.png", "Load and Go"), null);
    //addButton("stop.png", "Stop", null);
    addButton("stop.png", "Stop", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Monitor.end();
      }
    });
  }

  private ImageIcon createIcon(String file)
  {
    return new ImageIcon(getClass().getResource(file));

  }
  private JButton createButton(String iconFile, String tooltip)
  {
    return SwingUtil.createIconButton(createIcon(iconFile), 30, tooltip, null);
  }

}
