package com.arranger.apv;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.ControlSystem.CONTROL_MODES;
import com.arranger.apv.Switch.STATE;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.event.CommandInvokedEvent;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.event.DrawShapeEvent;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.gui.APVCommandFrame;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.scene.LikedScene;
import com.arranger.apv.util.APVAgent;
import com.arranger.apv.util.APVPulseListener;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FontHelper;
import com.arranger.apv.util.FrameStrober;
import com.arranger.apv.util.Gravity;
import com.arranger.apv.util.HelpDisplay;
import com.arranger.apv.util.LoggingConfig;
import com.arranger.apv.util.Oscillator;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.PerformanceMonitor;
import com.arranger.apv.util.SceneList;
import com.arranger.apv.util.SettingsDisplay;
import com.arranger.apv.util.SplineHelper;
import com.arranger.apv.util.VersionInfo;
import com.arranger.apv.util.VideoGameHelper;
import com.typesafe.config.Config;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	public static final int NUMBER_PARTICLES = 100;
	public static final String RENDERER = P3D;
	public static final int BUFFER_SIZE = 512;
	public static final int MAX_ALPHA = 255;
	public static final int SCRAMBLE_QUIET_WINDOW = 120; //2 to 4 seconds
	public static final char SPACE_BAR_KEY_CODE = ' ';
	private static final char [] HOT_KEYS = {'!', '@', '#', '$', '%', '^', '&', '*'}; //Shift + (1 through 8)

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
	protected APV<TransitionSystem> transitions;
	
	//Useful helper classes
	protected APVAgent agent;
	protected APVPulseListener pulseListener;
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
	protected SceneList sceneList;
	protected FontHelper fontHelper;
	protected SplineHelper splineHelper;
	protected Map<String, Switch> switches;
	protected Map<Command, HotKey> hotKeys;
	protected Map<EVENT_TYPES, APVEvent<? extends EventHandler>> eventMap;
	protected Map<SYSTEM_NAMES, APV<? extends APVPlugin>> systemMap;
	
	//Stateful data
	private Scene currentScene;
	private CONTROL_MODES currentControlMode; 
	private boolean scrambleMode = false;	
	private int lastScrambleFrame = 0;
	
	
	//Switches for runtime
	private Switch helpSwitch,
					showSettingsSwitch,
					frameStroberSwitch,
					continuousCaptureSwitch,
					videoGameSwitch,
					scrambleModeSwitch;

	public enum SWITCH_NAMES {
		
		HELP("Help"),
		SHOW_SETTINGS("ShowSettings"),
		FRAME_STROBER("FrameStrober"),
		CONTINUOUS_CAPTURE("ContinuousCapture"),
		SCRAMBLE_MODE("Scramble"),
		VIDEO_GAME("VideoGame");
		
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
		MESSAGES("messages"),
		PULSELISTENERS("pulseListeners", false),
		SCENES("scenes"),
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
	
	public FontHelper getFontHelper() {
		return fontHelper;
	}

	public SceneList getSceneList() {
		return sceneList;
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
	
	public APVAgent getAgent() {
		return agent;
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
	
	public CoreEvent getSetupEvent() {
		return (CoreEvent)eventMap.get(EVENT_TYPES.SETUP);
	}
	
	public CoreEvent getDrawEvent() {
		return (CoreEvent)eventMap.get(EVENT_TYPES.DRAW);
	}
	
	public CoreEvent getSceneCompleteEvent() {
		return (CoreEvent)eventMap.get(EVENT_TYPES.SCENE_COMPLETE);
	}
	
	public CoreEvent getStrobeEvent() {
		return (CoreEvent)eventMap.get(EVENT_TYPES.STROBE);
	}
	
	public DrawShapeEvent getSparkEvent() {
		return (DrawShapeEvent)eventMap.get(EVENT_TYPES.SPARK);
	}
	
	public DrawShapeEvent getCarnivalEvent() {
		return (DrawShapeEvent)eventMap.get(EVENT_TYPES.CARNIVAL);
	}
	
	public CommandInvokedEvent getCommandInvokedEvent() {
		return (CommandInvokedEvent)eventMap.get(EVENT_TYPES.COMMAND_INVOKED);
	}
	
	public void activateNextPlugin(String pluginName, SYSTEM_NAMES systemName) {
		APV<? extends APVPlugin> apv = systemMap.get(systemName);
		APVPlugin plugin = apv.getList().stream().filter(p -> p.getName().equals(pluginName)).findFirst().get();
		apv.setNextPlugin(plugin);
		apv.setEnabled(true);
	}
	
	public void setDefaultScene() {
		Scene defaultScene = scenes.getList().stream().filter(e -> e.isNormal()).findFirst().get();
		setNextScene(defaultScene);
	}
	
	public void setNextScene(Scene scene) {
		scenes.setNextPlugin(scene);
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}

	public ColorSystem getColor() {
		return colors.getPlugin();
	}
	
	public void setNextColor(ColorSystem cs) {
		colors.setNextPlugin(cs);
	}
	
	public void setNextLocation(LocationSystem ls) {
		locations.setNextPlugin(ls);
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
	
	public Map<Command, HotKey> getHotKeys() {
		return hotKeys;
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
	
	public float oscillate(float low, float high, float oscSpeed) {
		return oscillator.oscillate(low, high, oscSpeed);
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
		pulseListener = new APVPulseListener(this);
		sceneList = new SceneList(this);
		settingsDisplay = new SettingsDisplay(this);
		splineHelper = new SplineHelper(this);
		versionInfo = new VersionInfo(this);
		videoGameHelper = new VideoGameHelper(this);
		
		systemMap = new HashMap<SYSTEM_NAMES, APV<? extends APVPlugin>>();
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
		systemMap.put(SYSTEM_NAMES.TRANSITIONS, new APV<TransitionSystem>(this, SYSTEM_NAMES.TRANSITIONS));
		
		assignSystems();
		initControlMode();
		configureHotKeys();
		setupSystems();
		initializeCommands();
		
		//processing hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
		
		getSetupEvent().fire();
	}

	public void doScreenCapture() {
		String fileName = String.format("apv%08d.png", getFrameCount());
		new FileHelper(this).getFullPath(fileName);
		logger.info("Saving image: " + fileName);
		PImage pImage = get();
		pImage.save(fileName);
		
		sendMessage(new String[] {fileName});
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
	public void panic() {
		resetSwitches();
		initControlMode();
		commandSystem.panic();
	}
	
	/**
	 * Goes to instant manual mode and disables agents
	 */
	public void manual() {
		currentControlMode = CONTROL_MODES.MANUAL;
		getAgent().getSwitch().setState(STATE.DISABLED);
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
		try {
			_draw(); //Processing has an unusual exception handler
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	public void drawSystem(ShapeSystem s, String debugName) {
		drawSystem(s, debugName, true);
	}
	
	public void drawSystem(ShapeSystem s, String debugName, boolean safe) {
		if (safe) {
			pushStyle();
			pushMatrix();
		}
		settingsDisplay.debugSystem(s, debugName);
		s.draw();
		if (safe) {
			popMatrix();
			popStyle();
		}
	}
	
	public void reloadConfiguration() {
		configurator.reload();
		hotKeys.forEach((k, v) -> v.unregisterHotKey());
		
		Arrays.asList(SYSTEM_NAMES.values()).forEach(s -> reloadConfigurationForSystem(s));

		configureHotKeys();
		hotKeys.forEach((k, v) -> v.registerHotKey(k));
		panic();
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
			if (currentScene.isNormal()) {
				BackDropSystem backDrop = backDrops.getPlugin(true);
				ShapeSystem bgSys = backgrounds.getPlugin(true);
				Filter filter = filters.getPlugin(true);
				ShapeSystem fgSys = foregrounds.getPlugin(true);
	
				currentScene.setSystems(backDrop, bgSys, fgSys, filter, getColor(), getLocations().getPlugin());
			} else {
				//using a "non-normal" scene.  See if it is brand new?  If so, start a transition
				if (currentScene.isNew()) {
					transition = prepareTransition(true);
				}
			}
		}
		
		drawSystem(currentScene, "scene");
		
		perfMonitor.doMonitorCheck(currentScene);
		
		if (transition != null) {
			drawSystem(transition, "transition");
		}
		
		if (messages.isEnabled()) {
			drawSystem(getMessage(), "message");
		}

		if (videoGameSwitch.isEnabled()) {
			videoGameHelper.showStats();
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
		
		if (continuousCaptureSwitch.isEnabled()) {
			doScreenCapture();
		}
		
		getDrawEvent().fire();
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
			filters.scramble(true);
			messages.scramble(true);
		}

		locations.scramble(false);
		colors.scramble(false);
		
		//send out a cool message about the new system
		if (messages.isEnabled()) {
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
			
			sendMessage(msgs.toArray(new String[msgs.size()]));
		}
		
		//reset the flag
		scrambleMode = false;
	}

	protected void initControlMode() {
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(configurator.getRootConfig().getString("apv.controlMode"));
	}
	
	protected void initializeCommands() {
		registerSwitch(helpSwitch, Command.SWITCH_HELP);
		registerSwitch(showSettingsSwitch, Command.SWITCH_SETTINGS);
		registerSwitch(likedScenes.getSwitch(), Command.SWITCH_LIKED_SCENES);
		registerSwitch(agent.getSwitch(), Command.SWITCH_AGENT);
		registerSwitch(pulseListener.getSwitch(), Command.SWITCH_PULSE_LISTENER);
		registerSwitch(frameStroberSwitch, Command.SWITCH_FRAME_STROBER);
		registerSwitch(continuousCaptureSwitch, Command.SWITCH_CONTINUOUS_CAPTURE);
		registerSwitch(videoGameSwitch, Command.SWITCH_VIDEOGAME);
		
		register(SYSTEM_NAMES.FOREGROUNDS, Command.SWITCH_FOREGROUNDS, Command.CYCLE_FOREGROUNDS);
		register(SYSTEM_NAMES.BACKGROUNDS, Command.SWITCH_BACKGROUNDS, Command.CYCLE_BACKGROUNDS);
		register(SYSTEM_NAMES.BACKDROPS, Command.SWITCH_BACKDROPS, Command.CYCLE_BACKDROPS);
		register(SYSTEM_NAMES.COLORS, null, Command.CYCLE_COLORS);
		register(SYSTEM_NAMES.FILTERS, Command.SWITCH_FILTERS, Command.CYCLE_FILTERS);
		register(SYSTEM_NAMES.LOCATIONS, null, Command.CYCLE_LOCATIONS);
		register(SYSTEM_NAMES.MESSAGES, Command.SWITCH_MESSAGES, Command.CYCLE_MESSAGES);
		register(SYSTEM_NAMES.TRANSITIONS, Command.SWITCH_TRANSITIONS, Command.CYCLE_TRANSITIONS);
		
		likedScenes.registerHandler(Command.RIGHT_ARROW, e -> likedScenes.increment());
		likedScenes.registerHandler(Command.LEFT_ARROW, e -> likedScenes.decrement());
		
		hotKeys.forEach((k, v) -> v.registerHotKey(k));
		
		CommandSystem cs = commandSystem;
		cs.registerHandler(Command.CYCLE_CONTROL_MODE, e -> cycleMode(!e.isShiftDown())); 
		cs.registerHandler(Command.SCRAMBLE, e -> scramble());
		cs.registerHandler(Command.WINDOWS, e -> {new APVCommandFrame(this);});
		cs.registerHandler(Command.PANIC, e -> panic());
		cs.registerHandler(Command.MANUAL, e -> manual());	
		cs.registerHandler(Command.PERF_MONITOR, e -> perfMonitor.dumpMonitorInfo(e.isShiftDown()));
		cs.registerHandler(Command.SCREEN_SHOT, e -> doScreenCapture());
		cs.registerHandler(Command.SAVE_CONFIGURATION, event -> configurator.saveCurrentConfig());
		cs.registerHandler(Command.RELOAD_CONFIGURATION, event -> reloadConfiguration());
		cs.registerHandler(Command.UP_ARROW, e -> likeCurrentScene());
		cs.registerHandler(Command.DOWN_ARROW, e -> disLikeCurrentScene());
		cs.registerHandler(Command.TRANSITION_FRAMES_INC, e -> {transitions.forEach(t -> {t.incrementTransitionFrames();});});
		cs.registerHandler(Command.TRANSITION_FRAMES_DEC, e -> {transitions.forEach(t -> {t.decrementTransitionFrames();});});
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
		transitions = (APV<TransitionSystem>) systemMap.get(SYSTEM_NAMES.TRANSITIONS);
	}
	
	@SuppressWarnings("unchecked")
	protected void configureHotKeys() {
		hotKeys = new HashMap<Command, HotKey>();
		
		int index = 0; //assign an index and add to map
		List<HotKey> hks = (List<HotKey>)configurator.loadAVPPlugins(SYSTEM_NAMES.HOTKEYS, false);
		for (Iterator<HotKey> it = hks.iterator(); it.hasNext();) {
			Command cmd = Command.getCommand(HOT_KEYS[index++]);
			hotKeys.put(cmd, it.next());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void configureSwitches() {
		switches = new HashMap<String, Switch>();

		List<Switch> ss = (List<Switch>)configurator.loadAVPPlugins(SYSTEM_NAMES.SWITCHES);
		ss.forEach(s -> switches.put(s.name, s));
		
		helpSwitch = switches.get(SWITCH_NAMES.HELP.name);
		showSettingsSwitch = switches.get(SWITCH_NAMES.SHOW_SETTINGS.name);
		frameStroberSwitch = switches.get(SWITCH_NAMES.FRAME_STROBER.name);
		continuousCaptureSwitch = switches.get(SWITCH_NAMES.CONTINUOUS_CAPTURE.name);
		scrambleModeSwitch = switches.get(SWITCH_NAMES.SCRAMBLE_MODE.name);
		videoGameSwitch = switches.get(SWITCH_NAMES.VIDEO_GAME.name);
	}
	
	@SuppressWarnings("unchecked")
	protected void resetSwitches() {
		List<Switch> configSwitches = (List<Switch>)configurator.loadAVPPlugins(SYSTEM_NAMES.SWITCHES);
		configSwitches.forEach(cs -> {
			Switch s = switches.get(cs.name);
			s.setState(cs.state);
		});
	}
	
	protected enum EVENT_TYPES {
		SETUP, DRAW, SPARK, CARNIVAL, STROBE, SCENE_COMPLETE, COMMAND_INVOKED,
	}
	
	protected void initEvents() {
		eventMap = new HashMap<EVENT_TYPES, APVEvent<? extends EventHandler>>();
		eventMap.put(EVENT_TYPES.SETUP, new CoreEvent(this));
		eventMap.put(EVENT_TYPES.DRAW, new CoreEvent(this));
		eventMap.put(EVENT_TYPES.SCENE_COMPLETE, new CoreEvent(this));
		eventMap.put(EVENT_TYPES.STROBE, new CoreEvent(this));
		eventMap.put(EVENT_TYPES.COMMAND_INVOKED, new CommandInvokedEvent(this));
		eventMap.put(EVENT_TYPES.SPARK, new DrawShapeEvent(this));
		eventMap.put(EVENT_TYPES.CARNIVAL, new DrawShapeEvent(this));
	}
	
	public String getConfig() {
		StringBuffer buffer = new StringBuffer(System.lineSeparator());
		Config rootConfig = getConfigurator().getRootConfig();
		
		//Constants
		addConstant(buffer, "controlMode", getCurrentControlMode().name());
		addConstant(buffer, "fullScreen", String.valueOf(rootConfig.getBoolean("apv.fullScreen")));
		addConstant(buffer, "scrambleSystems", String.valueOf(rootConfig.getBoolean("apv.scrambleSystems")));
		addConstant(buffer, "screen.width", String.valueOf(width));
		addConstant(buffer, "screen.height", String.valueOf(height));		
		
		return buffer.toString();
	}
	
	private void addConstant(StringBuffer buffer, String name, String value) {
		buffer.append("apv." + name + " = " + value);
		buffer.append(System.lineSeparator());
	}
}
