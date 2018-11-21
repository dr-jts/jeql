package jeql.command.plot;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.text.Bidi;

public class PlotUtil {

  public static void drawHalo(String label, double haloRadius, Point2D p, Font font, Graphics2D graphics)
  {
    graphics.setStroke(new BasicStroke(2f * (float) haloRadius, BasicStroke.CAP_ROUND,
                  BasicStroke.JOIN_ROUND));

    Shape halo = getHaloShape(label, haloRadius, font, graphics);
    graphics.draw(halo);
  }
  
  private static Shape getHaloShape(String label, double haloRadius, Font font, Graphics2D graphics) 
  {
    GlyphVector gv = getTextGlyphVector(label, font, graphics);
    Shape haloShape = new BasicStroke(2f * (float) haloRadius,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(gv
        .getOutline());
    return haloShape;
  }
  
  private static GlyphVector getTextGlyphVector(String label, Font font, Graphics2D graphics) {
      // arabic and hebrew are scripted and right to left, they do require full layout
      // whilst western chars are easier to deal with. Find out which case we're dealing with,
      // and create the glyph vector with the appropriate call
      final char[] chars = label.toCharArray();
      final int length = label.length();
      GlyphVector textGlyphVector;
      if(Bidi.requiresBidi(chars, 0, length) && 
              new Bidi(label, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft())
        textGlyphVector = font.layoutGlyphVector(graphics.getFontRenderContext(), chars, 
                  0, length, Font.LAYOUT_RIGHT_TO_LEFT);
      else
          textGlyphVector = font.createGlyphVector(graphics.getFontRenderContext(), chars);
      return textGlyphVector;
  }
}
