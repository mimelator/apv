package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator.Context;
import com.arranger.apv.util.draw.DrawHelper;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

/**
 * https://www.reddit.com/r/procesPApplet.sing/comments/28sx5s/kaleidoscope/
 */
public class KaleidoscopeAgent extends BaseAgent {
	
	private static final int DEFAULT_NUM_SLICES = 32;
	private static final int DEFAULT_NUM_FLASH_DRAWS = 90;
	private static final float DEFAULT_RADIUS = 3.1f;
	
	private DrawHelper drawHelper;
	private int slices;
	private float angle;
	private float userRadius;
	private float radius;
	private int numFrames;
	private float offset = 0;

	public KaleidoscopeAgent(Main parent, int slices, float radius, int numFrames) {
		super(parent);
		this.slices = slices;
		angle = PI / slices;
		this.userRadius = radius;
		this.radius = PApplet.max(parent.width, parent.height) * userRadius;
		this.numFrames = numFrames;
		
		registerAgent(getKScopeEvent(), () -> {
			//drawKScope();
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
		
		public KShapeSystem(Main parent) {
			super(parent, null);
		}

		@Override
		public void onFactoryUpdate() {
			
		}

		@Override
		public void draw() {
			PImage img = parent.get();
			
			offset += PI / 180;
	
			PShape mySlice = parent.createShape();
			mySlice.beginShape(TRIANGLE);
			mySlice.texture(img);
			mySlice.noStroke();
			mySlice.vertex(0, 0, img.width / 2, img.height / 2);
			mySlice.vertex(PApplet.cos(angle) * radius, PApplet.sin(angle) * radius, PApplet.cos(angle + offset) * radius + img.width / 2,
					PApplet.sin(angle + offset) * radius + img.height / 2);
			mySlice.vertex(PApplet.cos(-angle) * radius, PApplet.sin(-angle) * radius, PApplet.cos(-angle + offset) * radius + img.width / 2,
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
