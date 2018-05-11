package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

public class Gravity extends APVPlugin {
	
	private static final float [] GRAVITY = {.5f, .25f, .1f, .05f, .001f};
	
	protected int gravityIndex = 0;
	
	public Gravity(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.GRAVITY, (command, source, modifiers) -> {
				if (Command.isShiftDown(modifiers)) {
					gravityIndex--;
				} else {
					gravityIndex++;
				}
				
				if (gravityIndex % GRAVITY.length == 0) {
					parent.getEarthquakeEvent().fire();
				}
			});
			cs.registerHandler(Command.SCRAMBLE,
					(command, source, modifiers) -> gravityIndex = (int) parent.random(GRAVITY.length - 1));
		});
	}
	
	public float getCurrentGravity() {
		return GRAVITY[Math.abs(gravityIndex) % GRAVITY.length];
	}
}
