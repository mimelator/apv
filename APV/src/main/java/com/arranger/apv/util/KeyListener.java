package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class KeyListener extends APVPlugin {
	
	@FunctionalInterface
	public static interface KeyEventListener {
		void onKeyEvent(KeyEvent keyEvent);
	}
	
	public static enum KEY_SYSTEMS {
		COMMAND, MENU, REWIND
	}
	
	private KEY_SYSTEMS system = KEY_SYSTEMS.COMMAND;

	public KeyListener(Main parent) {
		super(parent);
		parent.registerMethod("keyEvent", this);
	}

	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() != KeyEvent.RELEASE) {
			return;
		}
		
		KeyEventListener kel = null;
		switch (system) {
			case MENU:
				kel = parent.getMenu();
				break;
			case REWIND:
				kel = parent.getRewindHelper();
				break;
			case COMMAND:
			default:
				kel = parent.getCommandSystem();
		}
		
		kel.onKeyEvent(keyEvent);
	}

	public KEY_SYSTEMS getSystem() {
		return system;
	}

	public void setSystem(KEY_SYSTEMS system) {
		this.system = system;
	}
}
