package com.arranger.apv.factories;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;

public abstract class ParametricFactory extends PrimitiveShapeFactory {
	
	public static class HypocycloidFactory extends ParametricFactory {

		public HypocycloidFactory(Main parent) {
			super(parent, true, 20, 0, 2);
		}

		/**
		 * http://www-groups.dcs.st-and.ac.uk/history/Curves/Hypocycloid.html
		 * x = (a - b) cos(t) + b cos((a/b - 1)t), y = (a - b) sin(t) - b sin((a/b - 1)t)
		 */
		@Override
		protected double[] genPoint(double theta) {
			double a = 5;
	        double b = 3;

	        double x = (a - b) * Math.cos(theta) + b * Math.cos((a / b - 1) * theta);
	        double y = (a - b) * Math.sin(theta) - b * Math.sin((a / b - 1) * theta);
	        
	        return new double[]{x, y};	
		}
	}
	
	public static class InvoluteFactory extends ParametricFactory {

		public InvoluteFactory(Main parent) {
			super(parent, true, 15, 0, .5);
		}

		/**
		 * http://www-groups.dcs.st-and.ac.uk/history/Curves/Involute.html
		 * x = a(cos(t) + t sin(t)), y = a(sin(t) - t cos(t))
		 */
		@Override
		protected double[] genPoint(double theta) {
			double a = .3;

	        double x = a * Math.cos(theta) + (theta * Math.sin(theta));
	        double y = a * Math.sin(theta) - (theta * Math.cos(theta));
	        
	        return new double[]{x, y};	
		}
	}

	public static final double PHI = 1.6180339;
	
	private static final double X_OUT_OF_BOUNDS = 0;
    private static final double Y_OUT_OF_BOUNDS = 0;
	
	protected boolean closeShape = true;
	protected double periods = 20;
	protected double periodOffset = 0;
	protected double precision = 1;
	
	protected abstract double [] genPoint(double theta);
	
	private ParametricFactory(Main parent, 
			boolean closeShape, 
			double periods, 
			double periodOffset,
			double precision) {
		super(parent);
		this.closeShape = closeShape;
		this.precision = precision;
		this.periods = periods;
		this.periodOffset = periodOffset;
	}

	@Override
	public APVShape createShape(Data data) {
		return new PrimitiveShape(parent, data) {
			@Override
			protected Shape createPrimitiveShape(float size) {
				Point2D [] points = genPoints(size);
				return pointsToShape(points);
			}
		};
	}
	
    private Point2D[] genPoints(float size) {
        Point2D[] points = new Point2D[(int) (periods / precision)];
        int index = 0;
        double maxTheta = periods + periodOffset;
        for (double theta = 0 + periodOffset; theta <= maxTheta && index < points.length; theta += precision) {
            double [] shapePoint = genPoint(theta);
            
            double x = shapePoint[0] * size * .1;
            double y = shapePoint[1] * size * .1;

            if (x == Double.NaN || x == Double.NEGATIVE_INFINITY || x == Double.POSITIVE_INFINITY) {
                x = X_OUT_OF_BOUNDS;
            }
            if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY) {
                y = Y_OUT_OF_BOUNDS;
            }

            points[index++] = new Point2D.Double(x, y);
        }
        return points;
    }
	
    private GeneralPath pointsToShape(Point2D[] points) {
        if (points.length == 0) {
            System.out.println("points are of length 0");
            return new GeneralPath();
        }

        GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, (points.length + 1) * 2);
        generalPath.moveTo((float) points[0].getX(), (float) points[0].getY());
        for (int index = 1; index < points.length; index++) {
            generalPath.lineTo((float) points[index].getX(), (float) points[index].getY());
        }
        if (closeShape) {
            generalPath.closePath();
        }

        return generalPath;
    }
}
