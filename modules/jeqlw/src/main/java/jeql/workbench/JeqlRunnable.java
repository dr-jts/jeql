package jeql.workbench;

import jeql.api.JeqlOptions;
import jeql.api.JeqlRunner;

class JeqlRunnable implements Runnable
{
  public interface RunListener {
    void end(JeqlRunner runner);
  }

  private RunListener listener;
  private String scriptFile;
  private String scriptText;
  private boolean isKeepData = false;
  
  JeqlRunnable(RunListener listener)
  {
    this.listener = listener;
  }
  
  public void setScriptFile(String scriptFile)
  {
    this.scriptFile = scriptFile;
  }
  
  public void setScript(String scriptText)
  {
    this.scriptText = scriptText;
  }
  
  public void setKeepData(boolean isKeepData)
  {
    this.isKeepData = isKeepData;
  }
  
  public void run()
  {
    JeqlRunner runner = new JeqlRunner();
    JeqlOptions options = new JeqlOptions();
    options.setWorkbench(true);
    // Workbench Run mode => not keeping data => run in Monitor View mode
    options.setMonitorView(! isKeepData);
    runner.init(options);
    //TODO: fix exception handling
    boolean returnCode;
    try {
      returnCode = runScript(runner);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally {
      listener.end(runner);
    }
  }
  
  private boolean runScript(JeqlRunner runner)
  throws Exception
  {
    if (scriptFile != null) {
      return runner.execScriptFile(scriptFile, null);
    }
    return runner.execScript(scriptText, null);
  }
}