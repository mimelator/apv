package com.arranger.apv.systems.lite.cycle;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

public abstract class LiteCycleShapeSystem extends LiteShapeSystem {

	
	private static final int NUM_NEW_OBJECTS = 10;
	protected List<LiteCycleObj> lcObjects  = new ArrayList<LiteCycleObj>();
	protected int numNewObjects;
	
	public LiteCycleShapeSystem(Main parent) {
		super(parent);
		numNewObjects = NUM_NEW_OBJECTS;
	}

	public LiteCycleShapeSystem(Main parent, int numNewObjects) {
		super(parent);
		this.numNewObjects = numNewObjects;
	}
	
	@Override
	public void setup() {
		
	}
	
	@Override
	public void draw() {
		createNewObjects();
	
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

	protected void createNewObjects() {
		for (int i = 0; i < numNewObjects; i++) {
			lcObjects.add(createObj(i));
		}
	}

	protected abstract LiteCycleObj createObj(int index);

	public abstract class LiteCycleObj {
	
		public abstract boolean isDead();
		public abstract void update();
		public abstract void display();
	}
}
