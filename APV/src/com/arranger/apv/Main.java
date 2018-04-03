package com.arranger.apv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape.Data;
import com.arranger.apv.ColorSystem.RandomColor;
import com.arranger.apv.factories.CircleFactory;
import com.arranger.apv.factories.DotFactory;
import com.arranger.apv.factories.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factories.ParametricFactory.InvoluteFactory;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.factories.SquareFactory;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.systems.lifecycle.WarpSystem;
import com.arranger.apv.systems.lite.CarnivalShapeSystem;
import com.arranger.apv.systems.lite.PlasmaSystem;
import com.arranger.apv.systems.lite.RotSystem;
import com.arranger.apv.systems.lite.ShowerSystem;
import com.arranger.apv.systems.lite.StarWebSystem;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final String RENDERER = P2D;
	public static final boolean AUDIO_IN = true;
	private static final boolean USE_BG = true;
	private static final boolean FULL_SCREEN = true;
	private static final String SONG = "03 When Things Get Strange v10.mp3";
	
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024
	private static final int NUMBER_PARTICLES = 1000;

	private static final String SPRITE_PNG = "sprite.png";
	private static final boolean DEBUG_TEXT = true;
	
	private static class EmptyShapeFactory extends ShapeFactory {
		public EmptyShapeFactory(Main parent) {
			super(parent);
		}
		public APVShape createShape(Data data) {
			return null;
		}
	}
	
	protected List<ShapeSystem> systems = new ArrayList<ShapeSystem>();
	protected int systemIndex = 0;
	
	protected List<ShapeSystem> backgroundSystems = new ArrayList<ShapeSystem>();
	protected int backgroundSystemIndex = 0;

	protected List<LocationSystem> locationSystems = new ArrayList<LocationSystem>(); 
	protected int locationIndex = 0;
	
	protected Audio audio;
	protected Gravity gravity;
	protected ColorSystem colorSystem;

	
	public static void main(String[] args) {
		PApplet.main(new String[] {Main.class.getName()});
	}

	public void settings() {
		if (FULL_SCREEN) {
			fullScreen(RENDERER);
		} else {
			size(WIDTH, HEIGHT, RENDERER);
		}
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
	
	/**
	 * This little tool will keep interpolating between the low and high values based
	 * upon the frameCount.  It should complete a circuit every
	 */
	public float oscillate(float low, float high, float oscScalar) {
		float cos = cos(PI + frameCount / frameRate / oscScalar);
		return PApplet.map(cos, -1, 1, low, high);
	}
	
	public void setup() {
		locationSystems.add(new MouseLocationSystem(this));
		locationSystems.add(new CircularLocationSystem(this));
		locationSystems.add(new RectLocationSystem(this));
		
		colorSystem = new RandomColor(this);
		gravity = new Gravity(this);
		audio = new Audio(this, SONG, BUFFER_SIZE);

		//Graphics hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		
		//Create Shape Factories and Shape Systems
		if (USE_BG) {
			backgroundSystems.add(new ShowerSystem(this, new EmptyShapeFactory(this)));
			backgroundSystems.add(new PlasmaSystem(this, new EmptyShapeFactory(this), 255));
			backgroundSystems.add(new PlasmaSystem(this, new EmptyShapeFactory(this), 120));
			backgroundSystems.add(new WarpSystem(this, new DotFactory(this), 500));
			backgroundSystems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this)));
		}
		
		systems.add(new StarWebSystem(this, new EmptyShapeFactory(this)));
		systems.add(new GravitySystem(this, new SquareFactory(this), NUMBER_PARTICLES));
		systems.add(new GravitySystem(this, new CircleFactory(this), NUMBER_PARTICLES));
		systems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG), NUMBER_PARTICLES));
		systems.add(new RotSystem(this, new SquareFactory(this), NUMBER_PARTICLES));
		systems.add(new RotSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
		systems.add(new RotSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 4));
		
		for (ShapeSystem system : systems) {
			system.setup();
		}
		
		for (ShapeSystem system : backgroundSystems) {
			system.setup();
		}
	}
	
	public void draw() {
		//TODO Omit drawing a background to facilitate transitions from systems
		background(Color.BLACK.getRGB()); 
		
		if (USE_BG) {
			ShapeSystem bgSys = backgroundSystems.get(Math.abs(backgroundSystemIndex) % backgroundSystems.size());
			pushMatrix();
			bgSys.draw();
			popMatrix();
			addDebugMsg("bgSys" + bgSys.getClass().getSimpleName() + ":" + bgSys.factory.getClass().getSimpleName());
		}
		ShapeSystem fgSys = systems.get(Math.abs(systemIndex) % systems.size());
		pushMatrix();
		fgSys.draw();
		addDebugMsg("fgSys: " + fgSys.getClass().getSimpleName() + ":" + fgSys.factory.getClass().getSimpleName());
		popMatrix();
		
		if (DEBUG_TEXT) {
			doDebugMsg();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int code = event.getKeyCode();
		if (code == PApplet.RIGHT) {
			systemIndex++;
			backgroundSystemIndex++;
		} else if (code == PApplet.LEFT) {
			systemIndex--;
			backgroundSystemIndex--;
		} else if (code == PConstants.ENTER) {
			if (event.isShiftDown()) {
				locationIndex--;
			} else {
				locationIndex++;
			}
		} 
	}
	
	public static final int TEXT_SIZE = 16;
	public static final int TEXT_INDEX = 10;
	protected List<String> debugStatements = new ArrayList<String>();
	
	public void addDebugMsg(String msg) {
		debugStatements.add(msg);
	}
	
	protected void doDebugMsg() {
		addDebugMsg("Loc: " + getLocationSystem().getClass().getSimpleName());
		addDebugMsg("Frame rate: " + (int)frameRate);
		addDebugMsg("MouseXY:  " + mouseX + " " + mouseY);
		drawDebug();
	}

	protected void drawDebug() {
		fill(255);
		textAlign(PApplet.LEFT, PApplet.TOP);
		textSize(TEXT_SIZE);
		
		int offset = TEXT_INDEX;
		for (String s : debugStatements) {
			text(s, TEXT_INDEX, offset);
			offset += TEXT_SIZE;
		}
		
		debugStatements.clear();
	}
}
