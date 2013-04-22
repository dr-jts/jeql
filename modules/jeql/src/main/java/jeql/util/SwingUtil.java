package jeql.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

public class SwingUtil 
{



  /**
   * Centers the component on the screen
   */
  public static void centerOnScreen(Component comp) {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      comp.setLocation(
          (screenSize.width - comp.getWidth()) / 2,
          (screenSize.height - comp.getHeight()) / 2);
  }

  public static JButton createIconButton(ImageIcon icon, 
      int dimension, String tooltipText,
      java.awt.event.ActionListener action)
  {
    JButton btn = new JButton();
    btn.setToolTipText(tooltipText);
    btn.setIcon(icon);

    btn.setMaximumSize(new Dimension(dimension, dimension));
    btn.setMinimumSize(new Dimension(dimension, dimension));
    btn.setPreferredSize(new Dimension(dimension, dimension));
    btn.setMargin(new Insets(0, 0, 0, 0));

    if (action != null) btn.addActionListener(action);

    return btn;
  }

  public static JToggleButton createToggleButton(ImageIcon icon, 
      int dimension, String tooltipText,
      java.awt.event.ActionListener action)
  {
    JToggleButton btn = createToggleButton(icon, dimension, tooltipText);
    if (action != null) btn.addActionListener(action);
    return btn;
  }

  public static JToggleButton createToggleButton(ImageIcon icon, 
      int dimension, String tooltipText,
      java.awt.event.ItemListener action)
  {
    JToggleButton btn = createToggleButton(icon, dimension, tooltipText);
    if (action != null) btn.addItemListener(action);
    return btn;
  }

  public static JToggleButton createToggleButton(ImageIcon icon, 
      int dimension, String tooltipText)
  {
    JToggleButton btn = new JToggleButton();
    btn.setToolTipText(tooltipText);
    btn.setIcon(icon);

    btn.setMaximumSize(new Dimension(dimension, dimension));
    btn.setMinimumSize(new Dimension(dimension, dimension));
    btn.setPreferredSize(new Dimension(dimension, dimension));
    btn.setMargin(new Insets(0, 0, 0, 0));

    return btn;
  }

  public static ImageIcon createIcon(Object context, String file)
  {
    return new ImageIcon(context.getClass().getResource(file));
  }

  public static  FileFilter PNG_FILE_FILTER = createFileFilter("PNG File (*.png)", ".png");

  /**
   * 
   * Example usage:
   * <pre>
   * SwingUtil.createFileFilter("JEQL script (*.jql)", "jql")
   * </pre>
   * @param description
   * @param extension
   * @return
   */
  public static FileFilter createFileFilter(final String description, String extension)
  {
    final String dotExt = extension.startsWith(".") ? extension : "." + extension;
    FileFilter ff =  new FileFilter() {
      public String getDescription() {
        return description;
      }
      public boolean accept(File f) {
        return f.isDirectory() || f.toString().toLowerCase().endsWith(dotExt);
      }
    };
    return ff;
  }
  
  public static String chooseFilenameWithConfirm(Component comp, JFileChooser fileChooser) {
    try {
      if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(comp)) {
        File file = fileChooser.getSelectedFile();
        if (! SwingUtil.confirmOverwrite(comp, file)) return null;
        String fullFileName = fileChooser.getSelectedFile().toString();
        return fullFileName;
      }
    }
    catch (Exception x) {
      SwingUtil.reportException(comp, x);
    }
    return null;
  }

  public static boolean confirmOverwrite(Component comp, File file)
  {
    if (file.exists()) {
      return confirmAction(comp, "Confirmation",
          file.getName() + " exists. Overwrite?");
    }
    return true;
  }

  public static boolean confirmAction(Component comp, String title, String msg)
  {
      int decision = JOptionPane.showConfirmDialog(comp, msg, 
          title, JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (decision == JOptionPane.NO_OPTION) {
        return false;
      }
    return true;
  }

  public static void reportException(Component c, Exception e) {
    JOptionPane.showMessageDialog(c, StringUtil.wrap(e.toString(), 80), "Exception",
        JOptionPane.ERROR_MESSAGE);
    e.printStackTrace(System.out);
  }

}
