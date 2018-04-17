package com.arranger.apv.archive;

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

	public FontHelper(Main parent) {
		super(parent);
	}

	public int getTotalWidth(String text, PFont font) {
		return text.chars().map(c -> font.getGlyph(c).width).sum();
	}
}
