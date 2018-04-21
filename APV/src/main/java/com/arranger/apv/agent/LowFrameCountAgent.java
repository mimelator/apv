package com.arranger.apv.agent;

import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.StrobeFilter;
import com.arranger.apv.util.InvokeAfterOneEvent;

public class LowFrameCountAgent extends BaseAgent {

	private static final int FRAME_RATE_LIMIT = 15;
	private static final int FRAME_RATE_RESUME_LIMIT = 30;
	
	private boolean lowFrameRate = false; 
	
	public LowFrameCountAgent(Main parent) {
		super(parent);
		
		registerAgent(getDrawEvent(), () -> {
			float currentFrameRate = parent.frameRate;
			
			//are we already slow?
			if (lowFrameRate) {
				//have we spead up?
				if (currentFrameRate > FRAME_RATE_RESUME_LIMIT) {
					
					new InvokeAfterOneEvent(parent, parent.getStrobeEvent(), () -> {
						parent.getCommandSystem().invokeScramble();
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
		
		List<Filter> list = filters.getList();
		for (int index = 0; index < list.size(); index++) {
			Filter next = list.get(index);
			if (next instanceof StrobeFilter) {
				filters.setIndex(index);
				return;
			}
		}
		
		throw new RuntimeException("Unable to find the StrobeFilter");
	}
}
