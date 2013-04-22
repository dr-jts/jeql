package jeql.workbench.model;

import java.io.IOException;

import jeql.util.FileUtil;
import jeql.util.IOUtil;

public class ScriptSource
{
  public static ScriptSource createInternal(String name)
  {
    ScriptSource ss = new ScriptSource(name);
    ss.setInternal(true);
    return ss;
  }
  
  public static ScriptSource createFile(String filename)
  {
    ScriptSource ss = new ScriptSource("");
    ss.setFilename(filename);
    ss.setInternal(false);
    return ss;
  }
  
  private boolean isInternal = true;
  private String name;
  private String filename;
  private String text;
  private boolean isModified = false;

  public ScriptSource(String name)
  {
    isInternal = true;
    this.name = name;
    // filename is deliberately left unset
  }
  
  public boolean isModified()
  {
    return isModified;
  }

  public void setModified(boolean isModified)
  {
    this.isModified = isModified;
  }

  public String getName()
  {
    return name;
  }

  public String getTitle()
  {
    return name + (isModified ? "*" : "");
  }

  public void setInternal(boolean isInternal)
  {
    this.isInternal = isInternal;
  }
  
  public boolean isInternal() { return isInternal; }
  
  public void setFilename(String filename)
  {
    this.filename = filename;
    name = FileUtil.name(filename);
  }
  
  public String getFilename()
  {
    return filename;
  }
  
  public boolean hasFilename()
  {
    return filename != null;
  }
  public void loadText()
  {
    //scriptList.init();
    try {
      text = IOUtil.readFile(filename, true);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String getText()
  {
    return text;
  }

  public void save()
  {
    // TODO: allow saving anonymous scripts
    if (filename == null) return;
    
    IOUtil.writeToFileNoThrow(filename, text);
    isModified = false;
  }
}
