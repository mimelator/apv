package com.arranger.apv.gradient;

import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class GradientHelperPoints extends APVPlugin {
	
	private Point2D startPoint, endPoint;

	public GradientHelperPoints(Main parent, Point2D startPoint, Point2D endPoint) {
		super(parent);
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public GradientHelperPoints(Configurator.Context ctx) {
		this(ctx.getParent(), 
				new Point2D.Float(ctx.getInt(0, 0), ctx.getInt(1, 0)),
				new Point2D.Float(ctx.getInt(2, 0), ctx.getInt(3, 0)));
	}
	
	@Override
	public String getConfig() {
		//{GradientHelperPoints : [0, 0, 1, 5]}
		return String.format("{%s : [%s, %s, %s, %s]}", getName(), 
				(int)startPoint.getX(), (int)startPoint.getY(), (int)endPoint.getX(), (int)endPoint.getY());
	}

	public Point2D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point2D startPoint) {
		this.startPoint = startPoint;
	}

	public Point2D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point2D endPoint) {
		this.endPoint = endPoint;
	}
}
