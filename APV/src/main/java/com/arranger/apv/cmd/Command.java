package com.arranger.apv.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.event.Event;
import processing.event.KeyEvent;

public enum Command {
	
	//FREEZE('|', "Freeze", "Used for debugging purposes"),
	
	//Switches
	SWITCH_AGENT('x', "AgentSwitch", "Enables/Disables the registered agents"),
	SWITCH_HELP('h', "HelpSwitch", "Shows/Hides the Help Screen,"),
	SWITCH_WELCOME('e', "WelcomeSwitch", "Shows/Hides the Welcome Screen,"),
	SWITCH_SETTINGS('q', "SettingsSwitch", "Shows/Hides the Help Screen"),
	SWITCH_VIDEOGAME('v', "VideoGameSwitch", "Displays the game progress"),
	SWITCH_DEBUG_PULSE('d', "DebugPulseSwitch", "Displays the debug pulse agent"),
	SWITCH_LIKED_SCENES('l', "LikedScenesSwitch", "Enables/Disables the Liked Scene mode"),
	SWITCH_CONSOLE_OUTPUT(DeadCommandCode.next(), "Console Output Frame Switch", "Enables/Disables the frame info to console"),
	SWITCH_PULSE_LISTENER(DeadCommandCode.next(), "PulseListenerSwitch", "Enables/Disables the registered pulse listeners"),
	SWITCH_WATERMARK(DeadCommandCode.next(), "WatermarkSwitch", "Enables/Disables watermarks"),
	SWITCH_FRAME_STROBER(DeadCommandCode.next(), "FrameStroberSwitch", "Enables/Disables the strobing the screen"),
	SWITCH_CONTINUOUS_CAPTURE(DeadCommandCode.next(), "ContinuousCaptureSwitch", "Enables/Disables the saving all frames to disk"),
	
	SWITCH_FOREGROUNDS('1', "ForegroundsSwitch", "Enables/Disables/Freezes(use <CMD>) the foregrounds", false),
	SWITCH_BACKGROUNDS('2', "BackgroundsSwitch", "Enables/Disables/Freezes(use <CMD>) the backgrounds", false),
	SWITCH_BACKDROPS('3', "BackdropsSwitch", "Enables/Disables/Freezes(use <CMD>) the backdrops", false),
	SWITCH_FILTERS('4', "FiltersSwitch", "Enables/Disables/Freezes(use <CMD>) the filters", false),
	SWITCH_MESSAGES('5', "MessagesSwitch", "Enables/Disables/Freezes(use <CMD>) the messages", false),
	SWITCH_TRANSITIONS('6', "TransitionsSwitch", "Enables/Disables/Freezes(use <CMD>) the transitions", false),
	SWITCH_SHADERS('7', "ShadersSwitch", "Enables/Disables/Freezes(use <CMD>) the shader", false),
	
	FREEZE_FOREGROUNDS(DeadCommandCode.next(), "Foregrounds ", "Freezes/UnFreezes the foregrounds"),
	FREEZE_BACKGROUNDS(DeadCommandCode.next(), "Backgrounds", "Freezes/UnFreezes the backgrounds"),
	FREEZE_BACKDROPS(DeadCommandCode.next(), "Backdrops", "Freezes/UnFreezes the backdrops"),
	FREEZE_FILTERS(DeadCommandCode.next(), "Filters", "Freezes/UnFreezes the filters"),
	FREEZE_MESSAGES(DeadCommandCode.next(), "Messages", "Freezes/UnFreezes the messages"),
	FREEZE_TRANSITIONS(DeadCommandCode.next(), "Transitions", "Freezes/UnFreezes the transitions"),
	FREEZE_SHADERS(DeadCommandCode.next(), "Shaders", "Freezes/UnFreezes the shader"),
	
	//Cyclers
	CYCLE_MESSAGES('m', "Message", "Cycles through the message (SHIFT option)"),
	CYCLE_TRANSITIONS('n', "Transition", "Cycles through the transition (SHIFT option)"),
	CYCLE_COLORS('c', "Colors", "Cycles through the colors (SHIFT and ALT options)"),
	CYCLE_FILTERS('t', "Filter", "Cycles through the filters (SHIFT and ALT options)"),
	CYCLE_LOCATIONS(PApplet.ENTER, "Enter", "Cycles through the locations"),
	CYCLE_BACKDROPS('o', "Backdrop", "Cycles through the backdrops (SHIFT and ALT options)"),
	CYCLE_BACKGROUNDS('b', "Background", "Cycles through the backgrounds (SHIFT and ALT options)"),
	CYCLE_FOREGROUNDS('f', "Foreground", "Cycles through the foregrounds (SHIFT and ALT options)"),
	CYCLE_SHADERS('s', "Shader", "Cycles through the shaders (SHIFT and ALT options)"),
	CYCLE_CONTROL_MODE('z', "Cycle Control Mode", "Cycles between all the available Control Modes (SHIFT)"),
	CYCLE_SET_PACK('k', "Cycle Set Pack", "Cycles between all the available Set Packs (SHIFT)"),
	CYCLE_WATERMARK(DeadCommandCode.next(), "Next Watermark", "Queues up the next Watermark"),
	
	//Typical commands
	MANUAL('/', "Manual", "Sets mode to Manual and disabled Agents"),
	RESET('?', "Reset", "Resets switches to their defaults"),
	SCRAMBLE(Main.SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things"),
	RANDOMIZE_COLORS('`', "RandomizeSetPackColors", "Randomly changes the current Set Pack color scheme"),
	RANDOMIZE_SETPACK(DeadCommandCode.next(), "Randomize SetPack Songs and Colors", "Randomly changes the current Set Pack color scheme and shuffles songs"),
	WINDOWS('w', "SettingsWindow", "Popup window to display Help"),
	SAVE_CONFIGURATION('0', "Save Configuration", "Saves the current configuration to disk"),
	RELOAD_CONFIGURATION('u', "Reloads Configuration", "Reloads all of the current configuration"),
	SCREEN_SHOT('i', "ScreenShot", "Saves the current frame to disk"),
	PERF_MONITOR('j', "Perf Monitor", "Outputs the slow monitor data to the console"),
	REVERSE('r', "Reverse Path", "Changes the direction of the path"),
	AUDIO_INC('+', "Audio++", "Increases the audio sensitivity"),
	AUDIO_DEC('-', "Audio--", "Decreases the audio sensitivity"),
	PARTICLE_SCALAR('p', "Particle Scalar", "Increases/Decreases Number Particles by a %"),
	FRAME_STROBER_STROBE_FRAMES(DeadCommandCode.next(), "FrameStrober Strobe Frame", "Increases/Decreases the frames to strobe (hold shift to decrease)"),
	GRAVITY('g', "Gravity", "Increases/Decreases Gravity"),
	PULSE_SKIP_INC(DeadCommandCode.next(), "Pulse++", "Increases the number of pulses to skip in auto/perlin mode"),
	PULSE_SKIP_DEC(DeadCommandCode.next(), "Pulse--", "Decrease the number of pulses to skip in auto/perlin mode"),
	TRANSITION_FRAMES_INC(DeadCommandCode.next(), "Transition Frames", "Increments the number of frames for each transition"),
	TRANSITION_FRAMES_DEC(DeadCommandCode.next(), "Transition Frames", "Decrements the number of frames for each transition "),
	WALKER_INC(DeadCommandCode.next(), "Walker++", "Increases the stride of the Command Walker in Perlin mode"),
	WALKER_DEC(DeadCommandCode.next(), "Walker--", "Decrease the stride of the Command Walker in Perlin mode"),
	QUIET_WINDOW_LENGTH_INC(DeadCommandCode.next(), "QuietWindow++", "Increases the duration of the Quiet Window in Snap mode"),
	QUIET_WINDOW_LENGTH_DEC(DeadCommandCode.next(), "QuietWindow--", "Decreases the duration of the Quiet Window in Snap mode"),
	SHOW_WATERMARK(DeadCommandCode.next(), "Show Watermark", "Displays the watermark"),
	
	//Newer Remote oriented commands
	SHOW_SONG_QUEUE(DeadCommandCode.next(), "Show SongQueue", "Displays the current set of songs queued to be played"),
	SHOW_OCEAN_SET_INFO(DeadCommandCode.next(), "Show Ocean Set Info", "Displays the current Ocean and Set"),
	SHOW_AVAILABLE_SET_PACKS(DeadCommandCode.next(), "Shows Available Set Packs", "Shows the available set packs"),
	LOAD_AVAILABLE_SET_PACKS(DeadCommandCode.next(), "Loads all Available Set Packs", "Loads the available set packs"),
	
	PLAY_SET_PACK(DeadCommandCode.next(), "Plays the named setPack", "Plays the named setPack"),
	SHOW_MARQUEE_MESSAGE(DeadCommandCode.next(), "Shows a marquee Message", "Shows a marquee message"),
	FIRE_EVENT(DeadCommandCode.next(), "Fires an event", "Fires an event"),
	SHUTDOWN(DeadCommandCode.next(), "Shutsdown", "No Restart"),
	SHOW_TREE_SCENE(DeadCommandCode.next(), "Shows a tree Message", "Shows a tree message"),
	LIVE_SETTINGS(DeadCommandCode.next(), "Shows a watermark for the available live settings", "eg: LIVE_SETTINGS:FLAG:TREE_MIN_SIZE"),
	
	//Key code Commands
	DOWN_ARROW(PApplet.DOWN, "Down", "Removes the current scene from the 'liked' list"),
	UP_ARROW(PApplet.UP, "Up", "Adds the current scene to the 'liked' list"),
	LEFT_ARROW(PApplet.LEFT, "Left", "Cycles through the liked scenes in reverse"),
	RIGHT_ARROW(PApplet.RIGHT, "Right", "Cycles through the liked scenes"),
	
	//Set List Commands
	//for mac... TODO investigate
	FFWD(java.awt.event.KeyEvent.VK_NUMPAD9, "FwdSong", "Play the next song"),
	PLAY_PAUSE(java.awt.event.KeyEvent.VK_NUMPAD8, "Play/Pause Song", "Plays or Pauses the song"),
	PREV(java.awt.event.KeyEvent.VK_NUMPAD7, "PrevSong", "Play the previous song"),
//	FFWD(java.awt.event.KeyEvent.VK_F9, "FwdSong", "Play the next song"),
//	PLAY_PAUSE(java.awt.event.KeyEvent.VK_F8, "Play/Pause Song", "Plays or Pauses the song"),
//	PREV(java.awt.event.KeyEvent.VK_F7, "PrevSong", "Play the previous song"),
	
	//Hot Keys
	HOT_KEY_1('!', "Show Frequency Response", ""),
	HOT_KEY_2('@', "", ""),
	HOT_KEY_3('#', "", ""),
	HOT_KEY_4('$', "", ""),
	HOT_KEY_5('%', "", ""),
	HOT_KEY_6('^', "", ""),
	HOT_KEY_7('&', "", ""),
	HOT_KEY_8('*', "", ""),
	
	//Macros
	MACRO_1('1', "", "", Event.CTRL),
	MACRO_2('2', "", "", Event.CTRL),
	MACRO_3('3', "", "", Event.CTRL),
	MACRO_4('4', "", "", Event.CTRL),
	MACRO_5('5', "", "", Event.CTRL),
	MACRO_6('6', "", "", Event.CTRL),
	MACRO_7('7', "", "", Event.CTRL),
	MACRO_8('8', "", "", Event.CTRL);
	
	private static final List<Command> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	
	private int commandKey;
	private char charKey;
	private String helpText;
	private String displayName;
	private boolean hasCharKey = true;
	private int modifiers;
	private boolean acceptAnyModifier = true;
	
	private String primaryArg;
	private String [] args;
	
	private Command(char charKey, String displayName, String helpText) {
		this(charKey, displayName, helpText, 0);
	}

	private Command(char charKey, String displayName, String helpText, int modifiers) {
		this(charKey, displayName, helpText, modifiers, false);
	}
	
	private Command(char charKey, String displayName, String helpText, boolean acceptAnyModifier) {
		this(charKey, displayName, helpText, 0, acceptAnyModifier);
	}
	
	private Command(char charKey, String displayName, String helpText, int modifiers, boolean acceptAnyModifier) {
		this.charKey = charKey;
		this.displayName = displayName;
		this.helpText = helpText;
		this.modifiers = modifiers;
		this.acceptAnyModifier = acceptAnyModifier;
	}
	
	private Command(int commandKey, String displayName, String helpText) {
		this.commandKey = commandKey;
		this.displayName = displayName;
		this.helpText = helpText;
		hasCharKey = false;
	}
	
	public String getPrimaryArg() {
		return primaryArg;
	}

	public void setPrimaryArg(String argument) {
		this.primaryArg = argument;
	}
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public void reset() {
		this.primaryArg = null;
		this.args = null;
	}

	public boolean hasCharKey() {
		return hasCharKey;
	}
	
	public int getCommandKey() {
		return commandKey;
	}
	
	public char getCharKey() {
		return charKey;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getKey() {
		if (hasCharKey) {
			if (modifiers == 0 || acceptAnyModifier) {
				return String.valueOf(charKey);
			} else {
				return String.format("%s+%d", charKey, modifiers);
			}
		} else {
			return java.awt.event.KeyEvent.getKeyText(commandKey);
		}
	}
	
	public String getHelpText() {
		return helpText;
	}
	
	/**
	 * This is required for hotKeys to update the helpText
	 */
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	
	/**
	 * This is required for hotKeys to update a displayName
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public static Command getCommand(char key) {
		Command command = VALUES.stream().
				filter(c -> c.charKey == key).findFirst().get();
		return command;
	}
	
	public static Command getCommand(char key, int modifier) {
		Command command = VALUES.stream().
				filter(c -> c.charKey == key && c.modifiers == modifier).findFirst().get();
		return command;
	}
	
	public static String getKeyForKeyEvent(KeyEvent event) {
		char charKey = event.getKey();
		boolean simpleAscii =  ((int)charKey) <  0x0100  && ((int)charKey) > 0x20; 
		String key = (Character.isLetterOrDigit(charKey)  || Character.isWhitespace(charKey) || simpleAscii) ? 
				String.valueOf(Character.toLowerCase(charKey)) : 
					java.awt.event.KeyEvent.getKeyText(event.getKeyCode());

				
		int mods = event.getModifiers();
		if (mods == Event.CTRL) {
			key += "+" + String.valueOf(mods);
		}
		
		return key;
	}
	
	public static String getSource(KeyEvent keyEvent) {
		String source = keyEvent.getNative().toString();
		if (source.contains("EVENT")) { // this is extra data
			source = "KeyEvent";
		}
		return source;
	}
	
	public static boolean isShiftDown(int modifiers) {
		return (modifiers & KeyEvent.SHIFT) != 0;
	}

	public static boolean isControlDown(int modifiers) {
		return (modifiers & KeyEvent.CTRL) != 0;
	}

	public static boolean isMetaDown(int modifiers) {
		return (modifiers & KeyEvent.META) != 0;
	}

	public static boolean isAltDown(int modifiers) {
		return (modifiers & KeyEvent.ALT) != 0;
	}
	
	static {
		// a static checker to see if we have duplicate commands
		Set<String> checkMap = new HashSet<String>();
		Arrays.asList(Command.values()).forEach(c -> {
			String key = c.getKey();
			if (checkMap.contains(key)) {
				throw new RuntimeException("Duplicate keys found for command.  Key: " + key);
			} else {
				checkMap.add(key);
			}
			//System.out.printf("Command: %s -> %s\n", c, key);
		});
		//All done with the check
	}
}
