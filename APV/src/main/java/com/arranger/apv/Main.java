package com.arranger.apv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.arranger.apv.ControlSystem.CONTROL_MODES;
import com.arranger.apv.Switch.STATE;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.util.APVPulseListener;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FrameStrober;
import com.arranger.apv.util.Gravity;
import com.arranger.apv.util.HelpDisplay;
import com.arranger.apv.util.LoggingConfig;
import com.arranger.apv.util.PerformanceMonitor;
import com.arranger.apv.util.Oscillator;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.SceneList;
import com.arranger.apv.util.SettingsDisplay;
import com.arranger.apv.util.SplineInterpolator;
import com.arranger.apv.util.VersionInfo;
import com.typesafe.config.Config;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;

public class Main extends PApplet {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	public static final int NUMBER_PARTICLES = 1000;
	public static final String RENDERER = P3D;
	public static final int BUFFER_SIZE = 512;
	public static final int MAX_ALPHA = 255;
	public static final int SCRAMBLE_QUIET_WINDOW = 120; //2 to 4 seconds
	public static final char SPACE_BAR_KEY_CODE = ' ';

	protected List<ShapeSystem> foregrounds;
	protected int foregroundIndex = 0;
	
	protected List<ShapeSystem> backgrounds;
	protected int backgroundIndex = 0;
	
	protected List<BackDropSystem> backDrops;
	protected int backDropIndex = 0;

	protected List<LocationSystem> locations; 
	protected int locationIndex = 0;
	
	protected List<ColorSystem> colors; 
	protected int colorIndex = 0;
	
	protected List<TransitionSystem> transitions;
	protected int transitionIndex = 0;
	
	protected List<MessageSystem> messages;
	protected int messageIndex = 0;
	
	protected List<Filter> filters; 
	protected int filterIndex = 0;
	
	protected List<Scene> scenes;
	protected int sceneIndex;
	
	protected List<Scene> likedScenes;
	protected int likedSceneIndex = 0;
	
	protected Map<String, Switch> switches;
	protected List<ControlSystem> controls;
	protected CONTROL_MODES currentControlMode;
	protected List<APVPlugin> pulseListeners;
	
	//Useful helper classes
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
	protected APVPulseListener pulseListener;
	protected Particles particles;
	protected VersionInfo versionInfo;
	protected FileHelper fileHelper;
	protected SceneList sceneList;

	//Internal data
	private boolean scrambleMode = false;	//this is a flag to signal to the TransitionSystem for #onDrawStart
	private int lastScrambleFrame = 0;
	
	
	//Switches for runtime
	private Switch foreGroundSwitch, 
					backGroundSwitch, 
					backDropSwitch, 
					filtersSwitch, 
					transitionSwitch,
					messagesSwitch,
					helpSwitch,
					showSettingsSwitch,
					pulseListenerSwitch,
					likedScenesSwitch, 
					frameStroberSwitch,
					videoCaptureSwitch,
					scrambleModeSwitch;


	private Scene currentScene;

	private List<SetupListener> setupListeners = new ArrayList<SetupListener>();
	public void registerSetupListener(SetupListener sl) {
		setupListeners.add(sl);
	}
	
	private List<DrawListener> drawListeners = new ArrayList<DrawListener>();
	public void registerDrawListener(DrawListener dl) {
		drawListeners.add(dl);
	}
	
	@FunctionalInterface
	public static interface SetupListener {
		void onSetupComplete();
	}
	
	@FunctionalInterface
	public static interface DrawListener {
		void onDrawComplete();
	}
	
	public static void main(String[] args) {
		PApplet.main(Main.class, new String[0]);
	}

	public void settings() {
		//can't load the settings until i load the configurator
		//don't want to load the configurator until i load the logging
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
	
	public Particles getParticles() {
		return particles;
	}
	
	public SettingsDisplay getSettingsDisplay() {
		return settingsDisplay;
	}
	
	public CONTROL_MODES getCurrentControlMode() {
		return currentControlMode;
	}
	
	public void setNextScene(Scene scene) {
		for (int index = 0; index < scenes.size(); index++) {
			if (scene.equals(scenes.get(index))) {
				sceneIndex = index;
				return;
			}
		}
		throw new RuntimeException("Unable to find the nextScene");
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}

	public List<APVPlugin> getPulseListeners() {
		return pulseListeners;
	}
	
	public ColorSystem getColor() {
		return (ColorSystem)getPlugin(colors, colorIndex);
	}
	
	public ShapeSystem getForeground() {
		return (ShapeSystem)getPlugin(foregrounds, foregroundIndex);
	}

	public ShapeSystem getBackground() {
		return (ShapeSystem)getPlugin(backgrounds, backgroundIndex);
	}

	public BackDropSystem getBackDrop() {
		return (BackDropSystem)getPlugin(backDrops, backDropIndex);
	}
	
	public TransitionSystem getTransition() {
		return (TransitionSystem)getPlugin(transitions, transitionIndex);
	}
	
	public MessageSystem getMessage() {
		return (MessageSystem)getPlugin(messages, messageIndex);
	}
	
	public LocationSystem getLocation() {
		LocationSystem ls = null;
		ControlSystem cs = getControl();
		while (ls == null) {
			ls = (LocationSystem)getPlugin(locations, locationIndex);
			if (!cs.allowsMouseLocation() && ls instanceof MouseLocationSystem) {
				locationIndex++;
				ls = null;
			}
		}
		return ls;
	}
	
	public ControlSystem getControl() {
		//there is probably a more efficient way to do this 
		for (ControlSystem cs : controls) {
			if (cs.getControlMode() == currentControlMode) {
				return cs;
			}
		}

		throw new RuntimeException("Unable to find current control system: " + currentControlMode);
	}
	
	public void likeCurrentScene() {
		likedScenes.add(new Scene(currentScene));
		sendMessage(new String[] {"Liked :)"});
	}
	
	public void disLikeCurrentScene() {
		likedScenes.remove(currentScene);
		sendMessage(new String[] {"Disliked :("});
	}
	
	public List<Scene> getLikedScenes() {
		return likedScenes;
	}	
	
	public List<Scene> getScenes() {
		return scenes;
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
		SplineInterpolator si = new SplineInterpolator(start, end, start1, end1);
		return (float)si.interpolate(value);
	}
	
	public float oscillate(float low, float high, float oscSpeed) {
		return oscillator.oscillate(low, high, oscSpeed);
	}

	public String format(Color c) {
		return String.format("(%1s,%2s,%3s)", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	@SuppressWarnings("unchecked")
	public void setup() {
		versionInfo = new VersionInfo(this);
		fileHelper = new FileHelper(this);
		
		commandSystem = new CommandSystem(this);
		initializeCommands();
		
		oscillator = new Oscillator(this);
		pulseListener = new APVPulseListener(this);
		particles = new Particles(this);
		settingsDisplay = new SettingsDisplay(this);
		helpDisplay = new HelpDisplay(this);
		gravity = new Gravity(this);
		audio = new Audio(this, BUFFER_SIZE);
		perfMonitor = new PerformanceMonitor(this);
		frameStrober = new FrameStrober(this);
		sceneList = new SceneList(this);
		
		locations = (List<LocationSystem>)configurator.loadAVPPlugins("locations");
		colors = (List<ColorSystem>)configurator.loadAVPPlugins("colors");
		controls = (List<ControlSystem>)configurator.loadAVPPlugins("controls");
		backgrounds = (List<ShapeSystem>)configurator.loadAVPPlugins("backgrounds");
		backDrops = (List<BackDropSystem>)configurator.loadAVPPlugins("backDrops");
		foregrounds = (List<ShapeSystem>)configurator.loadAVPPlugins("foregrounds");
		filters = (List<Filter>)configurator.loadAVPPlugins("filters");
		transitions = (List<TransitionSystem>)configurator.loadAVPPlugins("transitions");
		messages = (List<MessageSystem>)configurator.loadAVPPlugins("messages");	
		pulseListeners = (List<APVPlugin>)configurator.loadAVPPlugins("pulse-listeners");
		scenes = (List<Scene>)configurator.loadAVPPlugins("scenes", false);
		likedScenes = (List<Scene>)configurator.loadAVPPlugins("liked-scenes");
		
		//currentControlMode
		initControlMode();

		setupSystems(foregrounds);
		setupSystems(backgrounds);
		setupSystems(backDrops);
		setupSystems(transitions);
		setupSystems(messages);
		setupSystems(scenes);
		setupSystems(likedScenes);
		//setupSystems(filters);  Filters get left out of the setup() for now because they don't extend ShapeSystem
		
		//processing hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
		
		setupListeners.forEach(sl -> sl.onSetupComplete());
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
		if (!transitionSwitch.isFrozen()) {
			transitionIndex += random(transitions.size() - 1); 
		}
	}
	
	/**
	 * Reset all switches and control mode
	 */
	public void panic() {
		resetSwitches();
		initControlMode();
		commandSystem.panic();
	}
	
	public void sendMessage(String [] messages) {
		if (messagesSwitch.isEnabled()) {
			getMessage().onNewMessage(messages);
		}
	}
	
	public void draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		scrambleModeSwitch.setState(isScrambleModeAvailable() ? STATE.ENABLED : STATE.DISABLED);
		if (frameStroberSwitch.isEnabled()) {
			if (frameStrober.isSkippingFrames()) {
				return;
			}
		}
		
		settingsDisplay.reset();
		
		TransitionSystem transition = prepareTransition(false);
		
		if (likedScenesSwitch.isEnabled() && !likedScenes.isEmpty()) {
			currentScene = (Scene)getPlugin(likedScenes, likedSceneIndex);
		} else {
			currentScene = (Scene)getPlugin(scenes, sceneIndex);
			if (currentScene.isNormal()) {
				BackDropSystem backDrop = null;
				if (backDropSwitch.isEnabled()) {
					backDrop = getBackDrop();
				}
	
				ShapeSystem bgSys = null;
				if (backGroundSwitch.isEnabled()) {
					bgSys = getBackground();
				}
	
				Filter filter = null;
				if (filtersSwitch.isEnabled()) {
					filter = (Filter) getPlugin(filters, filterIndex);
				}
	
				ShapeSystem fgSys = null;
				if (foreGroundSwitch.isEnabled()) {
					fgSys = getForeground();
				}
	
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
		
		if (messagesSwitch.isEnabled()) {
			drawSystem(getMessage(), "message");
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
		
		if (videoCaptureSwitch.isEnabled()) {
			doScreenCapture();
		}
		
		drawListeners.forEach(dl -> dl.onDrawComplete());
	}
	
	public void drawSystem(ShapeSystem s, String debugName) {
		pushStyle();
		pushMatrix();
		settingsDisplay.debugSystem(s, debugName);
		s.draw();
		popMatrix();
		popStyle();
	}
	
	protected TransitionSystem prepareTransition(boolean forceStart) {
		TransitionSystem transition = null;
		if (transitionSwitch.isEnabled()) {
			transition = getTransition();
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
		if (!likedScenesSwitch.isEnabled()) {
			if (!foreGroundSwitch.isFrozen()) {
				foregroundIndex += random(foregrounds.size() - 1);
			}
			
			if (!backGroundSwitch.isFrozen()) {
				backgroundIndex += random(backgrounds.size() - 1);
			}
			
			if (!backDropSwitch.isFrozen()) {
				backDropIndex += random(backDrops.size() - 1);
			}
			
			if (!filtersSwitch.isFrozen()) {
				filterIndex += random(filters.size() - 1);
			}
			
			if (!messagesSwitch.isFrozen()) {
				messageIndex += random(messages.size());
			}
		}
		
		locationIndex += random(locations.size() - 1);
		colorIndex += random(colors.size() - 1);
		
		//send out a cool message about the new system
		if (messagesSwitch.isEnabled()) {
			List<String> msgs = new ArrayList<String>();
			if (backDropSwitch.isEnabled()) {
				msgs.add(getBackDrop().getDisplayName());
			}

			if (foreGroundSwitch.isEnabled()) {
				msgs.add(getForeground().getDisplayName());
			}
			
			if (backGroundSwitch.isEnabled()) {
				msgs.add(getBackground().getDisplayName());
			}
			
			sendMessage(msgs.toArray(new String[msgs.size()]));
		}
		
		//reset the flag
		scrambleMode = false;
	}

	protected void initControlMode() {
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(configurator.getRootConfig().getString("apv.controlMode"));
	}
	
	@SuppressWarnings("unchecked")
	protected void configureSwitches() {
		configureSwitches((List<Switch>)configurator.loadAVPPlugins("switches"));
	}
	
	protected void initializeCommands() {
		CommandSystem cs = commandSystem;
		
		registerNonFreezableSwitchCommand(helpSwitch, 'h');
		registerNonFreezableSwitchCommand(showSettingsSwitch, 'q');
		registerNonFreezableSwitchCommand(likedScenesSwitch, 'l');
		
		registerSwitchCommand(foreGroundSwitch, '1');
		registerSwitchCommand(backGroundSwitch, '2');
		registerSwitchCommand(backDropSwitch, '3');
		registerSwitchCommand(filtersSwitch, '4');
		registerSwitchCommand(messagesSwitch, '5');
		registerSwitchCommand(transitionSwitch, '6');
		registerSwitchCommand(pulseListenerSwitch, '7');
		registerSwitchCommand(frameStroberSwitch, '8');
		registerSwitchCommand(videoCaptureSwitch, '9');
		//not registering scrambleModeSwitch  It is a synthetic switch
		
		cs.registerCommand('f', "Foreground", "Cycles through the foregrounds", 
				(event) -> {if (event.isShiftDown()) foregroundIndex--; else foregroundIndex++;});
		cs.registerCommand('b', "Background", "Cycles through the backgrounds", 
				(event) -> {if (event.isShiftDown()) backgroundIndex--; else backgroundIndex++;});
		cs.registerCommand('o', "Backdrop", "Cycles through the backdrops", 
				(event) -> {if (event.isShiftDown()) backDropIndex--; else backDropIndex++;});
		cs.registerCommand(PConstants.ENTER, "Enter", "Cycles through the locations (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) locationIndex--; else locationIndex++;});
		cs.registerCommand('t', "Filter", "Cycles through the filters (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) filterIndex--; else filterIndex++;});
		cs.registerCommand('c', "Colors", "Cycles through the colors (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) colorIndex--; else colorIndex++;});
		cs.registerCommand('n', "Transition", "Cycles through the transition (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) transitionIndex--; else transitionIndex++;});
		cs.registerCommand('m', "Message", "Cycles through the message (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) messageIndex--; else messageIndex++;});
		cs.registerCommand('z', "Cycle Mode", "Cycles between all the available Modes (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) cycleMode(false); else cycleMode(true);});
		
		cs.registerCommand(SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things", e -> scramble());
		cs.registerCommand('?', "Panic", "Resets switches to their defaults", e -> panic());
		cs.registerCommand('j', "Perf Monitor", "Outputs the slow monitor data to the console", event -> perfMonitor.dumpMonitorInfo());
		cs.registerCommand('s', "ScreenShot", "Saves the current frame to disk", event -> doScreenCapture());
		cs.registerCommand('0', "Configuration", "Saves the current configuration to disk", event -> configurator.saveCurrentConfig());
		
		cs.registerCommand(PApplet.RIGHT, "Right Arrow", "Cycles through the liked scenes", event -> likedSceneIndex++);
		cs.registerCommand(PApplet.LEFT, "Left Arrow", "Cycles through the liked scenes in reverse", event -> likedSceneIndex--);
		cs.registerCommand(PApplet.UP, "Up Arrow", "Adds the current scene to the 'liked' list", event -> likeCurrentScene());
		cs.registerCommand(PApplet.DOWN, "Down Arrow", "Removes the current scene from the 'liked' list", event -> disLikeCurrentScene());
		
		cs.registerCommand('}', "Transition Frames", "Increments the number of frames for each transition ", 
				(event) -> {transitions.forEach(s -> {s.incrementTransitionFrames();});});
		cs.registerCommand('{', "Transition Frames", "Decrements the number of frames for each transition ", 
				(event) -> {transitions.forEach(s -> {s.decrementTransitionFrames();});});
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
		CONTROL_MODES controlMode = getControl().getControlMode();
		if (advance) {
			currentControlMode = controlMode.getNext();
		} else {
			currentControlMode = controlMode.getPrevious();
		}
	}

	protected void setupSystems(List<? extends ShapeSystem> systems) {
		for (ShapeSystem system : systems) {
			system.setup();
		}
	}
	
	protected APVPlugin getPlugin(List<? extends APVPlugin> list, int index) {
		return list.get(Math.abs(index) % list.size());
	}
	
	protected void configureSwitches(List<Switch> ss) {
		switches = new HashMap<String, Switch>(ss.size());
		for (Iterator<Switch> it = ss.iterator(); it.hasNext();) {
			Switch nextSwitch = it.next();
			switches.put(nextSwitch.name, nextSwitch);
		}
		
		foreGroundSwitch = switches.get("ForeGround"); 
		backGroundSwitch = switches.get("BackGround");
		backDropSwitch = switches.get("BackDrop");
		filtersSwitch = switches.get("Filters");
		transitionSwitch = switches.get("Transitions");
		messagesSwitch = switches.get("Messages");
		helpSwitch = switches.get("Help");
		showSettingsSwitch = switches.get("ShowSettings");
		pulseListenerSwitch = switches.get("PulseListener");
		likedScenesSwitch = switches.get("LikedScenes");
		frameStroberSwitch = switches.get("FrameStrober");
		videoCaptureSwitch = switches.get("VideoCapture");
		scrambleModeSwitch = switches.get("Scramble");
	}
	
	@SuppressWarnings("unchecked")
	protected void resetSwitches() {
		List<Switch> configSwitches = (List<Switch>)configurator.loadAVPPlugins("switches");
		configSwitches.forEach(cs -> {
			Switch s = switches.get(cs.name);
			s.setState(cs.state);
		});
	}
	
	public String getConfig() {
		StringBuffer buffer = new StringBuffer(System.lineSeparator());
		
		//Constants
		addConstant(buffer, "controlMode", getCurrentControlMode().name());
		addConstant(buffer, "fullScreen", String.valueOf(getConfigurator().getRootConfig().getBoolean("apv.fullScreen")));
		addConstant(buffer, "scrambleSystems", String.valueOf(getConfigurator().getRootConfig().getBoolean("apv.scrambleSystems")));
		addConstant(buffer, "screen.width", String.valueOf(width));
		addConstant(buffer, "screen.height", String.valueOf(height));		
		
		return buffer.toString();
	}
	
	private void addConstant(StringBuffer buffer, String name, String value) {
		buffer.append("apv." + name + " = " + value);
		buffer.append(System.lineSeparator());
	}
}
