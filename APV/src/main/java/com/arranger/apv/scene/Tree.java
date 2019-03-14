package com.arranger.apv.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.factory.SpriteFactory;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.frame.Tracker;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * @see https://www.openprocessing.org/sketch/144159
 **/
public class Tree extends Animation {

	private static final int MAX_VELOCITY = -4; //-1;
	private static final int DEFAULT_SPRITE_SIZE = 5;
	private static final float MIN_SIZE = 3.5f;
	private static final int GROWTH_THRESHOLD = 50;
	private static final float SPEED = 3.0f;//1.5f;
	
	private Tracker<Tree> tracker;
	private List<PathFinder> paths = new ArrayList<PathFinder>();
	private int threshold;
	private float minSize;
	private APVShape apvShape;
	
	public Tree(Main parent) {
		super(parent);
		String threshVal = parent.getConfigValueForFlag(Main.FLAGS.TREE_COMPLEXITY_CUTOFF, String.valueOf(GROWTH_THRESHOLD));
		threshold = Integer.parseInt(threshVal);
		String minSizeVal = parent.getConfigValueForFlag(Main.FLAGS.TREE_MIN_SIZE, String.valueOf(MIN_SIZE));
		minSize = Float.parseFloat(minSizeVal);
	}
	
	@Override
	public boolean isNew() {
		return tracker != null;
	}

	@Override
	public void drawScene() {
		super.drawScene();
		
		if (paths.isEmpty()) {
			paths.add(new PathFinder());
			tracker = new Tracker<Tree>(parent, parent.getSceneCompleteEvent());
		}
		
		if (apvShape == null) {
			apvShape = new SpriteFactory(parent, ImageHelper.ICON_NAMES.random().name(), DEFAULT_SPRITE_SIZE).createShape(new Data() {});
		}
		
		Color col = parent.getColor().getCurrentColor();

		List<PathFinder> newPaths = new ArrayList<PathFinder>();
		for (PathFinder pf : paths) {
			PVector loc = pf.location;
			float diam = pf.diameter;
			
			apvShape.setColor(col.getRGB());
			parent.shape(apvShape.getShape(), loc.x, loc.y, diam, diam);
			
			
			PathFinder newPf = pf.update();
			if (newPf != null) {
				newPaths.add(newPf);
			}
		}
		
		if (!newPaths.isEmpty()) {
			paths.addAll(newPaths);
		}
		
		paths.removeIf(pf -> pf.diameter <= minSize);
		
		if (tracker != null) {
			if (tracker.isActive(e -> {return paths.size() > threshold;})) {
				if (paths.size() < threshold) {
//					System.out.println("Finished threshold: " + threshold);
					tracker.fireEvent();
					tracker = null;
					apvShape = null;
				}
			}
		}
	}

	class PathFinder {
		
		PVector location;
		PVector velocity;
		float diameter;

		PathFinder() {
			//TODO make the orientation? configurable
			int width = parent.width / 2;
			float xLocation = parent.random(width * .8f, width * 1.2f); //doesn't have to be in the center
			
			location = new PVector(xLocation, parent.height);
			velocity = new PVector(0, MAX_VELOCITY);
			diameter = 32;
		}

		PathFinder(PathFinder parent) {
			location = parent.location.copy();
			velocity = parent.velocity.copy();
			float area = PI * PApplet.sq(parent.diameter / 2);
			float newDiam = PApplet.sqrt(area / 2 / PI) * 2;
			diameter = newDiam;
			parent.diameter = newDiam;
		}

		PathFinder update() {
			if (diameter > 0.5) {
				location.add(velocity);
				PVector bump = new PVector(parent.random(-1, 1), parent.random(-1, 1));
				bump.mult(.5f); //.1f
				velocity.add(bump);
				velocity.normalize();
				velocity.mult(SPEED);
				if (parent.random(0, 1) < 0.02) {
					return new PathFinder(this);
				}
			}
			return null;
		}
	}
}
