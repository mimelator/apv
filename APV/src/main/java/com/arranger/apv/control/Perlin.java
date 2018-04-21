package com.arranger.apv.control;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.Command;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.CommandSystem.RegisteredCommandHandler;
import com.arranger.apv.Main;
import com.arranger.apv.Switch;
import com.arranger.apv.loc.PerlinNoiseWalkerLocationSystem;

import processing.core.PApplet;
import processing.event.KeyEvent;

public class Perlin extends PulseListeningControlSystem {
	
	private static final int DEF_PULSES_BETWEEN_COMMANDS = 16;

	private static final Logger logger = Logger.getLogger(Perlin.class.getName());
	
	private static final Command [] AUTO_CHAR_COMMANDS = {
			Command.SCRAMBLE, Command.SCRAMBLE, Command.SCRAMBLE, Command.SCRAMBLE,
			Command.REVERSE,
			Command.CYCLE_FOREGROUNDS,
			Command.CYCLE_BACKGROUNDS,
			Command.CYCLE_BACKDROPS,
			Command.CYCLE_FILTERS,
			Command.CYCLE_COLORS,
			Command.CYCLE_TRANSITIONS,
			Command.CYCLE_LOCATIONS,
	};
	
	private static final int COMMAND_SIZE = 10;
	
	private PerlinNoiseWalkerLocationSystem walker;
	private KeyEvent [][] commandGrid = null;
	
	public Perlin(Main parent) {
		super(parent);
		resetLocator(1);
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.WALKER_INC, event -> incWalker());
			cs.registerHandler(Command.WALKER_DEC, event -> decWalker());
		});
	}

	
	@Override
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---Walker Size: " + walker.getScale());
		super.addSettingsMessages();
	}
	
	@Override
	protected int getDefaultPulsesToSkip() {
		if (autoSkipPulseListener == null) {	//called from the constructor in order to create the autoSkipPulseListener
			return DEF_PULSES_BETWEEN_COMMANDS;
		} else {
			return autoSkipPulseListener.getPulsesToSkip();
		}
	}

	@Override
	protected KeyEvent _getNextCommand() {
		if (commandGrid == null) {
			initializeCommandGrid();
		}

		//Don't issue a command that is frozen
		int offset = 0;
		KeyEvent keyEvent = null;
		while (keyEvent == null) {
			keyEvent = getKeyEvent(offset);
			
			String switchName = (String)keyEvent.getNative();
			if (switchName != null) {
				Switch curSwitch = parent.getSwitch(switchName);
				if (curSwitch == null) {
					throw new RuntimeException("Unknown switch: " + switchName);
				}
				
				if (curSwitch.isFrozen()) {
					keyEvent = null;
					offset++;
				}
			}
		}
		
		debugKeyEvent(keyEvent);
		return keyEvent;
	}
	
	private KeyEvent getKeyEvent(int offset) {
		//Get the point at scale it to our grid
		Point2D currentPoint = walker.getCurrentPoint();
		int x = (int)(currentPoint.getX() + offset) % COMMAND_SIZE;
		int y = (int)(currentPoint.getY() + offset) % COMMAND_SIZE;
		
		if (logger.isLoggable(Level.FINE)) {
			DecimalFormat df2 = new DecimalFormat(".##");
			String format = String.format("Looking for command at point [%1s,%2s] scaled to [%3d,%4d]", 
					df2.format(currentPoint.getX()), df2.format(currentPoint.getY()), x, y);
			logger.fine(format);
		}
		
		return commandGrid[x][y];
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
		
		List<Command> cmdList = Arrays.asList(AUTO_CHAR_COMMANDS);
		Collections.shuffle(cmdList);
		
		Iterator<Command> it = cmdList.iterator();
		
		//create a grid of commands for the walker to walk over
		for (KeyEvent [] row : commandGrid) {
			for (int index = 0; index < row.length; index++) {
				if (!it.hasNext()) {
					it = cmdList.iterator();
				}
				KeyEvent event = keyEventHelper.createKeyEvent(it.next(), parent.randomBoolean() ? 0 : PApplet.SHIFT);
				row[index] = event;
			}
		}
		
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
			List<RegisteredCommandHandler> commandList = (List<RegisteredCommandHandler>)keyEvent.getNative();
			logger.fine("Command: " + commandList.get(0).getName() + " [shift=" + keyEvent.isShiftDown() + "]");
		}
	}
}
