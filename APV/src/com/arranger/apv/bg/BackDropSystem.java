package com.arranger.apv.bg;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public abstract class BackDropSystem extends APVPlugin {
	

	public BackDropSystem(Main parent) {
		super(parent);
	}
	
	public abstract void drawBackground();

}
