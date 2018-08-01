package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator.Context;
import com.arranger.apv.util.draw.DrawHelper;
import com.arranger.apv.util.frame.FrameFader;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

/**
 * https://www.reddit.com/r/procesPApplet.sing/comments/28sx5s/kaleidoscope/
 */
public class KaleidoscopeAgent extends BaseAgent {
	
	private static final int DEFAULT_NUM_SLICES = 32;
	private static final int MIN_NUM_SLICES = 5;
	
	private static final int SLICE_CYCLE_TIME = 15;
	
	private static final int DEFAULT_NUM_FLASH_DRAWS = 90;
	private static final float DEFAULT_RADIUS = 3.1f;
	private static final float START_RADIUS = .15f;
	private static final int EASE_IN_MODIFIER = 2;
	
	private DrawHelper drawHelper;
	private int slices;
	//private float angle;
	private float userRadius;
	private int numFrames;
	private float offset = 0;

	public KaleidoscopeAgent(Main parent, int slices, float userRadius, int numFrames) {
		super(parent);
		this.slices = slices;
//		angle = PI / slices;
		this.userRadius = userRadius;
		this.numFrames = numFrames;
		
		registerAgent(getKScopeEvent(), () -> {
			if (drawHelper == null) {
				drawHelper = new DrawHelper(parent, numFrames, new KShapeSystem(parent), () -> drawHelper = null);
			}
		});
	}
	
	public KaleidoscopeAgent(Context ctx) {
		this(ctx.getParent(), 
				ctx.getInt(0, DEFAULT_NUM_SLICES),
				ctx.getFloat(1, DEFAULT_RADIUS),
				ctx.getInt(2, DEFAULT_NUM_FLASH_DRAWS));
	}
	
	@Override
	public String getConfig() {
		//{KaleidoscopeAgent : [32, 3, 75]}
		return String.format("{%s : [%s, %s, %s]}", getName(), slices, userRadius, numFrames);
	}

	protected class KShapeSystem extends ShapeSystem {
		
		FrameFader fader;
		
		public KShapeSystem(Main parent) {
			super(parent, null);
			fader = new FrameFader(parent, numFrames / EASE_IN_MODIFIER);
		}

		@Override
		public void onFactoryUpdate() {
		}

		
		@Override
		public void draw() {
			PImage img = parent.get();
			
			int currentNumSlices = (int)parent.oscillate(MIN_NUM_SLICES, slices, SLICE_CYCLE_TIME);
			float angle = PI / currentNumSlices;
			
			offset += PI / 180;
			
			float radius = 0.0f;
			if (fader.isFadeActive()) {
				float fadePct = fader.getFadePct();
				float radiusOffset = parent.mapEx(1 - fadePct, 0, 1, START_RADIUS, userRadius);
				radius = PApplet.max(parent.width, parent.height) * radiusOffset;
			} else {
				radius = PApplet.max(parent.width, parent.height) * userRadius;
			}
			
			PShape mySlice = parent.createShape();
			mySlice.beginShape(TRIANGLE);
			mySlice.texture(img);
			mySlice.noStroke();
			mySlice.vertex(0, 0, img.width / 2, img.height / 2);
			mySlice.vertex(
					PApplet.cos(angle) * radius, 
					PApplet.sin(angle) * radius, 
					PApplet.cos(angle + offset) * radius + img.width / 2,
					PApplet.sin(angle + offset) * radius + img.height / 2);
			mySlice.vertex(
					PApplet.cos(-angle) * radius, 
					PApplet.sin(-angle) * radius, 
					PApplet.cos(-angle + offset) * radius + img.width / 2,
					PApplet.sin(-angle + offset) * radius + img.height / 2);
			mySlice.endShape();
	
			parent.translate(parent.width / 2, parent.height / 2);
			for (int i = 0; i < slices; i++) {
				parent.rotate(angle * 2);
				parent.shape(mySlice);
			}
		}
	}

}
