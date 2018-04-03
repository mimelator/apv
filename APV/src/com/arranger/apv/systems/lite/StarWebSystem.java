package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/147466
 */
public class StarWebSystem extends LiteShapeSystem {

	private static final int LARGE_RADIUS = 150;
	private static final int SMALL_RADIUS = 50;
	private static final int NUM_EDGES = 100;
	private static final int NUM_BALLS = 180;
	private static final int LINE_ALPHA = 150;
	private static final int BALL_CONNECTION_DISTANCE = 60;
	private static final float LINE_STROKE_WEIGHT = 1.5f;
	
	int fc, num = NUM_BALLS, edge = NUM_EDGES;
	List<Ball> balls = new ArrayList<Ball>();

	public StarWebSystem(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		int width = parent.width;
		int height = parent.height;
		for (int i = 0; i < num; i++) {
			PVector org = new PVector(random(edge, width - edge), random(edge, height - edge));
			float radius = random(SMALL_RADIUS, LARGE_RADIUS);
			PVector loc = new PVector(org.x + radius, org.y);
			float offSet = random(0, TWO_PI);
			int dir = 1;
			float r = random(0, 1);
			if (r > .5) {
				dir = -1;
			}
			Ball myBall = new Ball(org, loc, radius, dir, offSet);
			balls.add(myBall);
		}

	}

	@Override
	public void draw() {
		for (Ball b : balls) {
			b.run();
		}
	}
	
	class Ball {
		
		PVector org, loc;
		float sz = 10;
		float theta, radius, offSet;
		int s, dir, d = BALL_CONNECTION_DISTANCE;
		Color ballColor = parent.getColorSystem().getCurrentColor();
		Color lineColor = parent.getColorSystem().getCurrentColor();
		
		Ball(PVector _org, PVector _loc, float _radius, int _dir, float _offSet) {
			org = _org;
			loc = _loc;
			radius = _radius;
			dir = _dir;
			offSet = _offSet;
		}

		void run() {
			move();
			display();
			lineBetween();
		}

		void move() {
			loc.x = org.x + PApplet.sin(theta + offSet) * radius;
			loc.y = org.y + PApplet.cos(theta + offSet) * radius;
			theta += (0.0523 / 2 * dir);
		}

		void lineBetween() {
			for (Ball other : balls) {
				float distance = loc.dist(other.loc);
				if (distance > 0 && distance < d) {
					parent.strokeWeight(LINE_STROKE_WEIGHT);
					parent.stroke(lineColor.getRGB(), LINE_ALPHA); 
					parent.line(loc.x, loc.y, other.loc.x, other.loc.y);        
				}
			}
		}

		void display() {
			parent.noStroke();
			for (int i = 0; i < 5; i++) {
				parent.fill(ballColor.getRGB(), i * 50);
				parent.ellipse(loc.x, loc.y, sz - 2 * i, sz - 2 * i);
			}
		}
	}

}
