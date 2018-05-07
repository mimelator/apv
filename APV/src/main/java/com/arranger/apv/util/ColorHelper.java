package com.arranger.apv.util;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class ColorHelper extends APVPlugin {

	@FunctionalInterface
	public static interface GradientChangeHandler {
		public void onGradientChange(LinearGradientPaint gp);
	}
	
	@FunctionalInterface
	public static interface ColorChangeHandler {
		public void onColorChange(Color color);
	}

	@FunctionalInterface
	public static interface ColorChangeHandler2 {
		public void onColorChange(Color color, Color color2);
	}

	private static final String DECODE_COLOR_REGEX = "\\Q(\\E(\\d+),\\s?(\\d+),\\s?(\\d+)\\Q)\\E";
	private final Pattern COLOR_PATTERN = Pattern.compile(DECODE_COLOR_REGEX);

	private Map<String, ColorHolder> handlerMap = new HashMap<String, ColorHolder>();
	private Map<String, ColorHolder2> handlerMap2 = new HashMap<String, ColorHolder2>();
	private Map<String, GradientHolder> handlerMapGradient = new HashMap<String, GradientHolder>();

	public ColorHelper(Main parent) {
		super(parent);
	}

	public Color decode(String colorName) {
		Color result = null;
		if (colorName.contains("(")) {
			Matcher matcher = COLOR_PATTERN.matcher(colorName);
			if (matcher.matches()) {
				String r = matcher.group(1);
				String g = matcher.group(2);
				String b = matcher.group(3);
				result = new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
			} else {
				throw new RuntimeException("Invalid colorName: " + colorName);
			}
		} else {
			result = getColorForName(colorName);
		}

		return result;
	}
	
	public void register(String key, Color color, ColorChangeHandler handler) {
		handlerMap.put(key, new ColorHolder(handler, color));
	}

	public void register(String key, Color c1, Color c2, ColorChangeHandler2 handler) {
		handlerMap2.put(key, new ColorHolder2(handler, c1, c2));
	}
	
	public void registerGradientListener(String key, LinearGradientPaint paint, GradientChangeHandler handler) {
		handlerMapGradient.put(key, new GradientHolder(handler, paint));
	}

	public Map<String, ColorHolder> getHandlerMap() {
		return handlerMap;
	}

	public Map<String, ColorHolder2> getHandlerMap2() {
		return handlerMap2;
	}
	
	public Map<String, GradientHolder> getHandlerMapGradient() {
		return handlerMapGradient;
	}

	public void updateGradient(String key, LinearGradientPaint paint) {
		handlerMapGradient.get(key).getHandler().onGradientChange(paint);
	}

	public void updateColor(String key, Color c1) {
		handlerMap.get(key).getHandler().onColorChange(c1);
	}

	public void updateColor(String key, Color c1, Color c2) {
		handlerMap2.get(key).getHandler().onColorChange(c1, c2);
	}

	protected Color getColorForName(String colorName) {
		try {
			Field field = Color.class.getField(colorName);
			return (Color) field.get(null);
		} catch (Exception e) {
			return null;
		}
	}

	public class GradientHolder {
		GradientChangeHandler handler;
		LinearGradientPaint paint;

		public GradientHolder(GradientChangeHandler handler, LinearGradientPaint paint) {
			this.handler = handler;
			this.paint = paint;
		}

		public GradientChangeHandler getHandler() {
			return handler;
		}

		public LinearGradientPaint getLinearGradientPaint() {
			return paint;
		}
	}
	
	public class ColorHolder {
		ColorChangeHandler handler;
		Color color;

		public ColorHolder(ColorChangeHandler handler, Color color) {
			this.handler = handler;
			this.color = color;
		}

		public ColorChangeHandler getHandler() {
			return handler;
		}

		public Color getColor() {
			return color;
		}
	}

	public class ColorHolder2 {
		ColorChangeHandler2 handler;
		Color col1, col2;

		public ColorHolder2(ColorChangeHandler2 handler, Color col1, Color col2) {
			this.handler = handler;
			this.col1 = col1;
			this.col2 = col2;
		}

		public ColorChangeHandler2 getHandler() {
			return handler;
		}

		public Color getCol1() {
			return col1;
		}

		public Color getCol2() {
			return col2;
		}

	}
}
