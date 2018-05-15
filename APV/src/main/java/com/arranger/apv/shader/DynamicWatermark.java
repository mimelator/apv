package com.arranger.apv.shader;

import java.util.List;

import com.arranger.apv.Main;

import processing.core.PGraphics;

public class DynamicWatermark extends Watermark {
	
	public DynamicWatermark(Main parent, float alpha, float textSize, String text, List<SHADERS> shaders) {
		super(parent, text, alpha, false, null, shaders);
			
		PGraphics g = parent.createGraphics(parent.width, parent.height);
		g.beginDraw();
		g.stroke(255); //TODO use color?
		g.textSize(textSize);
		g.textAlign(CENTER, CENTER);
		g.text(text, parent.width / 2, parent.height / 2);
		g.endDraw();
		
		this.image = g.get();
	}


}
