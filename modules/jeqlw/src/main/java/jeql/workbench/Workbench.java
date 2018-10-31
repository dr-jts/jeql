package jeql.workbench;

import javax.swing.UIManager;

import jeql.engine.EngineContext;
import jeql.workbench.model.WorkbenchModel;
import jeql.workbench.ui.geomview.GeometryViewPanel;

public class Workbench
{
  private static Workbench WB;
  
  public static WorkbenchFrame view() { return WB.wbFrame; }
  public static WorkbenchModel model() { return WB.wbModel; }
  public static WorkbenchController controller() { return WB.controller; }
  public static GeometryViewController geomView() { return WB.geomView; }
  
  public static void main(String[] args) {
    try {
      initLookAndFeel();
      
      WB = new Workbench();
      if (args.length > 0) {
        //Workbench.model().setScriptFile(args[0]);
        // don't load here - done by Focus event
        //WB.controller().load();
      }
      Workbench.view().setVisible(true);
       

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void initLookAndFeel() throws Exception {
    if (UIManager.getLookAndFeel() != null
        && UIManager.getLookAndFeel().getClass().getName().equals(
            UIManager.getSystemLookAndFeelClassName())) {
      return;
    }
    String laf = System.getProperty("swing.defaultlaf");
    if (laf == null) {
      laf = UIManager.getSystemLookAndFeelClassName();
    }
    UIManager.setLookAndFeel(laf);
  }

  private WorkbenchFrame wbFrame = new WorkbenchFrame();
  private WorkbenchModel wbModel = new WorkbenchModel();
  private WorkbenchController controller = new WorkbenchController();
  private GeometryViewController geomView = new GeometryViewController(wbFrame.geomViewFrame);

  public Workbench()
  {
  }
  
  
}
