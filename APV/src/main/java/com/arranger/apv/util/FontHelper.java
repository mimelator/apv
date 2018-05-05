package com.arranger.apv.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PFont;

/**
 * This class will help out with loading different fonts
 * as well as calculating widths for maximum display on screens
 * 
 * Right now this is parked in archive because it isn't in use
 * @see https://forum.processing.org/two/discussion/comment/83538/#Comment_83538
 */
public class FontHelper extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(FontHelper.class.getName());
	
	private static final int DEFAULT_FONT_STYLE = Font.PLAIN;
	private static final int DEFAULT_FONT_SIZE = 32;
	private static final String DEFAULT_FONT_NAME = "ArialUnicodeMS";
	private static final String BACKUP_FONT_NAME = "Arial";
	private static final int DEFAULT_CHARS = 2000; //Grab the first 2k chars covers about 10 code blocks
	
	
	private Map<String, Font> fontMap;

	private PFont defaultFont;
	private String defaultCharSet;
	
	public FontHelper(Main parent) {
		super(parent);
	}
	
	public PFont getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(PFont defaultFont) {
		this.defaultFont = defaultFont;
		parent.textFont(defaultFont);
	}

	public int getTotalWidth(String text, PFont font) {
		return text.chars().map(c -> font.getGlyph(c).width).sum();
	}
	
	public PFont createFontForText(String text) {
		return createFontForText(text, DEFAULT_FONT_NAME);
	}
	
	public PFont createFontForText(String text, String fontName) {
		if (fontMap == null) {
			initalizeFontMap();
		}
		
		Font instance = fontMap.get(fontName);
		if (instance == null) {
			instance = fontMap.get(BACKUP_FONT_NAME);
			if (instance == null) {
				logger.warning("Unable to find font: " + fontName + " or " + BACKUP_FONT_NAME);
				return null;
			}
		}
		
		String resultText = defaultCharSet + text; 
		Font font = instance.deriveFont(DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
		return new PFont(font, true, resultText.toCharArray());
	}
	
	private void initalizeFontMap() {
		fontMap = new HashMap<String, Font>();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (Font f : ge.getAllFonts()) {
			fontMap.put(f.getName(), f);
		}
		
		char[] charset = new char[DEFAULT_CHARS];
		for (int index = 0; index < DEFAULT_CHARS; index++) {
			charset[index] = (char)index;
		}
		
		defaultCharSet = new String(charset);
	}
}
