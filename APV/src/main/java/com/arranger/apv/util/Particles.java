package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class Particles extends APVPlugin {
	
	private static final float INCREMENT_SIZE = .05f;
	private float pct = 1.0f;
	
	public Particles(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandSystem().registerHandler(Command.PARTICLE_SCALAR, (command, source, modifiers) -> {
				if (Command.isShiftDown(modifiers)) {
					decrement();
				} else {
					increment();
				}
			});
		});
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
