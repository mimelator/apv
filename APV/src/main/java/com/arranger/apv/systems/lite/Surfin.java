package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

/*
// Pixel-sized particles version, of 'surfs_up'.
// Particles are now directly noise driven omitting the flow field.
// Array[], particle, pixel, noise()
// Mouse click to reset, mouseX adjusts background clear.
//
	@see https://www.openprocessing.org/sketch/298646
 */
public class Surfin extends LiteShapeSystem {

	private static final int NUM_PARTICLES = 6000;
	private static final int DEFAULT_PULSES_TO_SKIP = 4;

	public enum COLOR_MODE {
		ORIG, APV, CUSTOM
	}
	
	Particle[] particles;
	COLOR_MODE colorMode = COLOR_MODE.ORIG;
	Color startColor, endColor;
	float alpha;

	public Surfin(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getPulseListener().registerHandler(() -> {
				setParticles();
			}, DEFAULT_PULSES_TO_SKIP, this);
		});
	}
	
	public Surfin(Main parent, COLOR_MODE colorMode, Color startColor, Color endColor) {
		this(parent);
		this.colorMode = colorMode;
		this.startColor = startColor;
 		this.endColor = endColor;
	}
	
	public Surfin(Configurator.Context ctx) {
		this(ctx.getParent());
		
		int args = ctx.argList.size();
		switch (args) {
			case 0:
				break;
			case 1:
				this.colorMode = COLOR_MODE.valueOf(ctx.getString(0, COLOR_MODE.ORIG.name()));
				break;
			case 2:
				this.startColor = ctx.getColor(0, Color.BLUE);
				this.endColor = ctx.getColor(1, Color.BLACK);
				this.colorMode = COLOR_MODE.CUSTOM;
				break;
			default:
				throw new RuntimeException("Too many arguments");
		}
		
		if (colorMode == COLOR_MODE.CUSTOM) {
			throw new RuntimeException("Unsupported mode yet.  Too many fixes requried");
		}
	}
	
	/**
	 * {Surfin: [APV]}
	 * {Surfin: [ORIG]}
	 * {Surfin: [WHITE, BLACK]} #APV mode is implied
	 */
	public String getConfig() {
		if (COLOR_MODE.CUSTOM != colorMode) {
			return String.format("{%s: [%s]}", getName(), colorMode.name());
		} else {
			return String.format("{%s : [%s, %s]}", getName(), format(startColor, true), format(endColor, true));
		}
	}
	
	private String format(Color c, boolean addQuote) {
		return parent.format(c, addQuote);
	}
	
	private String format(Color c) {
		return parent.format(c);
	}

	@Override
	public void setup() {
		setParticles();
	}

	@Override
	public void draw() {
		parent.noStroke();
		alpha = PApplet.map(parent.mouseX, 0, parent.width, 5, 35);
		parent.fill(0, alpha);
		parent.rect(0, 0, parent.width, parent.height);

		parent.loadPixels();
		for (Particle p : particles) {
			p.move();
		}
		parent.updatePixels();
		
		parent.addSettingsMessage(" --colorMode: " + colorMode);
		if (startColor != null && endColor != null) {
			String colors = String.format("%s %s", format(startColor), format(endColor));
			parent.addSettingsMessage(" --colors: " + colors);
		}
	}

	void setParticles() {
		particles = new Particle[NUM_PARTICLES];
		for (int i = 0; i < 6000; i++) {
			float x = random(parent.width);
			float y = random(parent.height);
			int c = getColor(x, y);
			particles[i] = new Particle(x, y, c);
		}
	}
	
	protected int getColor(float x, float y) {
		switch (colorMode) {
		case ORIG:
			float adj = PApplet.map(y, 0, parent.height, 255, 0);
			return parent.color(40, adj, 255);
		case APV:
			return parent.getColor().getCurrentColor().getRGB();
		case CUSTOM:
			float amt = PApplet.map(y, 0, parent.height, 255, 0);
			return parent.lerpColor(startColor.getRGB(), endColor.getRGB(), amt);
			default:
				throw new RuntimeException("Unknown case");
		}
	}

	class Particle {
		float posX, posY, incr, theta;
		int c;

		Particle(float xIn, float yIn, int cIn) {
			posX = xIn;
			posY = yIn;
			c = cIn;
		}

		public void move() {
			update();
			wrap();
			display();
		}

		void update() {
			incr += .008;
			theta = parent.noise(posX * .006f, posY * .004f, incr) * TWO_PI;
			posX += 2 * cos(theta);
			posY += 2 * sin(theta);
		}

		void display() {
			if (posX > 0 && posX < parent.width && posY > 0 && posY < parent.height) {
				parent.pixels[(int) posX + (int) posY * parent.width] = c;
			}
		}

		void wrap() {
			if (posX < 0)
				posX = parent.width;
			if (posX > parent.width)
				posX = 0;
			if (posY < 0)
				posY = parent.height;
			if (posY > parent.height)
				posY = 0;
		}
	}

}
