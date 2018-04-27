package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.color.OscillatingColor;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/394703
 */
public class SquareCircle extends LiteShapeSystem {

	private static final int BOX_ALPHA = 127;
	private static final int ROTATION_SPEED = 5;
	private static final int COLOR_OSC_SCALAR = 20;
	private static final int SPACE_BETWEEN_SQUARES = 50;
	private static final int SQUARE_SIDE = 30;
	
	private OscillatingColor oscColor;
	
	public SquareCircle(Main parent) {
		super(parent);
		oscColor = new OscillatingColor(parent, COLOR_OSC_SCALAR);
	}

	@Override
	public void draw() {
		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int)pt.getX();
		int mouseY = (int)pt.getY();
		
		Color color = oscColor.getCurrentColor();
		parent.fill(color.getRGB(), BOX_ALPHA);
		
		parent.stroke(1);
		parent.rectMode(CENTER);
		
		float theta = parent.oscillate(0, 360, ROTATION_SPEED);
		
		for (int i = 0; i < parent.width; i += SPACE_BETWEEN_SQUARES) {
			for (int j = 0; j < parent.height; j += SPACE_BETWEEN_SQUARES) {
				float dist = PApplet.sqrt(PApplet.sq(mouseX - i) + PApplet.sq(mouseY - j)) / 10;
				
				parent.pushMatrix();
				parent.translate(i, j);
				parent.rotate(PApplet.radians(theta));
				parent.translate(-i, -j);
				parent.rect(i, j, 
						PApplet.sq(PApplet.sqrt(SQUARE_SIDE - dist)), 
						PApplet.sq(PApplet.sqrt(SQUARE_SIDE - dist)));
				
				parent.popMatrix();
			}
		}
	}
}
