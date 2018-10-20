package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;

public class TubularSquares extends Tubular {

	public TubularSquares(Main parent) {
		super(parent);
	}


	@Override
	protected void setDrawMode() {
		parent.rectMode(CENTER);
	}

	@Override
	protected void drawTubeShape(float tubeSide) {
		parent.rect(parent.width / 2, parent.height / 2, tubeSide, tubeSide);
	}
}
