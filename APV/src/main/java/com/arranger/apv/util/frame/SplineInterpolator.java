package com.arranger.apv.util.frame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @see https://stackoverflow.com/questions/28335787/how-can-i-implement-easing-functions-with-a-thread
 */
public class SplineInterpolator {

    private final double points[];
    private final List<PointUnit> normalisedCurve;

    public SplineInterpolator(double x1, double y1, double x2, double y2) {
        points = new double[]{ x1, y1, x2, y2 };

        final List<Double> baseLengths = new ArrayList<>();
        double prevX = 0;
        double prevY = 0;
        double cumulativeLength = 0;
        for (double t = 0; t <= 1; t += 0.01) {
            Point2D xy = getXY(t);
            double length = cumulativeLength
                            + Math.sqrt((xy.getX() - prevX) * (xy.getX() - prevX)
                                            + (xy.getY() - prevY) * (xy.getY() - prevY));

            baseLengths.add(length);
            cumulativeLength = length;
            prevX = xy.getX();
            prevY = xy.getY();
        }

        normalisedCurve = new ArrayList<>(baseLengths.size());
        int index = 0;
        for (double t = 0; t <= 1; t += 0.01) {
            double length = baseLengths.get(index++);
            double normalLength = length / cumulativeLength;
            normalisedCurve.add(new PointUnit(t, normalLength));
        }
    }

    /**
     * Expects a value between 0 and 1
     */
    public double interpolate(double fraction) {
        int low = 1;
        int high = normalisedCurve.size() - 1;
        int mid = 0;
        while (low <= high) {
            mid = (low + high) / 2;

            if (fraction > normalisedCurve.get(mid).getPoint()) {
                low = mid + 1;
            } else if (mid > 0 && fraction < normalisedCurve.get(mid - 1).getPoint()) {
                high = mid - 1;
            } else {
                break;
            }
        }
        /*
         * The answer lies between the "mid" item and its predecessor.
         */
        final PointUnit prevItem = normalisedCurve.get(mid - 1);
        final double prevFraction = prevItem.getPoint();
        final double prevT = prevItem.getDistance();

        final PointUnit item = normalisedCurve.get(mid);
        final double proportion = (fraction - prevFraction) / (item.getPoint() - prevFraction);
        final double interpolatedT = prevT + (proportion * (item.getDistance() - prevT));
        return getY(interpolatedT);
    }

    protected Point2D getXY(double t) {
        final double invT = 1 - t;
        final double b1 = 3 * t * invT * invT;
        final double b2 = 3 * t * t * invT;
        final double b3 = t * t * t;
        final Point2D xy = new Point2D.Double((b1 * points[0]) + (b2 * points[2]) + b3, (b1 * points[1]) + (b2 * points[3]) + b3);
        return xy;
    }

    protected double getY(double t) {
        final double invT = 1 - t;
        final double b1 = 3 * t * invT * invT;
        final double b2 = 3 * t * t * invT;
        final double b3 = t * t * t;
        return (b1 * points[2]) + (b2 * points[3]) + b3;
    }

    public static class PointUnit {

        private final double distance;
        private final double point;

        public PointUnit(double distance, double point) {
            this.distance = distance;
            this.point = point;
        }

        public double getDistance() {
            return distance;
        }

        public double getPoint() {
            return point;
        }

    }

}