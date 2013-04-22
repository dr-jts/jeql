package jeql.command.io.kml;

public class DocumentModel {

  String name;
  String description;
  
  public DocumentModel() {
    super();
  }

  public void setName(String name)
  {
    this.name = name;
  }  
  
  public void setDescription(String desc)
  {
    description = desc;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  

}
