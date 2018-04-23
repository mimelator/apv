package com.arranger.apv.util;

import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SafePainter extends APVPlugin {
	
	private static final int INSET = 11;
	private static final int OFFSET = TextPainter.TEXT_OFFSET;
	
	public enum LOCATION {
		UPPER_LEFT(LEFT), UPPER_RIGHT(RIGHT), LOWER_RIGHT(RIGHT), LOWER_LEFT(LEFT);
		
	
		private int alignment;
		private LOCATION(int alignment) {
			this.alignment = alignment;
		}
	};
	
	private Painter painter;
	
	@FunctionalInterface
	public static interface Painter {
		void paint();
	}

	public SafePainter(Main parent, Painter painter) {
		super(parent);
		this.painter = painter;
	}

	public void paint() {
		paint(null);
	}
	
	public void paint(LOCATION location) {
		parent.pushStyle();
		parent.pushMatrix();
		
		if (location == null) {
			painter.paint();
		} else {
			Point2D pt = getCoordinatesForLocation(location);
			int x = (int)pt.getX();
			int y = (int)pt.getY();
			parent.translate(x, y);
			parent.textAlign(location.alignment);
			painter.paint();
			parent.translate(-x, -y);
		}
		
		parent.popMatrix();
		parent.popStyle();
	}
	
	private Point2D getCoordinatesForLocation(LOCATION corner) {
		int x, y;
		
		switch (corner) {
		case UPPER_LEFT:
			x = OFFSET;
			y = INSET;			
			break;
		case UPPER_RIGHT:
			x = parent.width - OFFSET;
			y = INSET;			
			break;
		case LOWER_RIGHT:
			x = parent.width - OFFSET;
			y = parent.height - (parent.height / INSET);			
			break;
		case LOWER_LEFT:
			default:
			x = OFFSET;
			y = parent.height - (parent.height / INSET);
		}
		
		return new Point2D.Float(x, y);
	}
}
