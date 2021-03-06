package com.arranger.apv.systems.lite.cycle;


import java.awt.geom.Point2D;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.agent.automation.AgentEvent;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.draw.DrawHelper;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/521068
 */
public class CarnivalShapeSystem extends LiteCycleShapeSystem {
	
	private static final float HALF_SEC = .5f;
	private static final float TWO_SEC = 2f;
	private static final int DEFAULT_NUM_EVENTS_TO_SKIP = 3;
	private static final int SKIP_RANGE = 3;
	
	private boolean useCustomColor = false;
	private int numEventsToSkip;
	private ColorSystem colorSystem;
	private DrawHelper drawHelper;

	public CarnivalShapeSystem(Main parent, boolean useCustomColor, int numEventsToSkip) {
		this(parent, null, useCustomColor, numEventsToSkip);
	}
	
	public CarnivalShapeSystem(Main parent, ShapeFactory factory, boolean useCustomColor, int numEventsToSkip) {
		super(parent);
		this.useCustomColor = useCustomColor;
		this.numEventsToSkip = numEventsToSkip;
		this.factory = factory;
		if (this.factory != null) {
			this.factory.setShapeSystem(this);
		}
		
		AgentEvent agentEvent = new AgentEvent(parent, parent.getCarnivalEvent(), numEventsToSkip, numEventsToSkip * SKIP_RANGE);
		agentEvent.setHandler(()-> {
			if (drawHelper == null) {
				int numDraws = (int)(parent.getFrameRate() * parent.random(HALF_SEC, TWO_SEC));
				//System.out.printf("Drawing: %s for %s frames\n", getConfig(), numDraws);
				drawHelper = new DrawHelper(parent, numDraws, this, () -> drawHelper = null);
			}
		});
}
	
	public CarnivalShapeSystem(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(ShapeFactory)ctx.loadPlugin(0),
				ctx.getBoolean(1, false),
				ctx.getInt(2, DEFAULT_NUM_EVENTS_TO_SKIP));
	}

	@Override
	public String getConfig() {
		//{CarnivalShapeSystem : [{CarnivalShapeSystem : [Emoji_Blitz_Star.png, 1.5]}, true, 2]}
		return String.format("{%s : [%s, %b, %s]}", 
				getName(), 
				factory == null ? "{}" : factory.getConfig(),
				useCustomColor,
				numEventsToSkip);
	}

	@Override
	public void setup() {
		shouldCreateNewObjectsEveryDraw = true;
		updateCustomColor();
		super.setup();
	}

	protected void updateCustomColor() {
		if (useCustomColor) {
			APV<ColorSystem> colSystem = parent.getColors();
			colorSystem = colSystem.getFirstInstanceOf(OscillatingColor.class);
		}
	}

	public void draw() {
		parent.colorMode(HSB);
		parent.rectMode(CENTER);
		
		super.draw();
	}
	
	@Override
	protected void reset() {
		updateCustomColor();
	}

	@Override
	protected LiteCycleObj createObj(int index) {
		Point2D point = parent.getCurrentPoint();
		float x = (float)point.getX();
		float y = (float)point.getY();
		int curSize = lcObjects.size();
		return new Particle(x, y, index + curSize, index + curSize);
	}

	private class Particle extends LiteCycleObj {
		private PVector loc, vel, acc;
		private int lifeSpan, passedLife;
		private boolean dead;
		private float alpha, weight, weightRange, decay, xOfst, yOfst;
		private int c;
		private APVShape factoryShape;

		private Particle(float x, float y, float xOfst, float yOfst) {
			loc = new PVector(x, y);

			float randDegrees = parent.random(360);
			vel = new PVector(PApplet.cos(PApplet.radians(randDegrees)), PApplet.sin(PApplet.radians(randDegrees)));
			vel.mult(parent.random(5));

			acc = new PVector(0, 0);
			lifeSpan = (int) (parent.random(30, 90));
			decay = parent.random(0.75f, 0.9f);
			
			ColorSystem cs = (colorSystem != null) ? colorSystem : parent.getColor();
			c = cs.getCurrentColor().getRGB();

			//Original color scheme
			//c = parent.color(parent.random(255), parent.random(255), 255);
			weightRange = parent.random(3, 50);

			this.xOfst = xOfst;
			this.yOfst = yOfst;
			
			if (factory != null) {
				factoryShape = factory.createShape(null);
			}
		}

		@Override
		public boolean isDead() {
			return dead;
		}
		
		@Override
		public void update() {
			if (passedLife >= lifeSpan) {
				dead = true;
			} else {
				passedLife++;
			}

			int frameCount = parent.getFrameCount();
			alpha = (float) (lifeSpan - passedLife) / lifeSpan * 70 + 50;
			weight = (float) (lifeSpan - passedLife) / lifeSpan * weightRange;

			acc.set(0, 0);

			float rn = (parent.noise((loc.x + frameCount + xOfst) * .01f, 
					(loc.y + frameCount + yOfst) * .01f) - .5f) * TWO_PI * 4;
			float mag = parent.noise((loc.y - frameCount) * .01f, (loc.x - frameCount) * .01f);
			PVector dir = new PVector(PApplet.cos(rn), PApplet.sin(rn));
			acc.add(dir);
			acc.mult(mag);

			float randRn = parent.random(TWO_PI);
			PVector randV = new PVector(PApplet.cos(randRn), PApplet.sin(randRn));
			randV.mult(.25f);
			acc.add(randV);

			vel.add(acc);
			vel.mult(decay);
			vel.limit(3);
			loc.add(vel);
		}

		@Override
		public void display() {
			if (factoryShape == null) {
				parent.strokeWeight(weight + 1.5f);
				parent.stroke(0, alpha);
				parent.point(loc.x, loc.y);
	
				parent.strokeWeight(weight);
				parent.stroke(c);
				parent.point(loc.x, loc.y);
			} else {
				factoryShape.setColor(c, alpha);
				PShape drawShape = factoryShape.getShape();
				parent.rectMode(CENTER);
				parent.stroke(4);
				drawShape.setFill(true);
				drawShape.rotate((float)Math.toRadians(parent.random(0, 15)));
				
				parent.shape(drawShape, loc.x, loc.y);
				
			}
		}
	}
}
