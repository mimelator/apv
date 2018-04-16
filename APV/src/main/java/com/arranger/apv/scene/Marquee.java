package com.arranger.apv.scene;

import java.awt.Color;
import java.util.ArrayList;

import com.arranger.apv.Main;
import com.arranger.apv.Scene;
import com.arranger.apv.util.Configurator;

import processing.core.PGraphics;

/**
 * @see https://www.openprocessing.org/sketch/498901
 */
public class Marquee extends Scene {

	private static final int FRAMES_REQUIRED_TO_RESET = 60;
	private static final int TEXT_SIZE = 200;
	private String text;
	private int characterColor;
	private PGraphics pg;
	private ArrayList<OneChr> chrs;

	public Marquee(Main parent, String text) {
		super(parent);
		this.text = text;
	}

	public Marquee(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, "Hello World"));
		
		parent.getCommandSystem().getMessageModeInterceptor().registerMessageListeners(msg -> {
			text = msg.trim();
			reset();
		});
	}
	
	@Override
	public char getHotKey() {
		return 'm';
	}

	@Override
	public String getConfig() {
		//{Marquee: ["He's trying!!!"]}
		return String.format("{%1s : [\"%2s\"]}", getName(), text); //quote the text
	}

	@Override
	public boolean isNormal() {
		return false;
	}

	@Override
	public boolean isNew() {
		int currentFrame = parent.getFrameCount();
		if (currentFrame > lastFrameDrawn + FRAMES_REQUIRED_TO_RESET) {
			reset();
		}
		
		return pg == null;
	}

	protected void reset() {
		pg = null;
		lastFrameDrawn = 0;
	}

	@Override
	public void drawScene() {
		if (pg == null) {
			init();
		}
		
		//draw background frame
		int insetY = parent.height / 3;
		parent.fill(0, 50);
		parent.stroke(255);
		parent.strokeWeight(10);
		parent.rectMode(CENTER);
		parent.rect(parent.width / 2, (parent.height / 2) + TEXT_SIZE / 4, parent.width, insetY);
		

		//draw text
		parent.fill(255);
		parent.stroke(0);
		parent.textAlign(CENTER, CENTER);

		if (chrs.size() < 2000) {
			for (int i = 0; i < 60; i++) {
				float x = parent.random(parent.width);
				float y = parent.random(parent.height);
				int c = pg.get((int) x, (int) y);
				if (c == characterColor) {
					chrs.add(new OneChr(x, y, 1));
				}
			}
		}
		
		for (OneChr oc : chrs) {
			oc.updateMe();
		}
	}
	
	protected void init() {
		chrs = new ArrayList<OneChr>();
		pg = parent.createGraphics(parent.width, parent.height, JAVA2D);
		pg.beginDraw();
		pg.textSize(TEXT_SIZE);
		pg.textAlign(CENTER, CENTER);
		pg.fill(characterColor);
		pg.text(text.trim(), pg.width / 2, pg.height / 2);
		pg.endDraw();
		
		characterColor = parent.color(0);
		lastFrameDrawn = parent.getFrameCount();
	}

	class OneChr {
		float x, y;
		float myRotate;
		int mySize;
		char myChr;
		int color;
		
		OneChr(float _x, float _y, float gS) {
			x = _x;
			y = _y;

			int radi = (int) Math.floor(parent.random(4));
			myRotate = (HALF_PI * radi);
			float sizeFactor = parent.random(2);
			mySize = (int) Math.max(10, Math.pow(sizeFactor, 5));
			myChr = (char) parent.random(33, 126);
			Color clr = parent.getColor().getCurrentColor();
			color = parent.color(clr.getRed(), clr.getGreen(), clr.getBlue());
		}

		void updateMe() {
			parent.noStroke();
			parent.fill(color);
			parent.pushMatrix();
			parent.translate(x, y);
			parent.rotate(myRotate);
			parent.textSize(mySize);

			parent.text(myChr, 0, 0);
			parent.popMatrix();
		}
	}
}
