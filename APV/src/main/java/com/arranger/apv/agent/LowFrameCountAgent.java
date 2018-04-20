package com.arranger.apv.agent;

import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.event.CoreEvent.CoreListener;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.StrobeFilter;

public class LowFrameCountAgent extends APVPlugin {

	private static final int FRAME_RATE_LIMIT = 15;
	private static final int FRAME_RATE_RESUME_LIMIT = 30;
	
	private boolean lowFrameRate = false; 
	CoreListener register = null;
	
	public LowFrameCountAgent(Main parent) {
		super(parent);
		
		parent.getDrawEvent().register(() -> {
			float currentFrameRate = parent.frameRate;
			
			//are we already slow?
			if (lowFrameRate) {
				//have we spead up?
				if (currentFrameRate > FRAME_RATE_RESUME_LIMIT) {
					//If we haven't subscribed
					if (register == null) {
						CoreEvent strobeEvent = parent.getStrobeEvent();
						register = parent.getStrobeEvent().register(() -> {
							
							//Scramble, unregister and reset (should be a command on Command System)
							parent.getCommandSystem().invokeCommand(Main.SPACE_BAR_KEY_CODE);
							strobeEvent.unregister(register);
							register = null;
						});
					
						//set filters enabled as well as the strob filter, & sped up, so lfr is false
						APV<Filter> filters = parent.getFilters();
						filters.setEnabled(true);
						setStrobeFilter(filters);
						lowFrameRate = false;
					}	
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
