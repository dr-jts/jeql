package jeql.workbench.ui.geomview;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import jeql.api.row.Row;

public class LayerList 
{
  private List<Layer> layers = new ArrayList<Layer>();
  
  public LayerList() 
  {
  }

  public int size() { return layers.size(); }
  
  public void add(Layer layer)
  {
    this.layers.add(layer);
  }
  
  public Layer getLayer(int i)
  { 
    return layers.get(i);
  }

  public Envelope getEnvelope(int i)
  {
    return getLayer(i).getEnvelope();
  }

  public Envelope getEnvelopeAll()
  {
    Envelope env = new Envelope();
    for (int i = 0; i < layers.size(); i++) {
      env.expandToInclude(getEnvelope(i));
    }
    return env;
  }
  
  public String locateRowDesc(Coordinate pt) {
    for (int i = 0; i < layers.size(); i++) {
      String desc = layers.get(i).locateRowDesc(pt);
      if (desc != null) return desc;
    }
    return null;
  }

  public RowWithSchema locateRow(Coordinate pt) {
    for (int i = 0; i < layers.size(); i++) {
      RowWithSchema row = layers.get(i).locateRow(pt);
      if (row != null) return row;
    }
    return null;
  }

  public String getName() {
    String name = "";
    for (int i = 0; i < layers.size(); i++) {
      if (i > 0) name += ",";
      name += layers.get(i).getName();
    }
    return name;
  }
    
}
