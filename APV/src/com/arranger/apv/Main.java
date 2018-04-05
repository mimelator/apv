package com.arranger.apv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape.Data;
import com.arranger.apv.ColorSystem.RandomColor;
import com.arranger.apv.bg.BackDropSystem;
import com.arranger.apv.bg.DefaultBackgroundSystem;
import com.arranger.apv.bg.GravAttractorSystem;
import com.arranger.apv.bg.OscilatingBackDrop;
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
import com.arranger.apv.systems.lifecycle.RotSystem;
import com.arranger.apv.systems.lifecycle.WarpSystem;
import com.arranger.apv.systems.lite.AttractorSystem;
import com.arranger.apv.systems.lite.LightWormSystem;
import com.arranger.apv.systems.lite.PlasmaSystem;
import com.arranger.apv.systems.lite.ShowerSystem;
import com.arranger.apv.systems.lite.cycle.BubbleShapeSystem;
import com.arranger.apv.systems.lite.cycle.CarnivalShapeSystem;
import com.arranger.apv.systems.lite.cycle.NoisyShapeSystem;
import com.arranger.apv.systems.lite.cycle.ScribblerShapeSystem;
import com.arranger.apv.systems.lite.cycle.StarWebSystem;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final int PLASMA_ALPHA_LOW = 120;
	private static final int PLASMA_ALPHA_HIGH = 255;
	private static final String RENDERER = P2D;
	public static final boolean AUDIO_IN = true;
	private static final boolean USE_BACKDROP = true;
	private static final boolean USE_BG = true;
	private static final boolean USE_FG = true;
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
	protected int backgroundIndex = 0;
	
	protected List<BackDropSystem> backDropSystems = new ArrayList<BackDropSystem>();
	protected int backDropIndex = 0;

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
			backgroundSystems.add(new BubbleShapeSystem(this, NUMBER_PARTICLES / 4));
			backgroundSystems.add(new AttractorSystem(this));
			backgroundSystems.add(new LightWormSystem(this, false, 4, 16));
			backgroundSystems.add(new LightWormSystem(this));
			backgroundSystems.add(new ScribblerShapeSystem(this, NUMBER_PARTICLES / 5));
			backgroundSystems.add(new NoisyShapeSystem(this, NUMBER_PARTICLES));
			backgroundSystems.add(new WarpSystem(this, new DotFactory(this, 7.3f), NUMBER_PARTICLES / 4));
			backgroundSystems.add(new ShowerSystem(this));
			backgroundSystems.add(new PlasmaSystem(this, PLASMA_ALPHA_HIGH));
			backgroundSystems.add(new PlasmaSystem(this, PLASMA_ALPHA_LOW));
			backgroundSystems.add(new WarpSystem(this, new DotFactory(this, 2.3f), NUMBER_PARTICLES / 2));
		}
		
		if (USE_FG) {
			systems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this)));
			systems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this), true));
			systems.add(new StarWebSystem(this, new SpriteFactory(this, SPRITE_PNG)));
			systems.add(new StarWebSystem(this, new CircleFactory(this), NUMBER_PARTICLES / 4));
			systems.add(new StarWebSystem(this, new SquareFactory(this, .5f)));
			systems.add(new StarWebSystem(this));
			systems.add(new RotSystem(this, new InvoluteFactory(this, .25f), NUMBER_PARTICLES / 4));
			systems.add(new RotSystem(this, new HypocycloidFactory(this, 2.5f), NUMBER_PARTICLES));
			systems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG, 2.5f), NUMBER_PARTICLES));
			systems.add(new GravitySystem(this, new SquareFactory(this, 2.5f), NUMBER_PARTICLES));
			systems.add(new GravitySystem(this, new CircleFactory(this), NUMBER_PARTICLES));
			systems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG), NUMBER_PARTICLES));
			systems.add(new RotSystem(this, new SquareFactory(this), NUMBER_PARTICLES));
			systems.add(new RotSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
			systems.add(new RotSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 4));
		}
		
		if (USE_BACKDROP) {
			backDropSystems.add(new GravAttractorSystem(this));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.GREEN.darker()));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.BLUE));
			backDropSystems.add(new DefaultBackgroundSystem(this));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.RED.darker()));
			backDropSystems.add(new DefaultBackgroundSystem(this, .5f));
		}
		
		for (ShapeSystem system : systems) {
			system.setup();
		}
		
		for (ShapeSystem system : backgroundSystems) {
			system.setup();
		}
		background(Color.BLACK.getRGB());
	}

	public void draw() {
		if (USE_BACKDROP) {
			pushStyle();
			pushMatrix();
			BackDropSystem backDropSystem = backDropSystems.get(Math.abs(backDropIndex) % backDropSystems.size());
			addDebugMsg("bDrop: " + backDropSystem.getClass().getSimpleName());
			backDropSystem.drawBackground();
			popMatrix();
			popStyle();
		}
		
		if (USE_BG) {
			pushStyle();
			pushMatrix();
			ShapeSystem bgSys = backgroundSystems.get(Math.abs(backgroundIndex) % backgroundSystems.size());
			debugSystem("bgSys", bgSys);
			bgSys.draw();
			popMatrix();
			popStyle();
		}
		
		if (USE_FG) {
			pushStyle();
			pushMatrix();
			ShapeSystem fgSys = systems.get(Math.abs(systemIndex) % systems.size());
			debugSystem("fgSys", fgSys);
			fgSys.draw();
			popMatrix();
			popStyle();
		}
		
		if (DEBUG_TEXT) {
			doDebugMsg();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		int code = event.getKeyCode();
		if (code == PApplet.RIGHT) {
			systemIndex++;
			backgroundIndex++;
			backDropIndex++;
		} else if (code == PApplet.LEFT) {
			systemIndex--;
			backgroundIndex--;
			backDropIndex--;
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
	
	protected void debugSystem(String name, ShapeSystem ss) {
		addDebugMsg(name +": " + ss.getClass().getSimpleName());
		if (ss.factory != null) {
			addDebugMsg("  --factory: " + ss.factory.getClass().getSimpleName());
			addDebugMsg("    --scale: " + ss.factory.getScale());
		}
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
