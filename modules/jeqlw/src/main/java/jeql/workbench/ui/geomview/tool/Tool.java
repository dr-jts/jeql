package jeql.workbench.ui.geomview.tool;

import java.awt.Cursor;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface Tool extends MouseListener, MouseMotionListener {

    Cursor getCursor();

    void activate();
}
