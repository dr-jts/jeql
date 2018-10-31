package jeql.util;

import java.awt.Graphics2D;

public class GraphicsUtil {
  public static void drawStringMultiLine(Graphics2D g, String text, int x, int y) {
    for (String line : text.split("\n"))
        g.drawString(line, x, y += g.getFontMetrics().getHeight());
  }
}
