package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.factory.CircleImageFactory;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class BubbleShapeSystem extends LiteCycleShapeSystem {
	
	private static final float PCT_TO_DIE = 99.75f;
	protected PImage circle;

	public BubbleShapeSystem(Main parent) {
		this(parent, Main.NUMBER_PARTICLES);
	}

	public BubbleShapeSystem(Main parent, int numNewObjects) {
		super(parent, numNewObjects);
		shouldCreateNewObjectsEveryDraw = false;
		circle = parent.loadImage(CircleImageFactory.CIRCLE_PNG);
	}
	
	public BubbleShapeSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, Main.NUMBER_PARTICLES));
	}
	
	@Override
	protected LiteCycleObj createObj(int index) {
		return new Bubble();
	}

	private class Bubble extends LiteCycleObj {
		
		private float x, y, vx, vy, step;
		private Color color;
		private PShape s;

		private Bubble() {
			this.x = random(parent.width);
			this.y = random(parent.height);
			this.vx = random(1, 5);
			this.vy = random(1, 5);
			this.step = random(5, 20);
			color = parent.getColor().getCurrentColor();
			createNewShape();
		}
		
		protected void createNewShape() {
			float size = parent.random(10,60);
			
			s = parent.createShape();
		    s.beginShape(PApplet.QUAD);
		    s.noStroke();
		    s.texture(circle);
		    s.normal(0, 0, 1);
		    s.vertex(-size/2, -size/2, 0, 0);
		    s.vertex(+size/2, -size/2, circle.width, 0);
		    s.vertex(+size/2, +size/2, circle.width, circle.height);
		    s.vertex(-size/2, +size/2, 0, circle.height);
		    s.endShape();
		}
		
		
		@Override
		public boolean isDead() {
			float random = random(100);
			return random > PCT_TO_DIE; 
		}

		@Override
		public void display() {
			parent.pushMatrix();
			parent.translate(this.x, this.y);
			parent.noStroke();
			for (int i = 5; i > 0; i--) {
				int c = parent.color(color.getRed(), color.getGreen(), color.getBlue(), i * step);
				s.setTint(c);
				parent.shape(s, 0, 0, i * step, i * step);
			}
			parent.popMatrix();
		}

		@Override
		public void update() {
			this.x += this.vx;
			this.y += this.vy;

			if (this.x > parent.width || this.x < 0) {
				this.vx *= -1;
			}

			if (this.y > parent.height || this.y < 0) {
				this.vy *= -1;
			}
		}
	}
}
