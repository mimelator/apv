package com.arranger.apv.agent;

import java.util.LinkedList;

import com.arranger.apv.Main;
import com.arranger.apv.transition.Fade;
import com.arranger.apv.util.Configurator.Context;

public class FrameBlurAgent extends BaseAgent {

	private static final int FRAME_RATE_LIMIT = 20;
	private static final int CYCLE_TIME = 5;
	
	protected LinkedList<Fade> rollingFades = new LinkedList<Fade>();
	protected float cycleTime = CYCLE_TIME;
	protected float low = 0;
	protected float high = 0;
	protected int frameRateLimit;

	public FrameBlurAgent(Main parent, float low, float high, float cycleTime, int frameRateLimit) {
		super(parent);
		this.low = low;
		this.high = high;
		this.cycleTime = cycleTime;
		this.frameRateLimit = frameRateLimit;
		
		registerAgent(getDrawEvent(), () -> {
			updateFades();
		});
	}
	
	public FrameBlurAgent(Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, 0), 
				ctx.getFloat(1, CYCLE_TIME), 
				ctx.getFloat(2, CYCLE_TIME), 
				ctx.getInt(3, FRAME_RATE_LIMIT));
	}
	
	@Override
	public String getConfig() {
		//{FrameBlurAgent : [0, 1, 4]}
		return String.format("{%s : [%s, %s, %s]}", getName(), low, high, cycleTime);
	}
	
	public void updateFades() {
		if (parent.frameRate < frameRateLimit) {
			rollingFades.clear();
			return;
		}
		
		
		//what if i modulate the num Fades
		float oscillate = parent.oscillate(low, high, cycleTime);
		
		parent.addSettingsMessage(String.format("FrameBuf: low[%s], high[%s], cycleTime[%s]", low, high, cycleTime));
		parent.addSettingsMessage(String.format("   ---Oscillate: %s", oscillate));
		
		//Pop an item off of the top of the list and dispose
		if (!rollingFades.isEmpty() && rollingFades.size() >= oscillate) {
			rollingFades.pop();
		}
		
		//Append the new image to the end
		Fade fade = new Fade(parent, (int)oscillate);
		fade.startTransition();
		rollingFades.add(fade);
		rollingFades.forEach(f -> f.draw());
	}
}
