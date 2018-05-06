package com.arranger.apv.gui;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVFrame extends APVPlugin {

	@FunctionalInterface
	protected interface WindowClosing {
		void onWindowClose();
	}
	
	public APVFrame(Main parent) {
		super(parent);
	}
	
	public JPanel getPanel() {
		return null;
	}

	public void onClose() {
		
	}
	
	protected JFrame createFrame(String title, int sizeX, int sizeY, JComponent content, WindowClosing wc) {
		content.setCursor(Cursor.getDefaultCursor());
		
		JFrame frame = new JFrame(title);
		frame.setSize(sizeX, sizeY);
		frame.setResizable(true);
		frame.getContentPane().add(content);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent e) {
				wc.onWindowClose();
            }	
		});
		return frame;
	}
	
}
