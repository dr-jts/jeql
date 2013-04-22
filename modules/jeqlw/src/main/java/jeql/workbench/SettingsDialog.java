package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.*;

import jeql.util.SwingUtil;

public class SettingsDialog  extends JDialog
{
  JCheckBox chkShowBlanks;
  
  public SettingsDialog()
  {
    this(null, "Settings", true);
  }

  public SettingsDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
        initUI();
        pack();
        SwingUtil.centerOnScreen(this);

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
  
  void initUI() throws Exception 
  {
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
    chkShowBlanks = new JCheckBox("Show non-printable characters in table data", false);
    controlPanel.add(chkShowBlanks);
    
    
    JButton btnOk = new JButton("Ok");
    btnOk.setToolTipText("Ok");
    btnOk.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          btnOk_actionPerformed(e);
        }
    });
    JButton btnCancel = new JButton("Cancel");
    btnCancel.setToolTipText("Cancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
            btnCancel_actionPerformed(e);
        }
    });
    JPanel btnPanel = new JPanel();
    btnPanel.add(btnOk, null);
    btnPanel.add(btnCancel, null);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    mainPanel.add(controlPanel, BorderLayout.CENTER);
    mainPanel.add(btnPanel, BorderLayout.SOUTH);

    getContentPane().add(mainPanel);
    
    setPreferredSize(new Dimension(300, 300));

  }
  
  private void btnOk_actionPerformed(ActionEvent e)
  {
    setVisible(false);
    pullValues();
    Workbench.controller().applySettings();
  }
  
  private void btnCancel_actionPerformed(ActionEvent e)
  {
    setVisible(false);
  }
  
  void pushValues()
  {
    chkShowBlanks.setSelected(Workbench.model().getSettings().showSpaces);
  }
  
  void pullValues()
  {
    Workbench.model().getSettings().showSpaces = chkShowBlanks.isSelected();
  }

}
