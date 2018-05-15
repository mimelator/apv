package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.model.ColorsModel;
import com.arranger.apv.model.ColorsModel.ColorEntry;
import com.arranger.apv.util.ColorHelper;

@SuppressWarnings("serial")
public class ColorsPanel extends SetPackPanel {
	
	private static final Dimension LABEL_SIZE = new Dimension(40, 40);
	
	private Map<ColorEntry, GradientLabel> gradientLabels = new HashMap<ColorEntry, GradientLabel>();
	private Map<ColorEntry, JLabel> color1Labels = new HashMap<ColorEntry, JLabel>();
	private Map<ColorEntry, JLabel> color2Labels = new HashMap<ColorEntry, JLabel>();
	private ColorsModel colorsModel;
	private JColorChooser colorChooser = new JColorChooser();
	
	public ColorsPanel(Main parent) {
		super(parent, PANELS.COLORS);
		colorsModel = parent.getColorsModel();
		
		colorsModel.getColorEntries().forEach(ce -> {
			add(createPanel(ce));
		});
		
		JButton button = new JButton("Randomize");
		button.addActionListener(evt -> {
			randomize();
		});
		add(button);
	}
	
	public void randomize() {
		colorsModel.randomize();
		
		//iterate through all the labels and repaint
		colorsModel.getColorEntries().forEach(ce -> {
			switch (ce.getType()) {
			case TWO:
				color2Labels.get(ce).setBackground(ce.getC2());
			case ONE:
				color1Labels.get(ce).setBackground(ce.getC1());
				break;
			case GRADIENT:
				GradientLabel gradientLabel = gradientLabels.get(ce);
				gradientLabel.paint = ce.getLGP();
				gradientLabel.repaint();
			}
		});
	}
	
	public void updateForDemo(boolean isDemoActive, Path parentDirectory) {
		ColorHelper colorHelper = parent.getColorHelper();
		colorsModel.getColorEntries().forEach(ce -> {
			ce.update(colorHelper, !isDemoActive);
		});
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) {
		//No files to create
	}
	
	protected JPanel createPanel(ColorEntry ce) {
		JPanel row = new JPanel();
		switch (ce.getType()) {
		case ONE:
			row.add(new JLabel("Filter"));
			row.add(getColorLabel(ce, true));
			break;
		case TWO:
			row.add(new JLabel("Pair"));
			row.add(getColorLabel(ce, true));
			row.add(getColorLabel(ce, false));
			break;
		case GRADIENT:
			row.add(new JLabel("Gradient"));
			row.add(getGradientLabel(ce));
		}
		
		return row;
	}
	
	protected JLabel getGradientLabel(ColorEntry ce) {
		GradientLabel label = new GradientLabel(ce.getLGP(), null);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LinearGradientPaint selected = new GradientPicker(parent).showDialog();
				if (selected != null) {
					ce.setLGP(selected);
					label.paint = selected;
				}
			}
		});
		
		label.setPreferredSize(LABEL_SIZE);
		label.setOpaque(true);
		gradientLabels.put(ce, label);
		
		return label;
	}
	
	protected JLabel getColorLabel(ColorEntry ce, boolean firstColor) {
		JLabel label = new JLabel();
		label.setPreferredSize(LABEL_SIZE);
		label.setOpaque(true);
		Color color = firstColor ? ce.getC1() : ce.getC2();
		label.setBackground(color);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JColorChooser.createDialog(null, "ColorPicker", true, colorChooser, evt -> {
					Color newColor = colorChooser.getColor();
					label.setBackground(newColor);
					if (firstColor) {
						ce.setC1(newColor);
					} else {
						ce.setC2(newColor);
					}
				}, null).setVisible(true);
			}
		});
		
		if (firstColor) {
			color1Labels.put(ce, label);
		} else {
			color2Labels.put(ce, label);
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
}