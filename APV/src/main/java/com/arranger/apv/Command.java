package com.arranger.apv;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import processing.core.PApplet;

public enum Command {
	
	//Switches
	SWITCH_AGENT('x', "AgentSwitch", "Enables/Disables the registered agents"),
	SWITCH_HELP('h', "HelpSwitch", "Shows/Hides the Help Screen"),
	SWITCH_SETTINGS('q', "SettingsSwitch", "Shows/Hides the Help Screen"),
	SWITCH_VIDEOGAME('v', "VideoGameSwitch", "Displays the game progress"),
	SWITCH_LIKED_SCENES('l', "LikedScenesSwitch", "Enables/Disables the Liked Scene mode"),
	SWITCH_PULSE_LISTENER('7', "PulseListenerSwitch", "Enables/Disables the registered pulse listeners"),
	SWITCH_FRAME_STROBER('8', "FrameStroberSwitch", "Enables/Disables the strobing the screen"),
	SWITCH_CONTINUOUS_CAPTURE('9', "ContinuousCaptureSwitch", "Enables/Disables the saving all frames to disk"),
	
	SWITCH_FOREGROUNDS('1', "ForegroundsSwitch", "Enables/Disables the foregrounds"),
	SWITCH_BACKGROUNDS('2', "BackgroundsSwitch", "Enables/Disables the backgrounds"),
	SWITCH_BACKDROPS('3', "BackdropsSwitch", "Enables/Disables the backdrops"),
	SWITCH_FILTERS('4', "FiltersSwitch", "Enables/Disables the filters"),
	SWITCH_MESSAGES('5', "MessagesSwitch", "Enables/Disables the messages"),
	SWITCH_TRANSITIONS('6', "TransitionsSwitch", "Enables/Disables the transitions"),
	
	//Cyclers
	CYCLE_MESSAGES('m', "Message", "Cycles through the message (reverse w/the shift key held)"),
	CYCLE_TRANSITIONS('n', "Transition", "Cycles through the transition (reverse w/the shift key held)"),
	CYCLE_COLORS('c', "Colors", "Cycles through the colors (reverse w/the shift key held)"),
	CYCLE_FILTERS('t', "Filter", "Cycles through the filters (reverse w/the shift key held)"),
	CYCLE_LOCATIONS(PApplet.ENTER, "Enter", "Cycles through the locations (reverse w/the shift key held)"),
	CYCLE_BACKDROPS('o', "Backdrop", "Cycles through the backdrops"),
	CYCLE_BACKGROUNDS('b', "Background", "Cycles through the backgrounds"),
	CYCLE_FOREGROUNDS('f', "Foreground", "Cycles through the foregrounds"),
	CYCLE_CONTROL_MODE('z', "Cycle Mode", "Cycles between all the available Modes (reverse w/the shift key held)"),
	
	//Typical commands
	MANUAL('/', "Manual", "Sets mode to Manual and disabled Agents"),
	PANIC('?', "Panic", "Resets switches to their defaults"),
	SCRAMBLE(Main.SPACE_BAR_KEY_CODE, "SpaceBar", "Scrambles all the things"),
	WINDOWS('w', "SettingsWindow", "Popup window to display Help"),
	SAVE_CONFIGURATION('0', "Save Configuration", "Saves the current configuration to disk"),
	SCREEN_SHOT('s', "ScreenShot", "Saves the current frame to disk"),
	PERF_MONITOR('j', "Perf Monitor", "Outputs the slow monitor data to the console"),
	REVERSE('r', "Reverse Path", "Changes the direction of the path"),
	AUDIO_INC('+', "Audio++", "Increases the audio sensitivity"),
	AUDIO_DEC('-', "Audio--", "Decreases the audio sensitivity"),
	PARTICLE_SCALAR('p', "Particle Scalar", "Increases/Decreases Number Particles by a %"),
	FRAME_STROBER_STROBE_FRAMES('a', "FrameStrober Strobe Frame", "Increases/Decreases the frames to strobe (hold shift to decrease)"),
	GRAVITY('g', "Gravity", "Increases/Decreases Gravity"),
	PULSE_SKIP_INC(']', "Pulse++", "Increases the number of pulses to skip in auto/perlin mode"),
	PULSE_SKIP_DEC('[', "Pulse--", "Decrease the number of pulses to skip in auto/perlin mode"),
	TRANSITION_FRAMES_INC('}', "Transition Frames", "Increments the number of frames for each transition"),
	TRANSITION_FRAMES_DEC('{', "Transition Frames", "Decrements the number of frames for each transition "),
	WALKER_INC('>', "Walker++", "Increases the stride of the Command Walker in Perlin mode"),
	WALKER_DEC('<', "Walker--", "Decrease the stride of the Command Walker in Perlin mode"),
	QUIET_WINDOW_LENGTH_INC(')', "QuietWindow++", "Increases the duration of the Quiet Window in Snap mode"),
	QUIET_WINDOW_LENGTH_DEC('(', "QuietWindow--", "Decreases the duration of the Quiet Window in Snap mode"),
	
	//Key code Commands
	DOWN_ARROW(PApplet.DOWN, "Down", "Removes the current scene from the 'liked' list"),
	UP_ARROW(PApplet.UP, "Up", "Adds the current scene to the 'liked' list"),
	LEFT_ARROW(PApplet.LEFT, "Left", "Cycles through the liked scenes in reverse"),
	RIGHT_ARROW(PApplet.RIGHT, "Right", "Cycles through the liked scenes"),
	
	//Hot Keys
	HOT_KEY_1('!', "HotKey1", "Triggers the HotKey"),
	HOT_KEY_2('@', "HotKey2", "Triggers the HotKey"),
	HOT_KEY_3('#', "HotKey3", "Triggers the HotKey"),
	HOT_KEY_4('$', "HotKey4", "Triggers the HotKey"),
	HOT_KEY_5('%', "HotKey5", "Triggers the HotKey"),
	HOT_KEY_6('^', "HotKey6", "Triggers the HotKey"),
	HOT_KEY_7('&', "HotKey6", "Triggers the HotKey"),
	HOT_KEY_8('*', "HotKey8", "Triggers the HotKey");
	
	private int commandKey;
	private char charKey;
	private String helpText;
	private String displayName;
	private boolean hasCharKey = true;
	
	private Command(char charKey, String displayName, String helpText) {
		this.charKey = charKey;
		this.displayName = displayName;
		this.helpText = helpText;
	}

	private Command(int commandKey, String displayName, String helpText) {
		this.commandKey = commandKey;
		this.displayName = displayName;
		this.helpText = helpText;
		hasCharKey = false;
	}
	
	public int getCommandKey() {
		return commandKey;
	}
	
	public char getCharKey() {
		return charKey;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getKey() {
		return hasCharKey ? String.valueOf(charKey) : String.valueOf(commandKey);
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
		Command command = Arrays.asList(Command.values()).stream().
				filter(c -> c.charKey == key).findFirst().get();
		return command;
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
		});
		//All done with the check
	}
}
