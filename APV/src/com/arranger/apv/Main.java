package com.arranger.apv;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import com.arranger.apv.factories.CircleImageFactory;
import com.arranger.apv.factories.DotFactory;
import com.arranger.apv.factories.EmptyShapeFactory;
import com.arranger.apv.factories.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factories.ParametricFactory.InvoluteFactory;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.factories.SquareFactory;
import com.arranger.apv.factories.StarFactory;
import com.arranger.apv.filter.BlendModeFilter;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.PulseShakeFilter;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.loc.PerlinNoiseWalkerLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.msg.CircularMessage;
import com.arranger.apv.msg.LocationMessage;
import com.arranger.apv.msg.LocationMessage.CORNER_LOCATION;
import com.arranger.apv.msg.RandomMessage;
import com.arranger.apv.msg.StandardMessage;
import com.arranger.apv.pl.SimplePL;
import com.arranger.apv.pl.StarPL;
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
import com.arranger.apv.transition.Shrink;
import com.arranger.apv.transition.Swipe;
import com.arranger.apv.transition.Twirl;
import com.arranger.apv.util.APVPulseListener;
import com.arranger.apv.util.HelpDisplay;
import com.arranger.apv.util.LoggingConfig;
import com.arranger.apv.util.Monitor;
import com.arranger.apv.util.Oscillator;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.SettingsDisplay;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	//Change these during active development
	public static final int MAX_ALPHA = 255;
	public static final int NUMBER_PARTICLES = 1000;
	
	private static final int DEFAULT_TRANSITION_FRAMES = 30;
	private static final int PLASMA_ALPHA_LOW = 120;
	private static final int PLASMA_ALPHA_HIGH = 255;
	public static final String RENDERER = P2D;
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	
	//Defaults (could be intialized by env variables in the future)
	public static final boolean AUDIO_IN = true;
	private static final boolean USE_BACKDROP = true;
	private static final boolean USE_BG = true;
	private static final boolean USE_FG = true;
	private static final boolean USE_FILTERS = true;
	private static final boolean FULL_SCREEN = true;
	private static final boolean USE_TRANSITIONS = true;
	private static final boolean SHOW_SETTINGS = true;
	private static final boolean USE_MESSAGES = true;
	private static final boolean USE_PULSE_LISTENER = true;
	private static final boolean MONITOR_FRAME_RATE = true;

	//This is a tradeoff between performance and precision for the audio system
	private static final int BUFFER_SIZE = 512; //Default is 1024

	//Don't change the following values
	private static final String SONG = "";
	private static final String SPRITE_PNG = "sprite.png";
	public static final char SPACE_BAR_KEY_CODE = ' ';

	
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
	protected CONTROL_MODES currentControlMode = CONTROL_MODES.MANUAL;
	
	//Useful helper classes
	protected CommandSystem commandSystem;
	protected Audio audio;
	protected Gravity gravity;
	protected Monitor monitor;
	protected SettingsDisplay settingsDisplay;
	protected Oscillator oscillator;
	protected LoggingConfig loggingConfig;
	protected HelpDisplay helpDisplay;
	protected APVPulseListener pulseListener;
	protected Particles particles;

	//Internal data
	private boolean scrambleMode = false;	//this is a flag to signal to the TransitionSystem for #onDrawStart
	private int transitionFrames = DEFAULT_TRANSITION_FRAMES;
	
	//Switches for runtime
	private Switch foreGroundSwitch, 
					backGroundSwitch, 
					backDropSwitch, 
					filtersSwitch, 
					transitionSwitch,
					messagesSwitch,
					helpSwitch,
					showSettingsSwitch,
					pulseListenerSwitch;
	
	
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
	
	public APVPulseListener getPulseListener() {
		return pulseListener;
	}
	
	public Particles getParticles() {
		return particles;
	}
	
	public SettingsDisplay getSettingsDisplay() {
		return settingsDisplay;
	}
	
	public CONTROL_MODES getCurrentControlMode() {
		return currentControlMode;
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
	
	public void addSettingsMessage(String msg) {
		settingsDisplay.addSettingsMessage(msg);
	}
	
	public Switch[] getSwitches() {
		return new Switch[] {
				foreGroundSwitch, backGroundSwitch, 
				backDropSwitch, filtersSwitch,
				transitionSwitch, messagesSwitch,
				helpSwitch, showSettingsSwitch, pulseListenerSwitch
		};
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public boolean randomBoolean() {
		return random(10) > 5;
	}
	
	public float oscillate(float low, float high, float oscSpeed) {
		return oscillator.oscillate(low, high, oscSpeed);
	}
	
	public void setup() {
		foreGroundSwitch = new Switch(this, "ForeGround", USE_FG);
		backGroundSwitch = new Switch(this, "BackGround", USE_BG);
		backDropSwitch = new Switch(this, "BackDrop", USE_BACKDROP);
		filtersSwitch = new Switch(this, "Filters", USE_FILTERS);
		transitionSwitch = new Switch(this, "Transitions", USE_TRANSITIONS);
		messagesSwitch = new Switch(this, "Messages", USE_MESSAGES);
		pulseListenerSwitch = new Switch(this, "PulseListener", USE_PULSE_LISTENER);
		showSettingsSwitch = new Switch(this, "ShowSettings", SHOW_SETTINGS);
		helpSwitch = new Switch(this, "Help");
		
		loggingConfig = new LoggingConfig(this);
		loggingConfig.configureLogging();
		
		if (MONITOR_FRAME_RATE) {
			monitor = new Monitor(this);
		}
		
		commandSystem = new CommandSystem(this);
		initializeCommands();
		
		oscillator = new Oscillator(this);
		pulseListener = new APVPulseListener(this);
		particles = new Particles(this);
		settingsDisplay = new SettingsDisplay(this);
		helpDisplay = new HelpDisplay(this);
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
			backgroundSystems.add(new BubbleShapeSystem(this, NUMBER_PARTICLES / 14));
			backgroundSystems.add(new AttractorSystem(this, new SpriteFactory(this, SPRITE_PNG)));
			backgroundSystems.add(new AttractorSystem(this, new HypocycloidFactory(this)));
			backgroundSystems.add(new FreqDetector(this));
			backgroundSystems.add(new GridShapeSystem(this, 30, 10));
			backgroundSystems.add(new BubbleShapeSystem(this, NUMBER_PARTICLES / 10));
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
			foregroundSystems.add(new GravitySystem(this, new CircleImageFactory(this), NUMBER_PARTICLES / 10));
			foregroundSystems.add(new RotatorSystem(this, new InvoluteFactory(this, .5f), NUMBER_PARTICLES / 20));
			foregroundSystems.add(new RotatorSystem(this, new HypocycloidFactory(this, 2.5f), NUMBER_PARTICLES / 10));
			foregroundSystems.add(new RotatorSystem(this, new SquareFactory(this), NUMBER_PARTICLES / 5));
			foregroundSystems.add(new StarWebSystem(this, new StarFactory(this, .5f), NUMBER_PARTICLES / 12, true));
			foregroundSystems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this)));
			foregroundSystems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG), NUMBER_PARTICLES));
			foregroundSystems.add(new StarWebSystem(this, new SpriteFactory(this, SPRITE_PNG)));
			foregroundSystems.add(new CarnivalShapeSystem(this, new EmptyShapeFactory(this), true));
			foregroundSystems.add(new GravitySystem(this, new SquareFactory(this, 2.5f), NUMBER_PARTICLES  / 10));			
			foregroundSystems.add(new StarWebSystem(this));
			foregroundSystems.add(new RotatorSystem(this, new HypocycloidFactory(this), NUMBER_PARTICLES));
			foregroundSystems.add(new StarWebSystem(this, new CircleImageFactory(this), NUMBER_PARTICLES / 4));
			foregroundSystems.add(new StarWebSystem(this, new SquareFactory(this, .5f)));
			foregroundSystems.add(new GravitySystem(this, new SpriteFactory(this, SPRITE_PNG, 2.5f), NUMBER_PARTICLES));
			foregroundSystems.add(new RotatorSystem(this, new InvoluteFactory(this), NUMBER_PARTICLES / 20));
		}
		
		if (USE_BACKDROP) {
			backDropSystems.add(new OscilatingBackDrop(this, Color.WHITE, Color.BLACK, "[White-Black]"));
			backDropSystems.add(new PulseRefreshBackDrop(this));
			backDropSystems.add(new PulseRefreshBackDrop(this, PulseListener.DEFAULT_PULSES_TO_SKIP / 2));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.WHITE, "[Black-White]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.GREEN, Color.BLACK, "[Green-Black]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.RED.darker(), "[Black-DarkRed]"));
			backDropSystems.add(new OscilatingBackDrop(this, Color.BLACK, Color.BLUE, "[Black-Blue]"));
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
			transitionSystems.add(new Twirl(this, transitionFrames));
			transitionSystems.add(new Shrink(this, transitionFrames));
			transitionSystems.add(new Fade(this, transitionFrames));
			transitionSystems.add(new Swipe(this, transitionFrames));
		}
		
		if (USE_MESSAGES) {
			messageSystems.add(new LocationMessage(this, CORNER_LOCATION.UPPER_LEFT));
			messageSystems.add(new CircularMessage(this));
			messageSystems.add(new RandomMessage(this));
			messageSystems.add(new StandardMessage(this));
			messageSystems.add(new StandardMessage(this));
			messageSystems.add(new StandardMessage(this));
			messageSystems.add(new StandardMessage(this));
		}
		
		setupSystems(foregroundSystems);
		setupSystems(backgroundSystems);
		setupSystems(backDropSystems);
		setupSystems(transitionSystems);
		setupSystems(messageSystems);
		//setupSystems(filters);  Filters get left out of the setup() for now because they don't extends the ShapeSystem
		
		//listeners
		new SimplePL(this);
		new StarPL(this);
		
		//init background
		background(Color.BLACK.getRGB());
	}

	protected void initializeCommands() {
		CommandSystem cs = commandSystem;
		
		registerNonFreezableSwitchCommand(helpSwitch, 'h');
		registerNonFreezableSwitchCommand(showSettingsSwitch, 'q');
		
		registerSwitchCommand(foreGroundSwitch, '1');
		registerSwitchCommand(backGroundSwitch, '2');
		registerSwitchCommand(backDropSwitch, '3');
		registerSwitchCommand(filtersSwitch, '4');
		registerSwitchCommand(messagesSwitch, '5');
		registerSwitchCommand(transitionSwitch, '6');
		registerSwitchCommand(pulseListenerSwitch, '7');
		
		cs.registerCommand('f', "Foreground", "Cycles through the foreground systems", 
				(event) -> {if (event.isShiftDown()) foregroundIndex--; else foregroundIndex++;});
		cs.registerCommand('b', "Background", "Cycles through the background systems", 
				(event) -> {if (event.isShiftDown()) backgroundIndex--; else backgroundIndex++;});
		cs.registerCommand('o', "Backdrop", "Cycles through the backdrop systems", 
				(event) -> {if (event.isShiftDown()) backDropIndex--; else backDropIndex++;});
		cs.registerCommand(PConstants.ENTER, "Enter", "Cycles through the locations (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) locationIndex--; else locationIndex++;});
		cs.registerCommand('t', "Filter", "Cycles through the filters (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) filterIndex--; else filterIndex++;});
		cs.registerCommand('c', "Colors", "Cycles through the color systems (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) colorIndex--; else colorIndex++;});
		cs.registerCommand('n', "Transition", "Cycles through the transition systems (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) transitionIndex--; else transitionIndex++;});
		cs.registerCommand('m', "Message", "Cycles through the message systems (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) messageIndex--; else messageIndex++;});
		cs.registerCommand('z', "Cycle Mode", "Cycles between all the available Modes (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) cycleMode(false); else cycleMode(true);});
		
		cs.registerCommand(SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things", e -> scramble());
		cs.registerCommand('p', "Perf Monitor", "Outputs the slow monitor data to the console", event -> monitor.dumpMonitorInfo());
		cs.registerCommand('s', "ScreenShot", "Saves the current frame to disk", event -> doScreenCapture());
		
		//More complex event handlers
		
		cs.registerCommand(PApplet.RIGHT, "Right Arrow", "Cycles through the plugins", 
				(event) -> {
					if (!foreGroundSwitch.isFrozen()) {
						foregroundIndex++; 
					}
					if (!backGroundSwitch.isFrozen()) {
						backgroundIndex++; 
					}
					if (!backDropSwitch.isFrozen()) {
						backDropIndex++;
					}
					});
		cs.registerCommand(PApplet.LEFT, "Left Arrow", "Cycles through the plugins in reverse", 
				(event) -> { 
					if (!foreGroundSwitch.isFrozen()) {
						foregroundIndex--; 
					}
					if (!backGroundSwitch.isFrozen()) {
						backgroundIndex--; 
					}
					if (!backDropSwitch.isFrozen()) {
						backDropIndex--;
					}
					});
		
		cs.registerCommand('}', "Transition Frames", "Increments the number of frames for each transition ", 
				(event) -> {
					for (TransitionSystem sys : transitionSystems) {
						sys.incrementTransitionFrames();
					}
				});
		cs.registerCommand('{', "Transition Frames", "Decrements the number of frames for each transition ", 
				(event) -> {
					for (TransitionSystem sys : transitionSystems) {
						sys.decrementTransitionFrames();
					}
				});
	}

	protected void registerNonFreezableSwitchCommand(Switch s, char charCode) {
		commandSystem.registerCommand(charCode, "Toggle " + s.getName(), 
				"Toggles between enabling " + s.getName(), 
				event -> s.toggleEnabled());
	}
	
	protected void registerSwitchCommand(Switch s, char charCode) {
		commandSystem.registerCommand(charCode, "Toggle " + s.getName(), 
									"Toggles between enabling or freezing " + s.getName() + ".  Use Command-" + charCode + " to Freeze/UnFreeze", 
									(event) -> {
										if (event.isMetaDown()) {
											s.toggleFrozen();
										} else {
											s.toggleEnabled();
										}
									});
	}
	
	protected void cycleMode(boolean advance) {
		if (advance) {
			currentControlMode = getControlSystem().getControlMode().getNext();
		} else {
			currentControlMode = getControlSystem().getControlMode().getPrevious();
		}
	}

	protected void setupSystems(List<? extends ShapeSystem> systems) {
		for (ShapeSystem system : systems) {
			system.setup();
		}
	}
	
	public void doScreenCapture() {
		String homeDir = System.getProperty("user.home");
		homeDir += File.separator;
		String fileName = String.format("%1sapv%08d.png", homeDir, getFrameCount());
		
		logger.info("Saving image: " + fileName);
		
		PImage pImage = get();
		pImage.save(fileName);
		
		sendMessage(new String[] {fileName});
	}
	
	public void scramble() {
		scrambleMode = true;
		
		//switch transitions now instead of in the #doScramble
		if (!transitionSwitch.isFrozen()) {
			transitionIndex += random(transitionSystems.size() - 1); 
		}
	}
	
	protected void doScramble() {
		//mess it all up, but transitions were already scrambled
		
		if (!foreGroundSwitch.isFrozen()) {
			foregroundIndex += random(foregroundSystems.size() - 1);
		}
		
		if (!backGroundSwitch.isFrozen()) {
			backgroundIndex += random(backgroundSystems.size() - 1);
		}
		
		if (!backDropSwitch.isFrozen()) {
			backDropIndex += random(backDropSystems.size() - 1);
		}
		
		if (!filtersSwitch.isFrozen()) {
			filterIndex += random(filters.size() - 1);
		}
		
		if (!messagesSwitch.isFrozen()) {
			messageIndex += random(messageSystems.size());
		}
		
		locationIndex += random(locationSystems.size() - 1);
		colorIndex += random(colorSystems.size() - 1);
		
		
		//send out a cool message about the new system
		if (messagesSwitch.isEnabled()) {
			List<String> msgs = new ArrayList<String>();
			if (backDropSwitch.isEnabled()) {
				msgs.add(getBackDropSystem().getDisplayName());
			}

			if (backGroundSwitch.isEnabled()) {
				msgs.add(getForegroundSystem().getDisplayName());
			}
			
			if (foreGroundSwitch.isEnabled()) {
				msgs.add(getBackgroundSystem().getDisplayName());
			}
			
			sendMessage(msgs.toArray(new String[msgs.size()]));
		}
		
		//reset the flag
		scrambleMode = false;
	}
	
	public void sendMessage(String [] messages) {
		if (messagesSwitch.isEnabled()) {
			getMessageSystem().onNewMessage(messages);
		}
	}
	
	public void draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		if (showSettingsSwitch.isEnabled()) {
			settingsDisplay.prepareSettingsMessages();
			settingsDisplay.addPrimarySettingsMessages();
		}
		
		TransitionSystem transition = null;
		if (transitionSwitch.isEnabled()) {
			transition = getTransitionSystem();
			if (scrambleMode) {
				transition.startTransition();
			}
			
			transition.onDrawStart();
		}
		
		BackDropSystem backDrop = null;
		if (backDropSwitch.isEnabled()) {
			backDrop = getBackDropSystem();
			drawSystem(backDrop, "backDrop");
		}
		
		ShapeSystem bgSys = null;
		if (backGroundSwitch.isEnabled()) {
			bgSys = getBackgroundSystem();
			drawSystem(bgSys, "bgSys");
		}
		
		Filter filter = null;
		if (filtersSwitch.isEnabled()) {
			filter = (Filter)getPlugin(filters, filterIndex);
			settingsDisplay.addSettingsMessage("filter: " + filter.getName());
			filter.preRender();
		}
		
		ShapeSystem fgSys = null;
		if (foreGroundSwitch.isEnabled()) {
			fgSys = getForegroundSystem();
			drawSystem(fgSys, "fgSys");
		}
		
		if (filtersSwitch.isEnabled()) {
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
		
		if (messagesSwitch.isEnabled()) {
			drawSystem(getMessageSystem(), "messageSystem");
		}
		
		if (showSettingsSwitch.isEnabled()) {
			settingsDisplay.drawSettingsMessages();
		}
		
		if (helpSwitch.isEnabled()) {
			helpDisplay.showHelp();
		}
		
		if (scrambleMode) {
			doScramble();
		}
		
		runControlMode();
		
		if (pulseListenerSwitch.isEnabled()) {
			pulseListener.checkPulse();
		}
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
		settingsDisplay.debugSystem(s, debugName);
		s.draw();
		popMatrix();
		popStyle();
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
}
