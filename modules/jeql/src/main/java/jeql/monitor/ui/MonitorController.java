package jeql.monitor.ui;

import jeql.monitor.MonitorModel;

import org.locationtech.jts.util.Memory;

public class MonitorController
{
  private MonitorFrame frame = new MonitorFrame();
  private final MonitorModel model;

  public MonitorController(MonitorModel model)
  {
    this.model = model;
  }
  
  public void setScript(String script)
  {
    if (frame == null) return;
    frame.setScript(script);
  }

  public void setVisible(boolean isVisible)
  {
    frame.setVisible(true);   
  }
  
  public void update() {
    //String txt = model.scriptView();
    //frame.setText(txt);
    frame.setModel(model);
    frame.update();
    frame.setTime(model.getTimeString());
    double time = model.getTime();
    int rowsPerSec = (int) (model.getRowTotal() / time * 1000);

    frame.setRowsPerSec(rowsPerSec);
    frame.setMemory(Memory.usedTotalString());
    model.update();
  }

  public void end()
  {
    frame.end();
  }
}
