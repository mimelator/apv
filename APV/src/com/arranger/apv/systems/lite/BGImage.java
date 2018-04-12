package com.arranger.apv.systems.lite;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

public class BGImage extends LiteShapeSystem {
	
	private APVShape shape;
	private float alpha = 1f;
	
	private float scaleX = 1, scaleY = 1;

	public BGImage(Main parent, SpriteFactory factory) {
		super(parent);
		this.factory = factory;
	}
	
	public BGImage(Main parent, SpriteFactory factory, float alpha) {
		super(parent);
		this.factory = factory;
		this.alpha = alpha;
	}
	
	public BGImage(Main parent, SpriteFactory factory, float alpha, float scaleX, float scaleY) {
		super(parent);
		this.factory = factory;
		this.alpha = alpha;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public BGImage(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(SpriteFactory)ctx.loadPlugin(0), 
				ctx.getFloat(1, 1),
				ctx.getFloat(2, 1),
				ctx.getFloat(3, 1));
		
	}
	
	@Override
	public void setup() {
		
	}

	@Override
	public void draw() {
		
		if (shape == null) {
			SpriteFactory sf = (SpriteFactory)factory;
			shape = sf.createShape(null);
			
		}
		
		float a = PApplet.lerp(0, Main.MAX_ALPHA, alpha);
		
		shape.setColor(parent.getColorSystem().getCurrentColor().getRGB(), a);
		parent.shape(shape.getShape(), 0, 0, parent.width * scaleX, parent.height * scaleY);
	}

}
