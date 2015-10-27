package jeql.command.plot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jeql.awt.geom.DefaultPointConverter;
import jeql.awt.geom.Java2DConverter;
import jeql.awt.geom.PointConverter;
import jeql.util.ColorUtil;
import jeql.util.ImageUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class Plot 
{  
  private int width = 500;
  private int height = 500;
  private int borderSize = 0;
  private int plotWidth = -1;
  private int plotHeight = -1;
  
  private Envelope worldExtent = null;
  private String bgColor = "ffffff";
  private String borderColor = "c0c0c0";
  private List dataTables = new ArrayList();
  private List labelTables = new ArrayList();
  
  private BufferedImage plotImg = null;
  private static final int IMG_TYPE = BufferedImage.TYPE_INT_ARGB;

  public Plot() {
  }

  public void setBorderSize(int borderSize)
  {
    this.borderSize = borderSize;
  }
  
  public void setBorderColor(String color)
  {
    this.borderColor = color;
  }
  
  public void setWidth(int width)
  {
   this.width = width; 
  }
  
  public int getWidth() { return width; }
  
  public void setHeight(int height)
  {
   this.height = height; 
  }
  public int getHeight() { return height; }

  public void setBackground(String bgColor)
  {
    this.bgColor = bgColor;
  }
  
  public void setExtent(Envelope extent)
  {
    this.worldExtent = extent;
  }
  
  public Envelope getExtent() { return worldExtent; }
  
  public void write(String filename)
  throws IOException
  {
    RenderedImage fullImg = createBorderImage();
    ImageIO.write(fullImg, ImageUtil.imageFormat(filename), new File(filename));
  }
  
  private int plotWidth() 
  {
    return width - 2 * borderSize;
  }
  private int plotHeight() 
  {
    return height - 2 * borderSize;
  }
  private RenderedImage createBorderImage()
  {
    if (borderSize == 0 && plotImg != null) return plotImg;
    
    BufferedImage img = new BufferedImage(width, height, IMG_TYPE); 
    clear(img, borderColor);
    
    if (plotImg != null) {
      Raster plotRaster = plotImg.getData();
      Raster transRaster = plotRaster.createTranslatedChild(borderSize, borderSize);
      img.setData(transRaster);
    }
    return img;
  }
  
  private Graphics2D gr = null;

  public Graphics2D getGraphics()
  {
    if (gr != null) return gr;
    
    gr = plotImg.createGraphics();
    
    // MD - no longer needed, done in ModelPointConverter
    // apply standard transforms to invert Y axis
    //gr.scale(1, -1);
    //gr.translate(0, -plotHeight);
    
    gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    return gr;
  }
  
  private Java2DConverter shapeConverter;
  private PointConverter ptConverter;

  public Java2DConverter getConverter() {
    if (shapeConverter != null) return shapeConverter;

    if (getExtent() != null) {
      ptConverter = new ModelPointConverter(getExtent(), plotWidth(), plotHeight());
      shapeConverter = new Java2DConverter(ptConverter);
    }
    else {
    	ptConverter = new DefaultPointConverter();
      shapeConverter = new Java2DConverter();
    }
    return shapeConverter;
  }
  
  public Point2D convert(Coordinate modelPt)
  {
    return ptConverter.toView(modelPt);
  }
  
  public void init()
  {
    plotWidth = width - 2 * borderSize;
    if (plotWidth < 0) plotWidth = 0;
    plotHeight = height - 2 * borderSize;
    if (plotHeight < 0) plotHeight = 0;

    if (plotHeight > 0 && plotWidth > 0) {
      // or  TYPE_INT_RGB or TYPE_INT_ARGB_PRE ?
      plotImg = new BufferedImage(plotWidth, plotHeight, IMG_TYPE); 
      clear(plotImg, bgColor);
    }
  }
  
  private void clear(BufferedImage img, String color)
  {
    Graphics2D gr = img.createGraphics();
    gr.setBackground(ColorUtil.RGBAtoColor(color));
    gr.clearRect(0, 0, img.getWidth(), img.getHeight());
  }
  
  /**
   * Transforms points from world coordinates to view coordinates.
   * 
   * @author Martin Davis
   *
   */
  private static class ModelPointConverter
  implements PointConverter
  {
    private double scale = 1.0;
    private double worldMinX = 0.0;
    private double worldMinY = 0.0;
    private int viewHeight = 0;
    
    public ModelPointConverter(Envelope worldExtent, int viewWidth, int viewHeight)
    {
      this.viewHeight = viewHeight;
      double xScale = viewWidth / worldExtent.getWidth();
      double yScale = viewHeight / worldExtent.getHeight();
      scale = Math.min(xScale, yScale);

      worldMinX = worldExtent.getMinX();
      worldMinY = worldExtent.getMinY();
    }
    
    public Point2D toView(Coordinate modelCoordinate)
    {
      double vx = scale * (modelCoordinate.x - worldMinX);
      double vy = viewHeight - scale * (modelCoordinate.y - worldMinY);
      return new Point2D.Double(vx, vy);
    }

  }
  
}
