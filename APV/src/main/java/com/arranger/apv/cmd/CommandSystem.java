package com.arranger.apv.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.KeyListener.KeyEventListener;

import processing.event.KeyEvent;

public class CommandSystem extends APVPlugin implements KeyEventListener {
	
	private static final Logger logger = Logger.getLogger(CommandSystem.class.getName());
	
	protected Map<Command, List<RegisteredCommandHandler>> registeredCommands = new HashMap<Command, List<RegisteredCommandHandler>>();
	protected Map<String, Command> keyBindingMap = new HashMap<String, Command>();
	
	private RegisteredCommandHandler lastCommand;
	
	public CommandSystem(Main parent) {
		super(parent);
	}
	
	/**
	 * Called from {@link Main#reset()}
	 * Reset the interceptors
	 */
	public void reset() {
		
	}
	
	public void invokeScramble(String source) {
		invokeCommand(Command.SCRAMBLE, source, 0);
	}
	
	public boolean unregisterHandler(Command command, CommandHandler handler) {
		List<RegisteredCommandHandler> list = registeredCommands.get(command);
		if (list != null) {
			Optional<RegisteredCommandHandler> rch = list.stream().filter(e -> {return e.handler.equals(handler);}).findFirst();
			if (rch.isPresent()) {
				return list.remove(rch.get());
			} else {
				throw new RuntimeException("Unable to unregister: " + command.getDisplayName() + " for handler: " + handler.toString());
			}
		}
		return false;
	}
	
	public void registerHandler(Command command, CommandHandler handler) {
		RegisteredCommandHandler rch = new RegisteredCommandHandler(command, handler);
		List<RegisteredCommandHandler> list = registeredCommands.get(rch.command);
		if (list == null) {
			list = new ArrayList<RegisteredCommandHandler>();
			registeredCommands.put(rch.command, list);
		}
		keyBindingMap.put(command.getKey(), command);
		list.add(rch);
	}
	
	public void onKeyEvent(KeyEvent keyEvent) {
		String key = Command.getKeyForKeyEvent(keyEvent);
		Command cmd = keyBindingMap.get(key);
		int modifiers = keyEvent.getModifiers();
		if (cmd != null) {
			String source = Command.getSource(keyEvent);
			logger.log(Level.INFO, "invoking keyEvent: " + cmd);
			
			if (!parent.isCommandMode()) {
				//Filter out many key commands when in default mode
				if (!parent.isDefaultCommand(cmd)) {
					System.out.println("Not executing advanced command: " + cmd.name());
					return;
				}
			} 
				
			invokeCommand(cmd, source, modifiers);
		} else {
			if (!Command.isMetaDown(modifiers) && !Command.isControlDown(modifiers) && !Command.isAltDown(modifiers)) {
				System.out.println("Unable to find commands for key: " + key);
			}
		}
	}

	public void invokeCommand(Command command, String source, int modifiers) {
		try {
			List<RegisteredCommandHandler> list = registeredCommands.get(command);
			if (list != null  && !list.isEmpty()) {
				list.forEach(c -> {
					logger.log(Level.INFO, "invoking command: " + c.getName());
					c.handler.onCommand(command, source, modifiers);
				});
				lastCommand = list.get(list.size() - 1);
				parent.getCommandInvokedEvent().fire(lastCommand.command, source);
			} else {
				System.out.println("No handlers for command: " + command.name());
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	@FunctionalInterface
	public static interface CommandHandler {
		public void onCommand(Command command, String source, int modifiers);
	}
	
	@FunctionalInterface
	public static interface MessageHandler {
		public void onMessage(String msg);
	}

	public Map<Command, List<RegisteredCommandHandler>> getCommands() {
		return new HashMap<Command, List<RegisteredCommandHandler>>(registeredCommands);
	}

	public Command getLastCommand() {
		return (lastCommand != null) ? lastCommand.command : null;
	}
	
	

	public static class RegisteredCommandHandler {
		
		private CommandHandler handler;
		private Command command;
		
		private RegisteredCommandHandler(Command command, CommandHandler handler) {
			this.command = command;
			this.handler = handler;
		}
		
		public Command getCommand() {
			return command;
		}

		public int getCommandKey() {
			return command.getCommandKey();
		}

		public char getCharKey() {
			return command.getCharKey();
		}

		public String getName() {
			return command.name();
		}

		public String getHelpText() {
			return command.getHelpText();
		}
	}
}
