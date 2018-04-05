package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;

import com.arranger.apv.Main;

public class BubbleShapeSystem extends LiteCycleShapeSystem {
	
	private static final float PCT_TO_DIE = 99.75f;

	public BubbleShapeSystem(Main parent) {
		this(parent, DEFAULT_NUM_NEW_OBJECTS);
	}

	public BubbleShapeSystem(Main parent, int numNewObjects) {
		super(parent, numNewObjects);
		shouldCreateNewObjectsEveryDraw = false;
	}

	@Override
	protected LiteCycleObj createObj(int index) {
		return new Bubble();
	}

	private class Bubble extends LiteCycleObj {
		
		private float x, y, vx, vy, step;
		private Color color;

		private Bubble() {
			this.x = random(parent.width);
			this.y = random(parent.height);
			this.vx = random(1, 5);
			this.vy = random(1, 5);
			this.step = random(5, 20);
			color = parent.getColorSystem().getCurrentColor();
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
				//parent.fill(255, 255, 0, i * this.step);  //Just shades of Yellow
				parent.fill(color.getRed(), color.getGreen(), color.getBlue(), i * this.step);
				parent.ellipse(0, 0, i * this.step, i * this.step);
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
