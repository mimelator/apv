package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class GradientPicker extends APVPlugin {
	
	private LinearGradientPaint selectedLGP;

	public GradientPicker(Main parent) {
		super(parent);
	}

	public LinearGradientPaint showDialog() {
		JPanel gui = new JPanel(new GridLayout(2, 4, 1, 1));
		addGradient(gui, 100, Color.YELLOW, Color.RED, Color.GREEN);
		addGradient(gui, 100, Color.GREEN, Color.YELLOW, Color.RED);
		addGradient(gui, 100, Color.RED, Color.GREEN, Color.YELLOW);
		addGradient(gui, 100, Color.BLUE, Color.MAGENTA, Color.PINK);
		addGradient(gui, 100, Color.WHITE, Color.RED, Color.BLACK);
		addGradient(gui, 100, Color.RED, Color.GREEN, Color.BLACK);
		addGradient(gui, 100, Color.BLUE, Color.PINK, Color.BLACK);
		addGradient(gui, 100, Color.BLUE, Color.CYAN, Color.BLACK);
		JOptionPane.showMessageDialog(null, gui);
		return selectedLGP;
	}
	
	private void addGradient(JPanel p, int s, Color pL, Color pR, Color sh) {
		LinearGradientPaint lgp = getLGP(s, pL, pR, sh);
		GradientLabel l = new GradientLabel(lgp, paint -> {
			this.selectedLGP = paint;
		});
		p.add(l);
	}
	
	private LinearGradientPaint getLGP(int size, Color primaryLeft, Color primaryRight, Color shadeColor) {
		LinearGradientPaint lgp = new LinearGradientPaint(
				new Point2D.Double(0, 0), 
				new Point2D.Double(size, 1), 
				new float[] {0, .35f, 1}, 
				new Color[] {primaryLeft, primaryRight, shadeColor});
		return lgp;
	}
	
	@FunctionalInterface
	private static interface Listener {
		void onSelect(LinearGradientPaint paint);
	}
	
	@SuppressWarnings("serial")
	private static class GradientLabel extends JLabel {
		private LinearGradientPaint paint;
		
		private GradientLabel(LinearGradientPaint paint, Listener listener) {
			this.paint = paint;
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					listener.onSelect(paint);
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
