package com.arranger.apv;

import java.awt.geom.Point2D;

public class LocationSystem {

	protected Main parent;

	public LocationSystem(Main parent) {
		this.parent = parent;
	}

	public Point2D getCurrentPoint() {
		return new Point2D.Float(parent.mouseX, parent.mouseY);
	}
}
