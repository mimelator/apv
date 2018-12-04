package com.arranger.apv.control;

import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public abstract class ControlSystem extends APVPlugin {
	
	public static enum CONTROL_MODES {
		AUTO, MANUAL, PERLIN;
		
		public CONTROL_MODES getNext() {
			return values()[(ordinal() + 1) % values().length];
		}
		
		public CONTROL_MODES getPrevious() {
			CONTROL_MODES[] values = values();
			int index = ordinal();
			if (index == 0) {
				index = values[values.length - 1].ordinal();
				index++;
			}
			return values[Math.abs((index + -1)) % values.length];
		}
	}

	public ControlSystem(Main parent) {
		super(parent);
	}
	
	public boolean allowsMouseLocation() {
		return true;
	}
	
	public void addSettingsMessages() {
		Point2D pt = parent.getCurrentPoint();
		parent.addSettingsMessage(String.format("   ---Location: [%s, %s]", (int)pt.getX(), (int)pt.getY()));
	}
	
	public abstract CONTROL_MODES getControlMode();
	
	/**
	 * Returns null if no command needed
	 */
	public abstract Command getNextCommand();
}
