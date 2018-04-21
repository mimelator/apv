package com.arranger.apv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.cmd.MessageModeInterceptor;
import com.arranger.apv.cmd.SceneSelectInterceptor;
import com.arranger.apv.util.KeyEventHelper;

import processing.event.KeyEvent;

public class CommandSystem extends APVPlugin {
	
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
	 * Called from {@link Main#panic()}
	 * Reset the interceptors
	 */
	public void panic() {
		messageModeInterceptor.panic();
		sceneSelectInterceptor.panic();
	}
	
	public void invokeScramble() {
		invokeCommand(Command.SCRAMBLE);
	}
	
	public void invokeCommand(Command command) {
		keyEvent(keyEventHelper.createKeyEvent(command));
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
		
		String key = (charKey != 0) ? String.valueOf(Character.toLowerCase(charKey)) : String.valueOf(keyEvent.getKeyCode());
		List<RegisteredCommandHandler> list = registeredCommands.get(key);
		if (list != null  && !list.isEmpty()) {
			list.forEach(c -> c.handler.onKeyPressed(keyEvent));
			lastCommand = list.get(0);
			
			parent.getCommandInvokedEvent().fire();
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
