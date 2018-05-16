package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * @see https://www.openprocessing.org/sketch/434598
 */
public class FidgetCubes extends LiteShapeSystem {

	private static final double SPIN_DECAY_RATE = 0.97;
	private static final double BOX_SPACER = 1.5;
	private static final int DISTURBANCE_RANGE_LOW = 2;
	private static final int DISTURBANCE_RANGE_HIGH = 7;
	private static final int DISTURBANCE_CYCLE_TIME = 20;
	private static final float BOX_SIZE = 25;
	private static final float PREVIOUS_MOUSE_OFFSET = .7f;
	
	
	private List<Cube> allCubes = new ArrayList<Cube>();
	private boolean customSat = false;

	public FidgetCubes(Main parent, boolean customSat) {
		super(parent);
		this.customSat = customSat;
	}
	
	public FidgetCubes(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getBoolean(0, false));
	}
	

	@Override
	public String getConfig() {
		//{FidgetCubes : [.01f, .06f, 20]}
		return String.format("{%s : [%b]}", getName(), customSat);
	}

	@Override
	public void setup() {
		for (float y = 0; y < parent.height; y += BOX_SIZE * BOX_SPACER) {
			for (float x = 0; x < parent.width; x += BOX_SIZE * BOX_SPACER) {
				allCubes.add(new Cube(x, y));
			}
		}
	}

	@Override
	public void draw() {
		parent.colorMode(HSB, 255);
		
		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int)pt.getX();
		int mouseY = (int)pt.getY();

		int pmouseX = (int)PREVIOUS_MOUSE_OFFSET * mouseX;
		int pmouseY = (int)PREVIOUS_MOUSE_OFFSET * mouseY;
		
		float [] hsbvals = new float[3];
		Color cc = parent.getColor().getCurrentColor();
		Color.RGBtoHSB(cc.getRed(), cc.getGreen(), cc.getBlue(), hsbvals);
		float targetHue = hsbvals[0] * 255;
		
		float disturbanceRange = BOX_SIZE * parent.oscillate(DISTURBANCE_RANGE_LOW, 
												DISTURBANCE_RANGE_HIGH, 
												DISTURBANCE_CYCLE_TIME);
		
		for (int i = 0; i < allCubes.size(); i++) {
			Cube c = allCubes.get(i);
			
			float d = PApplet.dist(mouseX, mouseY, c.pos.x, c.pos.y);

			if (d < disturbanceRange) {
				PVector motion = new PVector(pmouseX, pmouseY);
				motion.sub(mouseX, mouseY);
				c.spinX += motion.x * 0.1;
				c.spinY += motion.y * 0.1;
			}

			c.rot.x += c.spinX;
			c.spinX *= SPIN_DECAY_RATE;

			c.rot.y += c.spinY;
			c.spinY *= SPIN_DECAY_RATE;

			c.hueValue = targetHue;	
			
			c.hueValue += (PApplet.abs(c.spinX) + PApplet.abs(c.spinY)) * 0.05;
			c.hueValue %= 255;

			parent.pushMatrix();
			parent.translate(c.pos.x, c.pos.y, 0);
			parent.rotateY(PApplet.radians(-c.rot.x));
			parent.rotateX(PApplet.radians(c.rot.y));

			if (customSat && (c.rot.x != 0 || c.rot.y != 0)) {
				float sat = Math.abs((-c.rot.x + c.rot.y) % 360);
				sat = PApplet.map(sat, 0, 360, 255, 0);
				parent.fill(c.hueValue, sat, 255);
			} else {
				parent.fill(c.hueValue, 255, 255);
			}
			
			parent.box(BOX_SIZE);
			parent.popMatrix();
		}
	}

	class Cube {

		PVector pos;
		PVector rot;
		float spinX = 0;
		float spinY = 0;
		float hueValue = 0;

		Cube(float x, float y) {
			this.pos = new PVector(x, y);
			this.rot = new PVector(0, 0);
		}
	}

}
