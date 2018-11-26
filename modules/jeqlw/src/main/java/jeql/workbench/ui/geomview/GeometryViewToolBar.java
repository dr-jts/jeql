package jeql.workbench.ui.geomview;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import jeql.util.SwingUtil;
import jeql.workbench.AppStrings;
import jeql.workbench.BaseToolBar;
import jeql.workbench.Workbench;
import jeql.workbench.ui.geomview.tool.PanTool;
import jeql.workbench.ui.geomview.tool.ZoomTool;

public class GeometryViewToolBar extends BaseToolBar
{
  public GeometryViewToolBar()
  {
    super();
    init();
  }

  GeometryViewController geomView() {
    return Workbench.geomView();
  }
  private void init()
  {
    setFloatable(false);

    JToggleButton zoom = addToggleButton("MagnifyCursor.png", AppStrings.TIP_GEOMVIEW_ZOOM, 
        new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomView().setCurrentTool(ZoomTool.getInstance());
      }
    });
    zoom.setSelected(true);
    
    /*
     // no longer needed since Zoom tool does panning
    JToggleButton pan = addToggleButton("Pan.png", "Pan", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomViewFrame.geomView().setCurrentTool(PanTool.getInstance());
      }
    });
*/    
    
    addGap(10);

    
    addButton("World.png", "Zoom All", new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geomView().zoomToFullExtent();
      }
    });
    
    addGap(10);

    addButton("PasteSelection.png", AppStrings.TIP_GEOMVIEW_PASTE_SELECTION, new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
          geomView().pasteSelectionFromWKT();
      }
    });
    
    addGap(10);

    addButton("SaveImage.png", AppStrings.TIP_GEOMVIEW_SAVE_IMAGE, new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if ( SwingUtil.isCtlKeyPressed(e)) {
          geomView().saveAsPNG();
        } else {
          geomView().saveImageToClipboard();
        }
      }

    });
    

    ButtonGroup toolButtonGroup = new ButtonGroup();
    toolButtonGroup.add(zoom);
    //toolButtonGroup.add(pan);

  }

  
}
