package com.arranger.apv;

import processing.core.PApplet;

public class Gravity extends APVPlugin {
	

	private static final float [] GRAVITY = {.5f, .25f, .1f, .05f, .001f};
	
	protected int gravityIndex = 0;
	
	public Gravity(Main parent) {
		super(parent);
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand(PApplet.UP, "Gravity++", "Increases Gravity", event -> gravityIndex++);
		cs.registerCommand(PApplet.DOWN, "Gravity--", "Decreases Gravity", event -> gravityIndex--);
		cs.registerCommand(Main.SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things", event -> gravityIndex = (int)parent.random(GRAVITY.length - 1));
	}
	
	public float getCurrentGravity() {
		return GRAVITY[Math.abs(gravityIndex) % GRAVITY.length];
	}
}
