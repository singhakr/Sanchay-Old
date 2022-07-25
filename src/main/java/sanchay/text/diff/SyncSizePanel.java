/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text.diff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author anil
 */
public class SyncSizePanel extends JPanel {
    private static Vector panels = new Vector();

    private JPanel spacer = new JPanel();

    public JComponent component;

    public SyncSizePanel(JComponent component) {
        spacer.setBackground(Color.WHITE);

        panels.add(this);
        this.component = component;

        this.setLayout(new BorderLayout());
        this.add(component, BorderLayout.CENTER);
        this.add(spacer, BorderLayout.SOUTH);

        component.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                syncComponentSizes();
            }

        });
    }

    private void syncComponentSizes() {
        Dimension d = component.getPreferredSize();

        int w = d.width;
        int h = d.height;

        for (int i = 0; i < panels.size(); i++) {
            SyncSizePanel panel = (SyncSizePanel) panels.get(i);
            Component c = panel.component;

            if (panel != this) {
                Dimension d2 = c.getPreferredSize();

                w = w > d2.width ? w : d2.width;
                h = h > d2.height ? h : d2.height;
            }
        }

        for (int i = 0; i < panels.size(); i++) {
            SyncSizePanel panel = (SyncSizePanel) panels.get(i);
            panel.setSyncSize(w, h);
        }
    }

    private void setSyncSize(int width, int height) {
        Dimension d = component.getPreferredSize();
        spacer.setPreferredSize(new Dimension(width - d.width,
                height - d.height));
        revalidate();
    }
}
