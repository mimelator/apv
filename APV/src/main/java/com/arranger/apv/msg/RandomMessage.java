package com.arranger.apv.msg;

import java.util.Random;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;

public class RandomMessage extends MessageSystem {

	private static final int NUM_REPEATS = 5;

	private static final int MAX_TEXT_SIZE = 150;
	
	private long seed = 0;
	private Random r = null;
	
	public RandomMessage(Main parent) {
		super(parent);
	}

	@Override
	protected void _draw(FadingMessage fadingMessage) {
		if (fadingMessage.frameFader.isFadeNew() || r == null) {
			seed = System.currentTimeMillis();
		}
		r = new Random(seed);
		
		Main p = parent;
		
		p.textAlign(CENTER, CENTER);
		p.rotate(random(-1, +1)); // rotate text 
		p.fill(p.getColor().getCurrentColor().getRGB());
		p.textSize(random(MAX_TEXT_SIZE));
		
		for (String msg : fadingMessage.messages) {
			for (int index = 0; index < NUM_REPEATS; index++) {
				p.text(msg, random(p.width * .8f), random(p.height * .8f));
			}
		}
	}
	
	//https://github.com/processing/processing/blob/master/core%2Fsrc%2Fprocessing%2Fcore%2FPApplet.java
	
	public final float random(float low, float high) {
		if (low >= high)
			return low;
		float diff = high - low;
		float value = 0;
		// because of rounding error, can't just add low, otherwise it may hit high
		// https://github.com/processing/processing/issues/4551
		do {
			value = random(diff) + low;
		} while (value == high);
		return value;
	}

	public final float random(float high) {
		// avoid an infinite loop when 0 or NaN are passed in
		if (high == 0 || high != high) {
			return 0;
		}

		// for some reason (rounding error?) Math.random() * 3
		// can sometimes return '3' (once in ~30 million tries)
		// so a check was added to avoid the inclusion of 'howbig'
		float value = 0;
		do {
			value = r.nextFloat() * high;
		} while (value == high);
		return value;
	}
}
