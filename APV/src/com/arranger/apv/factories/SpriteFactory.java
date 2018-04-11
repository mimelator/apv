package com.arranger.apv.factories;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.systems.lifecycle.LifecycleSystem.LifecycleData;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class SpriteFactory extends ShapeFactory {
	
	protected PImage sprite;  
	
	public SpriteFactory(Main parent, String file) {
		super(parent);
		sprite = parent.loadImage(file);
	}
	
	public SpriteFactory(Main parent, String file, float scale) {
		this(parent, file);
		this.scale = scale;
	}

	@Override
	public APVShape createShape(Data data) {
		return new SpriteShape(parent, data);
	}

	public class SpriteShape extends APVShape {
		public SpriteShape(Main parent, Data data) {
			super(parent, data);
			if (data instanceof LifecycleData) {
				((LifecycleData)data).setAllowRespawn(false);
			}
		}
		
		@Override
		public void setColor(int color) {
			getShape().setTint(color);
		}

		@Override
		protected PShape createNewShape() {
			float size = parent.random(10,60);
			size *= getScale();
			
			PShape s = parent.createShape();
		    s.beginShape(PApplet.QUAD);
		    s.noStroke();
		    s.texture(sprite);
		    s.normal(0, 0, 1);
		    s.vertex(-size/2, -size/2, 0, 0);
		    s.vertex(+size/2, -size/2, sprite.width, 0);
		    s.vertex(+size/2, +size/2, sprite.width, sprite.height);
		    s.vertex(-size/2, +size/2, 0, sprite.height);
		    s.endShape();
		    
		    return s;
		}
	}
}
