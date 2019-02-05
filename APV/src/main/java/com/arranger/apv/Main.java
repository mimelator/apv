package com.arranger.apv;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.agent.APVAgent;
import com.arranger.apv.agent.MonitorAgent;
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
import com.arranger.apv.helpers.AutoAudioAdjuster;
import com.arranger.apv.helpers.HelpDisplay;
import com.arranger.apv.helpers.HotKeyHelper;
import com.arranger.apv.helpers.LIVE_SETTINGS;
import com.arranger.apv.helpers.MacroHelper;
import com.arranger.apv.helpers.PerformanceMonitor;
import com.arranger.apv.helpers.RewindHelper;
import com.arranger.apv.helpers.SettingsDisplay;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.helpers.Switch.STATE;
import com.arranger.apv.helpers.VideoGameHelper;
import com.arranger.apv.helpers.WelcomeDisplay;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.menu.APVMenu;
import com.arranger.apv.model.ColorsModel;
import com.arranger.apv.model.EmojisModel;
import com.arranger.apv.model.IconsModel;
import com.arranger.apv.model.SetPackModel;
import com.arranger.apv.model.SongsModel;
import com.arranger.apv.msg.MessageSystem;
import com.arranger.apv.scene.Forest;
import com.arranger.apv.scene.LikedScene;
import com.arranger.apv.scene.Marquee;
import com.arranger.apv.scene.Scene;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.transition.TransitionSystem;
import com.arranger.apv.util.APVSetListPlayer;
import com.arranger.apv.util.ColorHelper;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FontHelper;
import com.arranger.apv.util.Gravity;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.KeyListener;
import com.arranger.apv.util.KeyListener.KEY_SYSTEMS;
import com.arranger.apv.util.LoggingConfig;
import com.arranger.apv.util.MouseListener;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.VersionInfo;
import com.arranger.apv.util.cmdrunner.FileCommandRunner;
import com.arranger.apv.util.cmdrunner.StartupCommandRunner;
import com.arranger.apv.util.draw.DrawHelper;
import com.arranger.apv.util.draw.RandomMessagePainter;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.util.draw.StarPainter;
import com.arranger.apv.util.draw.TextDrawHelper;
import com.arranger.apv.util.frame.FrameStrober;
import com.arranger.apv.util.frame.Oscillator;
import com.arranger.apv.util.frame.Oscillator.Listener;
import com.arranger.apv.util.frame.SplineHelper;
import com.arranger.apv.wm.APVWatermark;
import com.arranger.apv.wm.WatermarkPainter;
import com.typesafe.config.Config;

import ch.bildspur.postfx.builder.PostFX;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.Event;
import processing.opengl.PShader;

public class Main extends PApplet {

	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	public static final int NUMBER_PARTICLES = 50;//100;
	public static final String RENDERER = P3D;
	public static final int BUFFER_SIZE = 512;
	public static final int MAX_ALPHA = 255;
	public static final int SCRAMBLE_QUIET_WINDOW = 600; //2 to 4 seconds
	public static final int DEFAULT_SKIP_FRAMES_FOR_CONSOLE_OUTPUT = 200;
	public static final char SPACE_BAR_KEY_CODE = ' ';

	protected APVAgent agent;
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
	protected APVWatermark watermark;
	
	//Useful helper classes
	protected APVMenu apvMenu;
	protected APVPulseListener apvPulseListener;
	protected APVSetListPlayer apvSetListPlayer;
	protected Audio audio;
	protected AutoAudioAdjuster autoAudioAdjuster;
	protected ColorHelper colorHelper;
	protected CommandSystem commandSystem;
	protected Configurator configurator;
	protected FileCommandRunner fileCommandRunner;
	protected FontHelper fontHelper;
	protected FrameStrober frameStrober;
	protected Gravity gravity;
	protected HelpDisplay helpDisplay;
	protected HotKeyHelper hotKeyHelper;
	protected ImageHelper imageHelper;
	protected KeyListener keyListener;
	protected LoggingConfig loggingConfig;
	protected MacroHelper macroHelper;
	protected MouseListener mouseListener;
	protected Oscillator oscillator;
	protected Particles particles;
	protected PerformanceMonitor perfMonitor;
	protected PostFX postFX;
	protected RandomMessagePainter randomMessagePainter;
	protected RewindHelper rewindHelper;
	protected SettingsDisplay settingsDisplay;
	protected SplineHelper splineHelper;
	protected StarPainter starPainter;
	protected StartupCommandRunner startupCommandRunner;
	protected VersionInfo versionInfo;
	protected VideoGameHelper videoGameHelper;
	protected WelcomeDisplay welcomeDisplay;
	
	
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
	
	private SongsModel songsModel;
	private ColorsModel colorsModel;
	private EmojisModel emojisModel;
	private IconsModel iconsModel;
	private SetPackModel setPackModel;
	
	
	//Switches for runtime
	private Switch 
		audioListenerDiagnosticSwitch,
		consoleOutputSwitch,
		debugPulseSwitch,
		flashFlagSwitch,
		frameStroberSwitch,				
		helpSwitch,
		popularityPoolSwitch,
		showSettingsSwitch,
		scrambleModeSwitch,
		videoGameSwitch,
		welcomeSwitch;
	
	public enum FLAGS {
		
		AUTO_ADD_SOBLE("autoAddSoble", "true|false"),
		AUTO_LOAD_SET_LIST_FOLDER("autoLoadSetListFolder", "true|false"),
		AUTO_LOADED_BACKGROUND_FOLDER("autoLoadedBackgroundFolder", "directory"),
		APV_CONFIG_VERSION("configVersion", "string"),
		CONTROL_MODE("controlMode", "PERLIN|MANUAL"),
		COUNTDOWN_PCT("countdownPct", "0 <> 1"),
		DEBUG_AGENT_MESSAGES("debugAgentMessages", "true|false"),
		DEBUG_SYS_MESSAGES("debugSystemMessages", "true|false"),
		DEFAULT_SHAPE_SYSTEM_ALPHA("defaultShapeSystemAlpha", "integer"),
		FONT_NAME("font.name", "string"),
		FONT_SIZE("font.size", "integer"),
		FONT_STYLE("font.style", "PLAIN|ITALIC|BOLD"),
		FRAME_RATE("frameRate", "integer"),
		FULL_SCREEN("fullScreen", "true|false"),
		LINE_IN("lineIn", "true|false"),
		LISTEN_ONLY("listenOnly", "true|false"),
		MARQUEE_FRAMES("marqueeFrames", "integer"),
		MONITORING_ENABLED("monitoring.enabled", "true|false"),
		MUSIC_DIR("musicDir", "directory"),
		OCEAN_NAME("ocean", "string"),
		PULSE_SENSITIVITY("pulseSensitivity", "integer"),
		QUIET_WINDOW_SIZE("quietWindowSize", "integer"),
		SCRAMBLE_SYSTEMS("scrambleSystems", "true|false"),
		SCREEN_WIDTH("screen.width", "integer"),
		SCREEN_HEIGHT("screen.height", "integer"),
		SET_LIST("setList", "true|false"),
		SET_LIST_FOLDER("setListFolder", "directory"),
		TREE_COMPLEXITY_CUTOFF("treeComplexityCutoff", "0<100"),
		TREE_MIN_SIZE("treeMinSize", "0<10"),
		WATERMARK_FRAMES("watermarkFrames", "integer");
		
		
		private String name;
		private String description;
		
		private FLAGS(String name, String description) {
			this.name = name;
			this.description = description;
		}
		
		public String plainName() {
			return name;
		}
		
		public String apvName() {
			return "apv." + name;
		}
		
		public String description() {
			return description;
		}
		
		public static final List<FLAGS> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		public static final int SIZE = VALUES.size();
	}

	public enum SWITCH_NAMES {
		AUDIO_LISTENER_DIAGNOSTIC("AudioListenerDiagnostic"),
		CONSOLE_OUTPUT("ConsoleOutput"),
		DEBUG_PULSE("DebugPulse"),
		FLASH_FLAG("FlashFlag"),
		FRAME_STROBER("FrameStrober"),
		HELP("Help"),
		MENU("Menu"),
		POPULARITY_POOL("PopularityPool"),
		SCRAMBLE_MODE("Scramble"),
		SHOW_SETTINGS("ShowSettings"),
		VIDEO_GAME("VideoGame"),
		WELCOME("Welcome");
		
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
		MENU("menu"),
		PULSELISTENERS("pulseListeners", false),
		SCENES("scenes"),
		SHADERS("shaders"),
		SWITCHES("switches", false),
		TRANSITIONS("transitions"),
		WATERMARKS("watermarks", false);
		
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

	private boolean procControl = false;
	
	public Main() {
		this(false);
	}
	
	public Main(boolean procControl) {
		this.procControl = procControl;
	}
	
	@Override
	public void settings() {
		loggingConfig = new LoggingConfig(this);
		loggingConfig.configureLogging();
		
		configurator = new Configurator(this);
		
		//check version
		//TODO if there is a mismatch, how to best handle that?
		Config rootConfig = configurator.getRootConfig();
		if (rootConfig.hasPath(FLAGS.APV_CONFIG_VERSION.apvName())) {
			String configurationVersion = rootConfig.getString(FLAGS.APV_CONFIG_VERSION.apvName());
			
			String version = new VersionInfo(this).getVersion();
			if (!version.equals(configurationVersion)) {
				System.out.println("Initializing with configuration version: " + configurationVersion + 
						" and running version: " + version);
			}
		}
		
		configureSwitches();
		
		if (!procControl) {
			boolean isFullScreen = rootConfig.getBoolean(FLAGS.FULL_SCREEN.apvName());
			if (isFullScreen) {
				fullScreen(RENDERER);
			} else {
				size(rootConfig.getInt(FLAGS.SCREEN_WIDTH.apvName()), rootConfig.getInt(FLAGS.SCREEN_HEIGHT.apvName()), RENDERER);
			}
			
			initEvents();
		}
		
		dumpStartupFlags();
	}
	
	public String getConfigValueForFlag(FLAGS flag, String defVal) {
		Config rootConfig = getConfigurator().getRootConfig();
		if (rootConfig.hasPath(flag.apvName())) {
			return rootConfig.getString(flag.apvName());
		} else {
			return defVal;
		}
	}
	
	public String getConfigValueForFlag(FLAGS flag) {
		Config rootConfig = getConfigurator().getRootConfig();
		if (rootConfig.hasPath(flag.apvName())) {
			return rootConfig.getString(flag.apvName());
		} else {
			return "";
		}
	}
	
	public boolean getConfigBooleanForFlag(FLAGS flag) {
		return getConfigurator().getRootConfig().getBoolean(flag.apvName());
	}
	
	public String getConfigString(String path) {
		return getConfigurator().getRootConfig().getString(path);
	}
	
	public int getConfigInt(String path) {
		return getConfigurator().getRootConfig().getInt(path);
	}
	
	public float getConfigFloat(String path) {
		return Float.parseFloat(getConfigString(path));
	}
	
	public boolean getConfigBoolean(String path) {
		return getConfigurator().getRootConfig().getBoolean(path);
	}
	
	public APV<? extends APVPlugin> getSystem(SYSTEM_NAMES name) {
		return systemMap.get(name);
	}
	
	public Collection<APV<? extends APVPlugin>> getSystems() {
		return systemMap.values();
	}
	
	public FontHelper getFontHelper() {
		return fontHelper;
	}
	
	public KeyListener getKeyListener() {
		return keyListener;
	}

	public MouseListener getMouseListener() {
		return mouseListener;
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
	
	public AutoAudioAdjuster getAutoAudioAdjuster() {
		return autoAudioAdjuster;
	}
	
	public Gravity getGravity() {
		return gravity;
	}
	
	public FrameStrober getFrameStrober() {
		return frameStrober;
	}
	
	public ImageHelper getImageHelper() {
		return imageHelper;
	}
	
	public ColorHelper getColorHelper() {
		return colorHelper;
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
		return apvPulseListener;
	}
	
	public APVSetListPlayer getSetListPlayer() {
		return apvSetListPlayer;
	}
	
	public APVAgent getAgent() {
		return agent;
	}
	
	public HelpDisplay getHelpDisplay() {
		return helpDisplay;
	}
	
	public WelcomeDisplay getWelcomeDisplay() {
		return welcomeDisplay;
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
	
	public void setCurrentControlMode(CONTROL_MODES mode) {
		currentControlMode = mode;
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
	
	public CoreEvent getKScopeEvent() {
		return (CoreEvent)eventMap.get(EventTypes.K_SCOPE);
	}
	
	public CoreEvent getSceneCompleteEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SCENE_COMPLETE);
	}
	
	public CoreEvent getSetListCompleteEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SETLIST_COMPLETE);
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
	
	public DrawShapeEvent getWatermarkEvent() {
		return (DrawShapeEvent)eventMap.get(EventTypes.WATERMARK);
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
	
	public CoreEvent getALDAEvent() {
		return (CoreEvent)eventMap.get(EventTypes.ALDA);
	}
	
	public CoreEvent getColorChangeEvent() {
		return (CoreEvent)eventMap.get(EventTypes.COLOR_CHANGE);
	}
	
	public CoreEvent getSongStartEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SONG_START);
	}
	
	public CoreEvent getSetPackStartEvent() {
		return (CoreEvent)eventMap.get(EventTypes.SET_PACK_START);
	}
	
	public CoreEvent getMousePulseEvent() {
		return (CoreEvent)eventMap.get(EventTypes.MOUSE_PULSE);
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
	
	public boolean isListenOnly() {
		return getConfigBoolean(FLAGS.LISTEN_ONLY.apvName());
	}
	
	public int getDefaultShapeSystemAlpha() {
		return getConfigInt(FLAGS.DEFAULT_SHAPE_SYSTEM_ALPHA.apvName());
	}
	
	public int getMarqueeFrames() {
		return getConfigInt(FLAGS.MARQUEE_FRAMES.apvName());
	}
	
	public int getWatermarkFrames() {
		return getConfigInt(FLAGS.WATERMARK_FRAMES.apvName());
	}
	
	public float getFrameRate() {
		return getConfigFloat(FLAGS.FRAME_RATE.apvName());
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
		Optional<? extends APVPlugin> findFirst = apv.getList().stream().filter(p -> {
			String displayName = p.getDisplayName();
			String name = p.getName();
			boolean b1 = pluginDisplayName.equalsIgnoreCase(displayName);
			boolean b2 = pluginDisplayName.equalsIgnoreCase(name);
			return b1 || b2;
			
		}).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		} else {
			System.out.println("Unable to find plugin by name: " + pluginDisplayName);
			return null;
		}
	}
	
	public void setDefaultScene(String cause) {
		Scene defaultScene = scenes.getList().stream().filter(e -> !e.isAnimation()).findFirst().get();
		setNextScene(defaultScene, cause);
	}
	
	public void setNextScene(Scene scene, String cause) {
		scenes.setNextPlugin(scene, cause, false);
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}
	
	public APV<ColorSystem> getColors() {
		return colors;
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
	
	public APVWatermark getWatermark() {
		return watermark;
	}
	
	public TransitionSystem getTransition() {
		return transitions.getPlugin();
	}
	
	public MessageSystem getMessage() {
		return messages.getPlugin();
	}
	
	public APVMenu getMenu() {
		return apvMenu;
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
	
	public RandomMessagePainter getRandomMessagePainter() {
		return randomMessagePainter;
	}
	
	public RewindHelper getRewindHelper() {
		return rewindHelper;
	}
	
	public StartupCommandRunner getStartupCommandRunner() {
		return startupCommandRunner;
	}
	
	public FileCommandRunner getFileCommandRunner() {
		return fileCommandRunner;
	}
	
	public void likeCurrentScene() {
		likedScenes.getList().add(new LikedScene(currentScene));
		updatePopularity(currentScene, true);
		getConfigurator().saveCurrentConfig();
		String likedMsg = "Liked :)";
		sendMarqueeMessage(likedMsg);
	}
	
	public void disLikeCurrentScene() {
		likedScenes.getList().remove(currentScene);
		updatePopularity(currentScene, false);
		getConfigurator().saveCurrentConfig();
		sendMessage(new String[] {"Disliked :("});
		scramble();
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
		int indexOf = fragFilename.indexOf("shader");
		if (indexOf > 0) {
			fragFilename = fragFilename.substring(indexOf, fragFilename.length());
		}
		
		try {
			return super.loadShader(fragFilename);
		} catch (Exception e) {
			//On Windows, some Custom Shaders aren't loading properly
			logger.log(Level.INFO, e.getMessage(), e);
			logger.log(Level.SEVERE, "Unable to load shader: " + fragFilename);
			return null;
		}
	}
	
	//Folder Selection Support.  JFileChooser (and similar) are buggy with processing
	
	@FunctionalInterface
	public interface FileSelectionHandler {
		void onSelection(File fileObject);
	}
	
	private transient FileSelectionHandler fsh;
	
	public void selectFolder(String prompt, FileSelectionHandler fsh) {
		this.fsh = fsh;
		super.selectFolder(prompt, "onFolderSelected");
	}
	
	public void selectFile(String prompt, FileSelectionHandler fsh) {
		this.fsh = fsh;
		super.selectOutput(prompt, "onFolderSelected");
	}
	
	public void selectInputFile(String prompt, FileSelectionHandler fsh) {
		this.fsh = fsh;
		super.selectInput(prompt, "onFolderSelected");
	}
	
	/**
	 * The PApplet.selectFolder() API requires a named call back in the class extending PApplet.
	 */
	public void onFolderSelected(File selection) {
		if (selection == null) {
			return;
		}
		
		if (fsh != null) {
			fsh.onSelection(selection);
			fsh = null;
		}
	}
	
	@Override
	public void setup() {
		songsModel = new SongsModel(this);
		colorsModel = new ColorsModel(this);
		emojisModel = new EmojisModel(this);
		iconsModel = new IconsModel(this);
		setPackModel = new SetPackModel(this);
		
		agent = new APVAgent(this);
		apvPulseListener = new APVPulseListener(this);
		audio = new Audio(this, BUFFER_SIZE);
		autoAudioAdjuster = new AutoAudioAdjuster(this);
		colorHelper = new ColorHelper(this);
		commandSystem = new CommandSystem(this);
		fileCommandRunner = new FileCommandRunner(this);
		fontHelper = new FontHelper(this);
		frameStrober = new FrameStrober(this);
		gravity = new Gravity(this);
		helpDisplay = new HelpDisplay(this);
		hotKeyHelper = new HotKeyHelper(this);
		imageHelper = new ImageHelper(this);
		keyListener = new KeyListener(this);
		mouseListener = new MouseListener(this);
		macroHelper = new MacroHelper(this);
		oscillator = new Oscillator(this);
		particles = new Particles(this);
		perfMonitor = new PerformanceMonitor(this);
		postFX  = new PostFX(this);
		randomMessagePainter = new RandomMessagePainter(this);
		rewindHelper = new RewindHelper(this);
		settingsDisplay = new SettingsDisplay(this);
		splineHelper = new SplineHelper(this);
		starPainter = new StarPainter(this);
		startupCommandRunner = new StartupCommandRunner(this);
		versionInfo = new VersionInfo(this);
		videoGameHelper = new VideoGameHelper(this);
		welcomeDisplay = new WelcomeDisplay(this);
		
		systemMap.put(SYSTEM_NAMES.BACKDROPS, new APV<BackDropSystem>(this, SYSTEM_NAMES.BACKDROPS));
		systemMap.put(SYSTEM_NAMES.BACKGROUNDS, new APV<ShapeSystem>(this, SYSTEM_NAMES.BACKGROUNDS));
		systemMap.put(SYSTEM_NAMES.COLORS, new APV<ColorSystem>(this, SYSTEM_NAMES.COLORS));
		systemMap.put(SYSTEM_NAMES.CONTROLS, new APV<ControlSystem>(this, SYSTEM_NAMES.CONTROLS));
		systemMap.put(SYSTEM_NAMES.FILTERS, new APV<Filter>(this, SYSTEM_NAMES.FILTERS));
		systemMap.put(SYSTEM_NAMES.FOREGROUNDS, new APV<ShapeSystem>(this, SYSTEM_NAMES.FOREGROUNDS));
		systemMap.put(SYSTEM_NAMES.LIKED_SCENES, new APV<Scene>(this, SYSTEM_NAMES.LIKED_SCENES));
		systemMap.put(SYSTEM_NAMES.LOCATIONS, new APV<LocationSystem>(this, SYSTEM_NAMES.LOCATIONS));
		systemMap.put(SYSTEM_NAMES.MESSAGES, new APV<MessageSystem>(this, SYSTEM_NAMES.MESSAGES));
		systemMap.put(SYSTEM_NAMES.MENU, new APVMenu(this));
		systemMap.put(SYSTEM_NAMES.SCENES, new APV<Scene>(this, SYSTEM_NAMES.SCENES, false));
		systemMap.put(SYSTEM_NAMES.SHADERS, new APV<Shader>(this, SYSTEM_NAMES.SHADERS));
		systemMap.put(SYSTEM_NAMES.TRANSITIONS, new APV<TransitionSystem>(this, SYSTEM_NAMES.TRANSITIONS));
		systemMap.put(SYSTEM_NAMES.WATERMARKS, new APVWatermark(this));
		
		assignSystems();
		initControlMode();
		hotKeyHelper.configure();
		macroHelper.configure();
		setupSystems();
		initializeCommands();
		
		//processing hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
		
		//Forward messages to the currentLikedScene if applicable
		getAPVChangeEvent().register((apv, plugin, cause) -> {
			if (currentScene != null && currentScene.isLikedScene()) {
				((LikedScene)currentScene).onPluginChange(apv, plugin, cause);
			}
		});

		setDefaultScene("setup");
		checkStartupSetList();
		fireSetupEvent();
		startupCommandRunner.runStartupCommands();
		frameRate(getFrameRate());
	}

	public void playSetList(File directory) {
		ensureSetListReadyToPlay();
		apvSetListPlayer.play(directory);
		songsModel.onSetListPlayerChange();
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
	
	public void randomizeCurrentSetPackColors() {
		colorsModel.randomize();
		colorsModel.setCurrentColors(false);
	}
	
	public void randomizeCurrentSetPack() {
		randomizeCurrentSetPackColors();
		
		songsModel.randomize();
		songsModel.playSong(0);
	}
	
	public IconsModel getIconsModel() {
		return iconsModel;
	}
	
	public SongsModel getSongsModel() {
		return songsModel;
	}
	
	public ColorsModel getColorsModel() {
		return colorsModel;
	}
	
	public EmojisModel getEmojisModel() {
		return emojisModel;
	}
	
	public SetPackModel getSetPackModel() {
		return setPackModel;
	}
	
	/**
	 * Reset all switches and control mode
	 */
	public void reset() {
		getSceneCompleteEvent().fire();
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
	
	/**
	 * Similar to {@link #manual()} but leaves agents alone
	 * As a result various agents might change the location mode away quickly
	 */
	public void mouseControl() {
		currentControlMode = CONTROL_MODES.MANUAL;
		activateNextPlugin(SYSTEM_NAMES.LOCATIONS, "Mouse", Command.MANUAL.name());
	}
	
	public void sendMessage(String [] msgs) {
		if (messages.isEnabled()) {
			getMessage().onNewMessage(msgs);
		}
	}
	
	public void sendMarqueeMessage(String message) {
		Marquee marquee = new Marquee(this, message);
		setNextScene(marquee, "marquee");
		
		//Send the message to the lower right for awhile
		new TextDrawHelper(this, getMarqueeFrames(), Arrays.asList(new String[] {message}), SafePainter.LOCATION.LOWER_RIGHT); 
	}
	
	public void showLiveSetting(Command cmd) {
		String argument = cmd.getPrimaryArg();
		LIVE_SETTINGS liveSetting = LIVE_SETTINGS.valueOf(argument);
		liveSetting.onCommand(this, cmd.getArgs());
	}
	
	public void sendTreeMessage(String message) {
		if (message == null) {
			//get one
			message = getRandomMessagePainter().getRandomMessage();
		}
		
		Forest forest = new Forest(this);
		setNextScene(forest, "forestMessage");
		
		//Send message and watermark
		sendMessage(new String[] {message});
		WatermarkPainter wp = new WatermarkPainter(this, getMarqueeFrames(), message, 1, LOCATION.MIDDLE, WatermarkPainter.WATERMARK_ALPHA);
		new DrawHelper(this, wp.getNumFrames(), wp, () -> {});
	}
	
	public void fireEvent(String event) {
		try {
			EventTypes evt = EventTypes.valueOf(event);
			APVEvent<?> apvEvent = eventMap.get(evt);
			apvEvent.fire();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void showSongQueue() {
		List<String> messages = new ArrayList<String>();
		messages.add("Song Queue:");
		
		SongsModel model = getSongsModel();
		List<File> songs = model.getSongs();
		songs = songs.subList(model.getIndex(), songs.size() - 1);
		messages.addAll(songs.stream().map(f -> f.getName()).collect(Collectors.toList()));
		
		new TextDrawHelper(this, getMarqueeFrames(), messages, SafePainter.LOCATION.UPPER_LEFT);
	}
	
	public void showOceanSetInfo() {
		List<String> messages = new ArrayList<String>();
		messages.add("Ocean: " + getConfigValueForFlag(Main.FLAGS.OCEAN_NAME));
		messages.add("SetPack: " + getFriendlySetPackName(getSetPackModel().getSetPackName()));
		
		new TextDrawHelper(this, getMarqueeFrames(), messages, SafePainter.LOCATION.MIDDLE);
	}
	
	public void showAvailableSetPacks() {
		List<String> messages = new ArrayList<String>();
		messages.add("Available SetPacks:");
		List<String> collect = getSetPackModel().getSetPackList().stream().map(s -> getFriendlySetPackName(s)).collect(Collectors.toList());
		messages.addAll(collect);
		
		new TextDrawHelper(this, getMarqueeFrames(), messages, SafePainter.LOCATION.UPPER_LEFT);
	}
	
	@FunctionalInterface
	private static interface QueuedCommand {
		void doCommand();
	}
	
	@FunctionalInterface
	public static interface QueuedCommandCompletionCallback {
		void onComplete();
	}
	
	private List<QueuedCommand> queuedCommands = new ArrayList<QueuedCommand>();
	
	@Override
	public void draw() {
		queuedCommands.forEach(q -> q.doCommand());
		queuedCommands.clear();
		
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
		reloadConfiguration(null, null);
	}
	
	public void reloadConfiguration(String file, QueuedCommandCompletionCallback callback) {
		queuedCommands.add(() -> {
			System.setProperty("defaultCommands.0", ""); //no default commands with reload
			doReloadConfiguration(file);
			if (callback != null) {
				callback.onComplete();
			}
			
			//restart(file);
		});
	}
	
	protected boolean restart = false;
	
	protected void restart(String file) {
		restart = true;
		
		//stop music
		getSongsModel().stop();
		
		//file watcher
		getFileCommandRunner().shutdown();
		
		//stop monitor agent
		MonitorAgent monitorAgent = (MonitorAgent)getAgent().getFirstInstanceOf(MonitorAgent.class);
		if (monitorAgent != null) {
			monitorAgent.shutdown();
		}
		
		//processing
		exit();
		
		//restart
		_restart(file);
	}
	
	protected void _restart(String file) {
		//no more default commands
		System.setProperty("defaultCommands.0", Command.SHOW_TREE_SCENE.name() + ":" + new FileHelper(this).getConfigBasedSetPackName(file));
		
		//set the config file
		System.setProperty("config.file", file);
		
		//start it
		Main.main(new String[] {});
	}
	
	@Override
	public void exitActual() {
		//If restarting, i don't want to exit the process
		if (!restart) {
			super.exitActual();
		}
	}
	
	protected void doReloadConfiguration(String file) {
		stop();
		
		//Color helper is used by the configurator during reload
		colorHelper.reset();
		
		getImageHelper().dispose(g);
		
		configurator.reload(file);
		SYSTEM_NAMES.VALUES.forEach(s -> reloadConfigurationForSystem(s));
		assignSystems();
		
		//special case a couple of helpers
		randomMessagePainter.reset();
		macroHelper.reloadConfiguration();
		hotKeyHelper.reloadConfiguration();
		agent.reloadConfiguration();
		watermark.reloadConfiguration();
		
		songsModel.reset();
		colorsModel.reset();
		emojisModel.reset();
		iconsModel.reset();
		setPackModel.reset();
		reset();
		
		registerSystemCommands();
		
		setDefaultScene("reload");
		checkStartupSetList();
		
		fireSetupEvent();
		
		String treeMsg = new FileHelper(this).getConfigBasedSetPackName(file);
		sendTreeMessage(treeMsg);
		
		start();
	}

	protected void fireSetupEvent() {
		CoreEvent setupEvent = getSetupEvent();
		setupEvent.fire();
		setupEvent.reset();
	}
	
	
	
	protected void _draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		if (apvMenu.isEnabled()) {
			apvMenu.drawMenu();
			return;
		}
		
		scrambleModeSwitch.setState(isScrambleModeAvailable() ? STATE.ENABLED : STATE.DISABLED);
		if (frameStroberSwitch.isEnabled()) {
			if (frameStrober.isSkippingFrames()) {
				return;
			}
		}
		settingsDisplay.reset();
		TransitionSystem transition = prepareTransition(false);
		
		if (keyListener.getSystem() == KEY_SYSTEMS.REWIND) {
			currentScene = rewindHelper.getScene();
		} else if (likedScenes.isEnabled()) {
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
		
		if (keyListener.getSystem() != KEY_SYSTEMS.REWIND) {
			rewindHelper.addScene(currentScene);
		}
		drawSystem(currentScene, "scene");
		
		final TransitionSystem t = transition;
		postScene(() -> perfMonitor.doMonitorCheck(currentScene));
		postScene(transition != null, () -> drawSystem(t, "transition"));
		postScene(messages.isEnabled(), () -> drawSystem(getMessage(), "message"));
		postScene(videoGameSwitch, () -> videoGameHelper.showStats());
		postScene(helpSwitch, () -> helpDisplay.showHelp());
		postScene(welcomeSwitch, () -> welcomeDisplay.showHelp());
		postScene(scrambleMode, () -> doScramble());
		postScene(() -> runControlMode());
		postScene(screenshotMode, () -> doScreenCapture());
		postScene(() -> getDrawEvent().fire());
		postScene(showSettingsSwitch, () -> settingsDisplay.drawSettingsMessages());
		postScene(consoleOutputSwitch, () -> drawConsoleFrameInfo());
	}
	
	@FunctionalInterface
	private static interface Action {
		void action();
	}
	
	private void postScene(Action action) {
		postScene(true, action);
	}
	
	private void postScene(Switch sw, Action action) {
		if (sw == null) {
			return;
		}
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
		Command nextCommand = cs.getNextCommand();
		if (nextCommand != null) {
			int modifiers = randomBoolean() ? Event.SHIFT : 0;
			getCommandSystem().invokeCommand(nextCommand, cs.getDisplayName(), modifiers);
		}
	}
	
	protected void drawConsoleFrameInfo() {
		Switch switch1 = this.switches.get(SWITCH_NAMES.CONSOLE_OUTPUT.name);
		int skipFrames = DEFAULT_SKIP_FRAMES_FOR_CONSOLE_OUTPUT;
		if (switch1.data != null) {
			skipFrames = Integer.parseInt(switch1.data);
		}
		
		if (this.frameCount % skipFrames == 0) {
			System.out.printf("Frame[%s]: %s (fps) %s", this.frameCount, this.frameRate, System.lineSeparator());
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
	
	protected void checkStartupSetList() {
		ensureSetListReadyToPlay();
		if (isSetList() && !isListenOnly()) {
			apvSetListPlayer.playStartupSongList();
		}
	}
	
	protected void ensureSetListReadyToPlay() {
		if (apvSetListPlayer != null) {
			apvSetListPlayer.stop();
		} else {
			apvSetListPlayer = new APVSetListPlayer(this);
		}
	}

	protected void initControlMode() {
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(getConfigString(FLAGS.CONTROL_MODE.apvName()));
	}
	
	protected void initializeCommands() {
		registerMainSwitches();
		registerSystemCommands();
		
		
		//TODO restore this?
//		likedScenes.registerHandler(Command.RIGHT_ARROW, (c,s,m) -> likedScenes.increment("->"));
//		likedScenes.registerHandler(Command.LEFT_ARROW, (c,s,m) -> likedScenes.decrement("<-"));
		
		hotKeyHelper.register();
		macroHelper.register();
		registerMainCommands();
	}

	protected void registerMainSwitches() {
		registerSwitch(agent.getSwitch(), Command.SWITCH_AGENT);
		registerSwitch(audioListenerDiagnosticSwitch, Command.SWITCH_AUDIO_LISTENER_DIAGNOSTIC);
		registerSwitch(consoleOutputSwitch, Command.SWITCH_CONSOLE_OUTPUT);
		registerSwitch(debugPulseSwitch, Command.SWITCH_DEBUG_PULSE);
		registerSwitch(flashFlagSwitch, Command.SWITCH_FLASH_FLAG);
		registerSwitch(frameStroberSwitch, Command.SWITCH_FRAME_STROBER);
		registerSwitch(helpSwitch, Command.SWITCH_HELP);
		registerSwitch(likedScenes.getSwitch(), Command.SWITCH_LIKED_SCENES);
		registerSwitch(popularityPoolSwitch, Command.SWITCH_POPULARITY_POOL);
		registerSwitch(apvPulseListener.getSwitch(), Command.SWITCH_PULSE_LISTENER);
		registerSwitch(showSettingsSwitch, Command.SWITCH_SETTINGS);
		registerSwitch(videoGameSwitch, Command.SWITCH_VIDEOGAME);
		registerSwitch(welcomeSwitch, Command.SWITCH_WELCOME);
	}
	
	protected void registerMainCommands() {
		CommandSystem cs = commandSystem;
		cs.registerHandler(Command.CYCLE_CONTROL_MODE, (cmd,src,mod) -> cycleMode(!Command.isShiftDown(mod)));  
		cs.registerHandler(Command.CYCLE_SET_PACK, (cmd,src,mod) -> cycleSetPack(!Command.isShiftDown(mod)));
		cs.registerHandler(Command.DOWN_ARROW, (cmd,src,mod) -> disLikeCurrentScene());
		cs.registerHandler(Command.FIRE_EVENT, (cmd,src,mod) -> fireEvent(cmd.getPrimaryArg()));
		cs.registerHandler(Command.LEFT_ARROW, (cmd,src,mod) -> rewindHelper.enterRewindMode());
		cs.registerHandler(Command.LIVE_SETTINGS, (cmd,src,mod) -> showLiveSetting(cmd));
		cs.registerHandler(Command.LOAD_CONFIGURATION, (cmd,src,mod)  -> configurator.reload(cmd.getPrimaryArg()));
		cs.registerHandler(Command.MANUAL, (cmd,src,mod) -> manual());
		cs.registerHandler(Command.MOUSE_CONTROL, (cmd,src,mod) -> mouseControl());	
		cs.registerHandler(Command.PERF_MONITOR, (cmd,src,mod) -> perfMonitor.dumpMonitorInfo(!Command.isShiftDown(mod)));
		cs.registerHandler(Command.PLAY_SET_PACK, (cmd,src,mod) -> getSetPackModel().playSetPack(Command.PLAY_SET_PACK.getPrimaryArg()));
		cs.registerHandler(Command.RANDOMIZE_COLORS, (cmd,src,mod) -> randomizeCurrentSetPackColors());
		cs.registerHandler(Command.RANDOMIZE_SETPACK, (cmd,src,mod) -> randomizeCurrentSetPack());
		cs.registerHandler(Command.RESET, (cmd,src,mod) -> reset());
		cs.registerHandler(Command.RELOAD_CONFIGURATION, (cmd,src,mod)  -> reloadConfiguration());
		cs.registerHandler(Command.SCRAMBLE, (cmd,src,mod) -> scramble());
		cs.registerHandler(Command.SAVE_CONFIGURATION, (cmd,src,mod)  -> configurator.saveCurrentConfig());
		cs.registerHandler(Command.SCREEN_SHOT, (cmd,src,mod) -> screenshotMode = true); //screenshot's can only be taking during draw
		cs.registerHandler(Command.SHOW_MARQUEE_MESSAGE, (cmd,src,mod) -> sendMarqueeMessage(cmd.getPrimaryArg()));
		cs.registerHandler(Command.SHOW_OCEAN_SET_INFO, (cmd,src,mod) -> showOceanSetInfo());
		cs.registerHandler(Command.SHOW_SONG_QUEUE, (cmd,src,mod) -> showSongQueue());
		cs.registerHandler(Command.SHOW_TREE_SCENE, (cmd,src,mod) -> sendTreeMessage(cmd.getPrimaryArg()));
		cs.registerHandler(Command.SHOW_WATERMARK, (cmd,src,mod) -> getWatermarkEvent().fire());
		cs.registerHandler(Command.SHUTDOWN, (cmd,src,mod) -> System.exit(0));
		cs.registerHandler(Command.TRANSITION_FRAMES_INC, (cmd,src,mod) -> {transitions.forEach(t -> {t.incrementTransitionFrames();});});
		cs.registerHandler(Command.TRANSITION_FRAMES_DEC, (cmd,src,mod) -> {transitions.forEach(t -> {t.decrementTransitionFrames();});});
		cs.registerHandler(Command.UP_ARROW, (cmd,src,mod) -> likeCurrentScene());
		cs.registerHandler(Command.WINDOWS, (cmd,src,mod) -> new APVWindow(this));
	}
	
	protected void registerSystemCommands() {
		register(SYSTEM_NAMES.FOREGROUNDS, Command.SWITCH_FOREGROUNDS, Command.CYCLE_FOREGROUNDS, Command.FREEZE_FOREGROUNDS);
		register(SYSTEM_NAMES.BACKGROUNDS, Command.SWITCH_BACKGROUNDS, Command.CYCLE_BACKGROUNDS, Command.FREEZE_BACKGROUNDS);
		register(SYSTEM_NAMES.BACKDROPS, Command.SWITCH_BACKDROPS, Command.CYCLE_BACKDROPS, Command.FREEZE_BACKDROPS);
		register(SYSTEM_NAMES.COLORS, null, Command.CYCLE_COLORS, null);
		register(SYSTEM_NAMES.FILTERS, Command.SWITCH_FILTERS, Command.CYCLE_FILTERS, Command.FREEZE_FILTERS);
		register(SYSTEM_NAMES.LOCATIONS, null, Command.CYCLE_LOCATIONS, null);
		register(SYSTEM_NAMES.MENU, Command.SWITCH_MENU, null, null);
		register(SYSTEM_NAMES.MESSAGES, Command.SWITCH_MESSAGES, Command.CYCLE_MESSAGES, Command.FREEZE_MESSAGES);
		register(SYSTEM_NAMES.SHADERS, Command.SWITCH_SHADERS, Command.CYCLE_SHADERS, Command.FREEZE_SHADERS);
		register(SYSTEM_NAMES.TRANSITIONS, Command.SWITCH_TRANSITIONS, Command.CYCLE_TRANSITIONS, null);
		register(SYSTEM_NAMES.WATERMARKS, Command.SWITCH_WATERMARK, Command.CYCLE_WATERMARK, null);
	}
	
	protected void register(SYSTEM_NAMES system, Command switchCommand, Command handlerCommand, Command freezeCommand) {
		APV<? extends APVPlugin> apv = systemMap.get(system);
		if (switchCommand != null) {
			apv.registerSwitchCommand(switchCommand, freezeCommand);
		}
		apv.registerHandler(handlerCommand);
	}

	protected void registerSwitch(Switch s, Command command) {
		commandSystem.registerHandler(command, (cmd,src,mod) -> s.toggleEnabled());
	}
	
	protected void cycleMode(boolean advance) {
		CONTROL_MODES controlMode = getControl().getControlMode();
		if (advance) {
			currentControlMode = controlMode.getNext();
		} else {
			currentControlMode = controlMode.getPrevious();
		}
	}
	
	protected void cycleSetPack(boolean advance) {
		if (advance) {
			getSetPackModel().launchNextSetPack();
		} else {
			getSetPackModel().launchPrevSetPack();
		}
	}
	
	protected void updatePopularity(Scene scene, boolean increment) {
		int value = increment ? 1 : -1;
		for (APVPlugin p : scene.getComponentsToDrawScene().getPlugins()) {
			p.setPopularityIndex(p.getPopularityIndex() + value);
		}
		
		//save to disk
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
		
		APV<? extends APVPlugin> reloadedAPV;
		switch (system) {
		case MENU:
			reloadedAPV = new APVMenu(this);
			break;
		case WATERMARKS:
			reloadedAPV = new APVWatermark(this);
			break;
			default:
				reloadedAPV = new APV<APVPlugin>(this, system);
		}
		
		systemMap.put(system, reloadedAPV);
		setupSystem(reloadedAPV);
		register(system, reloadedAPV.getSwitchCommand(), originalAPV.getCommand(), originalAPV.getFreezeCommand());
	}
	
	@SuppressWarnings("unchecked")
	protected void assignSystems() {
		apvMenu = (APVMenu) systemMap.get(SYSTEM_NAMES.MENU);
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
		watermark = (APVWatermark) systemMap.get(SYSTEM_NAMES.WATERMARKS);
	}
	
	@SuppressWarnings("unchecked")
	protected void configureSwitches() {
		List<Switch> ss = (List<Switch>)configurator.loadAVPPlugins(SYSTEM_NAMES.SWITCHES);
		ss.forEach(s -> switches.put(s.name, s));
		
		audioListenerDiagnosticSwitch = switches.get(SWITCH_NAMES.AUDIO_LISTENER_DIAGNOSTIC.name);
		consoleOutputSwitch = switches.get(SWITCH_NAMES.CONSOLE_OUTPUT.name);
		debugPulseSwitch = switches.get(SWITCH_NAMES.DEBUG_PULSE.name);
		frameStroberSwitch = switches.get(SWITCH_NAMES.FRAME_STROBER.name);
		flashFlagSwitch = switches.get(SWITCH_NAMES.FLASH_FLAG.name);
		helpSwitch = switches.get(SWITCH_NAMES.HELP.name);
		popularityPoolSwitch = switches.get(SWITCH_NAMES.POPULARITY_POOL.name);
		scrambleModeSwitch = switches.get(SWITCH_NAMES.SCRAMBLE_MODE.name);
		showSettingsSwitch = switches.get(SWITCH_NAMES.SHOW_SETTINGS.name);
		videoGameSwitch = switches.get(SWITCH_NAMES.VIDEO_GAME.name);
		welcomeSwitch = switches.get(SWITCH_NAMES.WELCOME.name);
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
		eventMap.put(EventTypes.K_SCOPE, new CoreEvent(this, EventTypes.K_SCOPE));
		eventMap.put(EventTypes.SCENE_COMPLETE, new CoreEvent(this, EventTypes.SCENE_COMPLETE));
		eventMap.put(EventTypes.SETLIST_COMPLETE, new CoreEvent(this, EventTypes.SETLIST_COMPLETE));
		eventMap.put(EventTypes.STROBE, new CoreEvent(this, EventTypes.STROBE));
		eventMap.put(EventTypes.COMMAND_INVOKED, new CommandInvokedEvent(this));
		eventMap.put(EventTypes.SPARK, new DrawShapeEvent(this, EventTypes.SPARK));
		eventMap.put(EventTypes.CARNIVAL, new DrawShapeEvent(this, EventTypes.CARNIVAL));
		eventMap.put(EventTypes.STAR, new DrawShapeEvent(this, EventTypes.STAR));
		eventMap.put(EventTypes.RANDOM_MESSAGE, new DrawShapeEvent(this, EventTypes.RANDOM_MESSAGE));
		eventMap.put(EventTypes.TWIRL, new DrawShapeEvent(this, EventTypes.TWIRL));
		eventMap.put(EventTypes.MARQUEE, new DrawShapeEvent(this, EventTypes.MARQUEE));
		eventMap.put(EventTypes.EARTHQUAKE, new DrawShapeEvent(this, EventTypes.EARTHQUAKE));
		eventMap.put(EventTypes.WATERMARK, new DrawShapeEvent(this, EventTypes.WATERMARK));
		eventMap.put(EventTypes.APV_CHANGE, new APVChangeEvent(this));
		eventMap.put(EventTypes.LOCATION, new CoreEvent(this, EventTypes.LOCATION));
		eventMap.put(EventTypes.ALDA, new CoreEvent(this, EventTypes.ALDA));
		eventMap.put(EventTypes.COLOR_CHANGE, new CoreEvent(this, EventTypes.COLOR_CHANGE));
		eventMap.put(EventTypes.SONG_START, new CoreEvent(this, EventTypes.SONG_START));
		eventMap.put(EventTypes.SET_PACK_START, new CoreEvent(this, EventTypes.SET_PACK_START));
		eventMap.put(EventTypes.MOUSE_PULSE, new CoreEvent(this, EventTypes.MOUSE_PULSE));
	}
	
	
	public String getConfig() {
		//Version number 
		StringBuffer buffer = new StringBuffer(System.lineSeparator());
		addConstant(buffer, FLAGS.APV_CONFIG_VERSION, getVersionInfo().getVersion());
		
		//Constants
		addConstant(buffer, FLAGS.AUTO_ADD_SOBLE, String.valueOf(isAutoAddSobleEnabled()));
		addConstant(buffer, FLAGS.AUTO_LOADED_BACKGROUND_FOLDER, "\"" + getConfigString(FLAGS.AUTO_LOADED_BACKGROUND_FOLDER.apvName()) + "\"");
		addConstant(buffer, FLAGS.CONTROL_MODE, getCurrentControlMode().name());
		addConstant(buffer, FLAGS.COUNTDOWN_PCT, getConfigString(FLAGS.COUNTDOWN_PCT.apvName()));
		addConstant(buffer, FLAGS.DEBUG_AGENT_MESSAGES, String.valueOf(getConfigBoolean(FLAGS.DEBUG_AGENT_MESSAGES.apvName())));
		addConstant(buffer, FLAGS.DEBUG_SYS_MESSAGES, String.valueOf(isDebugSystemMessages()));
		addConstant(buffer, FLAGS.DEFAULT_SHAPE_SYSTEM_ALPHA, String.valueOf(getDefaultShapeSystemAlpha()));
		addConstant(buffer, FLAGS.FULL_SCREEN, String.valueOf(getConfigBoolean(FLAGS.FULL_SCREEN.apvName())));
		addConstant(buffer, FLAGS.LINE_IN, String.valueOf(getConfigBoolean(FLAGS.LINE_IN.apvName())));
		addConstant(buffer, FLAGS.LISTEN_ONLY, String.valueOf(getConfigBoolean(FLAGS.LISTEN_ONLY.apvName())));
		addConstant(buffer, FLAGS.MARQUEE_FRAMES, String.valueOf(getMarqueeFrames()));
		addConstant(buffer, FLAGS.MUSIC_DIR, "\"" + getConfigString(FLAGS.MUSIC_DIR.apvName()) + "\"");
		addConstant(buffer, FLAGS.MONITORING_ENABLED, String.valueOf(isMonitoringEnabled()));
		addConstant(buffer, FLAGS.OCEAN_NAME, "\"" + getConfigString(FLAGS.OCEAN_NAME.apvName()) + "\"");
		addConstant(buffer, FLAGS.PULSE_SENSITIVITY, String.valueOf(getConfigurator().getRootConfig().getInt(FLAGS.PULSE_SENSITIVITY.apvName())));
		addConstant(buffer, FLAGS.QUIET_WINDOW_SIZE, String.valueOf(getConfigurator().getRootConfig().getInt(FLAGS.QUIET_WINDOW_SIZE.apvName())));
		addConstant(buffer, FLAGS.SCRAMBLE_SYSTEMS, String.valueOf(getConfigBoolean(FLAGS.SCRAMBLE_SYSTEMS.apvName())));
		addConstant(buffer, FLAGS.SCREEN_WIDTH, String.valueOf(width));
		addConstant(buffer, FLAGS.SCREEN_HEIGHT, String.valueOf(height));	
		addConstant(buffer, FLAGS.TREE_COMPLEXITY_CUTOFF, getConfigString(FLAGS.TREE_COMPLEXITY_CUTOFF.apvName()));
		addConstant(buffer, FLAGS.TREE_MIN_SIZE, getConfigString(FLAGS.TREE_MIN_SIZE.apvName()));
		addConstant(buffer, FLAGS.WATERMARK_FRAMES, String.valueOf(getWatermarkFrames()));
		addConstant(buffer, FLAGS.FRAME_RATE, String.valueOf(getFrameRate()));
		
		
		//helper configs
		addHelperConfig(buffer, getFontHelper());
		addHelperConfig(buffer, getRandomMessagePainter());
		addHelperConfig(buffer, getStartupCommandRunner());
		addHelperConfig(buffer, getFileCommandRunner());
		addHelperConfig(buffer, getImageHelper());
		
		//setList
		APVSetListPlayer sl = getSetListPlayer();
		if (sl != null && !sl.getSetList().isEmpty()) {
			addConstant(buffer, FLAGS.SET_LIST, String.valueOf(true));
			buffer.append(sl.getConfig());
		} else {
			addConstant(buffer, FLAGS.SET_LIST, String.valueOf(isSetList()));
		}
		
		return buffer.toString();
	}
	
	private void addHelperConfig(StringBuffer buffer, APVPlugin plugin) {
		if (plugin != null) {
			String config = plugin.getConfig();
			if (config != null) {
				buffer.append(config);
			}
		}
	}
	
	private void addConstant(StringBuffer buffer, FLAGS flag, String value) {
		buffer.append("apv." + flag.name + " = " + value);
		buffer.append(System.lineSeparator());
	}
	
	private void dumpStartupFlags() {
		StringBuffer buffer = new StringBuffer("Startup options for APV Version: ");
		buffer.append(new VersionInfo(this).getVersion());
		buffer.append(System.lineSeparator());
		
		Main.FLAGS.VALUES.forEach(flag -> {
			buffer.append("-D");
			buffer.append(flag.apvName());
			buffer.append("=");
			buffer.append(flag.description());
			buffer.append("   value: ");
			buffer.append(getConfigValueForFlag(flag));
			buffer.append(System.lineSeparator());
		});
		
		System.out.println(buffer.toString());
	}
	
	private String getFriendlySetPackName(String setpack) {
		File f = new File(setpack);
		return f.getName();
	}
}
