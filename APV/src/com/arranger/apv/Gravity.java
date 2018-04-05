package com.arranger.apv;

import processing.core.PApplet;
import processing.event.KeyEvent;

public class Gravity extends APVPlugin {

	private static final float [] GRAVITY = {.5f, .25f, .1f, .05f, .001f};
	
	protected int gravityIndex = 0;
	
	public Gravity(Main parent) {
		super(parent);
		parent.registerMethod("keyEvent", this);
	}
	
	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.RELEASE) {
			if (keyEvent.getKeyCode() == PApplet.UP) {
				gravityIndex++;
			} else if (keyEvent.getKeyCode() == PApplet.DOWN) {
				gravityIndex--;
			}
		}
	}
	
	public float getCurrentGravity() {
		return GRAVITY[Math.abs(gravityIndex) % GRAVITY.length];
	}
}
