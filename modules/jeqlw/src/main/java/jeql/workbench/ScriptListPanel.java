/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package jeql.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jeql.workbench.model.ScriptFile;

/**
 * @version 1.7
 */
public class ScriptListPanel extends JPanel
{
  private DefaultListModel listModel = new DefaultListModel();
  private JList list = new JList(listModel);

  public ScriptListPanel()
  {
    try {
      initUI();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    registerListSelectionListener();
  }

  private void initUI() throws Exception
  {
    //setSize(100, 250);
    setLayout(new BorderLayout());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectionBackground(Color.GRAY);
    list.setSize(100, 250);
    setPreferredSize(new Dimension(150, 250));
    list.setBorder(new EmptyBorder(2, 2, 2, 2));

    JScrollPane jScrollPane1 = new JScrollPane();
    add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(list, null);
    
    MouseListener mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          Workbench.controller().scriptLoad();
        }
      }
    };
    list.addMouseListener(mouseListener);

  }

  private void registerListSelectionListener()
  {
    list.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {

          public void valueChanged(ListSelectionEvent e)
          {
            if (list.getSelectedValue() == null)
              return;
          }
        });
  }

  public void populateList()
  {
    listModel.clear();
    for (ScriptFile s : Workbench.model().getScriptList().getList()) {
      listModel.addElement(s);
    }
  }

  public ScriptFile getSelected()
  {
    return (ScriptFile) list.getSelectedValue();
  }
}
