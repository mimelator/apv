package com.arranger.apv.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;

public class APVTextFrame extends APVFrame {
	
	private static final float EXTRA_SCROLL_PANE_DIMENSION = 1.5f;
	
	private APVEvent<EventHandler> event;
	private OutputPanel outputPanel;
	private EventHandler handler;
	private JPanel panel;
	
	@FunctionalInterface
	public static interface TextSupplier {
		public List<String> getMessages();
	}

	public APVTextFrame(Main parent, String title, int sizeX, int sizeY, TextSupplier ts) {
		this(parent, title, sizeX, sizeY, parent.getDrawEvent(), ts, true);
	}
	
	public APVTextFrame(Main parent, String title, int sizeX, int sizeY, APVEvent<EventHandler> event, TextSupplier ts, boolean launchWindow) {
		super(parent);
		outputPanel = new OutputPanel();
		outputPanel.setPreferredSize(new Dimension((int)(sizeX * EXTRA_SCROLL_PANE_DIMENSION), (int)(sizeY * EXTRA_SCROLL_PANE_DIMENSION)));
		outputPanel.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(outputPanel);
		scrollPane.setPreferredSize(new Dimension(sizeX, sizeY));	
		
		panel = new JPanel();
		if (launchWindow) {
			panel.add(new JLabel("Click text area to copy to clipboard"));
		}
		panel.add(scrollPane);
		if (launchWindow) {
			createFrame(title, sizeX, sizeY, panel, () -> event.unregister(handler));
		}
		
		handler = event.register(() -> {
			outputPanel.printText(ts.getMessages());
		});
		
		this.event = event;
	}
	
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
	@Override
	public void onClose() {
		event.unregister(handler);
	}

	@SuppressWarnings("serial")
	class OutputPanel extends JPanel {

		private static final int INSET = 10;
		List<String> msgs;

	    public OutputPanel() {
	    	addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					new ClipboardHelper(msgs.stream().collect(Collectors.joining(System.lineSeparator())));
				}
			});
	    }
	    
		public void printText(List<String> msgs) {
			this.msgs = new ArrayList<String>(msgs);
			repaint();
		}

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
