package jeql.monitor.ui;

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

import org.locationtech.jts.util.Memory;

public class MonitorStatusBar extends JPanel
{
  MonitorModel monitorModel;

  JLabel lblRowStat = new JLabel();
  JLabel lblTime = new JLabel();
  JLabel lblMem = new JLabel();
  JLabel lblScript = new JLabel();
  
  public MonitorStatusBar()
  {
    super();
    initUI();
  }

  private static final Font STATUS_FONT = new Font("SanSerif", Font.BOLD, 12);
  
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
    
    lblRowStat.setBackground(SystemColor.control);
    lblRowStat.setBorder(BorderFactory.createLoweredBevelBorder());
    lblRowStat.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRowStat.setFont(STATUS_FONT);

    lblScript.setBackground(SystemColor.control);
    lblScript.setBorder(BorderFactory.createLoweredBevelBorder());
    lblScript.setHorizontalAlignment(SwingConstants.LEFT);

    setLayout(new GridLayout(1,4));
    //statusBarPanel.add(lblScript);
    add(lblScript);
    add(lblMem);
    add(lblRowStat);
    add(lblTime);
    setBackground(SystemColor.control);
  }

  public void update()
  {
    setTime(monitorModel.getTimeString());
    double time = monitorModel.getTime();
    int rowsPerSec = (int) (monitorModel.getRowTotal() / time * 1000);

    setRowsPerSec(rowsPerSec);
    setMemory(Memory.usedTotalString());
  }

  public void setModel(MonitorModel model)
  {
    //table.setModel(new MonitorItemsTableModel());
    //table.initTable();
    this.monitorModel = model;
  }

  public void setScript(String s)
  {
    lblScript.setText(s);
  }
  
  private void setTime(String s)
  {
    lblTime.setText(s);
  }
  
  private void setMemory(String s)
  {
    lblMem.setText(s);
  }
  
  private DecimalFormat format = new DecimalFormat("#,###");

  private void setRowsPerSec(int rowsPerSec)
  {
    lblRowStat.setText(format.format(rowsPerSec) + " rows / sec");
  }
  


}
