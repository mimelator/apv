package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

import processing.event.KeyEvent;

public class KeyEventHelper extends APVPlugin {

	public KeyEventHelper(Main parent) {
		super(parent);
	}
	
	public String getSource(KeyEvent keyEvent) {
		String source = keyEvent.getNative().toString();
		if (source.contains("EVENT")) { // this is extra data
			source = "KeyEvent";
		}
		return source;
	}
	
	public KeyEvent createScramble(String source) {
		return createKeyEvent(Command.SCRAMBLE, source, 0);
	}
	
	public KeyEvent createKeyEvent(Command command, String source) {
		return createKeyEvent(command, command, 0);
	}
	
	public KeyEvent createKeyEvent(Command command, Object source, int modifiers) {
		if (command.getCommandKey() != 0) {
			return new KeyEvent(source, 0, KeyEvent.RELEASE, modifiers, (char)0, command.getCommandKey());
		} else {
			return new KeyEvent(source, 0, KeyEvent.RELEASE, modifiers, command.getCharKey(), 0);
		}
	}
	
	public KeyEvent createKeyEvent(KeyEvent evt, String source) {
		return new KeyEvent(source, 
				evt.getMillis(), 
				evt.getAction(), 
				evt.getModifiers(), 
				evt.getKey(), 
				evt.getKeyCode());
	}
}
