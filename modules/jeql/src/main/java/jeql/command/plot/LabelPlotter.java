package jeql.command.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.Bidi;
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
import jeql.util.ColorUtil;
import jeql.util.ImageUtil;
import jeql.util.TypeUtil;

import com.vividsolutions.jts.awt.FontGlyphReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LabelPlotter 
{
  public static void plot(Plot plot, List data)
  {
    for (Iterator i = data.iterator(); i.hasNext(); ) {
      Table tbl = (Table) i.next();
      LabelPlotter plotter = new LabelPlotter(plot);
      plotter.plot(tbl);
    }
  }

  private Plot plot;
  
  private int geomIndex = -1;
  private int labelIndex = -1;
  private int labelColorIndex = -1;
  private int labelSizeIndex = -1;
  private int labelHaloSizeIndex = -1;
  private int labelHaloColorIndex = -1;
  private int labelRotateIndex = -1;
  private int labelOffsetXIndex = -1;
  private int labelOffsetYIndex = -1;
  private Geometry labelPoint = null;

  private int labelPointIndex;
  
  public LabelPlotter(Plot plot) {
    this.plot = plot;
  }
  
  public void plot(Table tbl)
  {
    RowIterator ri = tbl.getRows().iterator();
    RowSchema schema = ri.getSchema();
    
    geomIndex = SchemaUtil.getColumnWithType(schema, Geometry.class);
    labelIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL);
    labelColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_COLOR);
    labelSizeIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.FONT_SIZE);
    labelHaloSizeIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.HALO_RADIUS);
    labelHaloColorIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.HALO_COLOR);
    labelOffsetXIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_OFFSET_X);
    labelOffsetYIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_OFFSET_Y);
    labelRotateIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_ROTATE);
    labelPointIndex = SchemaUtil.getColumnIndex(schema, StyleConstants.LABEL_POINT);
    
    if (geomIndex < 0) return;
    if (labelIndex < 0) return;
    
    Graphics2D graphics = plot.getGraphics();

    while (true) {
      Row row = ri.next();
      if (row == null)
        break;
      plot(row, graphics);
    }
  }

  private static final String DEFAULT_LABEL_CLR = "000000"; //"80ff80";
  private static final String DEFAULT_HALO_CLR = null; //"ff6060";
  
  private Font font;

  private void plot(Row row, Graphics2D graphics)
  {
    Geometry geom = (Geometry) row.getValue(geomIndex);
    if (geom == null) return;
    
//    Shape shp = plot.getConverter().toShape(centroid);

    boolean hasColor = labelColorIndex >= 0;
    
    // only use default colours if none are specified
    String labelClrStr = null;    
    if (! hasColor) {
      labelClrStr = DEFAULT_LABEL_CLR;    
    }
    if (labelColorIndex >= 0) 
      labelClrStr = (String) row.getValue(labelColorIndex);
    
    int labelSize = RowUtil.getInt(labelSizeIndex, row, 12);
    if (font == null) {
      font = new Font(FontGlyphReader.FONT_SANSERIF, Font.PLAIN, labelSize);
      graphics.setFont(font);
    }
    
    int haloSize = RowUtil.getInt(labelHaloSizeIndex, row, 0);
    String haloClrStr  = RowUtil.getString(labelHaloColorIndex, row, DEFAULT_HALO_CLR);
    // if halo clr is specified, make sure halo is visible even if size was omitted
    if (haloClrStr != null && labelHaloSizeIndex < 0) {
      haloSize = 1;
    }
    
    double rotate = RowUtil.getInt(labelRotateIndex, row, 0);
    double offsetX = RowUtil.getInt(labelOffsetXIndex, row, 0);
    double offsetY = RowUtil.getInt(labelOffsetYIndex, row, 0);
    Geometry lblPoint = RowUtil.getGeometry(labelPointIndex, row);

    String label = row.getValue(labelIndex).toString();
    
    if (lblPoint == null) {
      lblPoint = geom.getCentroid();
    }
    
    if (labelClrStr != null && labelClrStr.length() > 0) {
      Coordinate centroidPt = lblPoint.getCoordinate();
      Coordinate labelPt = new Coordinate(centroidPt.x + offsetX, centroidPt.y  + offsetY);
      Point2D p = plot.convert(labelPt);

      AffineTransform oldTrans = PlotUtil.transform(graphics, p, 0);
 //System.out.println(label + "  " + p);
 
      if (haloSize > 0) {
        graphics.setColor(ColorUtil.RGBAtoColor(haloClrStr));
        PlotUtil.drawHalo(graphics, label, haloSize, font);
      }
      
      Color clr = ColorUtil.RGBAtoColor(labelClrStr);
      graphics.setColor(clr);

      graphics.drawString(label, 0, 0);
      //graphics.drawString(label, (int) p.getX() + 5, (int) p.getY());
      graphics.setTransform(oldTrans);
    }  
  }
    




}
