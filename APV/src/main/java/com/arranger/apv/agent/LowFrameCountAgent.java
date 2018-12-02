package com.arranger.apv.agent;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.StrobeFilter;
import com.arranger.apv.util.frame.InvokeAfterOneEvent;

public class LowFrameCountAgent extends BaseAgent {

	private static final int FRAME_RATE_LIMIT = 15;
	private static final int FRAME_RATE_RESUME_LIMIT = 20;
	
	private static final int FRAME_STARTUP_ACTIVATION = 100;
	
	private boolean lowFrameRate = false; 
	
	public LowFrameCountAgent(Main parent) {
		super(parent);
		
		registerAgent(getDrawEvent(), () -> {
			if (FRAME_STARTUP_ACTIVATION > parent.getFrameCount()) {
				return;
			}
			
			float currentFrameRate = parent.frameRate;
			
			//are we already slow?
			if (lowFrameRate) {
				//have we spead up?
				if (currentFrameRate > FRAME_RATE_RESUME_LIMIT) {
					
					new InvokeAfterOneEvent(parent, parent.getStrobeEvent(), () -> {
						parent.getCommandSystem().invokeScramble(getName());
					});
					
					//Ensure Strobe Filter is primed; Frame Count has increased, so lfr is false
					APV<Filter> filters = parent.getFilters();
					filters.setEnabled(true);
					setStrobeFilter(filters);
					lowFrameRate = false;
				}
			} else if (currentFrameRate < FRAME_RATE_LIMIT) {
				lowFrameRate = true;
			}
		});
	}
	
	protected void setStrobeFilter(APV<Filter> filters) {
		Filter strobe = filters.getFirstInstanceOf(StrobeFilter.class);
		filters.setNextPlugin(strobe, getName());	
	}
}
