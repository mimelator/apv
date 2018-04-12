package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class RectLocationSystem extends PathLocationSystem {
	
	private static final int LOOP_IN_SECONDS = 5;
	private static final float SCALE = .75f;
	
	public RectLocationSystem(Main parent, boolean splitter) {
		super(parent, LOOP_IN_SECONDS, splitter);
	}
	
	public RectLocationSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getBoolean(0, false));
	}

	@Override
	protected Shape createPath() {
		float width = parent.width * SCALE;
		float height = parent.height * SCALE;
		float x = parent.width * (1 - SCALE) / 2;
		float y = parent.height * (1 - SCALE) / 2;
		return new Rectangle2D.Float(x, y, width, height);
	}
}