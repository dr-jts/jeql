package jeql.command.plot;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.Bidi;

public class PlotUtil {

  public static AffineTransform transform(Graphics2D g, Point2D p, double rotation) {
    AffineTransform curr = g.getTransform();
    AffineTransform trans = AffineTransform.getTranslateInstance(
        p.getX(), p.getY());
    g.translate(p.getX(), p.getY());
    
    if (rotation > 0) {
      g.rotate(-Math.toRadians(rotation));
    }
    return curr;
  }
  
  public static void drawHalo(Graphics2D g, String label, double haloRadius, Font font)
  {
    g.setStroke(new BasicStroke(2f * (float) haloRadius, BasicStroke.CAP_ROUND,
                  BasicStroke.JOIN_ROUND));
    Shape halo = getHaloShape(g, label, haloRadius, font);
    g.draw(halo);
  }
  
  public static Shape getHaloShape(Graphics2D g, String label, double haloRadius, Font font) 
  {
    GlyphVector gv = getTextGlyphVector(g, label, font);
    Shape haloShape = new BasicStroke(2f * (float) haloRadius,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(gv
        .getOutline());
    return haloShape;
  }
  
  private static GlyphVector getTextGlyphVector(Graphics2D g, String label, Font font) {
      // arabic and hebrew are scripted and right to left, they do require full layout
      // whilst western chars are easier to deal with. Find out which case we're dealing with,
      // and create the glyph vector with the appropriate call
      final char[] chars = label.toCharArray();
      final int length = label.length();
      GlyphVector textGlyphVector;
      if(Bidi.requiresBidi(chars, 0, length) && 
              new Bidi(label, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft())
        textGlyphVector = font.layoutGlyphVector(g.getFontRenderContext(), chars, 
                  0, length, Font.LAYOUT_RIGHT_TO_LEFT);
      else
          textGlyphVector = font.createGlyphVector(g.getFontRenderContext(), chars);
      return textGlyphVector;
  }
}
