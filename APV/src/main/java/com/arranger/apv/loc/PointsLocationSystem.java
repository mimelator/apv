package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public abstract class PointsLocationSystem extends PathLocationSystem {
	
	private static final float SCALER = .9f;

	public PointsLocationSystem(Main parent, boolean splitter, boolean allowRotation) {
		super(parent, splitter, allowRotation);
	}

	public PointsLocationSystem(Configurator.Context ctx) {
		super(ctx);
	}
	
	protected abstract double [][] getPoints();

	@Override
	protected Shape createPath() {
		double [][] pts = getPoints();
		
		//find center of points
		List<double[]> ptsList = Arrays.asList(pts);
		double xmid = ptsList.stream().mapToDouble(arr -> arr[0]).average().getAsDouble();
		double ymid = ptsList.stream().mapToDouble(arr -> arr[1]).average().getAsDouble();
		
		
		GeneralPath star = new GeneralPath();
		star.moveTo(pts[0][0], pts[0][1]);
		IntStream.range(0, pts.length).forEach(i -> {
			star.quadTo(xmid, ymid, pts[i][0], pts[i][1]);
		});
		star.closePath();
		
		Rectangle2D starBounds = star.getBounds2D();
		double scaleHeight = parent.height / starBounds.getHeight() * SCALER;
		double scaleWidth = parent.width / starBounds.getWidth() * SCALER;
		star.transform(AffineTransform.getScaleInstance(scaleWidth, scaleHeight));
		return star;
	}
}
