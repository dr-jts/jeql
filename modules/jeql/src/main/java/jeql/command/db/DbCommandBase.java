package jeql.command.db;

import jeql.api.command.Command;

public abstract class DbCommandBase 
implements Command
{
  protected String jdbcDriver;
  protected String url;
  protected String user = null;
  protected String password = null;

  public DbCommandBase() {
  }
  
  public void setDriver(String jdbcClass)
  {
    this.jdbcDriver = jdbcClass;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public void setUser(String user)
  {
    this.user = user;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  

}
