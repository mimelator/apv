package com.arranger.apv.systems.lite.cycle;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

public abstract class LiteCycleShapeSystem extends LiteShapeSystem {

	
	private static final int NUM_NEW_OBJECTS = 10;
	protected List<LiteCycleObj> lcObjects  = new ArrayList<LiteCycleObj>();
	protected int numNewObjects;
	protected boolean shouldCreateSetupObjects = true;
	protected boolean shouldCreateNewObjectsEveryDraw = true;
	protected boolean shouldRepopulateObjectsEveryDraw = false;
	protected int framesPerReset = Integer.MAX_VALUE;
	
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
		if (shouldCreateSetupObjects) {
			createNewObjects();
		}
	}
	
	@Override
	public void draw() {
		if (shouldCreateNewObjectsEveryDraw) {
			createNewObjects();
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
		
		if (shouldRepopulateObjectsEveryDraw) {
			while (lcObjects.size() < numNewObjects) {
				lcObjects.add(createObj(lcObjects.size()  - 1));
			}
		}
	}

	protected void createNewObjects() {
		if ((parent.frameCount % framesPerReset) == 0) {
			reset();
		} 
		
		for (int i = 0; i < numNewObjects; i++) {
			lcObjects.add(createObj(i));
		}
	}
	
	protected  void reset() {
		lcObjects.clear();
	}

	protected abstract LiteCycleObj createObj(int index);

	public abstract class LiteCycleObj {
	
		public abstract boolean isDead();
		public abstract void update();
		public abstract void display();
	}
}
