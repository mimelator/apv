package com.arranger.apv.loc;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class StarLocation extends PointsLocationSystem {

	private static final double STAR_POINTS [][] = { 
		    { 0, 85 }, { 75, 75 }, { 100, 10 }, { 125, 75 }, 
		    { 200, 85 }, { 150, 125 }, { 160, 190 }, { 100, 150 }, 
		    { 40, 190 }, { 50, 125 }, { 0, 85 } 
		};
	
	private static final int LOOP_IN_SECONDS = 5;
	
	
	public StarLocation(Main parent, boolean splitter, boolean allowRotation) {
		super(parent, splitter, allowRotation);
	}
	
	public StarLocation(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	public int getLoopInSeconds() {
		return LOOP_IN_SECONDS;
	}
	
	protected double [][] getPoints() {
		return STAR_POINTS;
	}
	
}
