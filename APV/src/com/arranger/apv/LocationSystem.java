package com.arranger.apv;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.arranger.apv.factories.PrimitiveShapeFactory;

public class LocationSystem {

	protected Main parent;

	public LocationSystem(Main parent) {
		this.parent = parent;
	}

	public Point2D getCurrentPoint() {
		return new Point2D.Float(parent.mouseX, parent.mouseY);
	}
	
	public static class CircularLocationSystem extends LocationSystem {
		
		private static final float SCALE = .75f;
		
		protected Point2D[] points;
		
		public CircularLocationSystem(Main parent) {
			super(parent);
			
			float width = parent.width * SCALE;
			float height = parent.height * SCALE;
			float x = parent.width * (1 - SCALE) / 2;
			float y = parent.height * (1 - SCALE) / 2;
			
			points = PrimitiveShapeFactory.flattenShape(new Ellipse2D.Float(x, y, width, height));
		}
		
		public Point2D getCurrentPoint() {
			return points[parent.frameCount % (points.length - 1)];
		}
	}
}
