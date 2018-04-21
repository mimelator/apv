package com.arranger.apv.agent;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.APVCallback.Handler;

public class BaseAgent extends APVPlugin {

	public BaseAgent(Main parent) {
		super(parent);
	}

	protected void registerAgent(Handler handler) {
		
		parent.getSetupEvent().register(() -> {
			parent.getAgent().registerHandler(() -> {
				handler.handle();
			});
		});
	}
}
