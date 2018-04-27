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
import com.arranger.apv.util.KeyEventHelper;

import processing.event.KeyEvent;

public class CommandSystem extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(CommandSystem.class.getName());
	
	protected Map<String, List<RegisteredCommandHandler>> registeredCommands = new HashMap<String, List<RegisteredCommandHandler>>();
	
	private KeyEventHelper keyEventHelper;
	private MessageModeInterceptor messageModeInterceptor;
	private SceneSelectInterceptor sceneSelectInterceptor;
	private RegisteredCommandHandler lastCommand;
	
	public CommandSystem(Main parent) {
		super(parent);
		messageModeInterceptor = new MessageModeInterceptor(parent);
		sceneSelectInterceptor = new SceneSelectInterceptor(parent);
		keyEventHelper = new KeyEventHelper(parent);
		parent.registerMethod("keyEvent", this);
	}

	public MessageModeInterceptor getMessageModeInterceptor() {
		return messageModeInterceptor;
	}
	
	public SceneSelectInterceptor getSceneSelectInterceptor() {
		return sceneSelectInterceptor;
	}
	
	/**
	 * Called from {@link Main#reset()}
	 * Reset the interceptors
	 */
	public void reset() {
		messageModeInterceptor.reset();
		sceneSelectInterceptor.reset();
	}
	
	public void invokeScramble(String source) {
		invokeCommand(Command.SCRAMBLE, source);
	}
	
	public void invokeCommand(Command command, String source) {
		keyEvent(keyEventHelper.createKeyEvent(command, source, 0));
	}
	
	public boolean unregisterHandler(Command command, CommandHandler handler) {
		List<RegisteredCommandHandler> list = registeredCommands.get(command.getKey());
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
		List<RegisteredCommandHandler> list = registeredCommands.get(rch.command.getKey());
		if (list == null) {
			list = new ArrayList<RegisteredCommandHandler>();
			registeredCommands.put(rch.command.getKey(), list);
		}
		list.add(rch);
	}
	
	public void keyEvent(KeyEvent keyEvent) {
		try {
			if (keyEvent.getAction() != KeyEvent.RELEASE) {
				return;
			}
			
			char charKey = keyEvent.getKey();
			if (messageModeInterceptor.intercept(charKey)) {
				return;
			}
			
			if (sceneSelectInterceptor.intercept(charKey)) {
				return;
			}
			
			String key = Command.getKeyForKeyEvent(keyEvent);
			List<RegisteredCommandHandler> list = registeredCommands.get(key);
			if (list != null  && !list.isEmpty()) {
				list.forEach(c -> {
					c.handler.onKeyPressed(keyEvent);
				});
				lastCommand = list.get(list.size() - 1);
				parent.getCommandInvokedEvent().fire(lastCommand.getCommand(), keyEventHelper.getSource(keyEvent));
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	@FunctionalInterface
	public static interface CommandHandler {
		public void onKeyPressed(KeyEvent event);
	}
	
	@FunctionalInterface
	public static interface MessageHandler {
		public void onMessage(String msg);
	}
	
	public String [] getInterceptorHelpMessages() {
		String [] results = new String[] {
			messageModeInterceptor.getHelpText(),
			sceneSelectInterceptor.getHelpText(),	
		};
		return results;
	}
	
	public Map<String, List<RegisteredCommandHandler>> getCommands() {
		return registeredCommands;
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
