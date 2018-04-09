package com.arranger.apv;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.arranger.apv.APVShape.Data;
import com.arranger.apv.CommandSystem.APVCommand;
import com.arranger.apv.ControlSystem.CONTROL_MODES;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.audio.FreqDetector;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.bg.BackDropSystem;
import com.arranger.apv.bg.BlurBackDrop;
import com.arranger.apv.bg.OscilatingBackDrop;
import com.arranger.apv.bg.PulseRefreshBackDrop;
import com.arranger.apv.bg.RefreshBackDrop;
import com.arranger.apv.color.BeatColorSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.color.RandomColor;
import com.arranger.apv.control.Auto;
import com.arranger.apv.control.Manual;
import com.arranger.apv.control.Perlin;
import com.arranger.apv.control.Snap;
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
import com.arranger.apv.loc.PerlinNoiseWalkerLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.msg.StandardMessage;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.systems.lifecycle.RotatorSystem;
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
import com.arranger.apv.transition.Fade;
import com.arranger.apv.transition.Swipe;
import com.arranger.apv.util.Monitor;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	//Change these during active development
	private static final int DEFAULT_TRANSITION_FRAMES = 30;
	private static final int PLASMA_ALPHA_LOW = 120;
	private static final int PLASMA_ALPHA_HIGH = 255;
	public static final String RENDERER = P2D;
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	
	//Features
	public static final boolean AUDIO_IN = true;
	private static final boolean USE_BACKDROP = true;
	private static final boolean USE_BG = true;
	private static final boolean USE_FG = true;
	private static final boolean USE_FILTERS = true;
	private static final boolean FULL_SCREEN = true;
	private static final boolean USE_TRANSITIONS = true;
	private static final boolean SHOW_SETTINGS = true;
	private static final boolean USE_MESSAGES = true;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024
	private static final int NUMBER_PARTICLES = 1000;

	//Don't change the following values
	private static final String CONFIG = "/config/log.properties";
	private static final String SONG = "";
	private static final String SPRITE_PNG = "sprite.png";
	public static final char SPACE_BAR_KEY_CODE = ' ';
	
	//Some default Monitoring params.  Probably don't need to change
	private static final boolean MONITOR_FRAME_RATE = true;

	private static final boolean DEBUG_LOG_CONFIG = false;
	
	public static class EmptyShapeFactory extends ShapeFactory {
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
	
	protected List<TransitionSystem> transitionSystems = new ArrayList<TransitionSystem>();
	protected int transitionIndex = 0;
	
	protected List<MessageSystem> messageSystems = new ArrayList<MessageSystem>();
	protected int messageIndex = 0;
	
	protected List<Filter> filters = new ArrayList<Filter>(); 
	protected int filterIndex = 0;
	
	protected List<ControlSystem> controlSystems = new ArrayList<ControlSystem>();
	protected CONTROL_MODES currentControlMode = CONTROL_MODES.AUTO;
	
	protected CommandSystem commandSystem;
	protected Audio audio;
	protected Gravity gravity;
	protected Monitor monitor;
	protected boolean showHelp = true;
	protected boolean showSettings = SHOW_SETTINGS;
	protected boolean scrambleMode = true;	//this is a flag to signal to the TransitionSystem for #onDrawStart
	
	private int transitionFrames = DEFAULT_TRANSITION_FRAMES;
	private boolean transitionMode = USE_TRANSITIONS;
	private boolean messagesEnabled = USE_MESSAGES;
	private boolean fgSysEnabled = USE_FG;
	private boolean bgSysEnabled = USE_BG;
	private boolean bDropEnabled = USE_BACKDROP;
	private boolean filtersEnabled = USE_FILTERS;
	
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
	
	public Monitor getMonitor() {
		return monitor;
	}

	public ColorSystem getColorSystem() {
		return (ColorSystem)getPlugin(colorSystems, colorIndex);
	}
	
	public ShapeSystem getForegroundSystem() {
		return (ShapeSystem)getPlugin(foregroundSystems, foregroundIndex);
	}

	public ShapeSystem getBackgroundSystem() {
		return (ShapeSystem)getPlugin(backgroundSystems, backgroundIndex);
	}

	public BackDropSystem getBackDropSystem() {
		return (BackDropSystem)getPlugin(backDropSystems, backDropIndex);
	}
	
	public TransitionSystem getTransitionSystem() {
		return (TransitionSystem)getPlugin(transitionSystems, transitionIndex);
	}
	
	public MessageSystem getMessageSystem() {
		return (MessageSystem)getPlugin(messageSystems, messageIndex);
	}
	
	public LocationSystem getLocationSystem() {
		LocationSystem ls = null;
		ControlSystem cs = getControlSystem();
		while (ls == null) {
			ls = (LocationSystem)getPlugin(locationSystems, locationIndex);
			if (!cs.allowsMouseLocation() && ls instanceof MouseLocationSystem) {
				locationIndex++;
				ls = null;
			}
		}
		return ls;
	}
	
	public ControlSystem getControlSystem() {
		//there is probably a more efficient way to do this 
		for (ControlSystem cs : controlSystems) {
			if (cs.getControlMode() == currentControlMode) {
				return cs;
			}
		}

		logger.warning("Unable to find current control system: " + currentControlMode);
		return null;
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
		float cos = cos(PI * getFrameCount() / fr / oscSpeed);
		return PApplet.map(cos, -1, 1, low, high);
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public boolean randomBoolean() {
		return random(10) > 5;
	}
	
	public void setup() {
		configureLogging();
		
		if (MONITOR_FRAME_RATE) {
			monitor = new Monitor(this);
		}
		
		commandSystem = new CommandSystem(this);
		
		//add commands
		commandSystem.registerCommand('f', "Foreground", "Cycles through the foreground systems", 
				(event) -> {if (event.isShiftDown()) foregroundIndex--; else foregroundIndex++;});
		commandSystem.registerCommand('b', "Background", "Cycles through the background systems", 
				(event) -> {if (event.isShiftDown()) backgroundIndex--; else backgroundIndex++;});
		commandSystem.registerCommand('o', "Backdrop", "Cycles through the backdrop systems", 
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
		commandSystem.registerCommand('n', "Transition", "Cycles through the transition systems (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) transitionIndex--; else transitionIndex++;});
	
		commandSystem.registerCommand(SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things", e -> scramble());
		commandSystem.registerCommand('p', "Perf Monitor", "Outputs the slow monitor data to the console", event -> monitor.dumpMonitorInfo());
		commandSystem.registerCommand('h', "Help", "Toggles the display of all the available commands", event -> showHelp = !showHelp);
		commandSystem.registerCommand('q', "Settings", "Toggles the display of all the debug information", event -> showSettings = !showSettings);
		commandSystem.registerCommand('m', "Message", "Toggles between showing messages", event -> messagesEnabled = !messagesEnabled);
		commandSystem.registerCommand('1', "Enable Foregrond", "Toggles between using foregrounds", event -> fgSysEnabled = !fgSysEnabled);
		commandSystem.registerCommand('2', "Enable Background", "Toggles between using backgrounds", event -> bgSysEnabled = !bgSysEnabled);
		commandSystem.registerCommand('3', "Enable BackDrop", "Toggles between using backdrops", event -> bDropEnabled = !bDropEnabled);
		commandSystem.registerCommand('4', "Enable Filters", "Toggles between using filters", event -> filtersEnabled = !filtersEnabled);
		commandSystem.registerCommand('z', "Cycle Mode", "Cycles between all the available Modes", event -> cycleMode());
		
		commandSystem.registerCommand('}', "Transition Frames", "Increments the number of frames for each transition ", 
				(event) -> {
					for (TransitionSystem sys : transitionSystems) {
						sys.incrementTransitionFrames();
					}
				});
		commandSystem.registerCommand('{', "Transition Frames", "Decrements the number of frames for each transition ", 
				(event) -> {
					for (TransitionSystem sys : transitionSystems) {
						sys.decrementTransitionFrames();
					}
				});
		
		gravity = new Gravity(this);
		audio = new Audio(this, SONG, BUFFER_SIZE);
		
		locationSystems.add(new PerlinNoiseWalkerLocationSystem(this));
		locationSystems.add(new MouseLocationSystem(this));
		locationSystems.add(new CircularLocationSystem(this, false));
		locationSystems.add(new CircularLocationSystem(this, true));
		locationSystems.add(new RectLocationSystem(this, false));
		locationSystems.add(new RectLocationSystem(this, true));
		
		colorSystems.add(new BeatColorSystem(this));
		colorSystems.add(new OscillatingColor(this));
		colorSystems.add(new RandomColor(this));
		
		controlSystems.add(new Manual(this));
		controlSystems.add(new Auto(this));
		controlSystems.add(new Snap(this));
		controlSystems.add(new Perlin(this));
		
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
			foregroundSystems.add(new RotatorSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new RotatorSystem(this, new InvoluteFactory(this, .25f), NUMBER_PARTICLES / 4));
			foregroundSystems.add(new StarWebSystem(this, new CircleFactory(this), NUMBER_PARTICLES / 4));
			foregroundSystems.add(new RotatorSystem(this, new HypocycloidFactory(this, 2.5f), NUMBER_PARTICLES));
			foregroundSystems.add(new StarWebSystem(this, new SquareFactory(this, .5f)));
			foregroundSystems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG, 2.5f), NUMBER_PARTICLES));
			foregroundSystems.add(new RotatorSystem(this, new SquareFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new GravitySystem(this, new CircleFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new RotatorSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 4));
		}
		
		if (USE_BACKDROP) {
			backDropSystems.add(new OscilatingBackDrop(this, Color.WHITE, Color.BLACK, "[White,Black]"));
			backDropSystems.add(new PulseRefreshBackDrop(this));
			backDropSystems.add(new PulseRefreshBackDrop(this, PulseListener.DEFAULT_PULSES_TO_SKIP / 2));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.WHITE, "[Black,White]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.GREEN, Color.BLACK, "[Green,Black]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.RED.darker(), "[Black,DarkRed]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.BLUE, "[Black,Blue]"));
			backDropSystems.add(new BackDropSystem(this));
			backDropSystems.add(new RefreshBackDrop(this,.95f));
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
		
		if (USE_TRANSITIONS) {
			transitionSystems.add(new Fade(this, transitionFrames));
			transitionSystems.add(new Swipe(this, transitionFrames));
		}
		
		if (USE_MESSAGES) {
			messageSystems.add(new StandardMessage(this));
		}
		
		setupSystems(foregroundSystems);
		setupSystems(backgroundSystems);
		setupSystems(backDropSystems);
		setupSystems(transitionSystems);
		setupSystems(messageSystems);
		//setupSystems(filters);  Filters get left out of the setup() for now because they don't extends the ShapeSystem
		
		//init background
		background(Color.BLACK.getRGB());
	}
	
	protected void cycleMode() {
		currentControlMode = getControlSystem().getControlMode().getNext();
	}

	protected void setupSystems(List<? extends ShapeSystem> systems) {
		for (ShapeSystem system : systems) {
			system.setup();
		}
	}
	
	public void scramble() {
		scrambleMode = true;
	}
	
	protected void doScramble() {
		//mess it all up
		foregroundIndex += random(foregroundSystems.size() - 1);
		backgroundIndex += random(backgroundSystems.size() - 1);
		backDropIndex += random(backDropSystems.size() - 1);
		locationIndex += random(locationSystems.size() - 1);
		filterIndex += random(filters.size() - 1);
		colorIndex += random(colorSystems.size() - 1);
		//transitionIndex += random(transitionSystems.size() - 1); //can't scramble the transitions.  Don't do it
		
		//send out a cool message about the new system
		if (messagesEnabled && USE_FG && USE_BG) {
			getMessageSystem().onNewMessage(new String[] {
												getBackDropSystem().getDisplayName(),
												getForegroundSystem().getDisplayName(),
												getBackgroundSystem().getDisplayName()
					});
		}
		
		//reset the flag
		scrambleMode = false;
	}
	
	public void draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		if (showSettings) {
			prepareSettingsMessages();
			addPrimarySettingsMessages();
		}
		
		TransitionSystem transition = null;
		if (transitionMode) {
			transition = getTransitionSystem();
			if (scrambleMode) {
				transition.startTransition();
			}
			
			transition.onDrawStart();
		}
		
		BackDropSystem backDrop = null;
		if (bDropEnabled) {
			backDrop = getBackDropSystem();
			drawSystem(backDrop, "backDrop");
		}
		
		ShapeSystem bgSys = null;
		if (bgSysEnabled) {
			bgSys = getBackgroundSystem();
			drawSystem(bgSys, "bgSys");
		}
		
		Filter filter = null;
		if (filtersEnabled) {
			filter = (Filter)getPlugin(filters, filterIndex);
			addSettingsMessage("filter: " + filter.getName());
			filter.preRender();
		}
		
		ShapeSystem fgSys = null;
		if (fgSysEnabled) {
			fgSys = getForegroundSystem();
			drawSystem(fgSys, "fgSys");
		}
		
		if (filtersEnabled) {
			if (filter != null) {
				filter.postRender();
			}
		}
		
		if (MONITOR_FRAME_RATE) {
			monitor.doMonitorCheck(backDrop, filter, bgSys, fgSys);
		}
		
		if (transition != null) {
			drawSystem(transition, "transition");
		}
		
		if (messagesEnabled) {
			drawSystem(getMessageSystem(), "messageSystem");
		}
		
		if (showSettings) {
			drawSettingsMessages();
		}
		
		if (showHelp) {
			showHelp();
		}
		
		if (scrambleMode) {
			doScramble();
		}
		
		runControlMode();
	}
	
	protected void runControlMode() {
		ControlSystem cs = getControlSystem();
		KeyEvent nextCommand = cs.getNextCommand();
		if (nextCommand != null) {
			getCommandSystem().keyEvent(nextCommand);
		}
	}

	protected void drawSystem(ShapeSystem s, String debugName) {
		pushStyle();
		pushMatrix();
		debugSystem(s, debugName);
		s.draw();
		popMatrix();
		popStyle();
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
	
	public static final int TEXT_SIZE = 16;
	public static final int TEXT_INDEX = 10;
	protected List<String> settingsMessages = new ArrayList<String>();
	
	public void addSettingsMessage(String msg) {
		settingsMessages.add(msg);
	}
	
	protected void debugSystem(ShapeSystem ss, String name) {
		logger.fine("Drawing system [" + name + "] [" + ss.getName() +"]");
		addSettingsMessage(name +": " + ss.getName());
		if (ss.factory != null) {
			addSettingsMessage("  --factory: " + ss.factory.getName());
			addSettingsMessage("    --scale: " + ss.factory.getScale());
		}
	}
	
	protected void prepareSettingsMessages() {
		settingsMessages.clear();
	}
	
	protected void addPrimarySettingsMessages() {
		addSettingsMessage("---------System Settings-------");
		addSettingsMessage("Messages Enabled: " + messagesEnabled);
		addSettingsMessage("Transitions Enabled: " + transitionMode);
		addSettingsMessage("Transitions Frames : " + getTransitionSystem().getTransitionFrames());
		addSettingsMessage("Audio: " + getAudio().getScaleFactor());
		addSettingsMessage("Color: " + getColorSystem().getName());
		addSettingsMessage("Loc: " + getLocationSystem().getName());
		addSettingsMessage("Frame rate: " + (int)frameRate);
		addSettingsMessage("MouseXY:  " + mouseX + " " + mouseY);
		addSettingsMessage("Mode: " + currentControlMode.name());
		getControlSystem().addSettingsMessages();
		
		//Last Command
		APVCommand lastCommand = getCommandSystem().getLastCommand();
		if (lastCommand != null) {
			addSettingsMessage("Last Command: " + lastCommand.getName());
		}
		
		addSettingsMessage(" ");
		addSettingsMessage("---------Live Settings-------");
	}

	protected void drawSettingsMessages() {
		drawText(settingsMessages);
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
		
		List<String> sortedMessages = new ArrayList<String>(messages);
		sortedMessages.sort(Comparator.naturalOrder());
		
		translate(width / 5, height / 5);
		drawText(new ArrayList<String>(sortedMessages));
	}
	
	protected void configureLogging()  {
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();
		try {
			InputStream configFile = Main.class.getResourceAsStream(CONFIG);
			logManager.readConfiguration(configFile);
			configFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Dump the loggers
		if (DEBUG_LOG_CONFIG) {
			Enumeration<String> loggerNames = logManager.getLoggerNames();
			while (loggerNames.hasMoreElements()) {
				debugLogger(logManager.getLogger(loggerNames.nextElement()));
			}
		}
	}
	
	protected void debugLogger(Logger l) {
		Level level = l.getLevel();
		String name = l.getName();
		System.out.println("name: " + name + " level: " + level);
		
		l = l.getParent();
		int indent = 1;
		while (l != null) {
			for (int index = 0; index < indent; index++) {
				System.out.print("   ");
			}
			level = l.getLevel();
			name = l.getName();
			System.out.println("name: " + name + " level: " + level);
			l = l.getParent();
			indent++;
		}
	}
}
