package com.arranger.apv.agent;

import com.arranger.apv.Main;

public class StrobeFilterChangeAgent extends BaseAgent {

	public StrobeFilterChangeAgent(Main parent) {
		super(parent);
		
		registerAgent(getStrobeEvent(), () -> {
			invokeCommand('t');
		});
	}
}
