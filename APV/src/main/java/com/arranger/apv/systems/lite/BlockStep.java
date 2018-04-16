package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/159091
 */
public class BlockStep extends LiteShapeSystem {

	private static final float PREV_LOC_OFFSET = .5f;
	private static final float TILE_WIDTH = 50;

	int nbTilesW = 30, nbTilesH = 30;
	float gapW = 4, gapH = 4;
	float tileW = TILE_WIDTH, tileH = TILE_WIDTH;
	float totalW, totalH;
	Tile[][] tiles;
	boolean enabled = true;

	public BlockStep(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		initialize();
	}

	void initialize() {
		totalW = (tileW + gapW) * nbTilesW - gapW;
		totalH = (tileH + gapH) * nbTilesH - gapH;
		tiles = new Tile[nbTilesW][nbTilesH];
		for (int i = 0; i < nbTilesW; i++) {
			for (int j = 0; j < nbTilesH; j++) {
				tiles[i][j] = new Tile(i * (tileW + gapW) - totalW / 2,
						PApplet.map(j, 0, nbTilesH, 0, totalH) - totalH / 2, i, j);
			}
		}
	}

	@Override
	public void draw() {
		parent.rectMode(CENTER);
		parent.strokeWeight(2);
		parent.stroke(0);
		parent.lights();

		Point2D currentPoint = parent.getLocation().getCurrentPoint();
		int mouseX = (int) currentPoint.getX();
		int mouseY = (int) currentPoint.getY();
		float prevOffset = PREV_LOC_OFFSET;
		int pmouseX = (int) (mouseX * prevOffset);
		int pmouseY = (int) (mouseY * prevOffset);

		parent.translate(parent.width / 2, parent.height / 2);
		PVector strength = new PVector(mouseX - pmouseX, mouseY - pmouseY);
		for (int i = 0; i < nbTilesW; i++) {
			for (int j = 0; j < nbTilesH; j++) {
				tiles[i][j].process(mouseX, mouseY, strength);
			}
		}
		for (int i = 0; i < nbTilesW; i++) {
			for (int j = 0; j < nbTilesH; j++) {
				tiles[i][j].display();
			}
		}
	}

	class Tile {
		float x, y;// position
		PVector dr;// delta rotation
		PVector othersImpact = new PVector(0, 0);
		PVector r = new PVector(0, 0);// rotation
		PVector minXY, maxXY; // position on screen
		Boolean mouseImpacted = false;
		int i, j;

		Tile(float p_x, float p_y, int p_i, int p_j) {
			x = p_x;
			y = p_y;
			i = p_i;
			j = p_j;
			float X = x + parent.width / 2 - tileW / 2;
			float Y = y + parent.height / 2 - tileH / 2;
			minXY = new PVector(parent.screenX(X, Y, 0), parent.screenY(X, Y, 0));
			maxXY = new PVector(parent.screenX(X + tileW, Y + tileH, 0), parent.screenY(X + tileW, Y + tileH, 0));
		}

		void process(int p_mX, int p_mY, PVector p_strength) {
			dr = new PVector(0, 0);// delta rotation
			if (minXY.x < p_mX && p_mX < maxXY.x && minXY.y < p_mY && p_mY < maxXY.y) {
				dr.x = PApplet.map(p_strength.y, -15, 15, PI / 4, -PI / 4);
				dr.y = PApplet.map(p_strength.x, -15, 15, -PI / 4, PI / 4);
				if (enabled) {
					mouseImpacted = true;
					processNeighbors(dr.copy());
				}
			}
		}

		void processNeighbors(PVector p_strength) {
			PVector l_strength = p_strength.copy();
			if (!mouseImpacted)
				othersImpact.add(l_strength);
			if (l_strength.mag() > .1) {
				l_strength.mult(.12f);
				if (i > 0)
					tiles[i - 1][j].processNeighbors(l_strength);// left tile
				if (i < nbTilesW - 1)
					tiles[i + 1][j].processNeighbors(l_strength);// right tile
				if (j > 0)
					tiles[i][j - 1].processNeighbors(l_strength);// top tile
				if (j < nbTilesH - 1)
					tiles[i][j + 1].processNeighbors(l_strength);// bottom tile
				l_strength.mult(.6f);
				if (i > 0 && j > 0)
					tiles[i - 1][j - 1].processNeighbors(l_strength);// corner top left tile
				if (i < nbTilesW - 1 && j > 0)
					tiles[i + 1][j - 1].processNeighbors(l_strength);// corner top right tile
				if (i > 0 && j < nbTilesH - 1)
					tiles[i - 1][j + 1].processNeighbors(l_strength);// corner bottom left tile
				if (i < nbTilesW - 1 && j < nbTilesH - 1)
					tiles[i + 1][j + 1].processNeighbors(l_strength);// corner bottom right tile
			}
		}

		void display() {
			dr.add(othersImpact);
			r.add(dr);
			r.mult(.9f);
			
			Color cc = parent.getColor().getCurrentColor();
			int color = parent.color(PApplet.map(r.x, 0, PI / 4, cc.getRed(), 0), 
									PApplet.map(r.y, 0, PI / 4, cc.getGreen(), 0), 
									PApplet.map(r.y, 0, PI / 4, cc.getBlue(), 0));
			parent.fill(color);

			parent.pushMatrix();
			parent.translate(x, y);
			parent.rotateX(r.x);
			parent.rotateY(r.y);
			parent.box(tileW, tileH, PApplet.min(tileW, tileH) / 2);
			parent.popMatrix();

			othersImpact = new PVector(0, 0);
			mouseImpacted = false;
		}
	}
}
