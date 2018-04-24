package com.arranger.apv.util.draw;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SafePainter extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(SafePainter.class.getName());
	
	public static final int INSET = 11;
	public static final int OFFSET = TextPainter.TEXT_OFFSET;
	
	public enum LOCATION {
		UPPER_LEFT(LEFT), UPPER_RIGHT(RIGHT), LOWER_RIGHT(RIGHT), LOWER_LEFT(LEFT), NONE(CENTER);
	
		private int alignment;
		private LOCATION(int alignment) {
			this.alignment = alignment;
		}
		
		public int getAlignment() {
			return alignment;
		}
		
		/**
		 * https://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum
		 */
		private static final List<LOCATION> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static LOCATION random() {
			return VALUES.get(RANDOM.nextInt(SIZE));
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
		paint(null, true);
	}
	
	public void paint(LOCATION location) {
		paint(location, true);
	}
	
	public void paint(LOCATION location, boolean isSafe) {
		try {
			if (isSafe) {
				parent.pushStyle();
				parent.pushMatrix();
			}

			if (location == null) {
				painter.paint();
			} else {
				Point2D pt = getCoordinatesForLocation(location);
				int x = (int) pt.getX();
				int y = (int) pt.getY();
				parent.translate(x, y);
				parent.textAlign(location.alignment);
				painter.paint();
				parent.translate(-x, -y);
			}

			if (isSafe) {
				parent.popMatrix();
				parent.popStyle();
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	public Point2D getCoordinatesForLocation(LOCATION corner) {
		int x = 0, y = 0;
		
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
			x = OFFSET;
			y = parent.height - (parent.height / INSET);
		case NONE:
			default:
		}
		
		return new Point2D.Float(x, y);
	}
}
