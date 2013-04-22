package jeql.command.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import jeql.api.row.Row;
import jeql.api.row.RowIterator;
import jeql.api.row.RowSchema;
import jeql.api.row.RowUtil;
import jeql.api.row.SchemaUtil;
import jeql.api.table.Table;
import jeql.awt.geom.Java2DConverter;
import jeql.style.StyleConstants;
import jeql.style.StyleExtracter;
import jeql.util.ColorUtil;
import jeql.util.ImageUtil;
import jeql.util.TypeUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class DataPlotter 
{
  public static void plot(Plot plot, List data)
  {
    for (Iterator i = data.iterator(); i.hasNext(); ) {
      Table tbl = (Table) i.next();
      DataPlotter plotter = new DataPlotter(plot);
      plotter.plot(tbl);
    }
  }

  private Plot plot;
  
  private int geomIndex = -1;
  private int lineColorIndex = -1;
  private int lineWidthIndex = -1;
  private int fillColorIndex = -1;
  
  public DataPlotter(Plot plot) {
    this.plot = plot;
  }
  
  public void plot(Table tbl)
  {
    RowIterator ri = tbl.getRows().iterator();
    RowSchema schema = ri.getSchema();
    
    StyleExtracter styler = new StyleExtracter(schema);
    geomIndex = SchemaUtil.getColumnWithType(schema, Geometry.class);
    lineColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE, StyleConstants.COLOR);
    lineWidthIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.STROKE_WIDTH);
    fillColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.FILL);
    
    if (geomIndex < 0) return;
    
    Graphics2D graphics = plot.getGraphics();
//    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//        RenderingHints.VALUE_ANTIALIAS_ON);
   
    while (true) {
      Row row = ri.next();
      if (row == null)
        break;
      plot(row, graphics);
    }
  }

  private static final String DEFAULT_FILL_CLR = "5555ff80";
  private static final String DEFAULT_LINE_CLR = "0000ffff";
  
  private void plot(Row row, Graphics2D graphics)
  {
    Geometry geom = (Geometry) row.getValue(geomIndex);
    if (geom == null) return;
    plot(geom, row, graphics);
  }
    
  private void plot(Geometry geom, Row row,  Graphics2D graphics)
  {
    Shape shp = plot.getConverter().toShape(geom);

    boolean isArea = geom.getDimension() >= 2;
    boolean hasColor = fillColorIndex >= 0 || lineColorIndex >= 0;
    
    // only use default colours if none are specified
    String fillClrStr = null;
    String lineClrStr = null;    
    if (! hasColor) {
      fillClrStr = DEFAULT_FILL_CLR;
      lineClrStr = DEFAULT_LINE_CLR;    
    }

    if (isArea) {
      if (fillColorIndex >= 0) 
        fillClrStr = RowUtil.getString(fillColorIndex, row, null);
        
      if (fillClrStr != null && fillClrStr.length() > 0) {
          Color clr = ColorUtil.RGBAtoColor(fillClrStr);
          graphics.setPaint(clr);
          graphics.fill(shp);
      }
    }
    
    if (lineWidthIndex >= 0) {
      double width = TypeUtil.toDouble(row.getValue(lineWidthIndex));
      graphics.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }
 
    if (lineColorIndex >= 0) 
      lineClrStr = (String) row.getValue(lineColorIndex);
     

    if (lineClrStr != null && lineClrStr.length() > 0) {
      Color clr = ColorUtil.RGBAtoColor(lineClrStr);
      graphics.setColor(clr);
      graphics.draw(shp);
    }  
  }
    

}
