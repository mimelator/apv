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

import processing.event.KeyEvent;

public class APV<T extends APVPlugin> extends APVPlugin implements CommandHandler {
	
	private static final Logger logger = Logger.getLogger(APV.class.getName());
	
	protected Main.SYSTEM_NAMES systemName;
	protected Switch sw; //switch is a keyword
	protected List<T> list;
	protected int index = 0;
	protected Command command, switchCommand;
	protected CommandHandler handler, switchHandler;
	protected T currentPlugin;
	
	
	public APV(Main parent, Main.SYSTEM_NAMES name) {
		this(parent, name, true);
	}
	
	@SuppressWarnings("unchecked")
	public APV(Main parent, Main.SYSTEM_NAMES name, boolean allowScramble) {
		super(parent);
		this.list = (List<T>)parent.getConfigurator().loadAVPPlugins(name, allowScramble);
		this.systemName = name;
		this.sw = parent.getSwitchForSystem(name);
		setIndex(0, "");
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
	
	@SuppressWarnings("unchecked")
	public void setNextPlugin(APVPlugin plugin, String cause) {
		if (currentPlugin.equals(plugin)) {
			return;
		}
		currentPlugin = (T)plugin;
		fireEvent(plugin, cause);
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
		sw.setState(isEnabled ? STATE.ENABLED : STATE.DISABLED);
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void forEach(Consumer<? super T> action) {
		list.forEach(action);
	}
	
	public void scramble(boolean checkSwitch) {
		if (checkSwitch && sw.isFrozen()) {
			return;
		}
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
	public void onKeyPressed(KeyEvent event) {
		if (isFrozen()) {
			return;
		}
		
		if (event.isAltDown()) {
			//find the next sibling
			List<T> sibs = getSiblings(getPlugin(false));
			T random = new RandomHelper(parent).random(sibs);
			setNextPlugin(random, "KeyEvent");
		} else {
			if (event.isShiftDown()) {
				decrement("KeyEvent"); 		
			} else {
				increment("KeyEvent");
			}
		}
	}
	
	public void unregisterHandler() {
		CommandSystem cs = parent.getCommandSystem();
		if (command != null && handler != null) {
			if (!cs.unregisterHandler(command, handler)) {
				logger.warning("Unable to unregister command: " + command.getDisplayName());
			}
		}
		if (switchCommand != null) {
			if (!cs.unregisterHandler(switchCommand, switchHandler)) {
				logger.warning("Unable to switch command: " + switchCommand.getDisplayName());
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
	
	public void registerSwitchCommand(Command command) {
		this.switchCommand = command;
		switchHandler = new CommandHandler() {
			@Override
			public void onKeyPressed(KeyEvent e) {
				if (e.isMetaDown()) {
					sw.toggleFrozen();
				} else {
					sw.toggleEnabled();
				}
			}
		};
				
		parent.getCommandSystem().registerHandler(command, switchHandler);
	}
	
	protected void setIndex(int newIndex, String cause) {
		if (list.isEmpty()) {
			return;
		}
		
		if (newIndex >= list.size()) {
			index = 0;
		} else if (newIndex < 0) {
			index = list.size() - 1;
		} else {
			index = newIndex;
		}
		
		//set current plugin
		currentPlugin = list.get(Math.abs(index) % list.size());
		fireEvent(currentPlugin, cause);
	}
	
	protected void fireEvent(APVPlugin plugin, String cause) {
		parent.getAPVChangeEvent().fire(this, plugin, cause);
	}
}

