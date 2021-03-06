
package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.factory.PrimitiveShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.Reverser;
import com.arranger.apv.util.frame.SingleFrameSkipper;

import processing.core.PApplet;

public abstract class PathLocationSystem extends LocationSystem {
	
	private static final Logger logger = Logger.getLogger(PathLocationSystem.class.getName());

	private static final int DEFAULT_PULSES_TO_REVERSE = 2; //Direction Change every two pulses
	protected Point2D[] points;
	protected int secondsPerPath; 
	private int startTime;
	private Reverser reverser;
	private boolean splitter = true;
	private boolean allowRotation = false;
	
	private SingleFrameSkipper skipper = new SingleFrameSkipper(parent);
	private Point2D lastAnswer = new Point2D.Float();
	
	public PathLocationSystem(Main parent, boolean splitter, boolean allowRotation) {
		super(parent);
		this.secondsPerPath = getLoopInSeconds();
		points = PrimitiveShapeFactory.flattenShape(createPath());
		startTime = parent.millis();
		this.splitter = splitter;
		this.allowRotation = allowRotation;
		reverser = new Reverser(parent, DEFAULT_PULSES_TO_REVERSE);
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.REVERSE, (command, source, modifiers) -> reverser.reverse());
			cs.registerHandler(Command.SCRAMBLE, (command, source, modifiers) -> reverser.reverse());
		});
	}
	
	public PathLocationSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getBoolean(0, false), ctx.getBoolean(1, false));
	}
	
	@Override
	public String getConfig() {
		//{PathLocationSystem : [true]}
		return String.format("{%s : [%b, %b]}", getName(), splitter, allowRotation);
	}
	
	public boolean isSplitter() {
		return splitter;
	}
	
	public void setReverseEnabled(boolean enabled) {
		reverser.setEnabled(enabled);
	}
	
	public abstract int getLoopInSeconds();
	
	protected abstract Shape createPath();
	
	public Point2D getCurrentPoint() {
		 //only want to respond once / frame
		if (skipper.isNewFrame()) {
			lastAnswer = rotatePoint(generatePoint());
		}
		return lastAnswer;
	}
	
	protected Point2D rotatePoint(Point2D pt) {	
		if (!allowRotation) {
			return pt;
		}

		Point2D result = new Point2D.Double();
		AffineTransform tra = AffineTransform.getTranslateInstance(parent.width / 2, parent.height / 2);
		tra.rotate(PApplet.sin(parent.getFrameCount()));
		tra.translate(-parent.width / 2, -parent.height / 2);
		tra.transform(pt, result);
		return result;
	}
		
	protected Point2D generatePoint() {	
		if (splitter) {
			reverser.reverse();
		}
		
		float pct = getPercentagePathComplete();
		if (Math.abs(1 - pct) < 0.01f) {
			parent.getLocationEvent().fire();
		}
		
		float result = PApplet.lerp(0, points.length - 1, pct);

		int indexFloor = (int)Math.floor(result);
		int indexCeil = (int)Math.ceil(result);
		float pct2 = result - indexFloor;
		
		float x1 = (float)points[indexFloor].getX();
		float y1 = (float)points[indexFloor].getY();
		float x2 = (float)points[indexCeil].getX();
		float y2 = (float)points[indexCeil].getY();
		
		//try to do further interpoloation
		float x = PApplet.lerp(x1, x2, pct2);
		float y = PApplet.lerp(y1, y2, pct2);
		
		lastAnswer = new Point2D.Float(x, y);
		return lastAnswer;
	}

	protected float getPercentagePathComplete() {
		int millisEllapsed = parent.millis() - startTime;
		float secEllapsed = millisEllapsed / 1000.f;
		secEllapsed %= secondsPerPath; 
		float result = secEllapsed / secondsPerPath;
		if (result < 0.0f || result > 1.0f) {
			logger.warning("Illegal value for pct: " + result);
			result = PApplet.constrain(result, 0.1f, .9f);
		}
		
		if (reverser.isReverse()) {
			return 1.0f - result;
		} else {
			return result;
		}
	}

}
