package com.arranger.apv.systems;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;

import processing.core.PApplet;
import processing.core.PConstants;

public class DirectLifecycleSystem extends ShapeSystem implements PConstants{

	public DirectLifecycleSystem(Main parent, ShapeFactory factory) {
		super(parent, factory);
		System.out.println("DirectLifecycleSystem: Use the ShapeFactory");
	}

	int n = 500; // number of warp stars
	float[] angle = new float[n]; // their angle versus the center of the screen
	float[] dist = new float[n]; // distance from center of the screen
	float[] speed = new float[n]; // speed leaving the center
	float[] bright = new float[n]; // brightness (start black, fade to white)
	float[] thick = new float[n]; // diameter of the warp star

	public void setup() {
		for (int i = 0; i < n; i++) { // create warp stars
			restartStar(i);
			dist[i] = parent.random(0, parent.width + parent.height);
		}
	}

	int randSeed = 0;

	public void draw() {
		randSeed += parent.frameCount;
		parent.randomSeed(randSeed % 120); // Make a predictable pattern (useful for
									// making the effect consistent)

		// Fade the tails drawn by the stars to black:
		parent.colorMode(PApplet.RGB);
		parent.fill(0, 50);
		parent.noStroke();
		int width = parent.width;
		int height = parent.height;
		
		
		parent.rect(0, 0, width, height);

		// Draw the warp stars:
		for (int i = 0; i < n; i++) {
			parent.pushMatrix();
			parent.translate(width / 2, height / 2);
			parent.rotate(angle[i]);
			parent.translate(dist[i], 0);
			parent.stroke(starcolor(bright[i]));
			parent.strokeWeight(thick[i]);
			parent.line(0, 0, speed[i], 0); // draw line from previous to next position
			parent.popMatrix();

			dist[i] += speed[i]; // Move the warp stars
			speed[i] += .1 * warp(); // accelerate as they leave center
			if (dist[i] > width + height)
				restartStar(i); // restart stars out of screen
			bright[i] += 5;
		}

		// Draw 'static' non-moving stars (when stationary)
		parent.randomSeed(0);
		parent.colorMode(PApplet.RGB);
		for (int i = 0; i < 400; i++) {
			parent.fill(PApplet.map(warp(), 0, 2, 200, 0)); // make visible only when warp is
												// between 0 and 2
			if (warp() > 2)
				parent.fill(0);
			parent.noStroke();
			float diameter = parent.random(1, 5); // draw random size static stars
			parent.ellipse(parent.random(0, width), parent.random(0, height), diameter, diameter);
		}

		// Draw the text of warp speed at top left
		parent.fill(255);
		parent.textSize(30);
		parent.textAlign(PApplet.LEFT, PApplet.TOP);
		parent.text("WARP " + PApplet.round(warp() * 10) / 10.0, 40, 40);
	}

	void restartStar(int i) {
		// Restart code for when star leaves screen and comes back
		angle[i] = parent.random(0, 2 * PApplet.PI * 100);
		dist[i] = parent.random(parent.width / 50, parent.width);
		speed[i] = parent.random(0 * warp(), .1f * warp());
		thick[i] = parent.random(1, 5);
		bright[i] = 0;
	}

	// Makes stars blue when faster
	int starcolor(float bright) {
		parent.colorMode(PApplet.HSB);
		float sat = PApplet.map(warp(), 1, 10, 0, 100);
		return parent.color(150, sat, bright);
	}

	float warp() { // returns a number from 0 to 10, increasing and decreasing
					// over time
		return PApplet.map(PApplet.cos(PApplet.PI + parent.frameCount / 60.0f / 5), -1, 1, 0, 10);
	}

	// protected class DirectData extends Data {
	//
	// float angle; //their angle versus the center of the screen
	// float dist; //distance from center of the screen
	// float speed; //speed leaving the center
	// float bright; //brightness (start black, fade to white)
	// float thick; //diameter of the warp star
	//
	// }

}
