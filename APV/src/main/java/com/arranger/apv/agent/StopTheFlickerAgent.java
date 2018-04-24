package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.PathLocationSystem;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.systems.lite.GridShapeSystem;

public class StopTheFlickerAgent extends BaseAgent {

	public StopTheFlickerAgent(Main parent) {
		super(parent);
		
		registerAgent(getDrawEvent(), () -> {
			LocationSystem ls = parent.getLocations().getPlugin();
			if (ls instanceof PathLocationSystem) {
				ShapeSystem ss = parent.getBackgrounds().getPlugin();
				if (ss instanceof GridShapeSystem) {
					if (((PathLocationSystem)ls).isSplitter()) {
						invokeCommand(Command.CYCLE_LOCATIONS);
					}
				}
			}
		});
	}
}
