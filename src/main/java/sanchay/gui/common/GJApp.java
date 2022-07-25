/*
 * GJApp.java
 *
 * Created on May 16, 2006, 5:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class GJApp extends WindowAdapter {
	static private JPanel statusArea = new JPanel();
	static private JLabel status = new JLabel(" ");

	public static void launch(final JFrame f, String title,
							  final int x, final int y, 
							  final int w, int h) {
		f.setTitle(title);
		f.setBounds(x,y,w,h);
		f.setVisible(true);

		statusArea.setBorder(BorderFactory.createEtchedBorder());
		statusArea.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		statusArea.add(status);
		status.setHorizontalAlignment(JLabel.LEFT);

		f.setDefaultCloseOperation(
							WindowConstants.DISPOSE_ON_CLOSE);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	static public JPanel getStatusArea() {
		return statusArea;
	}
	static public void updateStatus(String s) {
		status.setText(s);
	}
}
