package com.arranger.apv.factories;

import java.awt.Shape;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;

import processing.core.PShape;

public class StarFactory extends PrimitiveShapeFactory {
	
	public static PShape createStar(Main parent) {
		  PShape star = parent.createShape();
		  star.beginShape();
		  star.stroke(255);
		  star.strokeWeight(2);
		  star.vertex(0, -50);
		  star.vertex(14, -20);
		  star.vertex(47, -15);
		  star.vertex(23, 7);
		  star.vertex(29, 40);
		  star.vertex(0, 25);
		  star.vertex(-29, 40);
		  star.vertex(-23, 7);
		  star.vertex(-47, -15);
		  star.vertex(-14, -20);
		  star.endShape(CLOSE);
		  return star;
	}
	

	public StarFactory(Main parent) {
		super(parent);
	}

	public StarFactory(Main parent, float scale) {
		super(parent, scale);
	}

	@Override
	public APVShape createShape(Data data) {
		return new PrimitiveShape(parent, data) {
			@Override
			protected Shape createPrimitiveShape(float size) {
				return null; // not used
			}

			@Override
			protected PShape createNewShape() {
				return createStar(parent);
			}
		};
	}
}
