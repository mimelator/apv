package com.arranger.apv;

import processing.core.PConstants;

public class APVPlugin implements PConstants {
	
	public static int NO_ALPHA = 255;
	private static int ID = 0;
	
	/**
	 * Used for {@link #getDisplayName()}
	 */
	private static final String [] CLASS_NAMES_TO_ERASE = {
			"System", "Factory", "BackDrop", "Shape", "Message", "Location", "Menu" 
	};
	
	protected Main parent;
	protected int id;
	protected int popularityIndex = 1;
	protected boolean enabled = true;
	protected boolean supportsExtendedConfig = true;

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
	
	public String getConfigEx() {
		if (supportsExtendedConfig) {
			return String.format("%s {enabled:%s, popularityIndex:%s}", getConfig(), isEnabled(), getPopularityIndex());
		} else {
			return getConfig();
		}
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

	public int getPopularityIndex() {
		return popularityIndex;
	}

	public void setPopularityIndex(int popularityIndex) {
		this.popularityIndex = Math.max(0, popularityIndex);
	}
}
