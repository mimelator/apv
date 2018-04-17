package com.arranger.apv.filter;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.SavedImage;

public class StrobeFilter extends Filter {
	
	private static final int DEFAULT_STROBE_DURATION = 60;
	private static final int DEFAULT_STROBE_BLINK = 4;
	private static final int PULSES_TO_SKIP = 2;
	private static final boolean AUTO_CHANGE_FILTER_ON_COMPLETE = true;

	protected SavedImage savedImage;
	protected int strobeDuration;
	protected int flashDuration;
	
	public StrobeFilter(Main parent, int strobeDuration, int flashDuration) {
		super(parent);
		this.strobeDuration = strobeDuration;
		this.flashDuration = flashDuration;
		
		//register
		parent.registerSetupListener(() -> {
					parent.getPulseListener().registerPulseListener(() -> {
						onNewPulse();
				}, PULSES_TO_SKIP);
		});
	}
	
	public StrobeFilter(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_STROBE_DURATION), ctx.getInt(1, DEFAULT_STROBE_BLINK));
	}

	@Override
	public String getConfig() {
		//{StrobeFilter :[150, 10]}
		return String.format("{%1s : [%2s, %3s]}", getName(), strobeDuration, flashDuration);
	}
	
	protected void onNewPulse() {
		//Called late in the rendering process
		savedImage = new SavedImage(parent);
	}
	
	@Override
	public void preRender() {
		super.preRender();
		
		if (!checkSavedImage()) {
			return;
		}
		
		if (parent.getFrameCount() % flashDuration < flashDuration / 2) {
			parent.imageMode(CENTER);
			parent.image(savedImage.getSavedImage(), parent.width / 2, parent.height / 2);
		} else {
			//parent.color(255);
			parent.fill(0);
			parent.rect(0, 0, parent.width, parent.height);
		}
	}
	
	private boolean checkSavedImage() {
		if (savedImage == null) {
			return false;
		} else {
			int savedImageFrame = savedImage.getSavedImageFrame();
			int frameCount = parent.getFrameCount();
			int fof = strobeDuration;
			if (savedImageFrame + fof < frameCount) {
				//expired
				savedImage = null;
				if (AUTO_CHANGE_FILTER_ON_COMPLETE) {
					//issue command to change the filter
					parent.getCommandSystem().invokeCommand('t'); //t for filters
				}
				
				return false;
			}
		}
		return true;
	}
}
