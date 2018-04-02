package com.arranger.apv.systems;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PConstants;

public abstract class DirectLifecycleSystem extends LifecycleSystem implements PConstants{

	public DirectLifecycleSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}

	public void draw() {
		super.draw();
		
		parent.noStroke();
		for (APVShape p : particles) {
			DirectLifecycleData d = (DirectLifecycleData)p.getData();
			parent.pushMatrix();
			d.update();
			d.draw();
			parent.popMatrix();
		}
	}

	public abstract class DirectLifecycleData extends LifecycleData {

		public DirectLifecycleData() {
			super();
		}
		
		
		public abstract void draw();
	}
}