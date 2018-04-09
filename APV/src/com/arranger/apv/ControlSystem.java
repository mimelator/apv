package com.arranger.apv;

import processing.event.KeyEvent;

public abstract class ControlSystem extends APVPlugin {
	
	public static enum CONTROL_MODES {
		AUTO, SNAP, MANUAL, PERLIN;
		
		public CONTROL_MODES getNext() {
			return values()[(ordinal() + 1) % values().length];
		}
	}

	public ControlSystem(Main parent) {
		super(parent);
	}
	
	public boolean allowsMouseLocation() {
		return true;
	}
	
	public void addSettingsMessages() {
		//Do Nothing
	}
	
	public abstract CONTROL_MODES getControlMode();
	
	/**
	 * Returns null if no command needed
	 */
	public abstract KeyEvent getNextCommand();
	
	protected KeyEvent createKeyEvent(int keyCode) {
		return new KeyEvent(this, 0, KeyEvent.RELEASE, 0, (char)0, keyCode);
	}

	protected KeyEvent createKeyEvent(char character) {
		return new KeyEvent(this, 0, KeyEvent.RELEASE, 0, character, 0);
	}
}
