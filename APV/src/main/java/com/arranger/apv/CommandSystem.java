package com.arranger.apv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.event.KeyEvent;

public class CommandSystem extends APVPlugin {
	
	private static final char MESSAGE_ENTRY_KEY = '~';
	protected Map<Integer, List<APVCommand>> keyCommands = new HashMap<Integer, List<APVCommand>>();
	protected Map<Character, List<APVCommand>> charCommands = new HashMap<Character, List<APVCommand>>();
	protected List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
	
	private APVCommand lastCommand;
	private boolean isMessageEntryMode = false;
	private StringBuffer messageText;
	
	@FunctionalInterface
	public static interface IVisitor {
		void visit(Map.Entry<?, List<APVCommand>> commandEntry);
	}
	
	
	public void visitCommands(boolean isKeyCommands, IVisitor visitor) {
		if (isKeyCommands) {
			keyCommands.entrySet().forEach(e -> visitor.visit(e));
		} else {
			charCommands.entrySet().forEach(e -> visitor.visit(e));
		}
	}

	public CommandSystem(Main parent) {
		super(parent);
		parent.registerMethod("keyEvent", this);
	}
	
	public void registerCommand(int key, String name, String helpText, CommandHandler handler) {
		APVCommand apvCommand = new APVCommand(key, name, helpText, handler);
		List<APVCommand> list = keyCommands.get(key);
		if (list == null) {
			list = new ArrayList<APVCommand>();
			keyCommands.put(key, list);
		}
		list.add(apvCommand);
	}
	
	public void registerCommand(char key, String name, String helpText, CommandHandler handler) {
		key = Character.toLowerCase(key);
		APVCommand apvCommand = new APVCommand(key, name, helpText, handler);
		List<APVCommand> list = charCommands.get(key);
		if (list == null) {
			list = new ArrayList<APVCommand>();
			charCommands.put(key, list);
		}
		list.add(apvCommand);
	}
	
	public void registerMessageListeners(MessageHandler handler) {
		messageHandlers.add(handler);
	}
	
	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() != KeyEvent.RELEASE) {
			return;
		}
		
		char key = keyEvent.getKey();
		if (isMessageMode(key)) {
			return;
		}
		
		if (key == SHIFT) {
			return;
		}
		
		List<APVCommand> list = keyCommands.get(keyEvent.getKeyCode());
		if (list == null || list.isEmpty()) {
			list = charCommands.get(Character.toLowerCase(key));
		}
		
		if (list != null  && !list.isEmpty()) {
			list.forEach(c -> c.handler.onKeyPressed(keyEvent));
			lastCommand = list.get(0);
		}
	}

	protected boolean isMessageMode(char key) {
		if (key == MESSAGE_ENTRY_KEY) {
			if (isMessageEntryMode) {
				onMessage(messageText.toString());
			} else {
				//brand new
				messageText = new StringBuffer();
			}
			isMessageEntryMode = !isMessageEntryMode;
			return true;
		}
		
		if (isMessageEntryMode) {
			if (key != 65535) { //Don't understand what meta character i should be looking for
				messageText.append(String.valueOf(key));
			}
			return true;
		}
		
		return false;
	}
	
	protected void onMessage(String text) {
		messageHandlers.stream().forEach(e -> e.onMessage(text));
	}
	
	@FunctionalInterface
	public static interface CommandHandler {
		public void onKeyPressed(KeyEvent event);
	}
	
	@FunctionalInterface
	public static interface MessageHandler {
		public void onMessage(String msg);
	}
	
	public Map<Integer, List<APVCommand>> getKeyCommands() {
		return keyCommands;
	}

	public Map<Character, List<APVCommand>> getCharCommands() {
		return charCommands;
	}

	public APVCommand getLastCommand() {
		return lastCommand;
	}

	public static class APVCommand {
		
		private CommandHandler handler;
		private int commandKey;
		private char charKey;
		private String name, helpText;
		
		private APVCommand(int commandKey, String name, String helpText, CommandHandler handler) {
			this.commandKey = commandKey;
			this.name = name;
			this.helpText = helpText;
			this.handler = handler;
		}
		
		private APVCommand(char charKey, String name, String helpText, CommandHandler handler) {
			this.charKey = charKey;
			this.name = name;
			this.helpText = helpText;
			this.handler = handler;
		}

		public int getCommandKey() {
			return commandKey;
		}

		public char getCharKey() {
			return charKey;
		}

		public String getName() {
			return name;
		}

		public String getHelpText() {
			return helpText;
		}
	}
}
