package com.arranger.apv.agent;

import com.arranger.apv.Main;

public class SceneCompleteAgent extends BaseAgent {
	
	public SceneCompleteAgent(Main parent) {
		super(parent);
		
		registerAgent(getSceneCompleteEvent(), () -> {
			parent.getTransition().startTransition();
			parent.setDefaultScene();
		});
	}
	
}