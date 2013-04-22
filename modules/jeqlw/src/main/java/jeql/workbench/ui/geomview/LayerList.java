package jeql.workbench.ui.geomview;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

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
  
}
