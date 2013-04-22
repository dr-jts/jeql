package jeql.workbench;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jeql.monitor.MonitorModel;

import com.vividsolutions.jts.util.Memory;

public class ScriptStatusBar extends JPanel
{
  private JLabel lblRowCol = new JLabel();
  private JLabel lblTime = new JLabel();
  private JLabel lblMem = new JLabel();
  private JLabel lblScript = new JLabel();
  
  public ScriptStatusBar()
  {
    super();
    initUI();
  }

  private static final Font STATUS_FONT = new Font("SanSerif", Font.PLAIN, 12);
  
  public void initUI() 
  {
    lblTime.setBackground(SystemColor.control);
    lblTime.setBorder(BorderFactory.createLoweredBevelBorder());
    lblTime.setPreferredSize(new Dimension(21, 21));
    lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
    lblTime.setFont(STATUS_FONT);
    
    lblMem.setBackground(SystemColor.control);
    lblMem.setBorder(BorderFactory.createLoweredBevelBorder());
    lblMem.setPreferredSize(new Dimension(21, 21));
    lblMem.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMem.setFont(STATUS_FONT);
    
    lblRowCol.setBackground(SystemColor.control);
    lblRowCol.setBorder(BorderFactory.createLoweredBevelBorder());
    lblRowCol.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRowCol.setFont(STATUS_FONT);

    lblScript.setBackground(SystemColor.control);
    lblScript.setBorder(BorderFactory.createLoweredBevelBorder());
    lblScript.setHorizontalAlignment(SwingConstants.LEFT);

    setLayout(new GridLayout(1,4));
    //statusBarPanel.add(lblScript);
    add(lblScript);
    //add(lblMem);
    //add(lblTime);
    add(lblRowCol);
    setBackground(SystemColor.control);
    setRow(1,1);
  }

  public void setScript(String s)
  {
    lblScript.setText(s);
  }
  public void setRow(int row, int col)
  {
    lblRowCol.setText(row + " : " + col);
  }
  
  
  


}
