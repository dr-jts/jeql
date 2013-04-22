package jeql.command.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jeql.util.SwingUtil;

public class FlexiDialog 
  extends JDialog
{
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel controlPanel = new JPanel();
  int rowCount = 0;
  private ButtonPanel buttonPanel = null;
  private String buttonSelected;
  
  public FlexiDialog(String title) {
   super((Frame) null, title, true);
   try {
        init();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

  public String getButtonSelected()
  {
    return buttonSelected;
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
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException ex) {
      // eat it
    }

    controlPanel.setLayout(new GridBagLayout());
    
    this.setResizable(true);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(controlPanel, BorderLayout.CENTER);
  }
  
  public void setButtons(String[] buttons)
  {
    buttonPanel = new ButtonPanel(buttons);
    buttonPanel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
          buttonAction();
      }
  });

    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
  }
  
  private void buttonAction()
  {
    buttonSelected = buttonPanel.getSelectedText();
    this.setVisible(false);
    // TODO: allow Help, About actions
  }
  
  public void addRow(
      String fieldName,
      JComponent label,
      JComponent component,
//      EnableCheck[] enableChecks,
      String toolTipText) {
      if (toolTipText != null) {
          label.setToolTipText(toolTipText);
          component.setToolTipText(toolTipText);
      }
      /*
//      fieldNameToLabelMap.put(fieldName, label);
//      fieldNameToComponentMap.put(fieldName, component);
      if (enableChecks != null) {
          addEnableChecks(fieldName, Arrays.asList(enableChecks));
      }
      */
      
      int componentX;
      int componentWidth;
      int labelX;
      int labelWidth;
      if (component instanceof JCheckBox
          || component instanceof JRadioButton
          || component instanceof JLabel
          || component instanceof JPanel) {
          componentX = 1;
          componentWidth = 3;
          labelX = 4;
          labelWidth = 1;
      } else {
          labelX = 1;
          labelWidth = 1;
          componentX = 2;
          componentWidth = 1;
      }
      controlPanel.add(
          label,
          new GridBagConstraints(
              labelX,
              rowCount,
              labelWidth,
              1,
              0.0,
              0.0,
              GridBagConstraints.WEST,
              GridBagConstraints.NONE,
              new Insets(0, 0, 5, 10),
              0,
              0));
      //HORIZONTAL especially needed by separator. [Jon Aquino]
      controlPanel.add(
          component,
          new GridBagConstraints(
              componentX,
              rowCount,
              componentWidth,
              1,
              0,
              0.0,
              GridBagConstraints.WEST,
              component instanceof JPanel
                  ? GridBagConstraints.HORIZONTAL
                  : GridBagConstraints.NONE,
              new Insets(0, 0, 5, 0),
              0,
              0));
      rowCount++;
  }

  public JTextField addTextField(
      String fieldName,
      String initialValue,
      int approxWidthInChars,
 //     EnableCheck[] enableChecks,
      String toolTipText) {
      JTextField textField = new JTextField(initialValue, approxWidthInChars);
      addRow(fieldName, new JLabel(fieldName), textField, toolTipText);
      return textField;
  }

  public JComboBox addComboBox(
      String fieldName,
      String selectedItem,
      String[] items,
      String toolTipText) {
    return addComboBox(fieldName, selectedItem, Arrays.asList(items), toolTipText);
  }

  public JComboBox addComboBox(
      String fieldName,
      Object selectedItem,
      Collection items,
      String toolTipText) {
      JComboBox comboBox = new JComboBox(new Vector(items));
      comboBox.setSelectedItem(selectedItem);
      addRow(fieldName, new JLabel(fieldName), comboBox, toolTipText);
      return comboBox;
  }

  public static void main(String[] args) 
  {
    // put up a test dialog
    FlexiDialog d = new FlexiDialog("Test GUI");
    d.addTextField("Test", "aaaa", 10, "xxxxx");
    d.addTextField("Test wide label", "blarg", 20, "xxxxx");
    d.addComboBox("Test Combo", "two", 
        new String[] { "one", "two", "three"}, "xxxxx");
    d.setButtons(new String[] { "Ok", "Exit" });
    d.setVisible(true);
    System.out.println(d.getButtonSelected());
    System.exit(0);
 }

}
