package com.arranger.apv.factory;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.systems.lifecycle.LifecycleSystem.LifecycleData;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;
import com.arranger.apv.util.ImageHelper.ImageChangeHandler;
import com.arranger.apv.util.ReflectionHelper;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class SpriteFactory extends ShapeFactory implements ImageChangeHandler {
	
	protected PImage sprite;  
	protected String imageKey;
	
	public SpriteFactory(Main parent, String imageKey, float scale) {
		super(parent, scale);
		this.imageKey = imageKey;
		
		String resolvedImage = imageKey;
		
		ICON_NAMES icon = new ReflectionHelper<ICON_NAMES, ICON_NAMES>(ICON_NAMES.class, parent).getField(imageKey);
		if (icon != null) {
			//look it up
			String title = icon.getFullTitle();
			resolvedImage = parent.getConfigString(title);
		}
		
		sprite = parent.getImageHelper().loadImage(icon, resolvedImage, this);
		if (sprite == null) {
			sprite = new PImage(1, 1);
		}
	}
	
	public SpriteFactory(Configurator.Context ctx) {
		this(ctx.getParent(), 
						ctx.getString(0, ICON_NAMES.SPRITE.name()),
								ctx.getFloat(1, 1));
}
	
	@Override
	public void onImageChange(PImage image) {
		sprite = image;
		if (shapeSystem != null) {
			shapeSystem.onFactoryUpdate();
		}
	}

	@Override
	public String getConfig() {
		//{SpriteFactory : [SPRITE, 2.5]}
		return String.format("{%s : [%s, %s]}", getName(), imageKey, scale);
	}
	
	@Override
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---imageKey: " + imageKey);
		parent.addSettingsMessage("   ---scale: " + scale);
	}

	@Override
	public String getDisplayName() {
		return super.getDisplayName() + ":" + imageKey;
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
