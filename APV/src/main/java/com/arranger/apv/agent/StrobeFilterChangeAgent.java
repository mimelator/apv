package com.arranger.apv.agent;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class StrobeFilterChangeAgent extends APVPlugin {

	public StrobeFilterChangeAgent(Main parent) {
		super(parent);
		
		parent.getStrobeEvent().register(() -> {
			parent.getCommandSystem().invokeCommand('t');
		});
	}
}
