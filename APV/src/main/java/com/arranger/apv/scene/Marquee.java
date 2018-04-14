package com.arranger.apv.scene;

import java.util.ArrayList;

import com.arranger.apv.Main;
import com.arranger.apv.Scene;
import com.arranger.apv.util.Configurator;

import processing.core.PFont;
import processing.core.PGraphics;

/**
 * @see https://www.openprocessing.org/sketch/498901
 */
public class Marquee extends Scene {

	private String text;
	private boolean isInit = false;

	public Marquee(Main parent, String text) {
		super(parent);
		this.text = text;
	}

	public Marquee(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, "Hello World"));
	}

	@Override
	public boolean isNormal() {
		return false;
	}

	@Override
	public void drawScene() {
		if (!isInit) {
			setup();
		}
		draw();
	}

	int ELLIPSE_COLOR;
	int LINE_COLOR;
	int PGRAPHICS_COLOR;
	int LINE_LENGTH = 25;
	boolean reverseDrawing = false;

	PGraphics pg;
	PFont font;
	ArrayList<OneChr> chrs = new ArrayList<OneChr>();

	protected void setup() {
		ELLIPSE_COLOR = parent.color(0);
		LINE_COLOR = parent.color(0, 125);
		PGRAPHICS_COLOR = parent.color(0);

		// create and draw to PPraphics (see Getting Started > UsingPGraphics example)
		pg = parent.createGraphics(parent.width, parent.height, JAVA2D);
		pg.beginDraw();
		pg.textSize(200);
		pg.textAlign(CENTER, CENTER);
		pg.fill(PGRAPHICS_COLOR);
		pg.text(text, pg.width / 2, pg.height / 2);
		pg.endDraw();
		isInit = true;
	}

	protected void draw() {
		parent.fill(255);
		parent.stroke(0);
		parent.textAlign(CENTER, CENTER);

		if (chrs.size() < 2000) {
			for (int i = 0; i < 60; i++) {
				float x = parent.random(parent.width);
				float y = parent.random(parent.height);
				int c = pg.get((int) x, (int) y);
				if (c == PGRAPHICS_COLOR) {
					chrs.add(new OneChr(x, y, 1));
				}
			}
		}
		for (OneChr oc : chrs) {
			oc.updateMe();
		}
	}

	class OneChr {
		float x, y;
		float myRotate;
		float myBrightness, glowSpeed, glowOffs;
		int mySize;
		char myChr;

		OneChr(float _x, float _y, float gS) {
			x = _x;
			y = _y;
			glowSpeed = gS;
			myBrightness = 0;
			glowOffs = parent.random(40) * -1;

			int radi = (int) Math.floor(parent.random(4));
			myRotate = (HALF_PI * radi);
			float sizeFactor = parent.random(2);
			mySize = (int) Math.max(10, Math.pow(sizeFactor, 5));
			myChr = (char) parent.random(33, 126);
		}

		void updateMe() {
			parent.noStroke();
			parent.fill(255, Math.max(myBrightness + glowOffs, 0));
			parent.pushMatrix();
			parent.translate(x, y);
			parent.rotate(myRotate);
			parent.textSize(mySize);

			parent.text(myChr, 0, 0);
			parent.popMatrix();

			myBrightness += glowSpeed;
			myBrightness = Math.min(myBrightness, (255 + (-1 * glowOffs)));
		}
	}
}
