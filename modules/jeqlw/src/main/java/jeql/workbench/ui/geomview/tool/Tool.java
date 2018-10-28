package jeql.workbench.ui.geomview.tool;

import java.awt.Cursor;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public interface Tool extends MouseListener, MouseMotionListener, MouseWheelListener {

    Cursor getCursor();

    void activate();
}
