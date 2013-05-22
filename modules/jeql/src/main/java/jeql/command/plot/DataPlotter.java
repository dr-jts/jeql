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
  
  public DataPlotter(Plot plot) {
    this.plot = plot;
  }
  
  public void plot(Table tbl)
  {
    RowIterator ri = tbl.getRows().iterator();
    RowSchema schema = ri.getSchema();
    
    StyleExtracter styler = new StyleExtracter(schema);
    geomIndex = SchemaUtil.getColumnWithType(schema, Geometry.class);
    // no geometry, so nothing to plot
    if (geomIndex < 0) return;
    
    Graphics2D graphics = plot.getGraphics();
//    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//        RenderingHints.VALUE_ANTIALIAS_ON);
   
    while (true) {
      Row row = ri.next();
      if (row == null)
        break;
      plot(row, styler, graphics);
    }
  }

  private void plot(Row row, StyleExtracter styler, Graphics2D graphics)
  {
    Geometry geom = (Geometry) row.getValue(geomIndex);
    if (geom == null) return;
    plot(geom, row, styler, graphics);
  }
    
  private void plot(Geometry geom, Row row,  StyleExtracter styler, Graphics2D graphics)
  {
    Shape shp = plot.getConverter().toShape(geom);
    boolean isArea = geom.getDimension() >= 2;
    
    if (isArea) {
      Color clr = styler.fill(row);
      if (clr != null) {
        graphics.setPaint(clr);
        graphics.fill(shp);       
      }
    }
    
    Color lineClr = styler.stroke(row);
    if (lineClr != null) {
      if (styler.hasStrokeWidth()) {
        graphics.setStroke(new BasicStroke((float) styler.strokeWidth(row), 
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      }
      graphics.setColor(lineClr);
      graphics.draw(shp);
    }
  }
    

}
