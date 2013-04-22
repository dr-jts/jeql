package jeql.api;

public class JeqlOptions 
{
  boolean isVerbose = false;
  boolean isDebugMode = false;
  boolean isWorkbench = false;
  boolean isMonitorView = false;
  
  public JeqlOptions()
  {
    
  }
  
  public void setVerbose(boolean isVerbose)
  {
    this.isVerbose = isVerbose;
  }
  
  public void setDebug(boolean isDebug)
  {
    this.isDebugMode = isDebug;
  }

  public void setWorkbench(boolean isWorkbench)
  {
    this.isWorkbench = isWorkbench;
  }
  
  public void setMonitorView(boolean isMonitorView)
  {
    this.isMonitorView = isMonitorView;
  }
  
  public boolean isVerbose()
  {
    return isVerbose;
  }
}
