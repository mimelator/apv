package com.arranger.apv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.factories.CircleFactory;
import com.arranger.apv.factories.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factories.ParametricFactory.InvoluteFactory;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.factories.SquareFactory;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.systems.ParticleSystem;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	public static final boolean AUDIO_IN = true;
	private static final String SONG = "03 When Things Get Strange v10.mp3";
	
//	private static final int WIDTH = 1024;
//	private static final int HEIGHT = 768;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024
	private static final int NUMBER_PARTICLES = 100;//1000;

	private static final String SPRITE_PNG = "sprite.png";
	private static final boolean DEBUG_TEXT = false;
	
	protected List<ShapeSystem> systems = new ArrayList<ShapeSystem>();
	protected int systemIndex = 0;

	protected List<LocationSystem> locationSystems = new ArrayList<LocationSystem>(); 
	protected int locationIndex = 0;
	
	protected Audio audio;
	protected Gravity gravity;
	protected ColorSystem colorSystem;

	
	public static void main(String[] args) {
		PApplet.main(new String[] {Main.class.getName()});
	}

	public void settings() {
		//size(WIDTH, HEIGHT, P2D);
		fullScreen(P2D);
	}
	
	public Audio getAudio() {
		return audio;
	}
	
	public Gravity getGravity() {
		return gravity;
	}

	public ColorSystem getColorSystem() {
		return colorSystem;
	}
	
	public LocationSystem getLocationSystem() {
		return locationSystems.get(Math.abs(locationIndex) % locationSystems.size());
	}

	public void setup() {
		locationSystems.add(new MouseLocationSystem(this));
		locationSystems.add(new CircularLocationSystem(this));
		locationSystems.add(new RectLocationSystem(this));
		
		colorSystem = new ColorSystem(this);
		gravity = new Gravity(this);
		audio = new Audio(this, SONG, BUFFER_SIZE);

		//Graphics hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		
		//Create Shape Factories and Shape Systems
		systems.add(new ParticleSystem(this, new SpriteFactory(this, SPRITE_PNG), NUMBER_PARTICLES));
		systems.add(new ParticleSystem(this, new SquareFactory(this), NUMBER_PARTICLES));
		systems.add(new ParticleSystem(this, new CircleFactory(this), NUMBER_PARTICLES));
		systems.add(new ParticleSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
		systems.add(new ParticleSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 4));
		
		for (ShapeSystem system : systems) {
			system.setup();
		}
	}
	
	public void draw() {
		background(Color.BLACK.getRGB()); //TODO Omit drawing a background to facilitate transitions from systems
		ShapeSystem currentSystem = systems.get(Math.abs(systemIndex) % systems.size());
		currentSystem.draw();
		
		if (DEBUG_TEXT) {
			drawDebug();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == PApplet.RIGHT) {
			systemIndex++;
		} else if (event.getKeyCode() == PApplet.LEFT) {
			systemIndex--;
		} else {
			if (event.getKeyCode() == PConstants.ENTER) {
				locationIndex++;
			}
		} 
	}

	protected void drawDebug() {
		fill(255);
		textSize(16);
		text("Frame rate: " + (int)frameRate, 10, 20);
		text("mouseXY:  " + mouseX + " " + mouseY, 10, 36);
		text("Gravity: " + gravity.getCurrentGravity(), 10, 52);
	}
}
