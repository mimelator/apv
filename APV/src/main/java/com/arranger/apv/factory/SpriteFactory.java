package com.arranger.apv.factory;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.systems.lifecycle.LifecycleSystem.LifecycleData;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class SpriteFactory extends ShapeFactory {
	
	protected PImage sprite;  
	protected float alpha;
	protected String file;
	
	public SpriteFactory(Main parent, String file) {
		super(parent);
		this.file = file;
		sprite = parent.loadImage(file);
	}
	
	public SpriteFactory(Main parent, String file, float scale) {
		this(parent, file);
		this.scale = scale;
	}
	
	public SpriteFactory(Configurator.Context ctx) {
		super(ctx.getParent());
		
		//look for file, scale 
		this.file = ctx.getString(0, null);
		sprite = parent.loadImage(file);
		this.alpha = ctx.getFloat(1, 0);
	}
	
	@Override
	public String getConfig() {
		//{SpriteFactory : [triangle.png, 2.5]}
		return String.format("{%s : [%s, %s]}", getName(), file, alpha);
	}
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName() + ":" + file;
	}

	public int getImageWidth() {
		return sprite.width;
	}
	
	public int getImageHeight() {
		return sprite.height;
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
			PShape s = getShape();
			s.setTint(color);
		}
		
		@Override
		public void setColor(int color, float alpha) {
			PShape s = getShape();
			
			Color c = new Color(color); 
			color = parent.color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
			
			s.setTint(color);
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
