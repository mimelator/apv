package com.arranger.apv.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Main;

public class VideoGameDisplay extends APVPlugin {
	
	private static final DecimalFormat decFormat = new DecimalFormat("#.###");

	Map<String, Integer> commandCounter = new HashMap<String, Integer>();
	int totalCommands = 0;
	long startTime = System.currentTimeMillis();
	String lastCommandName = "";
	
	public VideoGameDisplay(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandInvokedEvent().register(() -> recordLastCommand());
		});
	}
	
	private void recordLastCommand() {
		Command cmd = parent.getCommandSystem().getLastCommand();
		lastCommandName = cmd.name();
		Integer count = commandCounter.get(lastCommandName);
		if (count == null) {
			commandCounter.put(lastCommandName, new Integer(1));
		} else {
			commandCounter.put(lastCommandName, ++count);
		}
		totalCommands++;
	}

	public void showStats() {
		
		long totalMillis = System.currentTimeMillis() - startTime;
		long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis);
		float commandsPerSec = (float)totalCommands / (float)totalSeconds;
		
		String [] msgs = new String[] {
				String.format("Time: %1s", format(totalMillis)),
				String.format("Cmds/sec: %1s", decFormat.format(commandsPerSec)),
				String.format("Last Command: %1s[%2d]", lastCommandName, commandCounter.get(lastCommandName)),
				String.format("Total Count: %1d", totalCommands),
		};
		
		new SafePainter(parent, () -> {
			Main p = parent;
			int x = SettingsDisplay.TEXT_OFFSET;
			int y = p.height - (p.height / 11);
			p.translate(x, y);
			p.getSettingsDisplay().drawText(Arrays.asList(msgs));
			p.translate(-x, -y);
		}).paint();
	}
	
	/**
	 * https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
	 */
	private String format(long millis) {
		return String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));   
	}
}
