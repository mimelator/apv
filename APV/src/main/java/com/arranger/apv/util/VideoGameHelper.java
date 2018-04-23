package com.arranger.apv.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Main;

public class VideoGameHelper extends APVPlugin {
	
	private static final DecimalFormat decFormat = new DecimalFormat("#.###");

	Map<String, Integer> cmdStatMap = new HashMap<String, Integer>();
	int totalCommands = 0;
	long startTime = System.currentTimeMillis();
	String lastCommandName = "";
	
	public VideoGameHelper(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandInvokedEvent().register(() -> recordLastCommand());
		});
	}
	
	public int getTotalCommands() {
		return totalCommands;
	}
	
	public long getTotalSeconds() {
		long totalMillis = System.currentTimeMillis() - startTime;
		return TimeUnit.MILLISECONDS.toSeconds(totalMillis);
	}
	
	public float getCommandsPerSec() {
		return (float)totalCommands / (float)getTotalSeconds();
	}

	public String getTimeStamp() {
		return format(System.currentTimeMillis() - startTime);
	}
	
	public String getLastCommand() {
		return lastCommandName;
	}
	
	public Map<String, Integer> getCommandStatMap() {
		return cmdStatMap;
	}
	
	public void showStats() {
		
		String [] msgs = new String[] {
				String.format("Time: %s", getTimeStamp()),
				String.format("Cmds/sec: %s", decFormat.format(getCommandsPerSec())),
				String.format("Last Command: %s[%d]", lastCommandName, cmdStatMap.get(lastCommandName)),
				String.format("Total Count: %d", totalCommands),
		};

		new TextPainter(parent).drawText(Arrays.asList(msgs), SafePainter.LOCATION.LOWER_LEFT);
	}
	
	private void recordLastCommand() {
		Command cmd = parent.getCommandSystem().getLastCommand();
		lastCommandName = cmd.name();
		Integer count = cmdStatMap.get(lastCommandName);
		if (count == null) {
			cmdStatMap.put(lastCommandName, new Integer(1));
		} else {
			cmdStatMap.put(lastCommandName, ++count);
		}
		totalCommands++;
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
