package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.util.ColorHelper;

@SuppressWarnings("serial")
public class ColorsPanel extends SetPackPanel {
	
	private static final Dimension LABEL_SIZE = new Dimension(40, 40);
	private static final int NUM_COLOR_PAIRS = 8;
	private static final int NUM_COLOR_FILTERS = 3;
	
	private static final String COLOR_PAIR_PATTERN = "color.pair.";
	private static final String COLOR_FILTER_PATTERN = "color.filter.";
	
	private List<ColorEntry> colorEntries = new ArrayList<ColorEntry>();
	
	public ColorsPanel(Main parent) {
		super(parent, PANELS.COLORS);
		ColorHelper colorHelper = parent.getColorHelper();
		
		IntStream.rangeClosed(1, NUM_COLOR_PAIRS).forEach(i -> {
			List<String> stringList = parent.getConfigurator().getRootConfig().getStringList(COLOR_PAIR_PATTERN + i);
			Color c1 = colorHelper.decode(stringList.get(0));
			Color c2  = colorHelper.decode(stringList.get(1));
			add(createColorPanel(i, c1, c2));
		});
		
		IntStream.rangeClosed(1, NUM_COLOR_FILTERS).forEach(i -> {
			String colorName = parent.getConfigString(COLOR_FILTER_PATTERN + i);
			Color color = colorHelper.decode(colorName);
			add(createColorPanel(i + NUM_COLOR_PAIRS, color));
		});
	}
	
	protected JPanel createColorPanel(int index, Color color) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(color);
		colorEntries.add(ce);
		
		row.add(new JLabel("Filter " + index));
		row.add(getColorLabel(ce, true));
		return row;
	}
	
	protected JPanel createColorPanel(int index, Color c1, Color c2) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(c1, c2);
		colorEntries.add(ce);
		
		row.add(new JLabel("Pair " + index));
		row.add(getColorLabel(ce, true));
		row.add(getColorLabel(ce, false));
		return row;
	}
	
	protected JLabel getColorLabel(ColorEntry ce, boolean firstColor) {
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
	
	public void updateForDemo(boolean isDemoActive) {
		//TODO
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
}