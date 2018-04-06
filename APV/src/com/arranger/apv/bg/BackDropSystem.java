package com.arranger.apv.bg;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;

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
}
