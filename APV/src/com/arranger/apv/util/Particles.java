package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class Particles extends APVPlugin {
	
	private static final float INCREMENT_SIZE = .05f;

	private float pct = 1.0f;
	
	public Particles(Main parent) {
		super(parent);
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand(PApplet.UP, "Up Arrow", "Increases Number Particles", event -> increment());
		cs.registerCommand(PApplet.DOWN, "Down Arrow", "Decreases Number Particles", event -> decrement());
	}

	public float getPct() {
		return pct;
	}

	public void setPct(float pct) {
		this.pct = pct;
	}

	public void increment() {
		pct += INCREMENT_SIZE;
	}
	
	public void decrement() {
		pct -= INCREMENT_SIZE;
	}
}