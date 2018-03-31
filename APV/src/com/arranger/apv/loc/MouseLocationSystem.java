package com.arranger.apv.loc;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

public class MouseLocationSystem extends LocationSystem {

	public MouseLocationSystem(Main parent) {
		super(parent);
	}

	public Point2D getCurrentPoint() {
		return new Point2D.Float(parent.mouseX, parent.mouseY);
	}
}
