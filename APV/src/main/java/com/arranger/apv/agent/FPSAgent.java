package com.arranger.apv.agent;

import com.arranger.apv.Main;

/**
 * https://stackoverflow.com/questions/3793400/is-there-a-function-in-java-to-get-moving-average
 * @author markimel
 *
 */
public class FPSAgent extends BaseAgent {

	long lastTime = 0;
	
	public FPSAgent(Main parent) {
		super(parent);
		
		parent.getDrawEvent().register(() -> {
			onDraw();
		});
	}

	protected void onDraw() {
		long currentTimeMillis = System.currentTimeMillis();
		if (lastTime == 0) {
			
		}
		
		lastTime = currentTimeMillis;
	}
}
