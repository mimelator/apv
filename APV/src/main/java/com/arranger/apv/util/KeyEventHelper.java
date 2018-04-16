package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class KeyEventHelper extends APVPlugin {

	public KeyEventHelper(Main parent) {
		super(parent);
	}
	
	public KeyEvent createScramble() {
		return createKeyEvent(Main.SPACE_BAR_KEY_CODE, this);
	}
	
	public KeyEvent createKeyEvent(int keyCode, Object obj) {
		return new KeyEvent(obj, 0, KeyEvent.RELEASE, 0, (char)0, keyCode);
	}

	public KeyEvent createKeyEvent(char character, Object obj) {
		return createKeyEvent(character, obj, false);
	}
	
	public KeyEvent createKeyEvent(char character, Object obj, boolean hasShift) {
		int modifiers = hasShift ? KeyEvent.SHIFT : 0;
		return new KeyEvent(obj, 0, KeyEvent.RELEASE, modifiers, character, 0);
	}

}
