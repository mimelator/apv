package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.loc.ArcLocation;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.PathLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.loc.StarLocation;

/**
 * @see https://www.openprocessing.org/sketch/105451
 */
public class Countdown extends LiteShapeSystem {

	private static final int FLASH_BACKGROUND = 230;
	private static final int HALF_SIZE = 200;
	private static final int SIZE = 400;
	private static final int TEXT_SIZE = 150;
	private static final int FLASH_INTERVAL = 90;

	int startCount;
	int frameCounter;
	PathLocationSystem ls;
	
	public Countdown(Main parent, int startCount) {
		super(parent);
		this.startCount = startCount;
		ls = createLocationSystem();
	}

	private PathLocationSystem createLocationSystem() {
		PathLocationSystem pls = null;;
		
		int options = (int)random(4);
		switch (options) {
		case 0:
			pls = new ArcLocation(parent, false, true);
			break;
		case 1:
			pls = new StarLocation(parent, false, true);
			break;
		case 2:
			pls = new CircularLocationSystem(parent, false, false);
			break;
		case 3:
			pls = new RectLocationSystem(parent, false, false);
			break;
			default:
				throw new RuntimeException("Unknown option: " + options);
		}
		
		pls.setReverseEnabled(false);
		return pls;
	}
	
	@Override
	public void draw() {
		if (startCount < 1) {
			return;
		}
		
		Point2D pt = ls.getCurrentPoint();
		parent.translate((float)pt.getX(), (float)pt.getY());
		parent.scale(.75f);
		
		parent.rectMode(CENTER);
		parent.ellipseMode(CENTER);
		parent.textAlign(CENTER, CENTER);
		
		drawBackground(random(170, 190));
		parent.textSize(TEXT_SIZE);

		Color c = parent.getColor().getCurrentColor();
		parent.stroke(c.getRGB());
		parent.fill(c.getRGB());
		
		parent.pushMatrix();
		parent.translate(-HALF_SIZE, -HALF_SIZE);
		
		parent.strokeWeight(1);
		parent.stroke(random(20, 80));
		parent.line(0, HALF_SIZE, SIZE, HALF_SIZE);
		parent.line(HALF_SIZE, 0, HALF_SIZE, SIZE);
		parent.strokeWeight(2);
		parent.line(HALF_SIZE, 0, HALF_SIZE, HALF_SIZE);

		parent.strokeWeight(2);
		parent.stroke(random(190, 220));
		parent.noFill();
		parent.ellipse(HALF_SIZE, HALF_SIZE, SIZE * .75f, SIZE * .75f);
		parent.ellipse(HALF_SIZE, HALF_SIZE, SIZE * .67f, SIZE * .67f);

		// effet usé
		parent.noStroke();
		parent.fill(random(50, 100));
		parent.ellipse(random(0, SIZE), random(0, SIZE), random(20), random(20));
		for (float i = 0; i < SIZE; i = i + random(0, SIZE)) {
			parent.strokeWeight(1);
			parent.stroke(random(50, 255));
			parent.line(i, 0, i, SIZE);
		}
		parent.popMatrix();
		
		parent.fill(0);
		if (frameCounter % FLASH_INTERVAL == 0) {
			startCount--;
		}
		parent.text(String.valueOf(startCount), 0, -TEXT_SIZE / 4);
		
		//line rotation
		parent.pushMatrix();
		parent.rotate(frameCounter * ((PI * 2) / FLASH_INTERVAL));
		parent.stroke(c.getRGB());
		parent.strokeWeight(7);
		parent.line(0, 0, 3, HALF_SIZE);
		parent.popMatrix();
		
		if (frameCounter % FLASH_INTERVAL == 0) {
			drawBackground(FLASH_BACKGROUND);
		}
		
		frameCounter++;
	}

	protected void drawBackground(float gray) {
		parent.pushStyle();
		parent.fill(gray);
		parent.rect(0, 0, SIZE, SIZE);
		parent.popStyle();
	}
}
