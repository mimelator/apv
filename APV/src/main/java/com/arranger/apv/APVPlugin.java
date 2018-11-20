package com.arranger.apv;

import processing.core.PConstants;

public class APVPlugin implements PConstants {
	
	public static int NO_ALPHA = 255;
	private static int ID = 0;
	
	/**
	 * Used for {@link #getDisplayName()}
	 */
	private static final String [] CLASS_NAMES_TO_ERASE = {
			"System", "Factory", "BackDrop", "Shape", "Message", "Location", 
	};
	
	protected Main parent;
	protected int id;
	protected boolean enabled = true;

	public APVPlugin(Main parent) {
		this.parent = parent;
		this.id = ID++;
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public Main getParent() {
		return parent;
	}
	
	public String getDisplayName() {
		String n = getName();
		for (String erase : CLASS_NAMES_TO_ERASE) {
			n = n.replaceAll(erase, "");
		}
		
		//erase too much?
		if (n.isEmpty()) {
			n = getClass().getSimpleName();
		}
		
		return n;
	}
	
	public String getConfig() {
		return String.format("{%s : []}", getName());
	}
	
	public void setup() {
		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggleEnabled() {
		setEnabled(!enabled);
	}
}
