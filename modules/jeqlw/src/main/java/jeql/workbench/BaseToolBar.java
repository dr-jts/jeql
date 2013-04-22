package jeql.workbench;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import jeql.util.SwingUtil;
import jeql.workbench.images.IconLoader;

public class BaseToolBar extends JToolBar
{

  public BaseToolBar(int orientation)
  {
    super(orientation);
  }

  public BaseToolBar()
  {
    super();
  }

  protected JButton addButton(String iconFile, String tooltip, java.awt.event.ActionListener action)
  {
    JButton btn = SwingUtil.createIconButton(IconLoader.createIcon(iconFile), 28, tooltip, action);
    add(btn, null);
    return btn;
  }

  protected JToggleButton addToggleButton(String iconFile, String tooltip, java.awt.event.ItemListener action)
  {
    ImageIcon icon = IconLoader.createIcon(iconFile);
    JToggleButton btn = SwingUtil.createToggleButton(icon, 28, tooltip, action);
    add(btn, null);
    return btn;
  }

  protected JToggleButton addToggleButton(String iconFile, String tooltip, java.awt.event.ActionListener action)
  {
    JToggleButton btn = SwingUtil.createToggleButton(IconLoader.createIcon(iconFile), 28, tooltip, action);
    add(btn, null);
    return btn;
  }


}
