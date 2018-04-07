package com.arranger.apv;

import processing.core.PConstants;

public class APVPlugin implements PConstants {

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
}
