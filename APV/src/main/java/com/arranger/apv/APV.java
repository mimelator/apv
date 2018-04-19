package com.arranger.apv;

import java.util.List;
import java.util.function.Consumer;

import com.arranger.apv.CommandSystem.CommandHandler;

import processing.event.KeyEvent;

public class APV<T> extends APVPlugin implements CommandHandler {
	
	protected String name;
	protected Switch sw; //switch is a keyword
	protected List<T> list;
	protected int index = 0;
	
	public APV(Main parent, String name) {
		this(parent, name, true);
	}
	
	@SuppressWarnings("unchecked")
	public APV(Main parent, String name, boolean allowScramble) {
		super(parent);
		this.list = (List<T>)parent.getConfigurator().loadAVPPlugins(name, allowScramble);
		this.name = name;
		this.sw = parent.getSwitch(name);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int newIndex) {
		if (newIndex >= list.size()) {
			index = 0;
		} else if (newIndex < 0) {
			index = list.size() - 1;
		} else {
			index = newIndex;
		}
	}

	public String getName() {
		return name;
	}

	public Switch getSwitch() {
		return sw;
	}

	public boolean isEnabled() {
		if (sw == null) {
			return true; //we're not a switcher
		}
		return sw.isEnabled() && !list.isEmpty(); 
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
		setIndex((int)parent.random(list.size()));
	}
	
	public void increment() {
		setIndex(index + 1);
	}
	
	public void decrement() {
		setIndex(index - 1);
	}
	
	public T getPlugin() {
		return getPlugin(false);
	}
	
	public T getPlugin(boolean checkEnabled) {
		if (!checkEnabled  || isEnabled()) {
			return list.get(Math.abs(index) % list.size());
		} else {
			return null;
		}
	}

	@Override
	public void onKeyPressed(KeyEvent event) {
		if (event.isShiftDown()) {
			decrement(); 		
		} else {
			increment();
		}
	}
	
	public void registerCommand(int key, String name, String helpText, CommandHandler handler) {
		parent.getCommandSystem().registerCommand(key, name, helpText, handler);
	}
	
	public void registerCommand(char key, String name, String helpText) {
		parent.getCommandSystem().registerCommand(key, name, helpText, this);
	}
	
	public void registerSwitchCommand(char charCode) {
		parent.getCommandSystem().registerCommand(charCode, "Toggle " + getName(), 
									"Toggles between enabling or freezing " + getName() + ".  Use Command-" + charCode + " to Freeze/UnFreeze", 
									(event) -> {
										if (event.isMetaDown()) {
											sw.toggleFrozen();
										} else {
											sw.toggleEnabled();
										}
									});
	}
}
