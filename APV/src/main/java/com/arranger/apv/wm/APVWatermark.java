package com.arranger.apv.wm;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.util.draw.DrawHelper;

public class APVWatermark extends APV<WatermarkPainter> {
	
	protected EventHandler token;

	public APVWatermark(Main parent) {
		super(parent, Main.SYSTEM_NAMES.WATERMARKS, false);
		register(parent);
	}

	protected void register(Main parent) {
		parent.getSetupEvent().register(() -> {
			token = parent.getWatermarkEvent().register(() -> {
				WatermarkPainter wp =  getPlugin();
				new DrawHelper(parent, wp.getNumFrames(), wp, () -> {});
				increment(parent.getWatermarkEvent().getDisplayName());
			});
		});
	}
	
	public void reloadConfiguration() {
		parent.getWatermarkEvent().unregister(token);
		initialize(parent, Main.SYSTEM_NAMES.WATERMARKS, false);
		register(parent);
	}
}
