package com.arranger.apv;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.agent.APVAgent;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.control.ControlSystem;
import com.arranger.apv.control.ControlSystem.CONTROL_MODES;
import com.arranger.apv.event.APVChangeEvent;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.event.CommandInvokedEvent;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.event.DrawShapeEvent;
import com.arranger.apv.event.EventTypes;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.gui.APVWindow;
import com.arranger.apv.helpers.APVPulseListener;
import com.arranger.apv.helpers.HelpDisplay;
import com.arranger.apv.helpers.HotKeyHelper;
import com.arranger.apv.helpers.MacroHelper;
import com.arranger.apv.helpers.MarqueeList;
import com.arranger.apv.helpers.PerformanceMonitor;
import com.arranger.apv.helpers.SettingsDisplay;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.helpers.Switch.STATE;
import com.arranger.apv.helpers.VideoGameHelper;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.msg.MessageSystem;
import com.arranger.apv.scene.LikedScene;
import com.arranger.apv.scene.Scene;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.transition.TransitionSystem;
import com.arranger.apv.util.APVSetList;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FontHelper;
import com.arranger.apv.util.Gravity;
import com.arranger.apv.util.LoggingConfig;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.VersionInfo;
import com.arranger.apv.util.draw.RandomMessagePainter;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.StarPainter;
import com.arranger.apv.util.frame.FrameStrober;
import com.arranger.apv.util.frame.Oscillator;
import com.arranger.apv.util.frame.Oscillator.Listener;
import com.arranger.apv.util.frame.SplineHelper;
import com.typesafe.config.Config;

import ch.bildspur.postfx.builder.PostFX;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.opengl.PShader;

public class Main extends PApplet {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	public static final int NUMBER_PARTICLES = 100;
	public static final String RENDERER = P3D;
	public static final int BUFFER_SIZE = 512;
	public static final int MAX_ALPHA = 255;
	public static final int SCRAMBLE_QUIET_WINDOW = 120; //2 to 4 seconds
	public static final char SPACE_BAR_KEY_CODE = ' ';

	protected APV<ShapeSystem> backgrounds;
	protected APV<BackDropSystem> backDrops;
	protected APV<ColorSystem> colors;
	protected APV<ControlSystem> controls;	
	protected APV<Filter> filters; 
	protected APV<ShapeSystem> foregrounds; 
	protected APV<LikedScene> likedScenes;
	protected APV<LocationSystem> locations; 
	protected APV<MessageSystem> messages;	
	protected APV<Scene> scenes;	
	protected APV<Shader> shaders;	
	protected APV<TransitionSystem> transitions;
	
	//Useful helper classes
	protected APVAgent agent;
	protected APVPulseListener pulseListener;
	protected APVSetList setList;
	protected Configurator configurator;
	protected CommandSystem commandSystem;
	protected Audio audio;
	protected Gravity gravity;
	protected FrameStrober frameStrober;
	protected PerformanceMonitor perfMonitor;
	protected SettingsDisplay settingsDisplay;
	protected Oscillator oscillator;
	protected LoggingConfig loggingConfig;
	protected HelpDisplay helpDisplay;
	protected Particles particles;
	protected VersionInfo versionInfo;
	protected VideoGameHelper videoGameHelper;
	protected MacroHelper macroHelper;
	protected HotKeyHelper hotKeyHelper;
	protected MarqueeList marqueeList;
	protected FontHelper fontHelper;
	protected RandomMessagePainter randomMessagePainter;
	protected SplineHelper splineHelper;
	protected StarPainter starPainter;
	protected PostFX postFX;
	
	//Collections
	protected Map<String, Switch> switches = new HashMap<String, Switch>();
	protected Map<EventTypes, APVEvent<?>> eventMap = new HashMap<EventTypes, APVEvent<?>>();
	protected Map<SYSTEM_NAMES, APV<? extends APVPlugin>> systemMap = new HashMap<SYSTEM_NAMES, APV<? extends APVPlugin>>();
	
	//Stateful data
	private SafePainter safePainter = new SafePainter(this, ()-> _draw());
	private Scene currentScene;
	private CONTROL_MODES currentControlMode; 
	private boolean scrambleMode = false;
	private boolean screenshotMode = false;
	private int lastScrambleFrame = 0;
	
	
	//Switches for runtime
	private Switch helpSwitch,
					showSettingsSwitch,
					frameStroberSwitch,
					continuousCaptureSwitch,
					videoGameSwitch,
					scrambleModeSwitch,
					debugPulseSwitch;
	
	public enum FLAGS {
		
		CONTROL_MODE("controlMode"),
		FULL_SCREEN("fullScreen"),
		SCRAMBLE_SYSTEMS("scrambleSystems"),
		SCREEN_WIDTH("screen.width"),
		SCREEN_HEIGHT("screen.height"),
		MONITORING_ENABLED("monitoring.enabled"),
		QUIET_WINDOW_SIZE("quietWindowSize"),
		AUTO_ADD_SOBLE("autoAddSoble"),
		DEBUG_SYS_MESSAGES("debugSystemMessages"),
		SET_LIST("setList"),
		DEFAULT_SHAPE_SYSTEM_ALPHA("defaultShapeSystemAlpha");
		
		public String name;
		private FLAGS(String name) {
			this.name = name;
		}
		
		public String apvName() {
			return "apv." + name;
		}
		
		public static final List<FLAGS> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		public static final int SIZE = VALUES.size();
	}

	public enum SWITCH_NAMES {
		
		HELP("Help"),
		SHOW_SETTINGS("ShowSettings"),
		FRAME_STROBER("FrameStrober"),
		CONTINUOUS_CAPTURE("ContinuousCapture"),
		SCRAMBLE_MODE("Scramble"),
		VIDEO_GAME("VideoGame"),
		DEBUG_PULSE("DebugPulse");
		
		public String name;

		private SWITCH_NAMES(String name) {
			this.name = name;
		}
	}
	
	public enum SYSTEM_NAMES {
		
		AGENTS("agents", false),
		BACKGROUNDS("backgrounds"),
		BACKDROPS("backDrops"),
		COLORS("colors"),
		CONTROLS("controls"),
		FILTERS("filters"),
		FOREGROUNDS("foregrounds"),
		HOTKEYS("hotKeys", false),
		LIKED_SCENES("likedScenes"),
		LOCATIONS("locations"),
		MACROS("macros", false),
		MESSAGES("messages"),
		PULSELISTENERS("pulseListeners", false),
		SCENES("scenes"),
		SHADERS("shaders"),
		SWITCHES("switches", false),
		TRANSITIONS("transitions");
		
		
		public String name;
		public boolean isFullSystem;

		private SYSTEM_NAMES(String name) {
			this(name, true);
		}
		
		private SYSTEM_NAMES(String name, boolean isFullSystem) {
			this.name = name;
			this.isFullSystem = isFullSystem;
		}
		
		/**
		 * @see SafePainter
		 */
		public static final List<SYSTEM_NAMES> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		public static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static SYSTEM_NAMES random() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
	
	public static void main(String[] args) {
		PApplet.main(Main.class, new String[0]);
	}

	public void settings() {
		loggingConfig = new LoggingConfig(this);
		loggingConfig.configureLogging();
		
		configurator = new Configurator(this);
		configureSwitches();
		
		Config rootConfig = configurator.getRootConfig();
		boolean isFullScreen = rootConfig.getBoolean("apv.fullScreen");
		if (isFullScreen) {
			fullScreen(RENDERER);
		} else {
			size(rootConfig.getInt("apv.screen.width"), rootConfig.getInt("apv.screen.height"), RENDERER);
		}
		
		initEvents();
	}
	
	public String getConfigValueForFlag(FLAGS flag) {
		return getConfigurator().getRootConfig().getString(flag.apvName());
	}
	
	public String getConfigString(String path) {
		return getConfigurator().getRootConfig().getString(path);
	}
	
	public int getConfigInt(String path) {
		return getConfigurator().getRootConfig().getInt(path);
	}
	
	public boolean getConfigBoolean(String path) {
		return getConfigurator().getRootConfig().getBoolean(path);
	}
	
	public APV<? extends APVPlugin> getSystem(SYSTEM_NAMES name) {
		return systemMap.get(name);
	}
	
	public FontHelper getFontHelper() {
		return fontHelper;
	}

	public MarqueeList getMarqueeList() {
		return marqueeList;
	}
	
	public MacroHelper getMacroHelper() {
		return macroHelper;
	}
	
	public HotKeyHelper getHotKeyHelper() {
		return hotKeyHelper;
	}
	
	public VersionInfo getVersionInfo() {
		return versionInfo;
	}
	
	public VideoGameHelper getVideoGameHelper() {
		return videoGameHelper;
	}
	
	public Audio getAudio() {
		return audio;
	}
	
	public Gravity getGravity() {
		return gravity;
	}
	
	public FrameStrober getFrameStrober() {
		return frameStrober;
	}
	
	public Configurator getConfigurator() {
		return configurator;
	}
	
	public CommandSystem getCommandSystem() {
		return commandSystem;
	}
	
	public PerformanceMonitor getPerformanceMonitor() {
		return perfMonitor;
	}
	
	public APVPulseListener getPulseListener() {
		return pulseListener;
	}
	
	public APVSetList getSetList() {
		return setList;
	}
	
	public APVAgent getAgent() {
		return agent;
	}
	
	public HelpDisplay getHelpDisplay() {
		return helpDisplay;
	}
	
	public Particles getParticles() {
		return particles;
	}
	
	public PostFX getPostFX() {
		return postFX;
	}
	
	public SettingsDisplay getSettingsDisplay() {
		return settingsDisplay;
	}
	
	public CONTROL_MODES getCurrentControlMode() {
		return currentControlMode;
	}
	
	@SuppressWarnings("unchecked")
	public APVEvent<EventHandler> getEventForType(EventTypes type) {
		 return (APVEvent<EventHandler>)eventMap.get(type);
	}
	
	public CoreEvent getSetupEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SETUP);
	}
	
	public CoreEvent getDrawEvent() {
		return (CoreEvent)eventMap.get(EventTypes.DRAW);
	}
	
	public CoreEvent getSceneCompleteEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SCENE_COMPLETE);
	}
	
	public CoreEvent getStrobeEvent() {
		return (CoreEvent)eventMap.get(EventTypes.STROBE);
	}
	
	public DrawShapeEvent getSparkEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.SPARK);
	}
	
	public DrawShapeEvent getCarnivalEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.CARNIVAL);
	}
	
	public DrawShapeEvent getStarEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.STAR);
	}
	
	public DrawShapeEvent getRandomMessageEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.RANDOM_MESSAGE);
	}
	
	public DrawShapeEvent getTwirlEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.TWIRL);
	}
	
	public DrawShapeEvent getMarqueeEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.MARQUEE);
	}
	
	public DrawShapeEvent getEarthquakeEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.EARTHQUAKE);
	}
	
	public CommandInvokedEvent getCommandInvokedEvent() {
		return (CommandInvokedEvent)eventMap.get(EventTypes.COMMAND_INVOKED);
	}
	
	public APVChangeEvent getAPVChangeEvent() {
		return (APVChangeEvent)eventMap.get(EventTypes.APV_CHANGE);
	}
	
	public CoreEvent getLocationEvent() {
		return (CoreEvent)eventMap.get(EventTypes.LOCATION);
	}
	
	public boolean isMonitoringEnabled() {
		return getConfigBoolean(FLAGS.MONITORING_ENABLED.apvName());	
	}
	
	public boolean isAutoAddSobleEnabled() {
		return getConfigBoolean(FLAGS.AUTO_ADD_SOBLE.apvName());
	}
	
	public boolean isDebugSystemMessages() {
		return getConfigBoolean(FLAGS.DEBUG_SYS_MESSAGES.apvName());
	}
	
	public boolean isSetList() {
		return getConfigBoolean(FLAGS.SET_LIST.apvName());
	}
	
	public int getDefaultShapeSystemAlpha() {
		return getConfigInt(FLAGS.DEFAULT_SHAPE_SYSTEM_ALPHA.apvName());
	}
	
	public void activateNextPlugin(SYSTEM_NAMES systemName, String pluginDisplayName, String cause) {
		activateNextPlugin(systemName, pluginDisplayName, cause, false);
	}
	
	public void activateNextPlugin(SYSTEM_NAMES systemName, String pluginDisplayName, String cause, boolean checkFrozen) {
		APV<? extends APVPlugin> apv = systemMap.get(systemName);
		if (checkFrozen && apv.isFrozen()) {
			return;
		}
		
		APVPlugin plugin  = getPluginByName(apv, pluginDisplayName);
		apv.setNextPlugin(plugin, cause);
		apv.setEnabled(true);
	}
	
	public APVPlugin getPluginByName(APV<? extends APVPlugin> apv, String pluginDisplayName) {
		return apv.getList().stream().filter(p -> {
			String displayName = p.getDisplayName();
			String name = p.getName();
			boolean b1 = pluginDisplayName.equalsIgnoreCase(displayName);
			boolean b2 = pluginDisplayName.equalsIgnoreCase(name);
			return b1 || b2;
			
		}).findFirst().get();
	}
	
	public void setDefaultScene(String cause) {
		Scene defaultScene = scenes.getList().stream().filter(e -> !e.isAnimation()).findFirst().get();
		setNextScene(defaultScene, cause);
	}
	
	public void setNextScene(Scene scene, String cause) {
		scenes.setNextPlugin(scene, cause);
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}

	public ColorSystem getColor() {
		return colors.getPlugin();
	}
	
	public void setNextColor(ColorSystem cs, String cause) {
		colors.setNextPlugin(cs, cause);
	}
	
	public void setNextLocation(LocationSystem ls, String cause) {
		locations.setNextPlugin(ls, cause);
	}
	
	public APV<ShapeSystem> getBackgrounds(){
		return backgrounds;
	}
	
	public APV<BackDropSystem> getBackDrops(){
		return backDrops;
	}
	
	public APV<ShapeSystem> getForegrounds() {
		return foregrounds;
	}
	
	public APV<Filter> getFilters() {
		return filters;
	}
	
	public APV<Shader> getShaders() {
		return shaders;
	}
	
	public APV<TransitionSystem> getTransitions() {
		return transitions;
	}
	
	public TransitionSystem getTransition() {
		return transitions.getPlugin();
	}
	
	public MessageSystem getMessage() {
		return messages.getPlugin();
	}
	
	public  APV<LocationSystem> getLocations() {
		return locations;
	}
	
	public Point2D getCurrentPoint() {
		return locations.getPlugin().getCurrentPoint();
	}
	
	public ControlSystem getControl() {
		//there is probably a more efficient way to do this 
		for (ControlSystem cs : controls.getList()) {
			if (cs.getControlMode() == currentControlMode) {
				return cs;
			}
		}

		throw new RuntimeException("Unable to find current control system: " + currentControlMode);
	}
	
	public void likeCurrentScene() {
		likedScenes.getList().add(new LikedScene(currentScene));
		sendMessage(new String[] {"Liked :)"});
	}
	
	public void disLikeCurrentScene() {
		likedScenes.getList().remove(currentScene);
		sendMessage(new String[] {"Disliked :("});
	}
	
	public List<LikedScene> getLikedScenes() {
		return likedScenes.getList();
	}	
	
	public List<Scene> getScenes() {
		return scenes.getList();
	}
	
	public void addSettingsMessage(String msg) {
		settingsDisplay.addSettingsMessage(msg);
	}

	public Map<String, Switch> getSwitches() {
		return switches;
	}
	
	public Switch getSwitchForSystem(Main.SYSTEM_NAMES name) {
		return switches.get(name.name);
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public boolean randomBoolean() {
		return random(10) > 5;
	}
	
	public float mapEx(float value, float start, float end, float start1, float end1) {
		return splineHelper.map(value, start, end, start1, end1);
	}
	
	public float oscillate(float low, float high, float cycleTime) {
		return oscillator.oscillate(low, high, cycleTime);
	}
	
	public float oscillate(float low, float high, float cycleTime, Listener l) {
		return oscillator.oscillate(low, high, cycleTime, l);
	}

	public String format(Color c) {
		return String.format("(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public String format(Color c, boolean addQuote) {
		if (!addQuote) {
			return format(c);
		} else {
			return "\"" + String.format("(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue()) + "\"";
		}
	}
	
	public float lerpAlpha(float pct) {
		return PApplet.lerp(0, MAX_ALPHA, pct);
	}
	
	/**
	 * The PostFX library has an odd way of loading resources.  Fortunately i can work around that
	 */
	public PShader loadShader(String fragFilename) {
		int indexOf = fragFilename.indexOf("shader/");
		if (indexOf > 0) {
			fragFilename = fragFilename.substring(indexOf, fragFilename.length());
		}
		return super.loadShader(fragFilename);
	}
	
	public void setup() {
		agent = new APVAgent(this);
		audio = new Audio(this, BUFFER_SIZE);
		commandSystem = new CommandSystem(this);
		frameStrober = new FrameStrober(this);
		fontHelper = new FontHelper(this);
		gravity = new Gravity(this);
		helpDisplay = new HelpDisplay(this);
		oscillator = new Oscillator(this);
		particles = new Particles(this);
		perfMonitor = new PerformanceMonitor(this);
		postFX  = new PostFX(this);
		pulseListener = new APVPulseListener(this);
		macroHelper = new MacroHelper(this);
		hotKeyHelper = new HotKeyHelper(this);
		marqueeList = new MarqueeList(this);
		randomMessagePainter = new RandomMessagePainter(this);
		settingsDisplay = new SettingsDisplay(this);
		splineHelper = new SplineHelper(this);
		starPainter = new StarPainter(this);
		versionInfo = new VersionInfo(this);
		videoGameHelper = new VideoGameHelper(this);
		
		systemMap.put(SYSTEM_NAMES.BACKDROPS, new APV<BackDropSystem>(this, SYSTEM_NAMES.BACKDROPS));
		systemMap.put(SYSTEM_NAMES.BACKGROUNDS, new APV<ShapeSystem>(this, SYSTEM_NAMES.BACKGROUNDS));
		systemMap.put(SYSTEM_NAMES.COLORS, new APV<ColorSystem>(this, SYSTEM_NAMES.COLORS));
		systemMap.put(SYSTEM_NAMES.CONTROLS, new APV<ControlSystem>(this, SYSTEM_NAMES.CONTROLS));
		systemMap.put(SYSTEM_NAMES.FILTERS, new APV<Filter>(this, SYSTEM_NAMES.FILTERS));
		systemMap.put(SYSTEM_NAMES.FOREGROUNDS, new APV<ShapeSystem>(this, SYSTEM_NAMES.FOREGROUNDS));
		systemMap.put(SYSTEM_NAMES.LIKED_SCENES, new APV<Scene>(this, SYSTEM_NAMES.LIKED_SCENES));
		systemMap.put(SYSTEM_NAMES.LOCATIONS, new APV<LocationSystem>(this, SYSTEM_NAMES.LOCATIONS));
		systemMap.put(SYSTEM_NAMES.MESSAGES, new APV<MessageSystem>(this, SYSTEM_NAMES.MESSAGES));
		systemMap.put(SYSTEM_NAMES.SCENES, new APV<Scene>(this, SYSTEM_NAMES.SCENES, false));
		systemMap.put(SYSTEM_NAMES.SHADERS, new APV<Shader>(this, SYSTEM_NAMES.SHADERS));
		systemMap.put(SYSTEM_NAMES.TRANSITIONS, new APV<TransitionSystem>(this, SYSTEM_NAMES.TRANSITIONS));
		
		assignSystems();
		initControlMode();
		hotKeyHelper.configure();
		macroHelper.configure();
		setupSystems();
		initializeCommands();
		
		//processing hints
		noCursor();
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
		
		fireSetupEvent();
		
		//Forward messages to the currentLikedScene if applicable
		getAPVChangeEvent().register((apv, plugin, cause) -> {
			if (currentScene instanceof LikedScene) {
				((LikedScene)currentScene).onPluginChange(apv, plugin, cause);
			}
		});
		
		if (isSetList()) {
			try {
				setList = new APVSetList(this);
				setList.play();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	public void playSetList(File directory) {
		try {
			if (setList != null) {
				setList.stop();
			} else {
				setList = new APVSetList(this);
			}
			setList.play(directory);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void doScreenCapture() {
		String fileName = String.format("apv%08d.png", getFrameCount());
		fileName = new FileHelper(this).getFullPath(fileName);
		logger.info("Saving image: " + fileName);
		PImage pImage = get();
		pImage.save(fileName);
		
		sendMessage(new String[] {fileName});
		screenshotMode = false;
	}
	
	public boolean isScrambleModeAvailable() {
		if (lastScrambleFrame + SCRAMBLE_QUIET_WINDOW > getFrameCount()) {
			return false;
		} else {
			return true;
		}
	}
	
	public void scramble() {
		lastScrambleFrame = getFrameCount();
		scrambleMode = true;
		
		//switch transitions now instead of in the #doScramble
		transitions.scramble(true);
	}
	
	/**
	 * Reset all switches and control mode
	 */
	public void reset() {
		resetSwitches();
		initControlMode();
		commandSystem.reset();
	}
	
	/**
	 * Goes to instant manual mode and disables agents
	 */
	public void manual() {
		currentControlMode = CONTROL_MODES.MANUAL;
		getAgent().getSwitch().setState(STATE.DISABLED);
		activateNextPlugin(SYSTEM_NAMES.LOCATIONS, "Mouse", Command.MANUAL.name());
	}
	
	public void sendMessage(String [] msgs) {
		if (messages.isEnabled()) {
			getMessage().onNewMessage(msgs);
		}
	}
	
	public void sendMarqueeMessage(String message) {
		getCommandSystem().getSceneSelectInterceptor().showMessageSceneWithText(message);
	}
	
	@Override
	public void draw() {
		safePainter.paint();
	}
	
	public void drawSystem(ShapeSystem s, String debugName) {
		drawSystem(s, debugName, true);
	}
	
	public void drawSystem(ShapeSystem s, String debugName, boolean safe) {
		new SafePainter(this, () -> {
			settingsDisplay.debugSystem(s, debugName);
			s.draw();
		}).paint(null, safe);
	}
	
	public void reloadConfiguration() {
		reloadConfiguration(null);
	}
	
	public void reloadConfiguration(String file) {
		configurator.reload(file);
		Arrays.asList(SYSTEM_NAMES.values()).forEach(s -> reloadConfigurationForSystem(s));
		
		macroHelper.reloadConfiguration();
		hotKeyHelper.reloadConfiguration();
		agent.reloadConfiguration();
		reset();
		
		registerSystemCommands();
		fireSetupEvent();
	}

	protected void fireSetupEvent() {
		CoreEvent setupEvent = getSetupEvent();
		setupEvent.fire();
		setupEvent.reset();
	}
	
	protected void _draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		scrambleModeSwitch.setState(isScrambleModeAvailable() ? STATE.ENABLED : STATE.DISABLED);
		if (frameStroberSwitch.isEnabled()) {
			if (frameStrober.isSkippingFrames()) {
				return;
			}
		}
		settingsDisplay.reset();
		TransitionSystem transition = prepareTransition(false);
		
		if (likedScenes.isEnabled()) {
			currentScene = likedScenes.getPlugin();
		} else {
			currentScene = scenes.getPlugin();
			if (!currentScene.isAnimation()) {
				BackDropSystem backDrop = backDrops.getPlugin(true);
				ShapeSystem bgSys = backgrounds.getPlugin(true);
				Filter filter = filters.getPlugin(true);
				ShapeSystem fgSys = foregrounds.getPlugin(true);
				Shader shader = shaders.getPlugin(true);
				currentScene.setSystems(backDrop, bgSys, fgSys, filter, shader, getColor(), getLocations().getPlugin());
			} else {
				//using an animation.  See if it is brand new?  If so, start a transition
				if (currentScene.isNew()) {
					transition = prepareTransition(true);
				}
			}
		}
		
		drawSystem(currentScene, "scene");
		
		final TransitionSystem t = transition;
		postScene(() -> perfMonitor.doMonitorCheck(currentScene));
		postScene(transition != null, () -> drawSystem(t, "transition"));
		postScene(messages.isEnabled(), () -> drawSystem(getMessage(), "message"));
		postScene(videoGameSwitch, () -> videoGameHelper.showStats());
		postScene(showSettingsSwitch, () -> settingsDisplay.drawSettingsMessages());
		postScene(helpSwitch, () -> helpDisplay.showHelp());
		postScene(scrambleMode, () -> doScramble());
		postScene(() -> runControlMode());
		postScene(continuousCaptureSwitch.isEnabled() || screenshotMode == true, () -> doScreenCapture());
		postScene(() -> getDrawEvent().fire());
	}
	
	@FunctionalInterface
	private static interface Action {
		void action();
	}
	
	private void postScene(Action action) {
		postScene(true, action);
	}
	
	private void postScene(Switch sw, Action action) {
		postScene(sw.isEnabled(), action);
	}
	
	private void postScene(boolean isEnabled, Action action) {
		if (!isEnabled) {
			return;
		}
		
		try {
			action.action();
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	protected TransitionSystem prepareTransition(boolean forceStart) {
		TransitionSystem transition = transitions.getPlugin(true);
		if (transition != null) {
			if (scrambleMode || forceStart) {
				transition.startTransition();
			}
			
			transition.onDrawStart();
		}
		return transition;
	}
	
	protected void runControlMode() {
		ControlSystem cs = getControl();
		KeyEvent nextCommand = cs.getNextCommand();
		if (nextCommand != null) {
			getCommandSystem().keyEvent(nextCommand);
		}
	}
	
	protected void doScramble() {
		//mess it all up, except for transitions which were already scrambled
		if (!likedScenes.isEnabled()) {
			foregrounds.scramble(true);
			backgrounds.scramble(true);
			backDrops.scramble(true);
			shaders.scramble(true);
			filters.scramble(true);
			messages.scramble(true);
		}

		locations.scramble(false);
		colors.scramble(false);
		
		//send out a cool message about the new system
		if (messages.isEnabled() && isDebugSystemMessages()) {
			List<String> msgs = new ArrayList<String>();
			if (backDrops.isEnabled()) {
				msgs.add(getBackDrops().getPlugin().getDisplayName());
			}

			if (foregrounds.isEnabled()) {
				msgs.add(getForegrounds().getPlugin().getDisplayName());
			}
			
			if (backgrounds.isEnabled()) {
				msgs.add(getBackgrounds().getPlugin().getDisplayName());
			}
			
			if (shaders.isEnabled()) {
				msgs.add(getShaders().getPlugin().getDisplayName());
			}
			
			sendMessage(msgs.toArray(new String[msgs.size()]));
		}
		
		//reset the flag
		scrambleMode = false;
	}

	protected void initControlMode() {
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(getConfigString(FLAGS.CONTROL_MODE.apvName()));
	}
	
	protected void initializeCommands() {
		registerMainSwitches();
		registerSystemCommands();
		
		likedScenes.registerHandler(Command.RIGHT_ARROW, e -> likedScenes.increment("->"));
		likedScenes.registerHandler(Command.LEFT_ARROW, e -> likedScenes.decrement("<-"));
		
		hotKeyHelper.register();
		macroHelper.register();
		registerMainCommands();
	}

	protected void registerMainSwitches() {
		registerSwitch(helpSwitch, Command.SWITCH_HELP);
		registerSwitch(showSettingsSwitch, Command.SWITCH_SETTINGS);
		registerSwitch(likedScenes.getSwitch(), Command.SWITCH_LIKED_SCENES);
		registerSwitch(agent.getSwitch(), Command.SWITCH_AGENT);
		registerSwitch(pulseListener.getSwitch(), Command.SWITCH_PULSE_LISTENER);
		registerSwitch(frameStroberSwitch, Command.SWITCH_FRAME_STROBER);
		registerSwitch(continuousCaptureSwitch, Command.SWITCH_CONTINUOUS_CAPTURE);
		registerSwitch(videoGameSwitch, Command.SWITCH_VIDEOGAME);
		registerSwitch(debugPulseSwitch, Command.SWITCH_DEBUG_PULSE);
	}

	protected void registerMainCommands() {
		CommandSystem cs = commandSystem;
		cs.registerHandler(Command.CYCLE_CONTROL_MODE, e -> cycleMode(!e.isShiftDown())); 
		cs.registerHandler(Command.SCRAMBLE, e -> scramble());
		cs.registerHandler(Command.WINDOWS, e -> new APVWindow(this));
		cs.registerHandler(Command.RESET, e -> reset());
		cs.registerHandler(Command.MANUAL, e -> manual());	
		cs.registerHandler(Command.PERF_MONITOR, e -> perfMonitor.dumpMonitorInfo(e.isShiftDown()));
		cs.registerHandler(Command.SCREEN_SHOT, e -> screenshotMode = true); //screenshot's can only be taking during draw
		cs.registerHandler(Command.SAVE_CONFIGURATION, event -> configurator.saveCurrentConfig());
		cs.registerHandler(Command.RELOAD_CONFIGURATION, event -> reloadConfiguration());
		cs.registerHandler(Command.UP_ARROW, e -> likeCurrentScene());
		cs.registerHandler(Command.DOWN_ARROW, e -> disLikeCurrentScene());
		cs.registerHandler(Command.TRANSITION_FRAMES_INC, e -> {transitions.forEach(t -> {t.incrementTransitionFrames();});});
		cs.registerHandler(Command.TRANSITION_FRAMES_DEC, e -> {transitions.forEach(t -> {t.decrementTransitionFrames();});});
	}

	protected void registerSystemCommands() {
		register(SYSTEM_NAMES.FOREGROUNDS, Command.SWITCH_FOREGROUNDS, Command.CYCLE_FOREGROUNDS);
		register(SYSTEM_NAMES.BACKGROUNDS, Command.SWITCH_BACKGROUNDS, Command.CYCLE_BACKGROUNDS);
		register(SYSTEM_NAMES.BACKDROPS, Command.SWITCH_BACKDROPS, Command.CYCLE_BACKDROPS);
		register(SYSTEM_NAMES.COLORS, null, Command.CYCLE_COLORS);
		register(SYSTEM_NAMES.FILTERS, Command.SWITCH_FILTERS, Command.CYCLE_FILTERS);
		register(SYSTEM_NAMES.LOCATIONS, null, Command.CYCLE_LOCATIONS);
		register(SYSTEM_NAMES.MESSAGES, Command.SWITCH_MESSAGES, Command.CYCLE_MESSAGES);
		register(SYSTEM_NAMES.SHADERS, Command.SWITCH_SHADERS, Command.CYCLE_SHADERS);
		register(SYSTEM_NAMES.TRANSITIONS, Command.SWITCH_TRANSITIONS, Command.CYCLE_TRANSITIONS);
	}
	
	protected void register(SYSTEM_NAMES system, Command switchCommand, Command handlerCommand) {
		APV<? extends APVPlugin> apv = systemMap.get(system);
		if (switchCommand != null) {
			apv.registerSwitchCommand(switchCommand);
		}
		apv.registerHandler(handlerCommand);
	}

	protected void registerSwitch(Switch s, Command command) {
		commandSystem.registerHandler(command, event -> s.toggleEnabled());
	}
	
	protected void cycleMode(boolean advance) {
		CONTROL_MODES controlMode = getControl().getControlMode();
		if (advance) {
			currentControlMode = controlMode.getNext();
		} else {
			currentControlMode = controlMode.getPrevious();
		}
	}
	
	protected void setupSystems() {
		systemMap.values().forEach(apv -> setupSystem(apv));
	}

	protected void setupSystem(APV<? extends APVPlugin> apv) {
		for (APVPlugin system : apv.getList()) {
			system.setup();
		}
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
	
	protected void reloadConfigurationForSystem(SYSTEM_NAMES system) {
		if (!system.isFullSystem) {
			return;
		}
		
		APV<? extends APVPlugin> originalAPV = systemMap.get(system);
		originalAPV.unregisterHandler();
		
		APV<APVPlugin> reloadedAPV = new APV<APVPlugin>(this, system);
		systemMap.put(system, reloadedAPV);
		setupSystem(reloadedAPV);
		register(system, reloadedAPV.getSwitchCommand(), originalAPV.getCommand());
		assignSystems();
	}
	
	@SuppressWarnings("unchecked")
	protected void assignSystems() {
		backDrops = (APV<BackDropSystem>)systemMap.get(SYSTEM_NAMES.BACKDROPS);
		backgrounds = (APV<ShapeSystem>) systemMap.get(SYSTEM_NAMES.BACKGROUNDS);
		colors = (APV<ColorSystem>) systemMap.get(SYSTEM_NAMES.COLORS);
		controls = (APV<ControlSystem>) systemMap.get(SYSTEM_NAMES.CONTROLS);
		filters = (APV<Filter>) systemMap.get(SYSTEM_NAMES.FILTERS);
		foregrounds = (APV<ShapeSystem>) systemMap.get(SYSTEM_NAMES.FOREGROUNDS);
		likedScenes = (APV<LikedScene>) systemMap.get(SYSTEM_NAMES.LIKED_SCENES);
		locations = (APV<LocationSystem>) systemMap.get(SYSTEM_NAMES.LOCATIONS);
		messages = (APV<MessageSystem>) systemMap.get(SYSTEM_NAMES.MESSAGES);
		scenes = (APV<Scene>) systemMap.get(SYSTEM_NAMES.SCENES);
		shaders = (APV<Shader>) systemMap.get(SYSTEM_NAMES.SHADERS);
		transitions = (APV<TransitionSystem>) systemMap.get(SYSTEM_NAMES.TRANSITIONS);
	}
	
	@SuppressWarnings("unchecked")
	protected void configureSwitches() {
		List<Switch> ss = (List<Switch>)configurator.loadAVPPlugins(SYSTEM_NAMES.SWITCHES);
		ss.forEach(s -> switches.put(s.name, s));
		
		helpSwitch = switches.get(SWITCH_NAMES.HELP.name);
		showSettingsSwitch = switches.get(SWITCH_NAMES.SHOW_SETTINGS.name);
		frameStroberSwitch = switches.get(SWITCH_NAMES.FRAME_STROBER.name);
		continuousCaptureSwitch = switches.get(SWITCH_NAMES.CONTINUOUS_CAPTURE.name);
		scrambleModeSwitch = switches.get(SWITCH_NAMES.SCRAMBLE_MODE.name);
		videoGameSwitch = switches.get(SWITCH_NAMES.VIDEO_GAME.name);
		debugPulseSwitch = switches.get(SWITCH_NAMES.DEBUG_PULSE.name);
	}
	
	protected void resetSwitches() {
		configurator.loadAVPPlugins(SYSTEM_NAMES.SWITCHES).forEach(cs -> {
			Switch orig = (Switch)cs;
			Switch s = switches.get(orig.name);
			s.setState(orig.state);
		});
	}
	
	protected void initEvents() {
		eventMap.put(EventTypes.SETUP, new CoreEvent(this, EventTypes.SETUP));
		eventMap.put(EventTypes.DRAW, new CoreEvent(this, EventTypes.DRAW));
		eventMap.put(EventTypes.SCENE_COMPLETE, new CoreEvent(this, EventTypes.SCENE_COMPLETE));
		eventMap.put(EventTypes.STROBE, new CoreEvent(this, EventTypes.STROBE));
		eventMap.put(EventTypes.COMMAND_INVOKED, new CommandInvokedEvent(this));
		eventMap.put(EventTypes.SPARK, new DrawShapeEvent(this, EventTypes.SPARK));
		eventMap.put(EventTypes.CARNIVAL, new DrawShapeEvent(this, EventTypes.CARNIVAL));
		eventMap.put(EventTypes.STAR, new DrawShapeEvent(this, EventTypes.STAR));
		eventMap.put(EventTypes.RANDOM_MESSAGE, new DrawShapeEvent(this, EventTypes.RANDOM_MESSAGE));
		eventMap.put(EventTypes.TWIRL, new DrawShapeEvent(this, EventTypes.TWIRL));
		eventMap.put(EventTypes.MARQUEE, new DrawShapeEvent(this, EventTypes.MARQUEE));
		eventMap.put(EventTypes.EARTHQUAKE, new DrawShapeEvent(this, EventTypes.EARTHQUAKE));
		eventMap.put(EventTypes.APV_CHANGE, new APVChangeEvent(this));
		eventMap.put(EventTypes.LOCATION, new CoreEvent(this, EventTypes.LOCATION));
	}
	
	public String getConfig() {
		StringBuffer buffer = new StringBuffer(System.lineSeparator());
		Config rootConfig = getConfigurator().getRootConfig();
		
		//Constants
		addConstant(buffer, FLAGS.CONTROL_MODE, getCurrentControlMode().name());
		addConstant(buffer, FLAGS.FULL_SCREEN, String.valueOf(getConfigBoolean(FLAGS.FULL_SCREEN.apvName())));
		addConstant(buffer, FLAGS.SCRAMBLE_SYSTEMS, String.valueOf(getConfigBoolean(FLAGS.SCRAMBLE_SYSTEMS.apvName())));
		addConstant(buffer, FLAGS.SCREEN_WIDTH, String.valueOf(width));
		addConstant(buffer, FLAGS.SCREEN_HEIGHT, String.valueOf(height));	
		addConstant(buffer, FLAGS.MONITORING_ENABLED, String.valueOf(isMonitoringEnabled()));
		addConstant(buffer, FLAGS.QUIET_WINDOW_SIZE, String.valueOf(rootConfig.getInt(FLAGS.QUIET_WINDOW_SIZE.apvName())));
		addConstant(buffer, FLAGS.AUTO_ADD_SOBLE, String.valueOf(isAutoAddSobleEnabled()));
		addConstant(buffer, FLAGS.DEBUG_SYS_MESSAGES, String.valueOf(isDebugSystemMessages()));
		addConstant(buffer, FLAGS.DEFAULT_SHAPE_SYSTEM_ALPHA, String.valueOf(getDefaultShapeSystemAlpha()));
		addConstant(buffer, FLAGS.SET_LIST, String.valueOf(isSetList()));
		
		return buffer.toString();
	}
	
	private void addConstant(StringBuffer buffer, FLAGS flag, String value) {
		buffer.append("apv." + flag.name + " = " + value);
		buffer.append(System.lineSeparator());
	}
}
