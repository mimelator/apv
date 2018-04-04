package com.arranger.apv.systems.lite.cycle;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

public abstract class LiteCycleShapeSystem extends LiteShapeSystem {

	
	private static final int NUM_NEW_PARTICLES = 10;
	protected List<LiteCycleObj> lcObjects  = new ArrayList<LiteCycleObj>();
	protected int numNewParticles;
	
	public LiteCycleShapeSystem(Main parent) {
		super(parent);
		numNewParticles = NUM_NEW_PARTICLES;
	}

	@Override
	public void setup() {
	}
	
	@Override
	public void draw() {
		for (int i = 0; i < numNewParticles; i++) {
			lcObjects.add(createObj(i));
		}
	
		for (int i = lcObjects.size() - 1; i > -1; i--) {
			LiteCycleObj obj = lcObjects.get(i);
			if (obj.isDead()) {
				lcObjects.remove(i);
			} else {
				obj.update();
				obj.display();
			}
		}
	}

	protected abstract LiteCycleObj createObj(int index);

	public abstract class LiteCycleObj {
	
		public abstract boolean isDead();
		public abstract void update();
		public abstract void display();
	}
}
