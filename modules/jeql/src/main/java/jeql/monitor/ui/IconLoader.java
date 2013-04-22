package jeql.monitor.ui;

import javax.swing.ImageIcon;

public class IconLoader
{
  public static ImageIcon MAGNIFY_CURSOR = new ImageIcon(IconLoader.class.getResource("MagnifyCursor.png"));
  public static ImageIcon SCRIPT_SMALL = new ImageIcon(IconLoader.class.getResource("script_small.png"));
  public static ImageIcon INSPECT_TEXT = new ImageIcon(IconLoader.class.getResource("show_chars_glasses.png"));
  
  public static ImageIcon createIcon(String file)
  {
    return new ImageIcon(IconLoader.class.getResource(file));
  }

}
