package com.arranger.apv;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.arranger.apv.util.VideoGameDisplay;
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
	//public static final int ASCII_SPACE = 32;

	protected APV<ShapeSystem> backgrounds;
	protected APV<BackDropSystem> backDrops;
	protected APV<ColorSystem> colors;
	protected APV<ControlSystem> controls;	
	protected APV<Filter> filters; 
	protected APV<ShapeSystem> foregrounds; 
	protected APV<Scene> likedScenes;
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
	protected VideoGameDisplay videoGameDisplay;
	protected FileHelper fileHelper;
	protected SceneList sceneList;
	protected FontHelper fontHelper;
	protected SplineHelper splineHelper;
	protected Map<String, Switch> switches;
	protected Map<EVENT_TYPES, APVEvent<? extends EventHandler>> eventMap;
	
	//Stateful data
	private Scene currentScene;
	private CONTROL_MODES currentControlMode; 
	private boolean scrambleMode = false;	
	private int lastScrambleFrame = 0;
	
	
	//Switches for runtime
	private Switch helpSwitch,
					showSettingsSwitch,
					frameStroberSwitch,
					videoCaptureSwitch,
					videoGameSwitch,
					scrambleModeSwitch;

	
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
	
	public FileHelper getFileHelper() {
		return fileHelper;
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
	
	public void setDefaultScene() {
		Scene defaultScene = scenes.getList().stream().filter(e -> e.isNormal()).findFirst().get();
		setNextScene(defaultScene);
	}
	
	public void setNextScene(Scene scene) {
		List<Scene> list = scenes.getList();
		for (int index = 0; index < list.size(); index++) {
			if (scene.equals(list.get(index))) {
				scenes.setIndex(index);
				return;
			}
		}
		throw new RuntimeException("Unable to find the nextScene");
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}

	public ColorSystem getColor() {
		return colors.getPlugin();
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
		likedScenes.getList().add(new Scene(currentScene));
		sendMessage(new String[] {"Liked :)"});
	}
	
	public void disLikeCurrentScene() {
		likedScenes.getList().remove(currentScene);
		sendMessage(new String[] {"Disliked :("});
	}
	
	public List<Scene> getLikedScenes() {
		return likedScenes.getList();
	}	
	
	public List<Scene> getScenes() {
		return scenes.getList();
	}
	
	public void addSettingsMessage(String msg) {
		settingsDisplay.addSettingsMessage(msg);
	}

	public Collection<Switch> getSwitches() {
		return switches.values();
	}
	
	public Switch getSwitch(String name) {
		return switches.get(name);
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
		return String.format("(%1d,%2d,%3d)", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public String format(Color c, boolean addQuote) {
		if (!addQuote) {
			return format(c);
		} else {
			return "\"" + String.format("(%1d,%2d,%3d)", c.getRed(), c.getGreen(), c.getBlue()) + "\"";
		}
	}
	
	public float lerpAlpha(float pct) {
		return PApplet.lerp(0, MAX_ALPHA, pct);
	}
	
	public void setup() {
		agent = new APVAgent(this);
		audio = new Audio(this, BUFFER_SIZE);
		commandSystem = new CommandSystem(this);
		fileHelper = new FileHelper(this);
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
		videoGameDisplay = new VideoGameDisplay(this);
		
		backDrops = new APV<BackDropSystem>(this, "backDrops");
		backgrounds = new APV<ShapeSystem>(this, "backgrounds");
		colors = new APV<ColorSystem>(this, "colors");
		controls = new APV<ControlSystem>(this, "controls");
		filters = new APV<Filter>(this, "filters");
		foregrounds = new APV<ShapeSystem>(this, "foregrounds");
		likedScenes = new APV<Scene>(this, "likedScenes");
		locations = new APV<LocationSystem>(this, "locations");
		messages = new APV<MessageSystem>(this, "messages");	
		scenes = new APV<Scene>(this, "scenes", false);
		transitions = new APV<TransitionSystem>(this, "transitions");
		
		//currentControlMode
		initControlMode();

		setupSystems(backDrops);
		setupSystems(backgrounds);
		setupSystems(foregrounds);
		setupSystems(likedScenes);
		setupSystems(messages);
		setupSystems(scenes);
		setupSystems(transitions);
		
		initializeCommands();
		
		//processing hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
		
		getSetupEvent().fire();
	}

	public void doScreenCapture() {
		String fileName = String.format("apv%08d.png", getFrameCount());
		fileHelper.getFullPath(fileName);
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
			System.out.println(t);
			t.printStackTrace();
		}
	}
	
	public void drawSystem(ShapeSystem s, String debugName) {
		pushStyle();
		pushMatrix();
		settingsDisplay.debugSystem(s, debugName);
		s.draw();
		popMatrix();
		popStyle();
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
	
				currentScene.setSystems(backDrop, bgSys, fgSys, filter);
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
			videoGameDisplay.showStats();
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
		
		if (videoCaptureSwitch.isEnabled()) {
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
		registerSimpleSwitch(helpSwitch, Command.SWITCH_HELP);
		registerSimpleSwitch(showSettingsSwitch, Command.SWITCH_SETTINGS);
		registerSimpleSwitch(likedScenes.getSwitch(), Command.SWITCH_LIKED_SCENES);
		registerSimpleSwitch(agent.getSwitch(), Command.SWITCH_AGENT);
		registerSimpleSwitch(pulseListener.getSwitch(), Command.SWITCH_PULSE_LISTENER);
		registerSimpleSwitch(frameStroberSwitch, Command.SWITCH_FRAME_STROBER);
		registerSimpleSwitch(videoCaptureSwitch, Command.SWITCH_CONTINUOUS_CAPTURE);
		registerSimpleSwitch(videoGameSwitch, Command.SWITCH_VIDEOGAME);
		
		foregrounds.registerSwitchCommand(Command.SWITCH_FOREGROUNDS);
		backgrounds.registerSwitchCommand(Command.SWITCH_BACKGROUNDS);
		backDrops.registerSwitchCommand(Command.SWITCH_BACKDROPS);
		filters.registerSwitchCommand(Command.SWITCH_FILTERS);
		messages.registerSwitchCommand(Command.SWITCH_MESSAGES);
		transitions.registerSwitchCommand(Command.SWITCH_TRANSITIONS);

		foregrounds.registerHandler(Command.CYCLE_FOREGROUNDS);
		backgrounds.registerHandler(Command.CYCLE_BACKGROUNDS);
		backDrops.registerHandler(Command.CYCLE_BACKDROPS);
		locations.registerHandler(Command.CYCLE_LOCATIONS);
		filters.registerHandler(Command.CYCLE_FILTERS);
		colors.registerHandler(Command.CYCLE_COLORS);
		transitions.registerHandler(Command.CYCLE_TRANSITIONS);
		messages.registerHandler(Command.CYCLE_MESSAGES);
		
		likedScenes.registerHandler(Command.RIGHT_ARROW, e -> likedScenes.increment());
		likedScenes.registerHandler(Command.LEFT_ARROW, e -> likedScenes.decrement());
		
		CommandSystem cs = commandSystem;
		cs.registerHandler(Command.CYCLE_CONTROL_MODE, e -> cycleMode(!e.isShiftDown())); 
		cs.registerHandler(Command.SCRAMBLE, e -> scramble());
		cs.registerHandler(Command.WINDOWS, e -> {new APVCommandFrame(this);});
		cs.registerHandler(Command.PANIC, e -> panic());
		cs.registerHandler(Command.MANUAL, e -> manual());	
		cs.registerHandler(Command.PERF_MONITOR, e -> perfMonitor.dumpMonitorInfo(e.isShiftDown()));
		cs.registerHandler(Command.SCREEN_SHOT, e -> doScreenCapture());
		cs.registerHandler(Command.SAVE_CONFIGURATION, event -> configurator.saveCurrentConfig());
		cs.registerHandler(Command.UP_ARROW, e -> likeCurrentScene());
		cs.registerHandler(Command.DOWN_ARROW, e -> disLikeCurrentScene());
		cs.registerHandler(Command.TRANSITION_FRAMES_INC, e -> {transitions.forEach(t -> {t.incrementTransitionFrames();});});
		cs.registerHandler(Command.TRANSITION_FRAMES_DEC, e -> {transitions.forEach(t -> {t.decrementTransitionFrames();});});
	}

	protected void registerSimpleSwitch(Switch s, Command command) {
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

	protected void setupSystems(APV<? extends ShapeSystem> apv) {
		for (ShapeSystem system : apv.getList()) {
			system.setup();
		}
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
	
	@SuppressWarnings("unchecked")
	protected void configureSwitches() {
		List<Switch> ss = (List<Switch>)configurator.loadAVPPlugins("switches");
		
		switches = new HashMap<String, Switch>(ss.size());
		for (Iterator<Switch> it = ss.iterator(); it.hasNext();) {
			Switch nextSwitch = it.next();
			switches.put(nextSwitch.name, nextSwitch);
		}
		
		helpSwitch = switches.get("Help");
		showSettingsSwitch = switches.get("ShowSettings");
		frameStroberSwitch = switches.get("FrameStrober");
		videoCaptureSwitch = switches.get("VideoCapture");
		scrambleModeSwitch = switches.get("Scramble");
		videoGameSwitch = switches.get("VideoGame");
	}
	
	@SuppressWarnings("unchecked")
	protected void resetSwitches() {
		List<Switch> configSwitches = (List<Switch>)configurator.loadAVPPlugins("switches");
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
