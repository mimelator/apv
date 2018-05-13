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
 */
public class FontHelper extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(FontHelper.class.getName());
	
	private static final String BACKUP_FONT_NAME = "Courier New";
	private static final int DEFAULT_CHARS = 2000; //Grab the first 2k chars covers about 10 code blocks
	
	private Map<String, Font> fontMap;

	private PFont currentFont;
	private String defaultCharSet;
	private String previousSampleText;
	
	public FontHelper(Main parent) {
		super(parent);
	}
	
	@Override
	public String getConfig() {
		if (currentFont == null) {
			currentFont = createFontForText("");
		}
		
		StringBuffer buffer = new StringBuffer();
		Font font = (Font)currentFont.getNative();
		buffer.append(Main.FLAGS.FONT_NAME.apvName()).append(" = ").append("\"").append(font.getName()).append("\"").append(System.lineSeparator());
		buffer.append(Main.FLAGS.FONT_SIZE.apvName()).append(" = ").append(font.getSize()).append(System.lineSeparator());
		buffer.append(Main.FLAGS.FONT_STYLE.apvName()).append(" = ").append(getStyleName(font.getStyle())).append(System.lineSeparator()); 
		
		return buffer.toString();
	}

	public PFont getCurrentFont() {
		return currentFont;
	}

	public void setCurrentFont(PFont pfont) {
		this.currentFont = pfont;
		parent.textFont(pfont);
	}

	public int getTotalWidth(String text, PFont font) {
		return text.chars().map(c -> font.getGlyph(c).width).sum();
	}
	
	public PFont createFontForText(String text) {
		String fontName = parent.getConfigString(Main.FLAGS.FONT_NAME.apvName());
		return createFontForText(text, fontName);
	}
	
	public PFont createFontForText(String text, String fontName) {
		if (fontName == null) {
			fontName = BACKUP_FONT_NAME;
		}
		
		if (fontMap == null) {
			initalizeFontMap();
		}
		
		Font instance = fontMap.get(fontName);
		if (instance == null) {
			instance = fontMap.values().iterator().next();
			if (instance == null) {
				logger.warning("Unable to find font: " + fontName + " or " + BACKUP_FONT_NAME);
				return null;
			}
		}
		
		previousSampleText = defaultCharSet + text; 
		int fontSize = parent.getConfigInt(Main.FLAGS.FONT_SIZE.apvName());
		fontSize = Math.max(24, fontSize);
		String fontStyleString = parent.getConfigString(Main.FLAGS.FONT_STYLE.apvName());
		int fontStyle = getFontStyleFromName(fontStyleString);
		
		Font font = instance.deriveFont(fontStyle, fontSize);
		return new PFont(font, true, previousSampleText.toCharArray());
	}
	
	public void updateCurrentFont(Font font) {
		if (fontMap == null) {
			initalizeFontMap();
		}
		
		PFont pf = new PFont(font, true, previousSampleText.toCharArray());
		setCurrentFont(pf);
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
	
	private String getStyleName(int fontStyle) {
		switch (fontStyle) {
		case Font.PLAIN:
			return "PLAIN";
		case Font.ITALIC:
			return "ITALIC";
		case Font.BOLD:
			return "BOLD";
		default:
			return "PLAIN";
		}
	}
	
	private int getFontStyleFromName(String fontStyle) {
		if (fontStyle == null) {
			fontStyle = "PLAIN";
		}
		return new ReflectionHelper<Font, Integer>(Font.class, parent).getField(fontStyle);
	}
}
