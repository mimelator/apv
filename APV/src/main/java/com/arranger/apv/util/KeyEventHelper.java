package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class KeyEventHelper extends APVPlugin {

	public KeyEventHelper(Main parent) {
		super(parent);
	}
	
	public KeyEvent createScramble() {
		return createKeyEvent(Command.SCRAMBLE);
	}
	
	public KeyEvent createKeyEvent(Command command) {
		return createKeyEvent(command, 0);
	}
	
	public KeyEvent createKeyEvent(Command command, int modifiers) {
		if (command.getCommandKey() != 0) {
			return new KeyEvent(command, 0, KeyEvent.RELEASE, modifiers, (char)0, command.getCommandKey());
		} else {
			return new KeyEvent(command, 0, KeyEvent.RELEASE, modifiers, command.getCharKey(), 0);
		}
	}
}
