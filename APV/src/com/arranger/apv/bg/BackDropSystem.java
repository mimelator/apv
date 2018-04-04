package com.arranger.apv.bg;

import com.arranger.apv.Main;

import processing.core.PConstants;

public abstract class BackDropSystem implements PConstants {
	
	protected Main parent;

	public BackDropSystem(Main parent) {
		this.parent = parent;
	}
	
	public abstract void drawBackground();

}
