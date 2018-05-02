package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.PathLocationSystem;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.systems.lite.GridShapeSystem;

public class NoSplitGrid extends LocationAgent {

	public NoSplitGrid(Main parent) {
		super(parent);
	}

	@Override
	protected boolean shouldChangeLocation() {
		LocationSystem	ls = parent.getLocations().getPlugin();
		ShapeSystem plugin = parent.getBackgrounds().getPlugin();
		if (ls instanceof PathLocationSystem) {
			PathLocationSystem pls = (PathLocationSystem)ls;
			if (pls.isSplitter() && plugin instanceof GridShapeSystem) {
				return true;
			}
		}
		return false;
	}

}
