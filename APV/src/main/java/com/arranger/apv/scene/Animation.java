package com.arranger.apv.scene;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator.Context;

public abstract class Animation extends Scene {

	public Animation(Main parent) {
		super(parent);
	}

	public Animation(Context ctx) {
		super(ctx);
	}

	public Animation(Scene o) {
		super(o);
	}
	
	@Override
	public void setup() {
	}
	
	public abstract boolean isNew();
}
