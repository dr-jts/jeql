package jeql.workbench;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import jeql.workbench.ui.geomview.tool.PanTool;
import jeql.workbench.ui.geomview.tool.ZoomTool;

public class GeometryViewToolBar extends BaseToolBar
{
  private static final String TIP_ZOOM = "<html>Zoom In/Out | Pan<br><br>Zoom In = Left-Btn<br>Zoom Extent = Left-Drag<br>Zoom Out = Right-Btn<br>Pan = Right-Drag | Ctl-Drag</html>";
  
  private GeometryViewFrame geomViewFrame;
  
  public GeometryViewToolBar(GeometryViewFrame geomViewPanel)
  {
    super();
    this.geomViewFrame = geomViewPanel;
    init();
    geomViewPanel.geomView().setCurrentTool(ZoomTool.getInstance());
  }

  private void init()
  {
    setFloatable(false);

    JToggleButton zoom = addToggleButton("MagnifyCursor.png", TIP_ZOOM, 
        new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomViewFrame.geomView().setCurrentTool(ZoomTool.getInstance());
      }
    });
    zoom.setSelected(true);
    
    /*
    JToggleButton pan = addToggleButton("Pan.png", "Pan", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomViewFrame.geomView().setCurrentTool(PanTool.getInstance());
      }
    });
*/    
    
    add(Box.createHorizontalStrut(10), null);

    
    addButton("World.png", "Zoom All", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomViewFrame.geomView().zoomToFullExtent();
      }
    });
    
    add(Box.createHorizontalStrut(10), null);

    addButton("SaveImage.png", "Save Image", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomViewFrame.saveAsPNG();
      }
    });
    
    ButtonGroup toolButtonGroup = new ButtonGroup();
    toolButtonGroup.add(zoom);
    //toolButtonGroup.add(pan);

  }


  
}
