package com.arranger.apv.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVTextFrame extends APVPlugin {
	
	private static final float EXTRA_SCROLL_PANE_DIMENSION = 1.5f;
	
	private OutputPanel outputPanel;
	private EventHandler handler;
	
	@FunctionalInterface
	public static interface TextSupplier {
		public List<String> getMessages();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public APVTextFrame(Main parent, String title, int sizeX, int sizeY, APVEvent event, TextSupplier ts) {
		super(parent);
		outputPanel = new OutputPanel();
		outputPanel.setPreferredSize(new Dimension((int)(sizeX * EXTRA_SCROLL_PANE_DIMENSION), (int)(sizeY * 1.5f)));
		JScrollPane scrollPane = new JScrollPane(outputPanel);
		outputPanel.setAutoscrolls(true);
		scrollPane.setPreferredSize(new Dimension(sizeX, sizeY));
		
		JFrame frame = new JFrame(title);
		frame.setSize(sizeX, sizeY);
		frame.setResizable(true);
		frame.getContentPane().add(scrollPane);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent e) {
				event.unregister(handler);
            }	
		});
		
		handler = event.register(() -> {
			outputPanel.printText(ts.getMessages());
		});
	}
	
	@SuppressWarnings("serial")
	class OutputPanel extends JPanel {

		private static final int INSET = 10;
		List<String> msgs;

	    public OutputPanel() {
	        super();
	    }
	    
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
