package com.arranger.apv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.cmd.CommandSystem.CommandHandler;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.helpers.Switch.STATE;
import com.arranger.apv.util.RandomHelper;
import com.arranger.apv.util.frame.QuietWindow;

public class APV<T extends APVPlugin> extends APVPlugin implements CommandHandler {
	
	private static final Logger logger = Logger.getLogger(APV.class.getName());
	
	private static final int DEFAULT_QUIET_WINDOW = 30;
	private static final String KEY = "apv.quietWindowSize";
	
	protected Main.SYSTEM_NAMES systemName;
	protected Switch sw; //switch is a keyword
	protected List<T> list;
	protected int index = 0;
	protected Command command, switchCommand, freezeCommand;
	protected CommandHandler handler, switchHandler, freezeHandler;
	protected T currentPlugin;
	protected QuietWindow quietWindow;
	protected int quietWindowSize = DEFAULT_QUIET_WINDOW;
	
	
	public APV(Main parent, Main.SYSTEM_NAMES name) {
		this(parent, name, true);
	}
	
	public APV(Main parent, Main.SYSTEM_NAMES name, boolean allowScramble) {
		super(parent);
		initialize(parent, name, allowScramble);
	}

	@SuppressWarnings("unchecked")
	protected void initialize(Main parent, Main.SYSTEM_NAMES name, boolean allowScramble) {
		list = (List<T>)parent.getConfigurator().loadAVPPlugins(name, allowScramble);
		systemName = name;
		sw = parent.getSwitchForSystem(name);
		quietWindow = new QuietWindow(parent, quietWindowSize);
		quietWindowSize = parent.getConfigInt(KEY);
		setIndex(0, "initialize");
	}

	public Main.SYSTEM_NAMES getSystemName() {
		return systemName;
	}

	public Switch getSwitch() {
		return sw;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public Command getSwitchCommand() {
		return switchCommand;
	}
	
	public Command getFreezeCommand() {
		return freezeCommand;
	}
	
	public void addPlugin(T plugin) {
		list.add(plugin);
	}
	
	public boolean removePlugin(T plugin) {
		return list.remove(plugin);
	}
	
	public void setNextPlugin(APVPlugin plugin, String cause) {
		setNextPlugin(plugin, cause, true);
	}
	
	@SuppressWarnings("unchecked")
	public void setNextPlugin(APVPlugin plugin, String cause, boolean checkQuietWindow) {
		if (currentPlugin.equals(plugin)) {
			return;
		}
		
		if (checkQuietWindow && quietWindow.isInQuietWindow()) {
			logger.fine(String.format("Rejected changing %s for %s due to quiet window", getSystemName(), cause));
			return;
		}
		
		currentPlugin = (T)plugin;
		fireEvent(plugin, cause);
		quietWindow.reset(quietWindowSize);
	}
	
	public boolean isFrozen() {
		if (sw == null) {
			return false;
		}
		return sw.isFrozen();
	}

	public boolean isEnabled() {
		if (sw == null) {
			return !list.isEmpty(); 
		}
 		return sw.isEnabled() && !list.isEmpty(); 
	}
	
	public void setEnabled(boolean isEnabled) {
		if (sw != null) {
			sw.setState(isEnabled ? STATE.ENABLED : STATE.DISABLED);
		}
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void forEach(Consumer<? super T> action) {
		list.forEach(action);
	}
	
	/*
	 * This doesn't scramble the overall order of the plugins
	 * it just jumps to a random index
	 */
	public void scramble(boolean checkSwitch) {
		if (checkSwitch && sw.isFrozen()) {
			return;
		}
		
		//TODO: Use Popularity pool to choose the index list
		
		setIndex((int)parent.random(list.size()), "scramble");
	}
	
	public void increment(String cause) {
		setIndex(index + 1, cause);
	}
	
	public void decrement(String cause) {
		setIndex(index - 1, cause);
	}
	
	public T getPlugin() {
		return getPlugin(false);
	}
	
	public T getPlugin(boolean checkEnabled) {
		if (!checkEnabled || isEnabled()) {
			if (currentPlugin == null) {
				currentPlugin = list.get(0);
			}
			return currentPlugin;
		} else {
			return null;
		}
	}
	
	public T getFirstInstanceOf(Class<? extends T> clazz) {
		T result = list.stream().filter(p -> p.getClass().equals(clazz)).findFirst().get();
		return result;
	}
	
	public List<T> getSiblings(APVPlugin plugin) {
		List<T> results = new ArrayList<T>();
		
		Class<? extends APVPlugin> pluginClass = plugin.getClass();
		forEach(p -> {
			if (pluginClass.equals(p.getClass())) {
				results.add(p);
			}
		});
		
		return results;
	}
	
	@Override
	public String getDisplayName() {
		return systemName.toString();
	}

	@Override
	public void onCommand(Command command, String cause, int modifiers) {
		if (isFrozen()) {
			return;
		}
		
		if (Command.isAltDown(modifiers)) {
			List<T> sibs = getSiblings(getPlugin(false));
			T random = new RandomHelper(parent).random(sibs);
			setNextPlugin(random, cause);
		} else {
			if (Command.isShiftDown(modifiers)) {
				decrement(cause); 		
			} else {
				increment(cause);
			}
		}
	}
	
	public void unregisterHandler() {
		CommandSystem cs = parent.getCommandSystem();
		if (command != null && handler != null) {
			if (!cs.unregisterHandler(command, handler)) {
				throw new RuntimeException("Unable to unregister command: " + command.getDisplayName());
			}
		}
		if (switchCommand != null) {
			if (!cs.unregisterHandler(switchCommand, switchHandler)) {
				throw new RuntimeException("Unable to switch command: " + switchCommand.getDisplayName());
			}
		}
		if (freezeCommand != null) {
			if (!cs.unregisterHandler(freezeCommand, freezeHandler)) {
				throw new RuntimeException("Unable to freeze command: " + freezeCommand.getDisplayName());
			}
		}
	}
	
	public void registerHandler(Command command) {
		registerHandler(command, this);
	}
	
	public void registerHandler(Command command, CommandHandler handler) {
		this.command = command;
		this.handler = handler;
		if (command != null && handler != null) {
			parent.getCommandSystem().registerHandler(command, handler);
		}
	}
	
	public void registerSwitchCommand(Command switchCommand, Command freezeCommand) {
		this.switchCommand = switchCommand;
		switchHandler = new CommandHandler() {
			@Override
			public void onCommand(Command e, String source, int modifiers) {
				if (Command.isMetaDown(modifiers)) {
					sw.toggleFrozen();
				} else {
					sw.toggleEnabled();
				}
			}
		};
				
		parent.getCommandSystem().registerHandler(switchCommand, switchHandler);
		
		if (freezeCommand != null) {
			this.freezeCommand = freezeCommand;
			freezeHandler = new CommandHandler() {
				@Override
				public void onCommand(Command e, String source, int modifiers) {
					sw.toggleFrozen();
				}
			};
					
			parent.getCommandSystem().registerHandler(freezeCommand, freezeHandler);
		}
	}
	
	protected void setIndex(int newIndex, String cause) {
		if (list.isEmpty()) {
			return;
		}
		
		//not checking for quiet window here. Only in the activate function
		if (newIndex >= list.size()) {
			index = 0;
		} else if (newIndex < 0) {
			index = list.size() - 1;
		} else {
			index = newIndex;
		}
		
		//set current plugin
		//if it's enabled
		currentPlugin = null;
		while (currentPlugin == null) {
			currentPlugin = list.get(Math.abs(index) % list.size());
			if (!currentPlugin.isEnabled()) {
				currentPlugin = null;
				scramble(false); //side effects of changing the index
			} 
		}
		
		fireEvent(currentPlugin, cause);
		quietWindow.reset(quietWindowSize);
	}
	
	protected void fireEvent(APVPlugin plugin, String cause) {
		parent.getAPVChangeEvent().fire(this, plugin, cause);
	}
}

