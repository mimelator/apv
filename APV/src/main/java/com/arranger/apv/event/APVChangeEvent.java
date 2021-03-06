package com.arranger.apv.event;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.APVChangeEvent.APVChangeEventHandler;

public class APVChangeEvent extends APVEvent<APVChangeEventHandler> {
	
	@FunctionalInterface
	public static interface APVChangeEventHandler {
		void onPluginChange(APV<? extends APVPlugin> apv, APVPlugin plugin, String cause);
	}

	public APVChangeEvent(Main parent) {
		super(parent, EventTypes.APV_CHANGE);
	}

	public void fire(APV<? extends APVPlugin> apv, APVPlugin plugin, String cause) {
		List<APVChangeEventHandler> temp = new ArrayList<APVChangeEventHandler>(listeners);
		temp.forEach(l -> ((APVChangeEventHandler)l).onPluginChange(apv, plugin, cause));
	}
}
