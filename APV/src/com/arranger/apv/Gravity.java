package com.arranger.apv;

import processing.core.PApplet;
import processing.event.KeyEvent;

public class Gravity {

	private static final float [] GRAVITY = {.5f, .25f, .1f, .05f, .001f};
	
	protected int gravityIndex = 0;
	
	public Gravity(Main parent) {
		parent.registerMethod("keyEvent", this);
	}
	
	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.RELEASE && keyEvent.getKeyCode() == PApplet.UP) {
			gravityIndex++;
		}
	}
	
	public float getCurrentGravity() {
		return GRAVITY[gravityIndex % GRAVITY.length];
	}
}
