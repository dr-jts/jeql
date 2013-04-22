package jeql.workbench.ui.geomview;

import java.awt.Color;

import jeql.util.ColorUtil;
import jeql.workbench.ui.geomview.style.BasicStyle;
import jeql.workbench.ui.geomview.style.Style;
import com.vividsolutions.jts.geom.Envelope;

public class Layer 
{
  private String name = "";
  private GeometryList geomCont;
  private boolean isEnabled = true;
  
  private Style style;
  //private StyleList styleList;
  
  public Layer(String name) {
    this.name = name;
    setStyle(new BasicStyle(Color.BLUE, ColorUtil.lighter(Color.BLUE)));
  }

  public String getName() { return name; }
  
  public void setEnabled(boolean isEnabled)
  {
    this.isEnabled = isEnabled;
  }
  
  public void setSource(GeometryList geomCont)
  {
    this.geomCont = geomCont;
  }
  
  public GeometryList getSource()
  {
    return geomCont;
  }
  
  public boolean isEnabled()
  {
  	return isEnabled;
  }
  public Style getStyle()
  {
    return style;
  }

  public void setStyle(BasicStyle style)
  {
    this.style = style;
  }
  
  public Envelope getEnvelope()
  {
    Envelope env = new Envelope();
    for (int i = 0; i < geomCont.size(); i++) {
      env.expandToInclude(geomCont.getGeometry(i).getEnvelopeInternal());
    }
    return env;
  }
  
}
