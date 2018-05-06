package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.util.ColorHelper;
import com.arranger.apv.util.ColorHelper.ColorHolder;
import com.arranger.apv.util.ColorHelper.ColorHolder2;

@SuppressWarnings("serial")
public class ColorsPanel extends SetPackPanel {
	
	private static final Dimension LABEL_SIZE = new Dimension(40, 40);
	
//	private static final String COLOR_PAIR_PATTERN = "color.pair.";
//	private static final String COLOR_FILTER_PATTERN = "color.filter.";
	
	private List<ColorEntry> colorEntries = new ArrayList<ColorEntry>();
	private JColorChooser colorChooser = new JColorChooser();
	
	public ColorsPanel(Main parent) {
		super(parent, PANELS.COLORS);
		ColorHelper colorHelper = parent.getColorHelper();
		
		colorHelper.getHandlerMap2().entrySet().forEach(entry -> {
			String key = entry.getKey();
			ColorHolder2 value = entry.getValue();
			add(createColorPanel(key, value.getCol1(), value.getCol2()));
		});
		
		colorHelper.getHandlerMap().entrySet().forEach(entry -> {
			String key = entry.getKey();
			ColorHolder value = entry.getValue();
			add(createColorPanel(key, value.getColor()));
		});
	}
	
	public void updateForDemo(boolean isDemoActive) {
		ColorHelper colorHelper = parent.getColorHelper();
		colorEntries.forEach(ce -> {
			ce.update(colorHelper, !isDemoActive);
		});
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) {
		//No files to create
	}
	
	protected JPanel createColorPanel(String key, Color color) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(key, color);
		colorEntries.add(ce);
		
		row.add(new JLabel("Filter"));
		row.add(getColorLabel(ce, true));
		return row;
	}
	
	protected JPanel createColorPanel(String key, Color c1, Color c2) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(key, c1, c2);
		colorEntries.add(ce);
		
		row.add(new JLabel("Pair"));
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
				JColorChooser.createDialog(null, "ColorPicker", true, colorChooser, evt -> {
					Color newColor = colorChooser.getColor();
					label.setBackground(newColor);
					if (firstColor) {
						ce.c1 = newColor;
					} else {
						ce.c2 = newColor;
					}
				}, null).setVisible(true);
			}
		});
		
		return label;
	}
	
	private class ColorEntry {
		private boolean has2Colors = false;
		private String key;
		private Color orig1, orig2;
		private Color c1, c2;
		
		ColorEntry(String key, Color c1) {
			this.key = key;
			this.orig1 = c1;
			this.c1 = c1;
		}
		
		ColorEntry(String key, Color c1, Color c2) {
			this.key = key;
			this.orig1 = c1;
			this.orig2 = c2;
			this.c1 = c1;
			this.c2 = c2;
			has2Colors = true;
		}
		
		void update(ColorHelper helper, boolean useOrig) {
			if (useOrig) {
				if (has2Colors) {
					helper.updateColor(key, orig1, orig2);
				} else {
					helper.updateColor(key, orig1);
				}
			} else {
				if (has2Colors) {
					helper.updateColor(key, c1, c2);
				} else {
					helper.updateColor(key, c1);
				}
			}
		}
	}
}