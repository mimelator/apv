package com.arranger.apv.factories;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PShape;

public class CircleFactory extends ShapeFactory {

	public CircleFactory(Main parent) {
		super(parent);
	}

	@Override
	public APVShape createShape(Data data) {
		return new CircleShape(parent, data);
	}

	/**
	 * This should only be responsible for the shape
	 */
	public class CircleShape extends APVShape {
		
		public CircleShape(Main parent, Data data) {
			super(parent, data);
		}

		@Override
		protected PShape createNewShape() {
			float size = parent.random(10, 60);
			Shape shape = new Ellipse2D.Float(0, 0, size, size);

			PShape result = parent.createShape();
			result.beginShape();
			for (Point2D point : flattenShape(shape)) {
				result.vertex((float)point.getX(), (float)point.getY());
			}
			
			result.endShape();
			return result;
		}
	}
	
	/**
	 * Helper function to translate an awt Shape to something useable by Processing
	 */
	protected Point2D[] flattenShape(Shape shape ) {
    	List<Point2D> points = new ArrayList<Point2D>();
        PathIterator pi = shape.getPathIterator(null, 1);
        float [] coords = new float[6];

        while (!pi.isDone()) {
        	pi.currentSegment(coords);
        	points.add(new Point2D.Float(coords[0], coords[1]));
        	pi.next();
        }
        return points.toArray(new Point2D[points.size()]);
    }
}
