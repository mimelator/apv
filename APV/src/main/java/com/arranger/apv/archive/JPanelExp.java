package com.arranger.apv.archive;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelExp {


	private static final Dimension LABEL_SIZE = new Dimension(20, 20);

	public static void main(String[] args) {
		
		JPanel panel = new JPanel();

		int index = 1;
		panel.add(createColorPanel(index++, Color.WHITE, Color.BLACK));
		panel.add(createColorPanel(index++, Color.BLACK, Color.WHITE));
		panel.add(createColorPanel(index++, Color.GREEN, Color.BLACK));
		panel.add(createColorPanel(index++, Color.BLACK, Color.RED));
		panel.add(createColorPanel(index++, Color.BLACK, Color.BLUE));
		panel.add(createColorPanel(index++, Color.WHITE, Color.RED));
		panel.add(createColorPanel(index++, Color.WHITE, Color.GREEN));
		panel.add(createColorPanel(index++, Color.RED, new Color(128, 0, 128)));
		
		panel.add(createColorPanel(index++, Color.BLACK));
		panel.add(createColorPanel(index++, Color.BLUE));
		panel.add(createColorPanel(index++, Color.RED));
		
		createFrame("JPanelExp", 300, 300, panel);
	}
	
	static class ColorEntry {
		Color c1, c2;
		
		public ColorEntry(Color c1, Color c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		
		public ColorEntry(Color c1) {
			this.c1 = c1;
		}
	}
	
	static List<ColorEntry> colorEntries = new ArrayList<ColorEntry>();
	
	protected static JPanel createColorPanel(int index, Color color) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(color);
		colorEntries.add(ce);
		
		row.add(new JLabel("Filter " + index));
		row.add(getColorLabel(ce, true));
		return row;
	}
	
	protected static JPanel createColorPanel(int index, Color c1, Color c2) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(c1, c2);
		colorEntries.add(ce);
		
		row.add(new JLabel("Pair " + index));
		row.add(getColorLabel(ce, true));
		row.add(getColorLabel(ce, false));
		return row;
	}
	
	protected static JLabel getColorLabel(ColorEntry ce, boolean firstColor) {
		JLabel label = new JLabel();
		label.setPreferredSize(LABEL_SIZE);
		label.setOpaque(true);
		Color color = firstColor ? ce.c1 : ce.c2;
		label.setBackground(color);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(null, "Change Color", color);
				label.setBackground(newColor);
				if (firstColor) {
					ce.c1 = newColor;
				} else {
					ce.c2 = newColor;
				}
			}
		});
		
		return label;
	}

	protected static JFrame createFrame(String title, int sizeX, int sizeY, JComponent content) {
		content.setCursor(Cursor.getDefaultCursor());
		
		JFrame frame = new JFrame(title);
		frame.setSize(sizeX, sizeY);
		frame.setResizable(true);
		frame.getContentPane().add(content);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		return frame;
	}
	
}
