package com.arranger.apv.archive;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

import processing.opengl.PShader;

public class ShaderTest extends LiteShapeSystem {

	public ShaderTest(Main parent) {
		super(parent);
		toon = parent.loadShader("ToonFrag.glsl", "ToonVert.glsl");
	}

	@Override
	public void draw() {
		parent.rectMode(CENTER);
		parent.background(0);
		toon.set("resolution", (float) parent.width, (float) parent.height);
		toon.set("time", parent.millis() / 200);
		parent.shader(toon);
		parent.rect(0, 0, parent.width, parent.height);
	}

	PShader toon;

}
