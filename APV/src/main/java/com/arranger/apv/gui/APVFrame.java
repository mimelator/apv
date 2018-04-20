package com.arranger.apv.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVFrame extends APVPlugin {

	@FunctionalInterface
	interface WindowClosing {
		void onWindowClose();
	}
	
	public APVFrame(Main parent) {
		super(parent);
	}

	protected JFrame createFrame(String title, int sizeX, int sizeY, JComponent content, WindowClosing wc) {
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
