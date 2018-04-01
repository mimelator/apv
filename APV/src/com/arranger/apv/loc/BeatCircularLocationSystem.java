package com.arranger.apv.loc;

import com.arranger.apv.Main;

public class BeatCircularLocationSystem extends CircularLocationSystem {
	
	private static float NUDGE_FORWARD_PCT = .1f;

	public BeatCircularLocationSystem(Main parent) {
		super(parent);
	}

	protected float getPercentagePathComplete() {
		float res = super.getPercentagePathComplete();

		//check for Beat
		if (parent.getAudio().getBeatInfo().getBeat().isKick()) {
			//punch it forward a bit down the path
			res += NUDGE_FORWARD_PCT;
			res %= 1.0f;
		}
		
		return res;
	}
}
