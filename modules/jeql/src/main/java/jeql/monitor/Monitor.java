package jeql.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.UIManager;

import jeql.api.row.RowList;
import jeql.api.table.Table;
import jeql.monitor.MonitorRowList.MonitorRowIterator;
import jeql.monitor.ui.MonitorController;

/**
 * The Controller class for the Monitor module,
 * and provides a simpler facade for clients
 * 
 * @author Martin Davis
 *
 */
public class Monitor 
{
  private static final int MAX_COUNT = 100;
  private static final int PAUSE_DURATION = 1000;

  private static MonitorController view;
  //private static PrintLogger logger;
  
  private static boolean isViewEnabled = false;
  private static boolean isEnabled = false;
  private static boolean isPaused = false;
  
  private static boolean isEnded = false;
  private static boolean isKeepingData = false;
  
  public static Object wrap(int line, String name, String tag, Object o) {
    if (! Monitor.isEnabled())
      return o;
    
    if (o instanceof RowList) {
      RowList rs = (RowList) o;
      MonitorRowList mrl = new MonitorRowList(line, name, tag, rs, isKeepingData);
      //INSTANCE.getModel().add(line, tag, mrl);
      return mrl;
    }
    else if (o instanceof Table) {
      Table t = (Table) o;
      MonitorRowList mrl = new MonitorRowList(line, name, tag, t.getRows(), isKeepingData);
      //INSTANCE.getModel().add(line, tag, mrl);
      return Table.replace(t, mrl);
    }
    return o;
  }

  public static void enableView()
  {
    isViewEnabled = true;
    isEnabled = true;
    isKeepingData = false;
    init();
    initView();
  }
  
  public static void enableWorkbench(boolean isKeepData)
  {
    isEnabled = true;
    isKeepingData = isKeepData;
    init();
  }
  
  public static void setPaused(boolean paused)
  {
    isPaused = paused;
  }
  
  public static boolean isEnabled()
  {
    return isEnabled;
  }
  
  public static void setScript(String script)
  {
    if (! isEnabled) return; 
    if (view != null) view.setScript(script);
  }
  
  public static void end()
  {
    if (! isEnabled) return; 
    INSTANCE.end();
    isEnded = true;
  }
  
  public static boolean isEnded() { return isEnded; }
  
  public static void add(MonitorRowIterator mri)
  {
    if (! isEnabled) return; 
    INSTANCE.getModel().add(mri);
  }
  
  public static void init() 
  {
    if (INSTANCE != null) return;
    try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return;
    }
    INSTANCE = new MonitorInstance();
    INSTANCE.startTimer();
  }
  
  public static void initView()
  {
    view = new MonitorController(INSTANCE.getModel());
    view.setVisible(true);
  }
  
  public static void clear()
  {
    INSTANCE = null;
    isEnded = false;
  }
  
  public static void checkStatus()
  {
    try {
      while (isPaused && ! isEnded()) {
        Thread.sleep(PAUSE_DURATION);
      }
    }
    catch (InterruptedException e) {
      // do nothing - just continue
    }
    
    if (isEnded()) {
      throw new TerminationException();
    }
  }
  
  public static MonitorModel model()
  {
    return INSTANCE.getModel();
  }
  
  private static MonitorInstance INSTANCE;
  
  private static class MonitorInstance 
  {
    private MonitorModel model = new MonitorModel();

    private MonitorInstance() {

    }

    public MonitorModel getModel()
    {
      return model;
    }
    
    public void end() {
      model.stop();
      
      if (view == null)
        return;
      updateDisplay();
      updateTimer.stop();
      //if (true) return;
      
      // update display one last time, to clear active colour
      updateDisplay();
      view.end();
      while (true) {
        // loop until MonitorView is closed
      }
    }

    private int count = 0;

    private void update() {
      count++;
      if (count < MAX_COUNT)
        return;
      count = 0;
      // updateDisplay();
    }

    private void updateDisplay() {
      init();
      if (view != null) view.update();
    }

    private Timer updateTimer;
    private long runMillis = 0;
    private static final int TIMER_DELAY_IN_MILLIS = 100;

    private void startTimer() {
      // System.out.println("timer started");
      updateTimer = new Timer(TIMER_DELAY_IN_MILLIS, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          runMillis += TIMER_DELAY_IN_MILLIS;
          // System.out.println("timer: " + runMillis);
          updateDisplay();
        }
      });
      
      updateTimer.setInitialDelay(0);
      updateTimer.start();
    }
  }
}
