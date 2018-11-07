package jeql.workbench;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.Timer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

import jeql.api.JeqlRunner;
import jeql.api.row.Row;
import jeql.api.row.RowSchema;
import jeql.monitor.Monitor;
import jeql.monitor.MonitorItem;
import jeql.util.ImageUtil;
import jeql.util.SwingUtil;
import jeql.workbench.model.ScriptSource;
import jeql.workbench.ui.assist.CodeSnippet;
import jeql.workbench.ui.geomview.GeometryList;
import jeql.workbench.ui.geomview.Layer;
import jeql.workbench.ui.geomview.LayerList;

public class WorkbenchController
{
  
  public WorkbenchController()
  {
    
  }
  
  public void scriptAdd()
  {
    Workbench.view().scriptAdd();
  }

  public void scriptClose()
  {
    Workbench.view().scriptClose();
    
  }

  public void scriptLoad()
  {
    String scriptFile = Workbench.view().getSelectedScriptFile();

    int tabIndex = Workbench.view().scriptTabIndex(scriptFile);
    if (tabIndex >= 0) {
      Workbench.view().showTab(tabIndex);
      return;
    }
      
    ScriptSource src = ScriptSource.createFile(scriptFile);
    src.loadText();

    Workbench.view().scriptLoad(src);
  }
  
  public void scriptSave()
  {
    Workbench.view().scriptSave();
    
  }
  public void scriptCopy()
  {
    String text = Workbench.view().getCurrentScriptText();
    Workbench.view().scriptAdd(text);
  }

  public void inspect(Object obj)
  {
    Workbench.view().inspectText(Inspector.inspectString(obj));
    Workbench.geomView().flash(obj);
  }
  
  public void inspect(RowSchema schema, Row row)
  {
    String str = Inspector.inspectString(schema, row);
    Workbench.view().inspectText(str);
  }
  



  /*
  public void inspectGeomView(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      Workbench.geomViewPanel().inspect(geom);
    }
  }
  
  public void flashGeomView(Object val)
  {
    if (val instanceof Geometry) {
      Geometry geom = (Geometry) val;
      Workbench.geomViewPanel().flash(geom);
    }
  }
*/
  
  public void insertCodeSnippet(CodeSnippet code)
  {
    Workbench.view().insertCodeSnippet(code);
  }


  public void updateFileList()
  {
    Workbench.model().updateFileList();
    Workbench.view().updateScript();
  }
  
  public void run()
  {
    run(false);
  }
  
  public void debug()
  {
    run(true);
  }
  
  public void run(boolean isDebug)
  {
    /*
     * Set up run for immediate script or script file
     */
    JeqlRunnable proc = new JeqlRunnable(new JeqlRunnable.RunListener() {
      public void end(JeqlRunner runner) {
        runEnd(runner);
      }
    });
    setRunData(proc, isDebug);
    
    // clear monitor data for new run
    Monitor.clear();
    // enable monitor first to ensure it is initialized and ready to read from
    Monitor.enableWorkbench(isDebug);
    
    // reload script from file
    //Workbench.model().load();
    
    Workbench.view().clearOutput();
    Workbench.view().run(Monitor.model());
    startTimer();
    
    new Thread(proc).start();
  }
  
  private void setRunData(JeqlRunnable proc, boolean isDebug)
  {
    if (Workbench.view().isInternalScript()) {
      proc.setScript(Workbench.view().getCurrentScriptText());
   }
   else { 
     proc.setScriptFile(Workbench.view().getCurrentScriptSource().getFilename());
   }
    proc.setKeepData(isDebug);
  }
  
  private void runEnd(JeqlRunner runner)
  {
    //updateTimer.stop();
    stopTimer = true;
    stop();
    //Workbench.view().stop();
    
    Workbench.view().reportOutput();
    if (! runner.getReturnCode()) {
      Workbench.view().reportScriptError(runner.getErrorMessage());
    }
  }
  
  public void pause()
  {
    Monitor.setPaused(true);
    Workbench.view().pause();
  }
  
  public void stop()
  {
    Monitor.end();
    Workbench.view().stop();
  }
  
  private Timer updateTimer;
  //private long runMillis = 0;
  private static final int TIMER_DELAY_IN_MILLIS = 200;
  private static boolean stopTimer = false;

  private void startTimer() {
    stopTimer = false;
    // System.out.println("timer started");
    updateTimer = new Timer(TIMER_DELAY_IN_MILLIS, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //runMillis += TIMER_DELAY_IN_MILLIS;
        // System.out.println("timer: " + runMillis);
        Monitor.model().update();
        Workbench.view().updateMonitor();
        if (stopTimer)
          updateTimer.stop();
      }
    });
    
    updateTimer.setInitialDelay(0);
    updateTimer.start();
  }

  /*
  private void stopTimer()
  {
    updateTimer.stop();
  }
  */
  
  public void help()
  {
    Workbench.view().helpDlg.loadContent();
    Workbench.view().helpDlg.setVisible(true);
  }

  public void viewGeoms(MonitorItem monitorItem)
  {
    LayerList lyrList = new LayerList();
    if (monitorItem != null) {
      GeometryList geomCont = new RowListGeometryList(monitorItem.getRowList());
      Layer lyr = new Layer(monitorItem.getName());
      lyr.setSource(geomCont);
      lyrList.add(lyr);
    }
    Workbench.geomView().setSource(lyrList);
    Workbench.geomView().setVisible(true);
   }

  public void displaySettings()
  {
    Workbench.view().settingsDlg.setVisible(true);
  }
  
  public void applySettings()
  {
    Workbench.view().monitorDataPanel.showSpaces(Workbench.model().getSettings().showSpaces);
    Workbench.view().monitorDataPanel.update();
  }

  public void highlightRow(Row row) {
    Workbench.view().monitorDataPanel.setHighlightRow(row);
  }
}


