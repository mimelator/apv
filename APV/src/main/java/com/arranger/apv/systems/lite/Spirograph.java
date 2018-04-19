package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/407620
 * Lots of hard coded constants
 */
public class Spirograph extends LiteShapeSystem {

	int nbCircles = 8;
	Circle[] circles;
	MyColor myColor;
	float rMax, dMin;

	public Spirograph(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getPulseListener().registerHandler(() -> {
				initialize(true);
			}, 16); //skip every 16 pulses
		});
	}

	@Override
	public void setup() {
		initialize(false);
	}

	void initialize(boolean random) {
		rMax = PApplet.min(parent.width, parent.height) / 2;
		dMin = PApplet.max(parent.width, parent.height) / 3.5f;
		circles = new Circle[nbCircles];
		
		for (int i = 0; i < nbCircles; i++) {
			circles[i] = new Circle(random(rMax), random ? random(-parent.width / 3, parent.width / 3) : 0,
					random ? random(-parent.height / 3, parent.height / 3) : 0);
		}
		myColor = new MyColor();
	}

	@Override
	public void draw() {
		parent.noStroke();
		parent.translate(parent.width / 2, parent.height / 2);
		myColor.update();
		for (int j = 0; j < nbCircles; j++) {
			circles[j].update();
			for (int i = j + 1; i < nbCircles; i++) {
				connect(circles[j], circles[i]);
			}
		}
	}

	void connect(Circle c1, Circle c2) {
		float d, x1, y1, x2, y2, r1 = c1.radius, r2 = c2.radius;
		float rCoeff = PApplet.map(PApplet.min(PApplet.abs(r1), PApplet.abs(r2)), 0, rMax, .08f, 1);
		int n1 = c1.nbLines, n2 = c2.nbLines;
		for (int i = 0; i < n1; i++) {
			x1 = c1.x + r1 * cos(i * TWO_PI / n1 + c1.theta);
			y1 = c1.y + r1 * sin(i * TWO_PI / n1 + c1.theta);
			for (int j = 0; j < n2; j++) {
				x2 = c2.x + r2 * cos(j * TWO_PI / n2 + c2.theta);
				y2 = c2.y + r2 * sin(j * TWO_PI / n2 + c2.theta);

				d = PApplet.dist(x1, y1, x2, y2);
				if (d < dMin) {
					parent.stroke(myColor.R + r2 / 1.5f, myColor.G + r2 / 2.2f, myColor.B + r2 / 1.5f,
							PApplet.map(d, 0, dMin, 140, 0) * rCoeff);
					parent.line(x1, y1, x2, y2);
				}
			}
		}
	}

	class Circle {
		float x, y, radius, theta = 0;
		int nbLines = (int) random(3, 25);
		float rotSpeed = (random(1) < .5 ? 1 : -1) * parent.random(.005f, .034f);
		float radSpeed = (random(1) < .5 ? 1 : -1) * parent.random(.3f, 1.4f);

		Circle(float p_radius, float p_x, float p_y) {
			radius = p_radius;
			x = p_x;
			y = p_y;
		}

		void update() {
			theta += rotSpeed;
			radSpeed *= PApplet.abs(radius += radSpeed) > rMax ? -1 : 1;
		}
	}

	class MyColor {
		float R, G, B, Rspeed, Gspeed, Bspeed;
		final static float minSpeed = .2f;
		final static float maxSpeed = .8f;

		MyColor() {
			Color currentColor = parent.getColor().getCurrentColor();
			R = currentColor.getRed();
			G = currentColor.getGreen();
			B = currentColor.getBlue();
			
//			R = random(20, 255);
//			G = random(20, 255);
//			B = random(20, 255);
			Rspeed = (random(1) > .5 ? 1 : -1) * random(minSpeed, maxSpeed);
			Gspeed = (random(1) > .5 ? 1 : -1) * random(minSpeed, maxSpeed);
			Bspeed = (random(1) > .5 ? 1 : -1) * random(minSpeed, maxSpeed);
		}

		public void update() {
			Rspeed = ((R += Rspeed) > 255 || (R < 20)) ? -Rspeed : Rspeed;
			Gspeed = ((G += Gspeed) > 255 || (G < 20)) ? -Gspeed : Gspeed;
			Bspeed = ((B += Bspeed) > 255 || (B < 20)) ? -Bspeed : Bspeed;
		}
	}
}
