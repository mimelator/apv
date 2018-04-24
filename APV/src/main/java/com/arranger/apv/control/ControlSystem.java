package com.arranger.apv.control;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.KeyEventHelper;

import processing.event.KeyEvent;

public abstract class ControlSystem extends APVPlugin {
	
	protected KeyEventHelper keyEventHelper;
	
	public static enum CONTROL_MODES {
		AUTO, SNAP, MANUAL, PERLIN;
		
		public CONTROL_MODES getNext() {
			return values()[(ordinal() + 1) % values().length];
		}
		
		public CONTROL_MODES getPrevious() {
			CONTROL_MODES[] values = values();
			int index = ordinal();
			if (index == 0) {
				index = values[values.length - 1].ordinal();
				index++;
			}
			return values[Math.abs((index + -1)) % values.length];
		}
	}

	public ControlSystem(Main parent) {
		super(parent);
		keyEventHelper = new KeyEventHelper(parent);
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
}
