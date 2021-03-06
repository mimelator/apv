package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FFTAnalysis;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/147466
 * 
 * I'd like to see some alpha
 */
public class StarWebSystem extends LiteCycleShapeSystem {

	private static final float MAX_AMP_SCALAR = 3f;//1.1f;
	
	private static final int LARGE_RADIUS = 150;
	private static final int SMALL_RADIUS = 50;
	private static final int NUM_EDGES = 100;
	private static final int DEFAULT_NUM_BALLS = 100;//180;
	private static final int LINE_ALPHA = 150;
	private static final int BALL_CONNECTION_DISTANCE = 90;//60;
	private static final float LINE_STROKE_WEIGHT = 1.5f;
	
	private static final int FACTORY_SHAPE_STROKE_WEIGHT = 4;
	private static final int STAR_WEB_FRAMES_PER_RESET = 20000;
	private static final float PCT_TO_DIE = 98.5f;//75;//99.75f;
	
	int fc, edge = NUM_EDGES;
	float deatRatePct = PCT_TO_DIE;
	boolean doRotateScale = false;
	FFTAnalysis fftAnalysis;
	
	public StarWebSystem(Main parent) {
		this(parent, null, DEFAULT_NUM_BALLS);
	}
	
	public StarWebSystem(Main parent, int numNewObjects) {
		this(parent, null, numNewObjects);
	}
	
	public StarWebSystem(Main parent, ShapeFactory factory) {
		this(parent, factory, DEFAULT_NUM_BALLS);
	}
	
	public StarWebSystem(Main parent, ShapeFactory factory, int numNewObjects) {
		this(parent, factory, numNewObjects, false);
	}
	
	/**
	 * @param deatRatePct between 0 and 1.0f
	 */
	public StarWebSystem(Main parent, ShapeFactory factory, int numNewObjects, boolean doRotateScale) {
		super(parent, numNewObjects);
		this.factory = factory;
		if (this.factory != null) {
			this.factory.setShapeSystem(this);
		}
		this.doRotateScale = doRotateScale;
		fftAnalysis = new FFTAnalysis(parent);
	}
	
	public StarWebSystem(Configurator.Context ctx) {
		this(ctx.getParent(),
				(ShapeFactory)ctx.loadPlugin(0),
				ctx.getInt(1, DEFAULT_NUM_BALLS),
				ctx.getBoolean(2, false));
	}
	
	@Override
	public String getConfig() {
		//{StarWebSystem : [{SpriteFactory : [Emoji_Blitz_Star.png, 1.5]}, ${ALL_PARTICLES}, true]}
		return String.format("{%s : [%s, %s, %b]}", 
				getName(), 
				factory == null ? "{}" : factory.getConfig(), 
				numNewObjects, 
				doRotateScale);
	}

	@Override
	public void setup() {
		shouldCreateNewObjectsEveryDraw = false;
		framesPerReset = STAR_WEB_FRAMES_PER_RESET;
		super.setup();
	}
	
	@Override
	protected LiteCycleObj createObj(int index) {
		PVector org = new PVector(random(edge, parent.width - edge), random(edge, parent.height - edge));
		float radius = random(SMALL_RADIUS, LARGE_RADIUS);
		PVector loc = new PVector(org.x + radius, org.y);
		float offSet = random(TWO_PI);
		int dir = 1;
		float r = random(0, 1);
		if (r > .5) {
			dir = -1;
		}
		return new StarWebObject(org, loc, radius, dir, offSet);
	}
	
	private class StarWebObject extends LiteCycleObj {
		
		
		private static final int DEFAULT_SIZE = 10;
		private static final int DEFAULT_DUPLICATES = 5;
		
		PVector org, loc;
		float sz = DEFAULT_SIZE;
		float theta, radius, offSet;
		int dir, d = BALL_CONNECTION_DISTANCE;
		Color nodeColor;
		Color lineColor;
		int alpha;
		float rotationRate;
		float scale;
		APVShape factoryShape;
		
		int numDuplicates = DEFAULT_DUPLICATES;
		
		private StarWebObject(PVector _org, PVector _loc, float _radius, int _dir, float _offSet) {
			org = _org;
			loc = _loc;
			radius = _radius;
			dir = _dir;
			offSet = _offSet;
			
			if (factory != null) {
				factoryShape = factory.createShape(null);
			}
			
			numDuplicates = (int)parent.random(1, DEFAULT_DUPLICATES);
			
			reset();
		}
		
		@Override
		public boolean isDead() {
			float random = random(100);
			return random > deatRatePct;
		}
		
		private void reset() {
			nodeColor = parent.getColor().getCurrentColor();
			lineColor = parent.getColor().getCurrentColor();
			alpha = (int)parent.random(Main.MAX_ALPHA);
			
			if (doRotateScale) {
				rotationRate = parent.random(.01f, .15f);
				if (parent.randomBoolean()) {
					rotationRate = -rotationRate;
				}
				
				scale = parent.random(.25f, 1.3f);
				
				if (factory != null) {
					PShape drawShape = factoryShape.getShape();
					drawShape.resetMatrix();
					drawShape.scale(scale);
					drawShape.rotate(PApplet.degrees(parent.random(PApplet.TWO_PI))); //initial rotation
				}
			}
		}

		@Override
		public void update() {
			loc.x = org.x + PApplet.sin(theta + offSet) * radius;
			loc.y = org.y + PApplet.cos(theta + offSet) * radius;
			theta += (0.0523 / 2 * dir);
		}

		@Override
		public void display() {
			drawShapes();
			lineBetween();
		}

		private void drawShapes() {
			if (factoryShape == null) {
				drawShapesWithoutFactory();
				return;
			}
			
			PShape drawShape = factoryShape.getShape();
			
			parent.rectMode(CENTER);
			parent.stroke(FACTORY_SHAPE_STROKE_WEIGHT);
			drawShape.setFill(true);
			factoryShape.setColor(nodeColor.getRGB(), alpha);
			
			if (doRotateScale) {
				float ampScalar = fftAnalysis.getMappedAmp(0, 1, 1, MAX_AMP_SCALAR);
				drawShape.rotate(rotationRate * ampScalar);
			}
			
			parent.shape(drawShape, 
					loc.x - (drawShape.width / 2), 
					loc.y - (drawShape.height / 2) );
		}
		
		private void drawShapesWithoutFactory() {
			for (int i = 1; i < numDuplicates + 1; i++) {
				parent.strokeWeight(sz);
				parent.stroke(nodeColor.getRGB());
				parent.point(loc.x, loc.y);
				
			}
		}
		
		private void lineBetween() {
			for (LiteCycleObj otherObj : lcObjects) {
				StarWebObject otherBall = (StarWebObject)otherObj;
				float distance = loc.dist(otherBall.loc);
				if (distance > 0 && distance < d) {
					parent.strokeWeight(LINE_STROKE_WEIGHT);
					parent.stroke(lineColor.getRGB(), LINE_ALPHA); 
					parent.line(loc.x, loc.y, otherBall.loc.x, otherBall.loc.y);        
				}
			}
		}
	}
}
