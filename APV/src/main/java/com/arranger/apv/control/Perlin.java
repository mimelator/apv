package com.arranger.apv.control;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.CommandSystem;
import com.arranger.apv.CommandSystem.APVCommand;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.loc.PerlinNoiseWalkerLocationSystem;

import processing.core.PApplet;
import processing.event.KeyEvent;

public class Perlin extends PulseListeningControlSystem {
	
	private static final Logger logger = Logger.getLogger(Perlin.class.getName());
	
	private static final Character [] AUTO_CHAR_COMMANDS = {
			Main.SPACE_BAR_KEY_CODE,
			'r',
			'f', 
			'b',
			'o',
			't',
			'c',
			'n',
			PApplet.ENTER,
	};
	
	private static final Integer [] AUTO_KEY_COMMANDS = {
			PApplet.UP,
			PApplet.DOWN,
			PApplet.RIGHT,
			PApplet.LEFT,
	};
	
	private static final int COMMAND_SIZE = 10;
	private static final int DEFAULT_PULSES_TO_SKIP = 24;
	
	private PerlinNoiseWalkerLocationSystem walker;
	private KeyEvent [][] commandGrid = null;
	
	public Perlin(Main parent) {
		super(parent);
		resetLocator(1);
		autoSkipPulseListener = new PulseListener(parent, DEFAULT_PULSES_TO_SKIP);
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand('>', "Walker++", "Increases the size of the Command Walker in Perlin mode", event -> incWalker());
		cs.registerCommand('<', "Walker--", "Decreases the size of the Command Walker in Perlin mode", event -> decWalker());
	}
	
	@Override
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---Walker Size: " + walker.getScale());
		super.addSettingsMessages();
	}
	
	@Override
	protected int getDefaultPulsesToSkip() {
		return DEFAULT_PULSES_TO_SKIP;
	}

	@Override
	protected KeyEvent _getNextCommand() {
		if (commandGrid == null) {
			initializeCommandGrid();
		}

		//Get the point at scale it to our grid
		Point2D currentPoint = walker.getCurrentPoint();
		int x = (int)currentPoint.getX() % COMMAND_SIZE;
		int y = (int)currentPoint.getY() % COMMAND_SIZE;
		
		if (logger.isLoggable(Level.FINE)) {
			DecimalFormat df2 = new DecimalFormat(".##");
			String format = String.format("Looking for command at point [%1s,%2s] scaled to [%3d,%4d]", 
					df2.format(currentPoint.getX()), df2.format(currentPoint.getY()), x, y);
			logger.fine(format);
		}
		
		KeyEvent keyEvent = commandGrid[x][y];
		debugKeyEvent(keyEvent);
		return keyEvent;
	}

	public void incWalker() {
		resetLocator(walker.getScale() + 1);
	}
	
	public void decWalker() {
		resetLocator(walker.getScale() - 1);
	}
	
	protected void resetLocator(int scale) {
		walker = new PerlinNoiseWalkerLocationSystem(parent, scale);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.PERLIN;
	}

	protected void initializeCommandGrid() {
		commandGrid = new KeyEvent[COMMAND_SIZE][COMMAND_SIZE];
		
		CommandSystem cs = parent.getCommandSystem();
		Map<Integer, List<APVCommand>> keyCommands = cs.getKeyCommands();
		Map<Character, List<APVCommand>> charCommands = cs.getCharCommands();
		
		List<Character> charList = Arrays.asList(AUTO_CHAR_COMMANDS);
		List<Integer> intList = Arrays.asList(AUTO_KEY_COMMANDS);
		
		Collections.shuffle(charList);
		Collections.shuffle(intList);
		
		Iterator<Character> charIterator = charList.iterator();
		Iterator<Integer> intIterator = intList.iterator();
		
		//create a grid of commands for the walker to walk over
		for (KeyEvent [] row : commandGrid) {
			for (int index = 0; index < row.length; index++) {
				KeyEvent event = null;
				if (parent.randomBoolean()) {
					if (!intIterator.hasNext()) {
						intIterator = intList.iterator();
					}
					Integer next = intIterator.next();
					event = createKeyEvent(next, keyCommands.get(next));
				} else {
					if (!charIterator.hasNext()) {
						charIterator = charList.iterator();
					}
					char next = charIterator.next();
					event = createKeyEvent(next, charCommands.get(next), parent.randomBoolean());
				}
				
				row[index] = event;
			}
		}
		
		//TODO add more scrambles
		
		if (logger.isLoggable(Level.FINE)) {
			for (KeyEvent [] row : commandGrid) {
				for (KeyEvent cmd : row) {
					debugKeyEvent(cmd);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void debugKeyEvent(KeyEvent keyEvent) {
		if (logger.isLoggable(Level.FINE)) {
			List<APVCommand> commandList = (List<APVCommand>)keyEvent.getNative();
			logger.fine("Command: " + commandList.get(0).getName() + " [shift=" + keyEvent.isShiftDown() + "]");
		}
	}

}
