package com.arranger.apv.back;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;

public class BackDropSystem extends ShapeSystem {
	

	public BackDropSystem(Main parent) {
		super(parent, null);
	}
	
	@Override
	public void setup() {
	}

	@Override
	public void draw() {
		drawBackground();
	}

	public void drawBackground() {
		parent.background(0);
	}
	
	/**
	 * Whether or not to push matrix and styles before and after drawing
	 */
	public boolean isSafe() {
		return true;
	}
}
