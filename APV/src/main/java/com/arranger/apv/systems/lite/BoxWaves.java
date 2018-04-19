package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.arranger.apv.Main;
import com.arranger.apv.util.PulseFader;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * 
 * https://www.openprocessing.org/sketch/434054
 * 
Box waves

Controls:
  - Move mouse to change its patterns.

Original author:
  aa_debdeb (openprocessing.org/sketch/394519)

Forked by:
  Jason Labbe
*/
public class BoxWaves extends LiteShapeSystem {

	//Tune these
	private static final float SCALE_FACTOR = 1.5f;
	private static final int LOOP_EVERY_N_FRAMES = 100;
	private static final int RESET_EVERY_N_PULSES = 32;
	private static final float ROTATION_RATE = .001f;
	
	//Could change, but not as important
	private static final float NUM_BLOCKS_PER_ROW = 30 / SCALE_FACTOR;
	private static final float SPACE_BETWEEN_BLOCKS = 15 * SCALE_FACTOR;
	private static final float WHOLE_BOX_SCREEN_HEIGHT_OFFSET = 50 * SCALE_FACTOR;
	private static final float MAX_BLOCK_WIDTH = 10 * SCALE_FACTOR;
	
	
	//Don't bother changing these yet
	float mult = 3.3f;
	float heightMult = 3;
	int lowColor = new Color(0, 200, 255).getRGB();
	int highColor = new Color(150, 220, 255).getRGB();
	float rotationStep = 0;

	PulseFader pulseFader;
	ArrayList<Block> blocks;

	public BoxWaves(Main parent) {
		super(parent);
		
		parent.registerSetupListener(() -> {
			parent.getPulseListener().registerHandler(() -> {
				updateLocation();
			}, RESET_EVERY_N_PULSES); //skip every 16 pulses
		});
		
		parent.registerSetupListener(() -> {
			pulseFader = new PulseFader(parent, 30, 15, heightMult);
		});
	}

	@Override
	public void setup() {
		blocks = new ArrayList<Block>();
		float gap = SPACE_BETWEEN_BLOCKS;//15;
		int num = (int)NUM_BLOCKS_PER_ROW;
		for (int y = 0; y <= num; y++) {
			for (int x = 0; x <= num; x++) {
				PVector loc = new PVector(x * gap - num * gap / 2, y * gap - num * gap / 2, 0.0f);
				blocks.add(new Block(loc));
			}
		}
	}

	@Override
	public void draw() {
		rotationStep += ROTATION_RATE;
		int transX = parent.width / 2;
		int transY = parent.height / 2;
		parent.translate(transX, transY);
		parent.rotateY(rotationStep);
		parent.translate(-transX, -transY);
		
		parent.translate(transX, transY, -WHOLE_BOX_SCREEN_HEIGHT_OFFSET);
		parent.rotateX(HALF_PI / 2);
		parent.rotateZ(HALF_PI / 3);
		
		parent.scale(SCALE_FACTOR);
		
		for (Block b : blocks) {
			b.display();
		}
		heightMult = pulseFader.getValue();
	}

	void updateLocation() {
		Point2D point = parent.getLocation().getCurrentPoint();
		
		mult = PApplet.map((float)point.getX(), 0, parent.width, 1, 6);
		heightMult = PApplet.map((float)point.getY(), 0, parent.height, 5, 0.1f);
	}

	class Block {

		PVector loc;
		int index;

		Block(PVector _loc) {
			loc = _loc;
			index = blocks.size();
		}

		void display() {

			Color apvColor = parent.getColor().getCurrentColor();

			int loopOffset = (int) (index * mult);
			parent.pushMatrix();
			float time = (parent.getFrameCount() + loopOffset) % LOOP_EVERY_N_FRAMES;
			float bx, by, bz;
			float lx, ly, lz;
			if (time < LOOP_EVERY_N_FRAMES / 4.0) {
				bx = MAX_BLOCK_WIDTH;
				by = MAX_BLOCK_WIDTH;
				bz = PApplet.map(time, 0, LOOP_EVERY_N_FRAMES / 4.0f, MAX_BLOCK_WIDTH, MAX_BLOCK_WIDTH / 5.0f);
				lx = 0.0f;
				ly = 0.0f;
				lz = bz / 2;
			} else if (time < LOOP_EVERY_N_FRAMES / 4.0f * 2) {
				bx = MAX_BLOCK_WIDTH;
				by = PApplet.map(time, LOOP_EVERY_N_FRAMES / 4.0f, LOOP_EVERY_N_FRAMES / 4.0f * 2, MAX_BLOCK_WIDTH, MAX_BLOCK_WIDTH / 5.0f);
				bz = MAX_BLOCK_WIDTH / 5.0f;
				lx = 0.0f;
				ly = MAX_BLOCK_WIDTH / 2 - by / 2;
				lz = bz / 2;
			} else if (time < LOOP_EVERY_N_FRAMES / 4.0f * 3) {
				bx = MAX_BLOCK_WIDTH;
				by = MAX_BLOCK_WIDTH / 5.0f;
				bz = PApplet.map(time, LOOP_EVERY_N_FRAMES / 4.0f * 2, LOOP_EVERY_N_FRAMES / 4.0f * 3, MAX_BLOCK_WIDTH / 5.0f, MAX_BLOCK_WIDTH);
				lx = 0.0f;
				ly = MAX_BLOCK_WIDTH / 2 - by / 2;
				lz = bz / 2;
			} else {
				bx = MAX_BLOCK_WIDTH;
				by = PApplet.map(time, LOOP_EVERY_N_FRAMES / 4.0f * 3, LOOP_EVERY_N_FRAMES / 4.0f * 4, MAX_BLOCK_WIDTH / 5.0f, MAX_BLOCK_WIDTH);
				bz = MAX_BLOCK_WIDTH;
				lx = 0.0f;
				ly = MAX_BLOCK_WIDTH / 2 - by / 2;
				lz = bz / 2;
			}
			float blendValue = PApplet.map(bz, 2, 10, 0, 1);
			int currentColor = parent.lerpColor(lowColor, apvColor.getRGB(), blendValue);
			parent.fill(currentColor);

			parent.translate(loc.x + lx, loc.y + ly, loc.z + lz);
			parent.box(bx, by, bz * heightMult);
			parent.popMatrix();
		}
	}
}
