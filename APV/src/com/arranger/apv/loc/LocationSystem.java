package com.arranger.apv.loc;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

public class LocationSystem {

	protected Main parent;

	public LocationSystem(Main parent) {
		this.parent = parent;
	}

	public Point2D getCurrentPoint() {
		return new Point2D.Float(parent.mouseX, parent.mouseY);
	}
}
