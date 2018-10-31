package jeql.util;

import java.awt.Graphics2D;

public class GraphicsUtil {
  public static void drawStringMultiLine(Graphics2D g, String text, int x, int y) {
    String[] lines = text.split("\n");
    for (String line : lines) {
        g.drawString(line, x, y);
        y += g.getFontMetrics().getHeight();
    }
  }
}
