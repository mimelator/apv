package com.arranger.apv;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

import com.arranger.apv.APVShape.Data;
import com.arranger.apv.CommandSystem.APVCommand;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.audio.FreqDetector;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.audio.SnapListener;
import com.arranger.apv.bg.BackDropSystem;
import com.arranger.apv.bg.BlurBackDrop;
import com.arranger.apv.bg.OscilatingBackDrop;
import com.arranger.apv.bg.PulseRefreshBackDrop;
import com.arranger.apv.bg.RefreshBackDrop;
import com.arranger.apv.color.BeatColorSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.color.RandomColor;
import com.arranger.apv.factories.CircleFactory;
import com.arranger.apv.factories.DotFactory;
import com.arranger.apv.factories.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factories.ParametricFactory.InvoluteFactory;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.factories.SquareFactory;
import com.arranger.apv.filter.BlendModeFilter;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.PulseShakeFilter;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.loc.PerlinNoiseWalker;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.systems.lifecycle.RotSystem;
import com.arranger.apv.systems.lifecycle.WarpSystem;
import com.arranger.apv.systems.lite.AttractorSystem;
import com.arranger.apv.systems.lite.GridShapeSystem;
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

public class Main extends PApplet {
	
	public static final char SPACE_BAR_KEY_CODE = ' ';
	
	private static final int PLASMA_ALPHA_LOW = 120;
	private static final int PLASMA_ALPHA_HIGH = 255;
	private static final String RENDERER = P2D;
	public static final boolean AUDIO_IN = true;
	private static final boolean USE_BACKDROP = true;
	private static final boolean USE_BG = true;
	private static final boolean USE_FG = true;
	private static final boolean USE_FILTERS = true;
	private static final boolean FULL_SCREEN = true;
	private static final boolean AUTO_MODE = true;
	private static final boolean SNAP_MODE = true;
	
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024
	private static final int NUMBER_PARTICLES = 1000;

	private static final String SONG = "03 When Things Get Strange v10.mp3";
	private static final String SPRITE_PNG = "sprite.png";
	
	private static final boolean DEBUG = true;
	private static final boolean MONITOR_FRAME_RATE = true;
	private static final int FRAME_RATE_THRESHOLD = 30;
	private static final int MIN_THRESHOLD_ENTRIES = 3;
	
	private static class EmptyShapeFactory extends ShapeFactory {
		public EmptyShapeFactory(Main parent) {
			super(parent);
		}
		public APVShape createShape(Data data) {
			return null;
		}
	}
	
	protected List<ShapeSystem> foregroundSystems = new ArrayList<ShapeSystem>();
	protected int foregroundIndex = 0;
	
	protected List<ShapeSystem> backgroundSystems = new ArrayList<ShapeSystem>();
	protected int backgroundIndex = 0;
	
	protected List<BackDropSystem> backDropSystems = new ArrayList<BackDropSystem>();
	protected int backDropIndex = 0;

	protected List<LocationSystem> locationSystems = new ArrayList<LocationSystem>(); 
	protected int locationIndex = 0;
	
	protected List<ColorSystem> colorSystems = new ArrayList<ColorSystem>(); 
	protected int colorIndex = 0;
	
	protected List<Filter> filters = new ArrayList<Filter>(); 
	protected int filterIndex = 0;
	
	protected CommandSystem commandSystem;
	protected Audio audio;
	protected Gravity gravity;
	protected boolean showHelp = true;
	protected boolean debug = DEBUG;
	
	//Auto system
	private SnapListener snapListener;
	private PulseListener pulseListener;
	private SingleFrameSkipper frameSkipper;
	private boolean autoMode = AUTO_MODE;
	private boolean snapMode = SNAP_MODE;
	
	
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
	
	public CommandSystem getCommandSystem() {
		return commandSystem;
	}

	public ColorSystem getColorSystem() {
		return (ColorSystem)getPlugin(colorSystems, colorIndex);
	}
	
	public LocationSystem getLocationSystem() {
		LocationSystem ls = null;
		while (ls == null) {
			ls = (LocationSystem)getPlugin(locationSystems, locationIndex);
			if (autoMode  && ls instanceof MouseLocationSystem) {
				locationIndex++;
				ls = null;
			}
		}
		return ls;
	}
	
	private static final int TARGET_FRAME_RATE_FOR_OSC = 30;
	
	/**
	 * This little tool will keep interpolating between the low and high values based
	 * upon the frameCount.  It should complete a circuit every
	 * 
	 * @param oscSpeed the lower the number the faster the cycling.  Typically between : 4 and 20
	 */
	public float oscillate(float low, float high, float oscSpeed) {
		float fr = TARGET_FRAME_RATE_FOR_OSC; //frameRate
		float cos = cos(PI * frameCount / fr / oscSpeed);
		return PApplet.map(cos, -1, 1, low, high);
	}
	
	public boolean randomBoolean() {
		return random(10) > 5;
	}
	
	public void setup() {
		commandSystem = new CommandSystem(this);
		
		//add commands
		commandSystem.registerCommand('f', "Foreground", "Cycles through the foreground systems", 
				(event) -> {if (event.isShiftDown()) foregroundIndex--; else foregroundIndex++;});
		commandSystem.registerCommand('b', "Background", "Cycles through the background systems", 
				(event) -> {if (event.isShiftDown()) backgroundIndex--; else backgroundIndex++;});
		commandSystem.registerCommand('p', "Backdrop", "Cycles through the backdrop systems", 
				(event) -> {if (event.isShiftDown()) backDropIndex--; else backDropIndex++;});
		commandSystem.registerCommand(PApplet.RIGHT, "Right Arrow", "Cycles through the plugins", 
				(event) -> { foregroundIndex++; backgroundIndex++; backDropIndex++;});
		commandSystem.registerCommand(PApplet.LEFT, "Left Arrow", "Cycles through the plugins in reverse", 
				(event) -> { foregroundIndex--; backgroundIndex--; backDropIndex--;});
		commandSystem.registerCommand(PConstants.ENTER, "Enter", "Cycles through the locations (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) locationIndex--; else locationIndex++;});
		commandSystem.registerCommand('t', "Filter", "Cycles through the filters (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) filterIndex--; else filterIndex++;});
		commandSystem.registerCommand('c', "Colors", "Cycles through the color systems (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) colorIndex--; else colorIndex++;});
		commandSystem.registerCommand(SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things", e -> scramble());
		commandSystem.registerCommand('m', "Slow Monitor", "Outputs the slow monitor data to the console", event -> dumpMonitorInfo());
		commandSystem.registerCommand('h', "Help", "Toggles the display of all the available commands", event -> showHelp = !showHelp);
		commandSystem.registerCommand('d', "Debug", "Toggles the display of all the debug information", event -> debug = !debug);
		commandSystem.registerCommand('a', "Auto", "Toggles between full Auto mode", event -> autoMode = !autoMode);
		commandSystem.registerCommand('s', "Snap", "Toggles between snap (pop the mic) scramble mode", event -> snapMode = !snapMode);
		
		
		gravity = new Gravity(this);
		audio = new Audio(this, SONG, BUFFER_SIZE);
		
		locationSystems.add(new PerlinNoiseWalker(this));
		locationSystems.add(new MouseLocationSystem(this));
		locationSystems.add(new CircularLocationSystem(this, false));
		locationSystems.add(new CircularLocationSystem(this, true));
		locationSystems.add(new RectLocationSystem(this, false));
		locationSystems.add(new RectLocationSystem(this, true));
		
		colorSystems.add(new BeatColorSystem(this));
		colorSystems.add(new OscillatingColor(this));
		colorSystems.add(new RandomColor(this));
		
		//Graphics hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		
		//Create Shape Factories and Shape Systems
		if (USE_BG) {
			backgroundSystems.add(new AttractorSystem(this, new SpriteFactory(this, SPRITE_PNG)));
			backgroundSystems.add(new AttractorSystem(this, new HypocycloidFactory(this)));
			backgroundSystems.add(new FreqDetector(this));
			backgroundSystems.add(new GridShapeSystem(this, 30, 10));
			backgroundSystems.add(new BubbleShapeSystem(this, NUMBER_PARTICLES / 4));
			backgroundSystems.add(new AttractorSystem(this));
			backgroundSystems.add(new LightWormSystem(this));
			backgroundSystems.add(new LightWormSystem(this, false, 4, 16));
			backgroundSystems.add(new ScribblerShapeSystem(this, NUMBER_PARTICLES / 5));
			backgroundSystems.add(new GridShapeSystem(this));
			backgroundSystems.add(new NoisyShapeSystem(this, NUMBER_PARTICLES));
			backgroundSystems.add(new WarpSystem(this, new DotFactory(this, 7.3f), NUMBER_PARTICLES / 4));
			backgroundSystems.add(new ShowerSystem(this));
			backgroundSystems.add(new PlasmaSystem(this, PLASMA_ALPHA_HIGH));
			backgroundSystems.add(new GridShapeSystem(this, 200, 300));
			backgroundSystems.add(new LightWormSystem(this));
			backgroundSystems.add(new GridShapeSystem(this, 20, 30));
			backgroundSystems.add(new PlasmaSystem(this, PLASMA_ALPHA_LOW));
			backgroundSystems.add(new WarpSystem(this, new DotFactory(this, 2.3f), NUMBER_PARTICLES / 2));
		}
		
		if (USE_FG) {
			foregroundSystems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this)));
			foregroundSystems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG), NUMBER_PARTICLES));
			foregroundSystems.add(new StarWebSystem(this, new SpriteFactory(this, SPRITE_PNG)));
			foregroundSystems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this), true));
			foregroundSystems.add(new GravitySystem(this, new SquareFactory(this, 2.5f), NUMBER_PARTICLES));			
			foregroundSystems.add(new StarWebSystem(this));
			foregroundSystems.add(new RotSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new RotSystem(this, new InvoluteFactory(this, .25f), NUMBER_PARTICLES / 4));
			foregroundSystems.add(new StarWebSystem(this, new CircleFactory(this), NUMBER_PARTICLES / 4));
			foregroundSystems.add(new RotSystem(this, new HypocycloidFactory(this, 2.5f), NUMBER_PARTICLES));
			foregroundSystems.add(new StarWebSystem(this, new SquareFactory(this, .5f)));
			foregroundSystems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG, 2.5f), NUMBER_PARTICLES));
			foregroundSystems.add(new RotSystem(this, new SquareFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new GravitySystem(this, new CircleFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new RotSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 4));
		}
		
		if (USE_BACKDROP) {
			backDropSystems.add(new OscilatingBackDrop(this, Color.WHITE, Color.WHITE));
			backDropSystems.add(new PulseRefreshBackDrop(this));
			backDropSystems.add(new PulseRefreshBackDrop(this, PulseListener.DEFAULT_FADE_OUT_FRAMES, PulseListener.DEFAULT_PULSES_TO_SKIP * 4));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.WHITE.darker().darker()));
			backDropSystems.add(new OscilatingBackDrop(this, Color.GREEN, Color.BLACK));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.RED.darker()));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.BLUE));
			backDropSystems.add(new BackDropSystem(this));
			backDropSystems.add(new RefreshBackDrop(this));
			backDropSystems.add(new BlurBackDrop(this));
		}
		
		if (USE_FILTERS) {
			filters.add(new PulseShakeFilter(this));
			filters.add(new PulseShakeFilter(this));
			filters.add(new PulseShakeFilter(this));
			filters.add(new Filter(this));
			filters.add(new Filter(this));
			filters.add(new Filter(this));
			filters.add(new Filter(this));
			filters.add(new Filter(this));
			filters.add(new BlendModeFilter(this, BlendModeFilter.BLEND_MODE.EXCLUSION));
			filters.add(new BlendModeFilter(this, BlendModeFilter.BLEND_MODE.ADD));
			filters.add(new BlendModeFilter(this, BlendModeFilter.BLEND_MODE.SUBTRACT));
		}
		
		for (ShapeSystem system : foregroundSystems) {
			system.setup();
		}
		
		for (ShapeSystem system : backgroundSystems) {
			system.setup();
		}
		background(Color.BLACK.getRGB());
		
		snapListener = new SnapListener(this);
		pulseListener = new PulseListener(this, 1, 16);
		frameSkipper = new SingleFrameSkipper(this);
	}

	public void scramble() {
		//mess it all up
		foregroundIndex += random(foregroundSystems.size() - 1);
		backgroundIndex += random(backgroundSystems.size() - 1);
		backDropIndex += random(backDropSystems.size() - 1);
		locationIndex += random(locationSystems.size() - 1);
		filterIndex += random(filters.size() - 1);
		colorIndex += random(colorSystems.size() - 1);
	}

	public void draw() {
		//Auto Stuff
		if (snapMode && snapListener.isSnap() && frameSkipper.isNewFrame()) {  //listen for loud POPs!
			scramble();
		} else if (autoMode && frameSkipper.isNewFrame() && pulseListener.isNewPulse()) {
			scramble();
		}
		
		BackDropSystem backDrop = null;
		if (USE_BACKDROP) {
			backDrop = (BackDropSystem)getPlugin(backDropSystems, backDropIndex);
			drawSystem(backDrop, "backDrop");
		}
		
		ShapeSystem bgSys = null;
		if (USE_BG) {
			bgSys = (ShapeSystem)getPlugin(backgroundSystems, backgroundIndex);
			drawSystem(bgSys, "bgSys");
		}
		
		Filter filter = null;
		if (USE_FILTERS) {
			filter = (Filter)getPlugin(filters, filterIndex);
			addDebugMsg("filter: " + filter.getName());
			filter.preRender();
		}
		
		ShapeSystem fgSys = null;
		if (USE_FG) {
			fgSys = (ShapeSystem)getPlugin(foregroundSystems, foregroundIndex);
			drawSystem(fgSys, "fgSys");
		}
		
		if (USE_FILTERS) {
			if (filter != null) {
				filter.postRender();
			}
		}
		
		if (debug) {
			doDebugMsg();
		}
		
		if (MONITOR_FRAME_RATE) {
			doMonitorCheck(backDrop, filter, bgSys, fgSys);
		}
		
		if (showHelp) {
			showHelp();
		}
	}
	
	protected void drawSystem(ShapeSystem s, String debugName) {
		pushStyle();
		pushMatrix();
		addDebugMsg(debugName + ": " + s.getName());
		s.draw();
		popMatrix();
		popStyle();
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
	
	public static final int TEXT_SIZE = 16;
	public static final int TEXT_INDEX = 10;
	protected List<String> debugStatements = new ArrayList<String>();
	
	public void addDebugMsg(String msg) {
		debugStatements.add(msg);
	}
	
	protected void debugSystem(String name, ShapeSystem ss) {
		addDebugMsg(name +": " + ss.getName());
		if (ss.factory != null) {
			addDebugMsg("  --factory: " + ss.factory.getName());
			addDebugMsg("    --scale: " + ss.factory.getScale());
		}
	}
	
	protected void doDebugMsg() {
		addDebugMsg("Auto: " + autoMode);
		addDebugMsg("SnapMode: " + snapMode);
		addDebugMsg("Audio: " + getAudio().getScaleFactor());
		addDebugMsg("Color: " + getColorSystem().getName());
		addDebugMsg("Loc: " + getLocationSystem().getName());
		addDebugMsg("Frame rate: " + (int)frameRate);
		addDebugMsg("MouseXY:  " + mouseX + " " + mouseY);
		drawDebug();
	}

	protected void drawDebug() {
		drawText(debugStatements);
		debugStatements.clear();
	}

	protected void drawText(List<String> msgs) {
		fill(255);
		textAlign(PApplet.LEFT, PApplet.TOP);
		textSize(TEXT_SIZE);
		
		int offset = TEXT_INDEX;
		for (String s : msgs) {
			text(s, TEXT_INDEX, offset);
			offset += TEXT_SIZE;
		}
	}
	
	private Map<String, List<Float>> monitorRecords = new HashMap<String, List<Float>>();
	private static DecimalFormat decFormat = new DecimalFormat(".##");
	
	private void doMonitorCheck(APVPlugin backDrop, 
								APVPlugin filter, 
								APVPlugin bg, 
								APVPlugin fg) {
		if (frameRate < FRAME_RATE_THRESHOLD) {
			//This is an ugly way to build a key
			StringBuilder builder = new StringBuilder();
			builder.append((backDrop != null) ? backDrop.getName() : "()").append(':'); 
			builder.append((filter != null) ? filter.getName() : "()").append(':');
			builder.append((bg != null) ? bg.getName() : "()").append(':');
			builder.append((fg != null) ? fg.getName() : "()");
			String key = builder.toString();
			
			List<Float> frames = monitorRecords.get(key);
			if (frames == null) {
				frames = new ArrayList<Float>();
				monitorRecords.put(key, frames);
			}
			
			frames.add(frameRate);
		}
	}
	
	private void dumpMonitorInfo() {
		System.out.println("name, numEntries, avgTime");
		for (Map.Entry<String, List<Float>> entry : monitorRecords.entrySet()) {
			List<Float> counts = entry.getValue();
			if (counts.size() < MIN_THRESHOLD_ENTRIES) {
				continue;
			}
			
			//get the average
			OptionalDouble average = counts.stream().mapToDouble(a -> a).average();
			System.out.println(entry.getKey() + "," + counts.size() + "," + decFormat.format(average.getAsDouble()));
		}
	}
	
	private void showHelp() {
		Set<String> messages = new HashSet<String>();
		commandSystem.visitCommands(true, e -> {
			List<APVCommand> cmds = e.getValue();
			cmds.forEach(c -> {
				messages.add(c.getName() + ": " + c.getHelpText());
			});
		});
		commandSystem.visitCommands(false, e -> {
			List<APVCommand> cmds = e.getValue();
			cmds.forEach(c -> {
				messages.add(String.valueOf(c.getCharKey()).trim() + ": " + c.getName() + ": " + c.getHelpText());
			});
		});
		
		translate(width / 5, height / 5);
		drawText(new ArrayList<String>(messages));
	}
}
