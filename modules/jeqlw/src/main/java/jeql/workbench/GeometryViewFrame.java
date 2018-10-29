package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import jeql.engine.EngineContext;
import jeql.man.ManGenerator;
import jeql.util.ImageUtil;
import jeql.util.SwingUtil;
import jeql.workbench.ui.geomview.GeometryViewPanel;
import jeql.workbench.ui.geomview.LayerList;

public class GeometryViewFrame extends JFrame
{
  JPanel statusBarPanel = new JPanel();
  JLabel lblMousePos = new JLabel();

  GeometryViewPanel geomViewPanel = new GeometryViewPanel();
  GeometryViewToolBar toolbar = new GeometryViewToolBar(this);

  String manText;
  
  public GeometryViewFrame(Frame frame)
  {
    super("Geometry Viewer");
    try {
      initUI();
      pack();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initUI()
  {
    lblMousePos.setBackground(SystemColor.text);
    lblMousePos.setBorder(BorderFactory.createLoweredBevelBorder());
    lblMousePos.setPreferredSize(new Dimension(21, 21));
    lblMousePos.setHorizontalAlignment(SwingConstants.RIGHT);

    statusBarPanel.setLayout(new GridLayout(1,2));
    //statusBarPanel.add(testCaseIndexLabel);
    //statusBarPanel.add(lblPrecisionModel);
    statusBarPanel.add(lblMousePos);

    JPanel borderPanel = new JPanel();
    borderPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    borderPanel.setLayout(new BorderLayout());
    borderPanel.add(geomViewPanel, BorderLayout.CENTER);
    
    setLayout(new BorderLayout());
    add(toolbar, BorderLayout.NORTH);
    add(borderPanel, BorderLayout.CENTER);
    add(statusBarPanel, BorderLayout.SOUTH);
    
    getContentPane().setPreferredSize(new Dimension(600, 600));
    
    geomViewPanel.addMouseMotionListener(
        new java.awt.event.MouseMotionAdapter() {

          public void mouseMoved(MouseEvent e) {
            viewPanel_mouseMoved(e);
          }
          public void mouseDragged(MouseEvent e) {
            viewPanel_mouseMoved(e);
          }
        });
  }
  
  void viewPanel_mouseMoved(MouseEvent e) {
    String cursorPos = geomViewPanel.cursorLocation(e.getPoint());
    lblMousePos.setText(cursorPos);
//    System.out.println(cursorPos);
  }

  public GeometryViewPanel geomView() 
  {
    return geomViewPanel;
  }
  
  public void saveAsPNG() {
    initFileChoosers();
    try {
      String fullFileName = SwingUtil.chooseFilenameWithConfirm(this, pngFileChooser);  
      if (fullFileName == null) return;
        ImageUtil.writeImage(geomViewPanel, 
            fullFileName,
            ImageUtil.IMAGE_FORMAT_NAME_PNG);
    }
    catch (Exception x) {
      SwingUtil.reportException(this, x);
    }
  }

  public void saveImageToClipboard() {
    try {
      ImageUtil.saveImageToClipboard(geomViewPanel, ImageUtil.IMAGE_FORMAT_NAME_PNG);
    } catch (Exception ex) {
      SwingUtil.reportException(this, ex);
    }
  }
  
  private JFileChooser pngFileChooser;

  private void initFileChoosers() {
    if (pngFileChooser == null) {
      pngFileChooser = new JFileChooser();
      pngFileChooser.addChoosableFileFilter(SwingUtil.PNG_FILE_FILTER);
      pngFileChooser.setDialogTitle("Save PNG");
      pngFileChooser.setSelectedFile(new File("image.png"));
    }
  }

}
