package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.control.ControlSystem;
import com.arranger.apv.control.ControlSystem.CONTROL_MODES;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.util.Configurator;

public class MouseControlAgent extends LocationAgent {
	
	private static final int DEFAULT_NUM_FRAMES_TO_SWITCH = 100;

	private int prevMouseX, prevMouseY;
	private int numFramesNotMoving = 0;
	private int numFramesToSwitch;
	private CONTROL_MODES targetMode;
	
	
	public MouseControlAgent(Main parent, int numFramesToSwitch, CONTROL_MODES targetMode) {
		super(parent);
		this.numFramesToSwitch = numFramesToSwitch;
		this.targetMode = targetMode;
		
		//Looking for two things
		// 1. A mouse that doesn't move.  Change to Perlin and hide the mouse.
		// 2. A mouse that is moving.  Ensure we're in Manual and that the mouse isn't hiding
		registerAgent(getDrawEvent(), () -> {
			//is mouse moving?
			if (parent.mouseX == prevMouseX && parent.mouseY == prevMouseY) {
				//how long has it been
				if (numFramesNotMoving > numFramesToSwitch) {
					if (parent.getControl().getControlMode() != targetMode) {
						parent.activateNextPlugin(SYSTEM_NAMES.CONTROLS, targetMode.name(), getName());
						parent.setCurrentControlMode(targetMode);
						
					}
					parent.noCursor();
				}
				
				numFramesNotMoving++;
				
			} else {
				//mouse moved
				numFramesNotMoving = 0;
				prevMouseX = parent.mouseX;
				prevMouseY = parent.mouseY;		
				
				//if the mouse moved, switch to mouseControl
				if (parent.getControl().getControlMode() != CONTROL_MODES.MANUAL) {
					parent.mouseControl();
				}
				parent.cursor();
			}
		});
	}
	
	public MouseControlAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_NUM_FRAMES_TO_SWITCH), 
				CONTROL_MODES.valueOf(ctx.getString(1, CONTROL_MODES.PERLIN.name())));
	}
	
	@Override
	public String getConfig() {
		//{MouseControlAgent : [100, Perlin]}
		return String.format("{%s : [%s, %s]}", getName(), numFramesToSwitch, targetMode.name());
	}

	protected boolean shouldChangeLocation() {
		ControlSystem cs = parent.getControl();
		LocationSystem	ls = parent.getLocations().getPlugin();
		return !cs.allowsMouseLocation() && ls instanceof MouseLocationSystem;
	}
}
