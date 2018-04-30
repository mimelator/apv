package com.arranger.apv.loc;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator.Context;

public class ArcLocation extends PointsLocationSystem {

	private static final int LOOP_IN_SECONDS = 8;
	private static final double ARC_POINTS [][] = { 
		    { 0, 0 }, { 100, 0 }, { 100, 100 }, { 0, 100 }, { 0, 0 },
		};
	
	public ArcLocation(Main parent, boolean splitter) {
		super(parent, splitter);
	}

	public ArcLocation(Context ctx) {
		super(ctx);
	}

	@Override
	protected double[][] getPoints() {
		return ARC_POINTS;
	}

	@Override
	public int getLoopInSeconds() {
		return LOOP_IN_SECONDS;
	}

}
