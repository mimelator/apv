package com.arranger.apv.test;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class ColorDecodeTest extends APVPluginTest {

	public ColorDecodeTest() {
	}
	
	@Test
	public void testDecodingColr() {
		testColorText("(128,0,128)");
		testColorText("(128, 0, 128)");
	}

	protected void testColorText(String colorName ) {
		Color purple = decode(colorName);
		assert(purple != null);
		assert(purple.getRed() == 128);
		assert(purple.getGreen() == 0);
		assert(purple.getBlue() == 128);
	}
	
	private Color decode(String colorName) {
		if (colorName.contains("(")) {
			//try to parse it
			String quote = "\\Q(\\E(\\d+),\\s?(\\d+),\\s?(\\d+)\\Q)\\E";
			Matcher matcher = Pattern.compile(quote).matcher(colorName);
			if (matcher.matches()) {
				String r = matcher.group(1);
				String g = matcher.group(2);
				String b = matcher.group(3);
				return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
			}
		}
		
		try {
		    Field field = Color.class.getField(colorName);
		    return (Color)field.get(null);
		} catch (Exception e) {
		    return null;
		}
	}
	
	@Override
	protected void setFrameIndexes() {

	}
}
