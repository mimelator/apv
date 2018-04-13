package com.arranger.apv;

import java.awt.Color;
import java.io.File;
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
import com.arranger.apv.pl.SimplePL;
import com.arranger.apv.pl.StarPL;
import com.arranger.apv.util.APVPulseListener;
import com.arranger.apv.util.Configurator;
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
	public static final int NUMBER_PARTICLES = 1000;
	public static final String RENDERER = P3D;//P2D;
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private static final int BUFFER_SIZE = 512;
	public static final int DEFAULT_TRANSITION_FRAMES = 30;
	public static final int MAX_ALPHA = 255;
	
	//Defaults 
	private static final boolean FULL_SCREEN = true;

	//Don't change the following values
	public static final char SPACE_BAR_KEY_CODE = ' ';

	protected List<ShapeSystem> foregroundSystems;
	protected int foregroundIndex = 0;
	
	protected List<ShapeSystem> backgroundSystems;
	protected int backgroundIndex = 0;
	
	protected List<BackDropSystem> backDropSystems;
	protected int backDropIndex = 0;

	protected List<LocationSystem> locationSystems; 
	protected int locationIndex = 0;
	
	protected List<ColorSystem> colorSystems; 
	protected int colorIndex = 0;
	
	protected List<TransitionSystem> transitionSystems;
	protected int transitionIndex = 0;
	
	protected List<MessageSystem> messageSystems;
	protected int messageIndex = 0;
	
	protected List<Filter> filterSystems; 
	protected int filterIndex = 0;
	
	protected Map<String, Switch> switches;
	protected List<ControlSystem> controlSystems;
	protected CONTROL_MODES currentControlMode;
	
	//Useful helper classes
	protected Configurator configurator;
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
					monitorSwitch;
	
	
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

	public Collection<Switch> getSwitches() {
		return switches.values();
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
		loggingConfig = new LoggingConfig(this);
		loggingConfig.configureLogging();
		
		configurator = new Configurator(this);
		configureSwitches((List<Switch>)configurator.loadAVPPlugins("switches"));

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
		
		locationSystems = (List<LocationSystem>)configurator.loadAVPPlugins("locationSystems");
		colorSystems = (List<ColorSystem>)configurator.loadAVPPlugins("colorSystems");
		controlSystems = (List<ControlSystem>)configurator.loadAVPPlugins("controlSystems");
		backgroundSystems = (List<ShapeSystem>)configurator.loadAVPPlugins("backgroundSystems");
		backDropSystems = (List<BackDropSystem>)configurator.loadAVPPlugins("backDropSystems");
		foregroundSystems = (List<ShapeSystem>)configurator.loadAVPPlugins("foregroundSystems");
		filterSystems = (List<Filter>)configurator.loadAVPPlugins("filterSystems");
		transitionSystems = (List<TransitionSystem>)configurator.loadAVPPlugins("transitionSystems");
		messageSystems = (List<MessageSystem>)configurator.loadAVPPlugins("messageSystems");	
		
		//currentControlMode
		currentControlMode = ControlSystem.CONTROL_MODES.valueOf(configurator.getRootConfig().getString("apv.controlMode"));

		setupSystems(foregroundSystems);
		setupSystems(backgroundSystems);
		setupSystems(backDropSystems);
		setupSystems(transitionSystems);
		setupSystems(messageSystems);
		//setupSystems(filters);  Filters get left out of the setup() for now because they don't extends the ShapeSystem
		
		//listeners
		new SimplePL(this);
		new StarPL(this);
		
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
		//mess it all up, except for transitions which were already scrambled
		
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
			filterIndex += random(filterSystems.size() - 1);
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
			filter = (Filter)getPlugin(filterSystems, filterIndex);
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
		
		if (monitorSwitch.isEnabled()) {
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
	
	protected void configureSwitches(List<Switch> ss) {
		switches = new HashMap<String, Switch>(ss.size());
		for (Iterator<Switch> it = ss.iterator(); it.hasNext();) {
			Switch nextSwitch = it.next();
			switches.put(nextSwitch.name, nextSwitch);
		}
		
		foreGroundSwitch = switches.get("ForeGround"); 
		backGroundSwitch = switches.get("BackGround");
		backDropSwitch = switches.get("ForeGround");
		filtersSwitch = switches.get("Filters");
		transitionSwitch = switches.get("Transitions");
		messagesSwitch = switches.get("Messages");
		helpSwitch = switches.get("Help");
		showSettingsSwitch = switches.get("ShowSettings");
		pulseListenerSwitch = switches.get("PulseListener");
		monitorSwitch = switches.get("Monitor");
	}
}
