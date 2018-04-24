package com.arranger.apv.scene;

import java.awt.Color;
import java.util.ArrayList;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.Tracker;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @see https://www.openprocessing.org/sketch/498901
 */
public class Marquee extends Animation {

	private static final int NEW_CHARS_PER_DRAW = 120;//60;
	private static final int MAX_TEXT_LENGTH = 30;
	private static final int MAX_TEXT_LENGTH_OFFSET = 120;
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
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandSystem().getMessageModeInterceptor().registerMessageListeners(msg -> {
				text = msg.trim();
				reset();
			});
		});
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public char getHotKey() {
		return 'm';
	}

	@Override
	public String getConfig() {
		//{Marquee: ["He's trying!!!"]}
		return String.format("{%s : [\"%s\"]}", getName(), text); //quote the text
	}

	@Override
	protected void reset() {
		pg = null;
	}

	private static final int DRAWN_CHAR_THRESHOLD = 800000;
	private Tracker<Marquee> tracker;
	int drawCount = 0;
	
	@Override
	public void drawScene() {
		if (pg == null) {
			init();
			drawCount = 0;
			tracker = new Tracker<Marquee>(parent, parent.getSceneCompleteEvent());
		}
		
		//draw background frame always the same size
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
			for (int i = 0; i < NEW_CHARS_PER_DRAW; i++) {
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
			drawCount++;
		}
		
		if (tracker != null && tracker.isActive(e -> true)) {
			if (drawCount > DRAWN_CHAR_THRESHOLD) {
				tracker.fireEvent();
				tracker = null;
			}
		}
	}
	
	protected void init() {
		//adjust the textSize based on the length of the text
		int textOffset = (int)PApplet.map(text.length(), 1, MAX_TEXT_LENGTH, 0, MAX_TEXT_LENGTH_OFFSET);
		int textSize = TEXT_SIZE - textOffset;
		
		chrs = new ArrayList<OneChr>();
		pg = parent.createGraphics(parent.width, parent.height, JAVA2D);
		pg.beginDraw();
		pg.textSize(textSize);
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
