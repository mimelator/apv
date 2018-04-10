package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/147466
 * 
 * I'd like to see some alpha
 */
public class StarWebSystem extends LiteCycleShapeSystem {

	private static final int LARGE_RADIUS = 150;
	private static final int SMALL_RADIUS = 50;
	private static final int NUM_EDGES = 100;
	private static final int DEFAULT_NUM_BALLS = 180;
	private static final int LINE_ALPHA = 150;
	private static final int BALL_CONNECTION_DISTANCE = 60;
	private static final float LINE_STROKE_WEIGHT = 1.5f;
	
	private static final int DEFAULT_ELLIPSE_STROKE_WEIGHT = 1;
	private static final int FACTORY_SHAPE_STROKE_WEIGHT = 4;
	
	private static final int STAR_WEB_FRAMES_PER_RESET = 20000;
	
	int fc, edge = NUM_EDGES;

	public StarWebSystem(Main parent) {
		super(parent, DEFAULT_NUM_BALLS);
	}
	
	public StarWebSystem(Main parent, ShapeFactory factory) {
		this(parent, factory, DEFAULT_NUM_BALLS);
	}
	
	public StarWebSystem(Main parent, ShapeFactory factory, int numNewObjects) {
		super(parent, numNewObjects);
		this.factory = factory;
	}
	
	public StarWebSystem(Main parent, int numNewObjects) {
		super(parent, numNewObjects);
	}
	
	@Override
	public void setup() {
		shouldCreateNewObjectsEveryDraw = false;
		framesPerReset = STAR_WEB_FRAMES_PER_RESET;
		super.setup();
	}
	
	@Override
	protected void createNewObjects() {
		int width = parent.width;
		int height = parent.height;
		
		for (int i = 0; i < numNewObjects; i++) {
			PVector org = new PVector(random(edge, width - edge), random(edge, height - edge));
			float radius = random(SMALL_RADIUS, LARGE_RADIUS);
			PVector loc = new PVector(org.x + radius, org.y);
			float offSet = random(TWO_PI);
			int dir = 1;
			float r = random(0, 1);
			if (r > .5) {
				dir = -1;
			}
			lcObjects.add(new Ball(org, loc, radius, dir, offSet));
		}
	}

	@Override
	protected LiteCycleObj createObj(int index) {
		throw new RuntimeException("StarWebSystem::createObj not implemented");
	}
	
	private class Ball extends LiteCycleObj {
		
		private static final float PCT_TO_DIE = 75;//99.75f;
		private static final int DEFAULT_SIZE = 10;
		private static final int NUM_SHAPES = 5;
		
		PVector org, loc;
		float sz = DEFAULT_SIZE;
		float theta, radius, offSet;
		int dir, d = BALL_CONNECTION_DISTANCE;
		Color ballColor;
		Color lineColor;
		int alpha;
		APVShape factoryShape;
		
		private Ball(PVector _org, PVector _loc, float _radius, int _dir, float _offSet) {
			org = _org;
			loc = _loc;
			radius = _radius;
			dir = _dir;
			offSet = _offSet;
			
			if (factory != null) {
				factoryShape = factory.createShape(null);
			}
			
			resetColor();
		}
		
		@Override
		public boolean isDead() {
			float random = random(100);
			if (random > PCT_TO_DIE) {
				resetColor();
			}
			
			return false;
		}
		
		private void resetColor() {
			ballColor = parent.getColorSystem().getCurrentColor();
			lineColor = parent.getColorSystem().getCurrentColor();
			alpha = (int)parent.random(Main.MAX_ALPHA);
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
			for (int i = 0; i < NUM_SHAPES; i++) {
				if (factoryShape != null) {
					PShape drawShape = factoryShape.getShape();
					
					parent.rectMode(CENTER);
					parent.stroke(FACTORY_SHAPE_STROKE_WEIGHT);
					//drawShape.setStroke(FACTORY_SHAPE_STROKE_WEIGHT);
					drawShape.setFill(true);
					factoryShape.setColor(ballColor.getRGB(), alpha);
					
					parent.shape(drawShape, 
							loc.x - (drawShape.width / 2), 
							loc.y - (drawShape.height / 2) );
				} else {
					parent.stroke(DEFAULT_ELLIPSE_STROKE_WEIGHT);
					parent.fill(ballColor.getRGB(), i * 50);
					parent.ellipse(loc.x, loc.y, sz - 2 * i, sz - 2 * i);
				}
			}
		}
		
		private void lineBetween() {
			for (LiteCycleObj otherObj : lcObjects) {
				Ball otherBall = (Ball)otherObj;
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
