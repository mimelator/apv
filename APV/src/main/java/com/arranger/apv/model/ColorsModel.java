package com.arranger.apv.model;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.gui.creator.GradientPicker;
import com.arranger.apv.util.ColorHelper;
import com.arranger.apv.util.ColorHelper.ColorHolder;
import com.arranger.apv.util.ColorHelper.ColorHolder2;
import com.arranger.apv.util.ColorHelper.GradientHolder;

public class ColorsModel extends APVModel {

	private List<ColorEntry> colorEntries = new ArrayList<ColorEntry>();
	
	public ColorsModel(Main parent) {
		super(parent);
		reset();
	}
	
	public List<ColorEntry> getColorEntries() {
		return colorEntries;
	}

	protected void init() {
		ColorHelper colorHelper = parent.getColorHelper();
		
		colorHelper.getHandlerMap2().entrySet().forEach(entry -> {
			String key = entry.getKey();
			ColorHolder2 value = entry.getValue();
			colorEntries.add(new ColorEntry(key, value.getCol1(), value.getCol2()));
		});
		
		colorHelper.getHandlerMap().entrySet().forEach(entry -> {
			String key = entry.getKey();
			ColorHolder value = entry.getValue();
			colorEntries.add(new ColorEntry(key, value.getColor()));
		});
		
		colorHelper.getHandlerMapGradient().entrySet().forEach(entry -> {
			String key = entry.getKey();
			GradientHolder value = entry.getValue();
			colorEntries.add(new ColorEntry(key, value.getLinearGradientPaint()));
		});
	}
	
	@Override
	public void reset() {
		colorEntries.clear();
		parent.getSetupEvent().register(() -> {
			init();
		});
	}
	
	@Override
	public void randomize() {
		colorEntries.forEach(ce -> ce.randomize());
	}

	public enum TYPE {TWO, ONE, GRADIENT};

	public class ColorEntry {
		
		private TYPE type;
		private String key;
		private Color orig1, orig2;
		private Color c1, c2;
		private LinearGradientPaint paint, origPaint;

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
		
		public String getKey() {
			return key;
		}
		
		public Color getC1() {
			return c1;
		}
		
		public Color getC2() {
			return c2;
		}
		
		public void setC1(Color c1) {
			this.c1 = c1;
		}
		
		public void setC2(Color c2) {
			this.c2 = c2;
		}
		
		public LinearGradientPaint getLGP() {
			return paint;
		}
		
		public void setLGP(LinearGradientPaint paint) {
			this.paint = paint;
		}
		
		public TYPE getType() {
			return type;
		}
		
		public void randomize() {
			switch (type) {
			case TWO:
				c2 = getRandomColor();
			case ONE:
				c1 = getRandomColor();
				break;
			case GRADIENT:
				paint = getRandomGradient();
				break;
			}
		}
		
		private Color getRandomColor() {
			return new Color((int)parent.random(256), (int)parent.random(256), (int)parent.random(256));
		}
		
		private LinearGradientPaint getRandomGradient() {
			return new LinearGradientPaint(
					new Point2D.Double(0, 0), 
					new Point2D.Double(GradientPicker.DISTANCE, 1), 
					new float[] {0, .35f, 1}, 
					new Color[] {getRandomColor(), getRandomColor(), getRandomColor()});
		}
		
		public void update(ColorHelper helper, boolean useOrig) {
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
