package com.arranger.apv.util;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class ColorHelper extends APVPlugin {

	private static final String DECODE_COLOR_REGEX = "\\Q(\\E(\\d+),\\s?(\\d+),\\s?(\\d+)\\Q)\\E";
	private final Pattern COLOR_PATTERN = Pattern.compile(DECODE_COLOR_REGEX);
	
	public ColorHelper(Main parent) {
		super(parent);
	}
	
	public Color decode(String colorName) {
		if (colorName.contains("(")) {
			Matcher matcher = COLOR_PATTERN.matcher(colorName);
			if (matcher.matches()) {
				String r = matcher.group(1);
				String g = matcher.group(2);
				String b = matcher.group(3);
				return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
			}
		}
		
		return getColorForName(colorName);
	}

	protected Color getColorForName(String colorName) {
		try {
		    Field field = Color.class.getField(colorName);
		    return (Color)field.get(null);
		} catch (Exception e) {
		    return null;
		}
	}
}
