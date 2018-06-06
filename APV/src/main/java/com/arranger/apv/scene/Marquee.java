package com.arranger.apv.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.Main;
import com.arranger.apv.db.entity.DJEntity;
import com.arranger.apv.db.entity.SetpackEntity;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;
import com.arranger.apv.util.frame.Tracker;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @see https://www.openprocessing.org/sketch/498901
 */
public class Marquee extends Animation {

	private static final int DRAWN_CHAR_THRESHOLD = 800000;
	private static final int MAX_TEXT_LENGTH = 30;
	private static final int TEXT_SIZE = 200;
	private static final int MAX_TEXT_LENGTH_REDUCTION = (int)(TEXT_SIZE * .4f);
	private static final int REQUIRED_HITS_PER_FRAME = 3;
	private static final int ASCII_HIGH = 126;
	private static final int ASCII_LOW = 33;
	
	private int threshold;
	private String text;
	private int characterColor;
	private PGraphics pg;
	private ArrayList<OneChr> chrs;
	private Tracker<Marquee> tracker;
	private int drawCount = 0;
	private int requiredHitsPerFrame;

	private Map<SetpackEntity, DJEntity> djMap = new HashMap<SetpackEntity, DJEntity>();
	
	public Marquee(Main parent, String text) {
		super(parent);
		this.text = text;
		if (text.length() > MAX_TEXT_LENGTH) {
			text = text.substring(0, MAX_TEXT_LENGTH);
		}
		float countdownPct = Float.parseFloat(parent.getConfigValueForFlag(Main.FLAGS.COUNTDOWN_PCT));
		this.threshold = (int)(DRAWN_CHAR_THRESHOLD * countdownPct);
		this.requiredHitsPerFrame = (int)(REQUIRED_HITS_PER_FRAME / countdownPct);
	}

	public Marquee(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, "Hello World"));
	}
	
	@Override
	public String getConfig() {
		//{Marquee: ["He's trying!!!"]}
		return String.format("{%s : [\"%s\"]}", getName(), text); //quote the text
	}

	@Override
	public boolean isNew() {
		boolean isNew = false;
		if (pg == null) {
			initOffScreenGraphics();
			
			drawCount = 0;
			chrs = new ArrayList<OneChr>();
			tracker = new Tracker<Marquee>(parent, parent.getSceneCompleteEvent());
			parent.getMarqueeEvent().fire();
			isNew = true;
		}
		
		return isNew;
	}

	@Override
	public void drawScene() {
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
		
		int hits = 0;
		
		while (hits < requiredHitsPerFrame) {
			float x = parent.random(parent.width);
			float y = parent.random(parent.height);
			int c = pg.get((int) x, (int) y);
			if (c == characterColor) {
				chrs.add(new OneChr(x, y));
				hits++;
			}
		}
		
		parent.image(pg.get(), 0, 0);
		
		for (OneChr oc : chrs) {
			oc.draw();
			drawCount++;
		}

		//Automatically draw the SetPack Name
		SetpackEntity setpackEntity = parent.getSetPackModel().getSetpackEntity();
		if (setpackEntity != null) {
			DJEntity dJforSetpack = djMap.get(setpackEntity);
			if (dJforSetpack == null) {
				dJforSetpack = parent.getDBSupport().getDJforSetpack(setpackEntity);
				djMap.put(setpackEntity, dJforSetpack);
			}
			
			//draw some stuff!
			List<String> messages = new ArrayList<String>();
			messages.add(String.format("SetPack: %s", setpackEntity.getName()));
			if (dJforSetpack != null) {
				messages.add(String.format("DJ: %s", dJforSetpack.getName()));
			}
			
			new TextPainter(parent).drawText(messages, SafePainter.LOCATION.UPPER_LEFT);
		}
		
		if (tracker != null && tracker.isActive(e -> drawCount > threshold)) {
			tracker.fireEvent();
			tracker = null;
		}
	}
	
	protected void initOffScreenGraphics() {
		//adjust the textSize based on the length of the text
		int textOffset = (int)PApplet.map(text.length(), 1, MAX_TEXT_LENGTH, 0, MAX_TEXT_LENGTH_REDUCTION);
		int textSize = TEXT_SIZE - textOffset;
		characterColor = parent.color(0);
		
		pg = parent.createGraphics(parent.width, parent.height, JAVA2D);
		pg.beginDraw();
		pg.textSize(textSize);
		pg.textAlign(CENTER, CENTER);
		pg.fill(characterColor);
		pg.text(text.trim(), pg.width / 2, pg.height / 2);
		pg.endDraw();
	}

	class OneChr {
		
		float x, y;
		float myRotate;
		int mySize;
		char myChr;
		int color;
		
		OneChr(float _x, float _y) {
			x = _x;
			y = _y;

			int radi = (int) Math.floor(parent.random(4));
			myRotate = (HALF_PI * radi);
			float sizeFactor = parent.random(2);
			mySize = (int) Math.max(10, Math.pow(sizeFactor, 5));
			myChr = (char) parent.random(ASCII_LOW, ASCII_HIGH);
			Color clr = parent.getColor().getCurrentColor();
			color = parent.color(clr.getRed(), clr.getGreen(), clr.getBlue());
		}

		void draw() {
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
