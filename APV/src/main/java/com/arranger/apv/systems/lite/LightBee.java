package com.arranger.apv.systems.lite;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

/**
 * @see https://www.openprocessing.org/sketch/387711
 * TODO: Create an agent that will always turn a blur on with this foreground
 */
public class LightBee extends LiteShapeSystem {

	public LightBee(Main parent) {
		super(parent);
	}

	private static final float LIGHT_FORCE_RATIO_LOW = .1f;
	private static final float LIGHT_FORCE_RATIO_HIGH = 5;
	private static final int CYCLE_TIME = 20;
	private static final int LIGHT_DISTANCE = 75 * 75;
	private static final int BORDER = 400;
	private static final float RED_ADD = 1.2f;
	private static final float GREEN_ADD = 1.7f;
	private static final float BLUE_ADD = 2.3f;
	
	Particle p = new Particle();
	float baseRed, baseGreen, baseBlue;
	float baseRedAdd, baseGreenAdd, baseBlueAdd;
	
	
	@Override
	public void setup() {
		baseRed = 0;
		baseRedAdd = RED_ADD;

		baseGreen = 0;
		baseGreenAdd = GREEN_ADD;

		baseBlue = 0;
		baseBlueAdd = BLUE_ADD;
	}

	@Override
	public void draw() {

		float lightForceRatio = parent.oscillate(LIGHT_FORCE_RATIO_LOW, LIGHT_FORCE_RATIO_HIGH, CYCLE_TIME);
		
		baseRed += baseRedAdd;
		baseGreen += baseGreenAdd;
		baseBlue += baseBlueAdd;

		colorOutCheck();

		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int)pt.getX();
		int mouseY = (int)pt.getY();
		
		p.move(mouseX, mouseY);

		int tRed = (int) baseRed;
		int tGreen = (int) baseGreen;
		int tBlue = (int) baseBlue;

		tRed *= tRed;
		tGreen *= tGreen;
		tBlue *= tBlue;

		parent.loadPixels();

		int left = Math.max(0, p.x - BORDER);
		int right = Math.min(parent.width, p.x + BORDER);
		int top = Math.max(0, p.y - BORDER);
		int bottom = Math.min(parent.height, p.y + BORDER);

		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {
				int pixelIndex = x + y * parent.width;

				int r = parent.pixels[pixelIndex] >> 16 & 0xFF;
				int g = parent.pixels[pixelIndex] >> 8 & 0xFF;
				int b = parent.pixels[pixelIndex] & 0xFF;

				int dx = x - p.x;
				int dy = y - p.y;
				int distance = (dx * dx) + (dy * dy);

				if (distance < LIGHT_DISTANCE) {
					int fixFistance = (int)(distance * lightForceRatio);

					if (fixFistance == 0) {
						fixFistance = 1;
					}
					r = r + tRed / fixFistance;
					g = g + tGreen / fixFistance;
					b = b + tBlue / fixFistance;
				}
				parent.pixels[x + y * parent.width] = parent.color(r, g, b);
			}
		}

		parent.updatePixels();
	}

	void colorOutCheck() {
		if (baseRed < 10) {
			baseRed = 10;
			baseRedAdd *= -1;
		} else if (baseRed > 255) {
			baseRed = 255;
			baseRedAdd *= -1;
		}

		if (baseGreen < 10) {
			baseGreen = 10;
			baseGreenAdd *= -1;
		} else if (baseGreen > 255) {
			baseGreen = 255;
			baseGreenAdd *= -1;
		}

		if (baseBlue < 10) {
			baseBlue = 10;
			baseBlueAdd *= -1;
		} else if (baseBlue > 255) {
			baseBlue = 255;
			baseBlueAdd *= -1;
		}
	}

	class Particle {
		int x, y;
		float vx, vy;
		float slowLevel;

		Particle() {
			x = (int) random(parent.width);
			y = (int) random(parent.height);
			slowLevel = random(100) + 5;
		}

		void move(float targetX, float targetY) {
			vx = (targetX - x);
			vy = (targetY - y);

			x = (int) (x + vx);
			y = (int) (y + vy);
		}
	}

}
