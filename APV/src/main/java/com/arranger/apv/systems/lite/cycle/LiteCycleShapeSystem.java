package com.arranger.apv.systems.lite.cycle;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

public abstract class LiteCycleShapeSystem extends LiteShapeSystem {

protected List<LiteCycleObj> lcObjects  = new ArrayList<LiteCycleObj>();
	
	protected boolean shouldCreateSetupObjects = true;
	protected boolean shouldCreateNewObjectsEveryDraw = true;
	protected boolean shouldRepopulateObjectsEveryDraw = true;
	protected int framesPerReset = Integer.MAX_VALUE;
	
	protected int numNewObjects;
	
	public LiteCycleShapeSystem(Main parent) {
		this(parent, Main.NUMBER_PARTICLES);
	}

	public LiteCycleShapeSystem(Main parent, int numNewObjects) {
		super(parent);
		this.numNewObjects = numNewObjects;
		
		parent.getSetupEvent().register(() -> {
			parent.getColorChangeEvent().register(() -> onFactoryUpdate());
		});
	}
	
	@Override
	public String getConfig() {
		//{AttractorSystem : [{SpriteFactory : [purple.png, .3]}]}
		String result = null;
		if (factory != null) {
			String childConfig = factory.getConfig();
			result = String.format("{%s : [%s, %s]}", getName(), childConfig, numNewObjects);
		} else {
			result = String.format("{%s : [%s]}", getName(), numNewObjects);
		}
		return result;
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
			int total = getNumNewObjects();
			while (lcObjects.size() < total) {
				lcObjects.add(createObj(lcObjects.size()  - 1));
			}
		}
	}

	protected void createNewObjects() {
		if ((parent.getFrameCount() % framesPerReset) == 0) {
			reset();
		} 
		
		int total = getNumNewObjects();
		while (lcObjects.size() < total) {
			lcObjects.add(createObj(lcObjects.size()  - 1));
		}
	}
	
	@Override
	public void onFactoryUpdate() {
		super.onFactoryUpdate();
		reset();
	}

	protected void reset() {
		lcObjects.clear();
	}

	protected int getNumNewObjects() {
		float result = numNewObjects * parent.getParticles().getPct();
		return (int)result;
	}
	
	protected abstract LiteCycleObj createObj(int index);

	public abstract class LiteCycleObj {
	
		public abstract boolean isDead();
		public abstract void update();
		public abstract void display();
	}
}
