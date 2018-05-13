package com.arranger.apv.agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.arranger.apv.Main;
import com.arranger.apv.helpers.VideoGameHelper;
import com.arranger.apv.util.Configurator.Context;
import com.arranger.apv.util.CounterMap;
import com.arranger.apv.util.FileHelper;

public class VideoGameStatsAgent extends PulseAgent {
	
	private static final String STATS_NAME = "apvStats";
	private String fileName;
	

	public VideoGameStatsAgent(Context ctx) {
		super(ctx);
	}

	public VideoGameStatsAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}

	@Override
	protected void onPulse() {

		VideoGameHelper vg = parent.getVideoGameHelper();
		int totalCmds = vg.getTotalCommands();
		float cmdsPerSec = vg.getCommandsPerSec();
		String timeStamp = vg.getTimeStamp();
		FileHelper fh = new FileHelper(parent);
		
		Path statsPath = fh.getStatsFolder().toPath();
		if (fileName == null) {
			fileName = String.format("%s-%s.txt", STATS_NAME, timestamp());
			fileName = statsPath.resolve(fileName).toString();
		}
		
		try {
			//version info and header
			Files.createDirectories(statsPath);
			StringBuffer buffer = new StringBuffer("APV Version: " + parent.getVersionInfo().getVersion());
			buffer.append(System.lineSeparator());
			buffer.append("NumCommands, Cmds/Sec, TotalTime");
			buffer.append(System.lineSeparator());
			buffer.append(String.format("%d, %f, %s", totalCmds, cmdsPerSec, timeStamp) + System.lineSeparator());
			
			//command maps
			buffer.append(format("Commands", vg.getCommandMap()));
			buffer.append(format("CommandSources", vg.getCommandSourceMap()));
			buffer.append(format("Plugins", vg.getPluginMap()));
			buffer.append(format("PluginSources", vg.getPluginSourceMap()));
			buffer.append(format("SceneComponents", vg.getSceneComponents()));
			
			fh.saveFile(fileName, buffer.toString(), false);
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private String format(String name, CounterMap map) {
		Map<String, Integer> sortedMap = sortByValue(map.getMap());
		StringBuffer buffer = new StringBuffer(name);
		buffer.append(System.lineSeparator());
		sortedMap.entrySet().forEach(entry -> {
			buffer.append(String.format("%-30s = %-20d", entry.getKey(), entry.getValue()));
			buffer.append(System.lineSeparator());
		});
		buffer.append(System.lineSeparator());
		return buffer.toString();
	}
	
	/**
	 * https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
	 */
    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	/**
	 * https://stackoverflow.com/questions/8150155/java-gethours-getminutes-and-getseconds
	 */
	private String timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		
		return String.format("%02d_%02d_%02d", hours, minutes, seconds);
	}
}
