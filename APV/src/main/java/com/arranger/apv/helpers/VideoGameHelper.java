package com.arranger.apv.helpers;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.scene.Scene.Components;
import com.arranger.apv.util.CounterMap;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;

public class VideoGameHelper extends APVPlugin {
	
	public static final DecimalFormat decFormat = new DecimalFormat("#.###");

	//stats
	CounterMap commandMap = new CounterMap();
	CounterMap commandSourceMap = new CounterMap();
	CounterMap pluginMap = new CounterMap();
	CounterMap pluginSourceMap = new CounterMap();
	CounterMap sceneComponents = new CounterMap();
	
	int totalCommands = 0;
	int totalPluginChanges = 0;
	long startTime = System.currentTimeMillis();
	String lastCommandName = "";
	String lastNewPluginName = "";
	
	int checkPointTotalCommands;
	long checkPointStartTime;
	
	public VideoGameHelper(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandInvokedEvent().register((c, s) -> recordLastCommand(c, s));
			parent.getAPVChangeEvent().register((apv, plugin, cause) -> recordPluginChange(apv, plugin, cause));
			parent.getDrawEvent().register(() -> recordScene());
		});
		
		resetCheckPoint();
	}
	
	public CounterMap getCommandMap() {
		return commandMap;
	}

	public CounterMap getCommandSourceMap() {
		return commandSourceMap;
	}

	public CounterMap getPluginMap() {
		return pluginMap;
	}

	public CounterMap getPluginSourceMap() {
		return pluginSourceMap;
	}

	public CounterMap getSceneComponents() {
		return sceneComponents;
	}

	public int getTotalCommands() {
		return totalCommands;
	}
	
	public long getTotalSeconds() {
		long totalMillis = System.currentTimeMillis() - startTime;
		return TimeUnit.MILLISECONDS.toSeconds(totalMillis);
	}
	
	public long getCheckpointSeconds() {
		long totalMillis = System.currentTimeMillis() - checkPointStartTime;
		return TimeUnit.MILLISECONDS.toSeconds(totalMillis);
	}
	
	public float getCommandsPerSec() {
		return (float)totalCommands / (float)getTotalSeconds();
	}
	
	public float getCheckPointCommandsPerSec() {
		return (float)checkPointTotalCommands / (float)getCheckpointSeconds();
	}

	public String getTimeStamp() {
		return format(System.currentTimeMillis() - startTime);
	}
	
	public String getLastCommand() {
		return lastCommandName;
	}
	
	public String getLastNewPluginName() {
		return lastNewPluginName;
	}
	
	public int getTotalPluginChanges() {
		return totalPluginChanges;
	}
	
	public void showStats() {
		
		String [] msgs = new String[] {
				String.format("Time: %s", getTimeStamp()),
				String.format("Cmds/sec: %s", decFormat.format(getCommandsPerSec())),
				String.format("Checkpoint Cmds/sec: %s", decFormat.format(getCheckPointCommandsPerSec())),
				String.format("Last Command: %s[%d]", lastCommandName, commandMap.get(lastCommandName)),
				String.format("Last New Plugin: %s", lastNewPluginName),
				String.format("Total Cmd Count: %d", totalCommands),
				String.format("Total Plugin Changes: %d", totalPluginChanges),
				String.format("FPS: %s", parent.getFrameRate()),
		};

		new TextPainter(parent).drawText(Arrays.asList(msgs), SafePainter.LOCATION.LOWER_LEFT);
	}
	
	public void resetCheckPoint() {
		checkPointTotalCommands = 0;
		checkPointStartTime = System.currentTimeMillis();
	}
	
	private void recordPluginChange(APV<? extends APVPlugin> apv, APVPlugin plugin, String cause) {
		pluginMap.add(plugin.getDisplayName());
		pluginSourceMap.add(cause);
		
		lastNewPluginName = String.format("%s [%s]", plugin.getDisplayName(), cause);
		totalPluginChanges++;
	}
	
	private void recordLastCommand(Command cmd, String source) {
		commandSourceMap.add(source);
		commandMap.add(cmd.name());
		lastCommandName = cmd.name();
		totalCommands++;
		checkPointTotalCommands++;
	}
	
	private void recordScene() {
		Components comps = parent.getCurrentScene().getComponentsToDrawScene();
		Main.SYSTEM_NAMES.VALUES.forEach(s -> {
			APVPlugin comp = comps.getComponentFromSystem(s);
			if (comp != null) {
				sceneComponents.add(comp.getDisplayName());
			}
		});
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
