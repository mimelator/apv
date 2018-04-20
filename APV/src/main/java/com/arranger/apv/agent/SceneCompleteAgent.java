package com.arranger.apv.agent;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SceneCompleteAgent extends APVPlugin {

	public SceneCompleteAgent(Main parent) {
		super(parent);
		
		parent.getSceneCompleteEvent().register(() -> {
			parent.getTransition().startTransition();
			parent.setDefaultScene();
		});
	}
}