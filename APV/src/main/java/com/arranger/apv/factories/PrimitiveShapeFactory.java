package com.arranger.apv.factories;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.util.Configurator;

import processing.core.PShape;

public abstract class PrimitiveShapeFactory extends ShapeFactory {
	
	private static final int LARGE_SHAPE_SIZE = 60;
	private static final int SMALL_SHAPE_SIZE = 10;
	
	public PrimitiveShapeFactory(Main parent) {
		super(parent);
	}
	
	public PrimitiveShapeFactory(Main parent, float scale) {
		super(parent, scale);
	}
	
	public PrimitiveShapeFactory(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, DEFAULT_SCALE));
	}
	
	protected float newShapeSize() {
		float size = parent.random(SMALL_SHAPE_SIZE, LARGE_SHAPE_SIZE);
		size *= scale;
		return size;
	}
	
	public abstract class PrimitiveShape extends APVShape {

		public PrimitiveShape(Main parent, Data data) {
			super(parent, data);
		}

		protected abstract Shape createPrimitiveShape(float size);
		
		@Override
		protected PShape createNewShape() {
			float size = newShapeSize();
			Shape shape = createPrimitiveShape(size);

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
	public static Point2D[] flattenShape(Shape shape) {
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
