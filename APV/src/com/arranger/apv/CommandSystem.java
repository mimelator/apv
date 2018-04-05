package com.arranger.apv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.event.KeyEvent;

public class CommandSystem extends APVPlugin {
	
	protected Map<Integer, List<APVCommand>> keyCommands = new HashMap<Integer, List<APVCommand>>();
	protected Map<Character, List<APVCommand>> charCommands = new HashMap<Character, List<APVCommand>>();
	
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
	
	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.RELEASE) {
			List<APVCommand> list = keyCommands.get(keyEvent.getKeyCode());
			if (list == null || list.isEmpty()) {
				list = charCommands.get(Character.toLowerCase(keyEvent.getKey()));
			}
			
			if (list != null) {
				list.forEach(c -> c.handler.onKeyPressed(keyEvent));
			}
		}
	}
	
	@FunctionalInterface
	public static interface CommandHandler {
		public void onKeyPressed(KeyEvent event);
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
