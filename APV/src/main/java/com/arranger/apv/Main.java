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
import com.arranger.apv.util.Monitor;
import com.arranger.apv.util.Oscillator;
import com.arranger.apv.util.Particles;
import com.arranger.apv.util.SettingsDisplay;
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
	
	protected Map<String, Switch> switches;
	protected List<ControlSystem> controls;
	protected CONTROL_MODES currentControlMode;
	protected List<APVPlugin> listeners;
	
	//Useful helper classes
	protected Configurator configurator;
	protected CommandSystem commandSystem;
	protected Audio audio;
	protected Gravity gravity;
	protected FrameStrober frameStrober;
	protected Monitor monitor;
	protected SettingsDisplay settingsDisplay;
	protected Oscillator oscillator;
	protected LoggingConfig loggingConfig;
	protected HelpDisplay helpDisplay;
	protected APVPulseListener pulseListener;
	protected Particles particles;
	protected VersionInfo versionInfo;
	protected FileHelper fileHelper;

	//Internal data
	private boolean scrambleMode = false;	//this is a flag to signal to the TransitionSystem for #onDrawStart
	
	
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
					monitorSwitch, 
					frameStroberSwitch,
					videoCaptureSwitch;

	
	public static void main(String[] args) {
		PApplet.main(new String[] {Main.class.getName()});
	}

	@SuppressWarnings("unchecked")
	public void settings() {
		//can't load the settings until i load the configurator
		//don't want to load the configurator until i load the logging
		loggingConfig = new LoggingConfig(this);
		loggingConfig.configureLogging();
		
		configurator = new Configurator(this);
		configureSwitches((List<Switch>)configurator.loadAVPPlugins("switches"));
		
		Config rootConfig = configurator.getRootConfig();
		boolean isFullScreen = rootConfig.getBoolean("apv.fullScreen");
		if (isFullScreen) {
			fullScreen(RENDERER);
		} else {
			size(rootConfig.getInt("apv.screen.width"), rootConfig.getInt("apv.screen.height"), RENDERER);
		}
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

	public List<APVPlugin> getListeners() {
		return listeners;
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
	
	public float oscillate(float low, float high, float oscSpeed) {
		return oscillator.oscillate(low, high, oscSpeed);
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
		monitor = new Monitor(this);
		frameStrober = new FrameStrober(this);
		
		locations = (List<LocationSystem>)configurator.loadAVPPlugins("locations");
		colors = (List<ColorSystem>)configurator.loadAVPPlugins("colors");
		controls = (List<ControlSystem>)configurator.loadAVPPlugins("controls");
		backgrounds = (List<ShapeSystem>)configurator.loadAVPPlugins("backgrounds");
		backDrops = (List<BackDropSystem>)configurator.loadAVPPlugins("backDrops");
		foregrounds = (List<ShapeSystem>)configurator.loadAVPPlugins("foregrounds");
		filters = (List<Filter>)configurator.loadAVPPlugins("filters");
		transitions = (List<TransitionSystem>)configurator.loadAVPPlugins("transitions");
		messages = (List<MessageSystem>)configurator.loadAVPPlugins("messages");	
		listeners = (List<APVPlugin>)configurator.loadAVPPlugins("pulse-listeners");
		scenes = (List<Scene>)configurator.loadAVPPlugins("scenes");
		
		//currentControlMode
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(configurator.getRootConfig().getString("apv.controlMode"));

		setupSystems(foregrounds);
		setupSystems(backgrounds);
		setupSystems(backDrops);
		setupSystems(transitions);
		setupSystems(messages);
		setupSystems(scenes);
		//setupSystems(filters);  Filters get left out of the setup() for now because they don't extends the ShapeSystem
		
		//processing hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		background(Color.BLACK.getRGB());
	}

	protected void initializeCommands() {
		CommandSystem cs = commandSystem;
		
		registerNonFreezableSwitchCommand(helpSwitch, 'h');
		registerNonFreezableSwitchCommand(showSettingsSwitch, 'q');
		registerNonFreezableSwitchCommand(monitorSwitch, 'm');
		
		registerSwitchCommand(foreGroundSwitch, '1');
		registerSwitchCommand(backGroundSwitch, '2');
		registerSwitchCommand(backDropSwitch, '3');
		registerSwitchCommand(filtersSwitch, '4');
		registerSwitchCommand(messagesSwitch, '5');
		registerSwitchCommand(transitionSwitch, '6');
		registerSwitchCommand(pulseListenerSwitch, '7');
		registerSwitchCommand(frameStroberSwitch, '8');
		registerSwitchCommand(videoCaptureSwitch, '9');
		
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
		cs.registerCommand('e', "Scenes", "Cycles through the scenes (reverse w/the shift key held)", 
				(event) -> {if (event.isShiftDown()) sceneIndex--; else sceneIndex++;});
		
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
					for (TransitionSystem sys : transitions) {
						sys.incrementTransitionFrames();
					}
				});
		cs.registerCommand('{', "Transition Frames", "Decrements the number of frames for each transition ", 
				(event) -> {
					for (TransitionSystem sys : transitions) {
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
			currentControlMode = getControl().getControlMode().getNext();
		} else {
			currentControlMode = getControl().getControlMode().getPrevious();
		}
	}

	protected void setupSystems(List<? extends ShapeSystem> systems) {
		for (ShapeSystem system : systems) {
			system.setup();
		}
	}
	
	public void doScreenCapture() {
		String fileName = String.format("apv%08d.png", getFrameCount());
		fileHelper.getFullPath(fileName);
		logger.info("Saving image: " + fileName);
		PImage pImage = get();
		pImage.save(fileName);
		
		sendMessage(new String[] {fileName});
	}
	
	public void scramble() {
		scrambleMode = true;
		
		//switch transitions now instead of in the #doScramble
		if (!transitionSwitch.isFrozen()) {
			transitionIndex += random(transitions.size() - 1); 
		}
	}
	
	protected void doScramble() {
		//mess it all up, except for transitions which were already scrambled
		
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
		
		locationIndex += random(locations.size() - 1);
		colorIndex += random(colors.size() - 1);
		
		//send out a cool message about the new system
		if (messagesSwitch.isEnabled()) {
			List<String> msgs = new ArrayList<String>();
			if (backDropSwitch.isEnabled()) {
				msgs.add(getBackDrop().getDisplayName());
			}

			if (backGroundSwitch.isEnabled()) {
				msgs.add(getForeground().getDisplayName());
			}
			
			if (foreGroundSwitch.isEnabled()) {
				msgs.add(getBackground().getDisplayName());
			}
			
			sendMessage(msgs.toArray(new String[msgs.size()]));
		}
		
		//reset the flag
		scrambleMode = false;
	}
	
	public void sendMessage(String [] messages) {
		if (messagesSwitch.isEnabled()) {
			getMessage().onNewMessage(messages);
		}
	}
	
	public void draw() {
		logger.info("Drawing frame: " + getFrameCount());
		
		if (frameStroberSwitch.isEnabled()) {
			if (frameStrober.isSkippingFrames()) {
				return;
			}
		}
		
		if (showSettingsSwitch.isEnabled()) {
			settingsDisplay.prepareSettingsMessages();
			settingsDisplay.addPrimarySettingsMessages();
		}
		
		TransitionSystem transition = prepareTransition(false);
		
		Scene scene = (Scene)getPlugin(scenes, sceneIndex);
		if (scene.isNormal()) {
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

			scene.setSystems(backDrop, bgSys, fgSys, filter);
		} else {
			//using a "non-normal" scene.  See if it is brand new?  If so, start a transition
			if (scene.isNew()) {
				transition = prepareTransition(true);
			}
		}
		
		drawSystem(scene, "scene");
		
		if (monitorSwitch.isEnabled()) {
			monitor.doMonitorCheck(scene);
		}
		
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
	
	public void drawSystem(ShapeSystem s, String debugName) {
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
		monitorSwitch = switches.get("Monitor");
		frameStroberSwitch = switches.get("FrameStrober");
		videoCaptureSwitch = switches.get("VideoCapture");
	}
	
	/**
	 * collect: Constants
	 */
	public String getConfig() {
		StringBuffer buffer = new StringBuffer(System.lineSeparator());
		
		//Constants
		addConstant(buffer, "controlMode", getCurrentControlMode().name());
		addConstant(buffer, "fullScreen", String.valueOf(getConfigurator().getRootConfig().getBoolean("apv.fullScreen")));
		addConstant(buffer, "screen.width", String.valueOf(width));
		addConstant(buffer, "screen.height", String.valueOf(height));		
		
		return buffer.toString();
	}
	
	private void addConstant(StringBuffer buffer, String name, String value) {
		buffer.append("apv." + name + " = " + value);
		buffer.append(System.lineSeparator());
	}
}
