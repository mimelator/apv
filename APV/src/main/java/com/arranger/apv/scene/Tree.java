package com.arranger.apv.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.util.frame.Tracker;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * @see https://www.openprocessing.org/sketch/144159
 **/
public class Tree extends Animation {

	private static final int GROWTH_THRESHOLD = 50;
	
	private static final float SPEED = 1.5f;
	
	private Tracker<Tree> tracker;
	private List<PathFinder> paths = new ArrayList<PathFinder>();
	
	public Tree(Main parent) {
		super(parent);
	}
	
	@Override
	public char getHotKey() {
		return 't';
	}
	
	@Override
	protected void reset() {
		tracker = null;
		paths.clear();
	}

	@Override
	public void drawScene() {
		if (paths.isEmpty()) {
			paths.add(new PathFinder());
			tracker = new Tracker<Tree>(parent, parent.getSceneCompleteEvent());
		}
		
		parent.ellipseMode(CENTER);
		Color col = parent.getColor().getCurrentColor();
		parent.fill(col.getRed(), col.getGreen(), col.getBlue());

		List<PathFinder> newPaths = new ArrayList<PathFinder>();
		for (PathFinder pf : paths) {
			PVector loc = pf.location;
			float diam = pf.diameter;
			parent.ellipse(loc.x, loc.y, diam, diam);
			PathFinder newPf = pf.update();
			if (newPf != null) {
				newPaths.add(newPf);
			}
		}
		
		if (!newPaths.isEmpty()) {
			paths.addAll(newPaths);
		}
		
		paths.removeIf(pf -> pf.diameter <= .5);
		
		if (tracker != null) {
			if (tracker.isActive(e -> {return paths.size() > GROWTH_THRESHOLD;})) {
				if (paths.size() < GROWTH_THRESHOLD) {
					tracker.fireEvent();
					tracker = null;
				}
			}
		}
	}

	class PathFinder {
		
		PVector location;
		PVector velocity;
		float diameter;

		PathFinder() {
			location = new PVector(parent.width / 2, parent.height);
			velocity = new PVector(0, -1);
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
				bump.mult(.1f);
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
