package com.arranger.apv.test;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.FontHelper;

import processing.core.PFont;

public class FontInfoTest extends APVPluginTest {
	
	private static final String [] MSGS = new String[] {
			"ʘ‿ʘ",
			"ಠ_ಠ",
			"¯\\_(ツ)_/¯",
			"( ͡° ͜ʖ ͡°)",
			" (•̀ᴗ•́)و ̑̑ ",
			"(ง •̀_•́)ง ",
			"(•_•) ( •_•)>⌐■-■ (⌐■_■)",	
			
	};

	public FontInfoTest() {
	}

	@Test
	public void createCustomFont() throws Exception {
		String collect = Arrays.stream(MSGS).collect(Collectors.joining());
		FontHelper fh = new FontHelper(parent);
		PFont font = fh.createFontForText(collect);
		assert(font != null);
	}
	
	@Override
	protected void setFrameIndexes() {

	}

}
