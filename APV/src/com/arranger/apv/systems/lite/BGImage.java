package com.arranger.apv.systems.lite;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.factories.SpriteFactory;

import processing.core.PApplet;

public class BGImage extends LiteShapeSystem {
	
	private APVShape shape;
	private float alpha = 1f;

	public BGImage(Main parent, SpriteFactory factory) {
		super(parent);
		this.factory = factory;
	}
	
	public BGImage(Main parent, SpriteFactory factory, float alpha) {
		super(parent);
		this.factory = factory;
		this.alpha = alpha;
	}

	@Override
	public void setup() {
		
	}

	float scaleX = 1, scaleY = 1;
	
	@Override
	public void draw() {
		
		if (shape == null) {
			SpriteFactory sf = (SpriteFactory)factory;
			shape = sf.createShape(null);
			scaleX = parent.width / sf.getImageWidth();
			scaleY = parent.height / sf.getImageHeight();
		}
		
		float a = PApplet.lerp(0, Main.MAX_ALPHA, alpha);
		
		shape.setColor(parent.getColorSystem().getCurrentColor().getRGB(), a);
		parent.shape(shape.getShape(), 0, 0, parent.width * scaleX, parent.height * scaleY);
	}

}
