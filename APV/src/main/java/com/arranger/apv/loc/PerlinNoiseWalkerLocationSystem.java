package com.arranger.apv.loc;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.PerlinNoiseWalker;

/**
 * http://natureofcode.com/book/introduction/
 */
public class PerlinNoiseWalkerLocationSystem extends LocationSystem {

	protected static final int SCALE = 5;
	
	private PerlinNoiseWalker walker;
	private int scale = SCALE;
	private int lastFrameChecked = -1;
	
	public PerlinNoiseWalkerLocationSystem(Main parent) {
		super(parent);
		walker = new PerlinNoiseWalker(parent);
	}

	public PerlinNoiseWalkerLocationSystem(Main parent, int scale) {
		super(parent);
		this.scale = scale;
		walker = new PerlinNoiseWalker(parent);
	}
	
	public PerlinNoiseWalkerLocationSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, SCALE));
	}
	
	@Override
	public Point2D getCurrentPoint() {
		while (lastFrameChecked < parent.getFrameCount()) {
			walker.step(scale);
			lastFrameChecked++;
		}
		
		return new Point2D.Float(walker.x, walker.y);
	}

	public int getScale() {
		return scale;
	}
}
