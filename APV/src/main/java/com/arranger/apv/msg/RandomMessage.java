package com.arranger.apv.msg;

import java.util.stream.IntStream;

import com.arranger.apv.Main;

public class RandomMessage extends MessageSystem {

	private static final int NUM_REPEATS = 5;
	private static final int MIN_TEXT_SIZE = 15;
	private static final int MAX_TEXT_SIZE = 150;
	
	private static final int LOC_X = 0;
	private static final int LOC_Y = 1;
	
	private float rotation;
	private float textSize;
	private float [][] locations;
	
	public RandomMessage(Main parent) {
		super(parent);
	}

	@Override
	protected void onCreatedFadingMessage(FadingMessage fadingMessage) {
		reset();
	}
	
	@Override
	protected void _draw(FadingMessage fadingMessage) {
		Main p = parent;
		
		p.textAlign(CENTER, CENTER);
		p.rotate(rotation); // rotate text 
		p.fill(p.getColor().getCurrentColor().getRGB());
		p.textSize(textSize);
		
		for (String msg : fadingMessage.messages) {
			for (int index = 0; index < NUM_REPEATS; index++) {
				p.text(msg, 
						locations[index][LOC_X], 
						locations[index][LOC_Y]
						);
			}
		}
	}
	
	private void reset() {
		locations = new float[NUM_REPEATS][2];
		IntStream.range(0, NUM_REPEATS).forEach(i -> {
			locations[i] = new float[2];
			locations[i][LOC_X] = random(parent.width * .8f);
			locations[i][LOC_Y] = random(parent.height * .8f);
		});
		rotation = random(-1, +1);
		textSize = random(MIN_TEXT_SIZE, MAX_TEXT_SIZE);
	}
	
	protected float random(float high) {
		return parent.random(high);
	}
	
	protected float random(float low, float high) {
		return parent.random(low, high);
	}
}
