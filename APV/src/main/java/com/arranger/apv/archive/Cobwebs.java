package com.arranger.apv.archive;

import java.awt.geom.Point2D;
import java.util.stream.IntStream;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

/**
 * @see https://www.openprocessing.org/sketch/386707
 */
public class Cobwebs extends LiteShapeSystem {

	private static final int NUM_BALLS = 500;
	private static final float THOLD = 5;
	private static final float SPIFAC = 1.05f;
	private static final float DRAG = 0.01f;
	
	Ball balls[] = new Ball[NUM_BALLS];
	float mX;
	float mY;
	
	boolean mousePressed = true;
	
	public Cobwebs(Main parent) {
		super(parent);
		
		IntStream.range(0, NUM_BALLS).forEach(i -> {
			balls[i] = new Ball();
		});
	}


	@Override
	public void draw() {
		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int)pt.getX();
		int mouseY = (int)pt.getY();
		
		parent.strokeWeight(1);
		parent.stroke(255, 255, 255, 5);
		
		if (mousePressed) {
			mX += 0.3 * (mouseX - mX);
			mY += 0.3 * (mouseY - mY);
		}
		
		mX += 0.3 * (mouseX - mX);
		mY += 0.3 * (mouseY - mY);
		for (int i = 0; i < NUM_BALLS; i++) {
			balls[i].render();
		}
	}

	class Ball {
		float X;
		float Y;
		float Xv;
		float Yv;
		float pX;
		float pY;
		float w;

		Ball() {
			X = random(parent.width);
			Y = random(parent.height);
			w = random(1 / THOLD, THOLD);
		}

		void render() {
			if (!mousePressed) {
				Xv /= SPIFAC;
				Yv /= SPIFAC;
			}
			Xv += DRAG * (mX - X) * w;
			Yv += DRAG * (mY - Y) * w;
			X += Xv;
			Y += Yv;
			parent.line(X, Y, pX, pY);
			pX = X;
			pY = Y;
		}
	}
}
