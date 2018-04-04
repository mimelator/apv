package com.arranger.apv.bg;

import com.arranger.apv.Main;

public abstract class BackDropSystem {
	
	protected Main parent;

	public BackDropSystem(Main parent) {
		this.parent = parent;
	}
	
	public abstract void drawBackground();

}
