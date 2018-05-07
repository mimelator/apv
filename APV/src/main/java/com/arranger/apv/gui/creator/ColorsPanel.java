package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.util.ColorHelper;
import com.arranger.apv.util.ColorHelper.ColorHolder;
import com.arranger.apv.util.ColorHelper.ColorHolder2;
import com.arranger.apv.util.ColorHelper.GradientHolder;

@SuppressWarnings("serial")
public class ColorsPanel extends SetPackPanel {
	
	private static final Dimension LABEL_SIZE = new Dimension(40, 40);
	
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
		
		colorHelper.getHandlerMapGradient().entrySet().forEach(entry -> {
			String key = entry.getKey();
			GradientHolder value = entry.getValue();
			add(createGradientPanel(key, value.getLinearGradientPaint()));
		});
		
		JButton button = new JButton("Randomize");
		button.addActionListener(evt -> {
			colorEntries.forEach(ce -> ce.randomize());
		});
		add(button);
	}
	
	public void updateForDemo(boolean isDemoActive, Path parentDirectory) {
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
	
	protected JPanel createGradientPanel(String key, LinearGradientPaint paint) {
		JPanel row = new JPanel();
		ColorEntry ce = new ColorEntry(key, paint);
		colorEntries.add(ce);
		
		row.add(new JLabel("Gradient"));
		row.add(getColorLabel(ce));
		return row;
	}
	
	protected JLabel getColorLabel(ColorEntry ce) {
		GradientLabel label = new GradientLabel(ce.paint, null);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LinearGradientPaint selected = new GradientPicker(parent).showDialog();
				if (selected != null) {
					ce.paint = selected;
					label.paint = selected;
				}
			}
		});
		
		label.setPreferredSize(LABEL_SIZE);
		label.setOpaque(true);
		ce.gradientLabel = label;

		return label;
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
		
		if (firstColor) {
			ce.label = label;
		} else {
			ce.label2 = label;
		}
		return label;
	}
	
	@FunctionalInterface
	static interface Listener {
		void onSelect(LinearGradientPaint paint);
	}
	
	class GradientLabel extends JLabel {
		private LinearGradientPaint paint;
		
		public GradientLabel(LinearGradientPaint paint, Listener listener) {
			this.paint = paint;
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (listener != null) {
						listener.onSelect(paint);
					}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			
			g2.setPaint(paint);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	enum TYPE {TWO, ONE, GRADIENT};
	
	private class ColorEntry {
		
		private TYPE type;
		private String key;
		private Color orig1, orig2;
		private Color c1, c2;
		private LinearGradientPaint paint, origPaint;
		private JLabel label, label2;
		private GradientLabel gradientLabel;
		
		ColorEntry(String key, LinearGradientPaint paint) {
			this.key = key;
			this.paint = paint;
			this.origPaint = paint;
			type = TYPE.GRADIENT;
		}
		
		ColorEntry(String key, Color c1) {
			this.key = key;
			this.orig1 = c1;
			this.c1 = c1;
			type = TYPE.ONE;
		}
		
		ColorEntry(String key, Color c1, Color c2) {
			this.key = key;
			this.orig1 = c1;
			this.orig2 = c2;
			this.c1 = c1;
			this.c2 = c2;
			type = TYPE.TWO;
		}
		
		void randomize() {
			switch (type) {
			case TWO:
				c2 = getRandomColor();
				label2.setBackground(c2);
			case ONE:
				c1 = getRandomColor();
				label.setBackground(c1);
				break;
			case GRADIENT:
				paint = getRandomGradient();
				gradientLabel.paint = paint;
				gradientLabel.repaint();
				break;
			}
		}
		
		Color getRandomColor() {
			return new Color((int)parent.random(256), (int)parent.random(256), (int)parent.random(256));
		}
		
		LinearGradientPaint getRandomGradient() {
			return new LinearGradientPaint(
					new Point2D.Double(0, 0), 
					new Point2D.Double(GradientPicker.DISTANCE, 1), 
					new float[] {0, .35f, 1}, 
					new Color[] {getRandomColor(), getRandomColor(), getRandomColor()});
		}
		
		void update(ColorHelper helper, boolean useOrig) {
			if (useOrig) {
				switch (type) {
				case ONE:
					helper.updateColor(key, orig1);
					break;
				case TWO:
					helper.updateColor(key, orig1, orig2);
					break;
				case GRADIENT:
					helper.updateGradient(key, origPaint);
					break;
				}
			} else {
				switch (type) {
				case ONE:
					helper.updateColor(key, c1);
					break;
				case TWO:
					helper.updateColor(key, c1, c2);
					break;
				case GRADIENT:
					helper.updateGradient(key, paint);
					break;
				}
			}
		}
	}
}