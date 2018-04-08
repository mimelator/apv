package com.arranger.apv;

import processing.core.PConstants;

public class APVPlugin implements PConstants {
	
	/**
	 * Used for {@link #getDisplayName()}
	 */
	private static final String [] CLASS_NAMES_TO_ERASE = {
			"System", "Factory", "BackDrop", "Shape", "Message"
	};
	
	protected Main parent;

	public APVPlugin(Main parent) {
		this.parent = parent;
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
		return n;
	}
}
