package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PShape;

public class StarMaker extends APVPlugin {

	public StarMaker(Main parent) {
		super(parent);
	}

	public PShape createStar() {
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
}
