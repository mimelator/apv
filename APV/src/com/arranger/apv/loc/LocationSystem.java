package com.arranger.apv.loc;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

public abstract class LocationSystem {
	
	protected Main parent;

	public LocationSystem(Main parent) {
		this.parent = parent;
	}

	public abstract Point2D getCurrentPoint();
	
	protected Point2D getDefaultLocation() {
		return new Point2D.Float(parent.width / 2, parent.height / 2);
	}

}
