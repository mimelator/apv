package com.arranger.apv.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class PopupWindow extends APVPlugin {
	
	public PopupWindow(Main parent) {
		super(parent);
	}
	
	public WindowTextPrinter launchWindow(String text, int sizeX, int sizeY) {
		OutputPanel out = new OutputPanel();
		JFrame frame = new JFrame(text); 
		frame.setSize(sizeX, sizeY);
		frame.setResizable(true);
		frame.getContentPane().add(out);
		frame.setVisible(true);
		return out;
	}
	
	@FunctionalInterface
	public interface WindowTextPrinter {
		public void printText(List<String> msgs);
	}
	
	@SuppressWarnings("serial")
	class OutputPanel extends JPanel implements WindowTextPrinter {

		private static final int INSET = 10;
		List<String> msgs;

	    public OutputPanel() {
	        super();
	    }
	    
	    @Override
		public void printText(List<String> msgs) {
			this.msgs = new ArrayList<String>(msgs);
			repaint();
		}

	    //paint
		public void paintComponent(Graphics g) {
			if (msgs != null) { 
				drawText(g, msgs);
			}
		}
		
		public void drawText(Graphics g, List<String> msgsToDraw) {
			int fontHeight = g.getFontMetrics().getHeight();
			int offset = fontHeight;
			for (String s : msgsToDraw) {
				g.drawString(s, INSET, offset);
				offset += fontHeight;
			}
		}
	}
}
