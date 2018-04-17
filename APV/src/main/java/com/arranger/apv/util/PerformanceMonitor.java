package com.arranger.apv.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Scene;

public class PerformanceMonitor extends APVPlugin {

	private static final int FRAME_RATE_THRESHOLD = 30;
	private static final int MIN_THRESHOLD_ENTRIES = 3;
	
	public PerformanceMonitor(Main parent) {
		super(parent);
	}

	private Map<String, List<Float>> monitorRecords = new HashMap<String, List<Float>>();
	private static DecimalFormat decFormat = new DecimalFormat(".##");
	
	public void doMonitorCheck(Scene scene) {
		if (parent.frameRate < FRAME_RATE_THRESHOLD) {
			
			APVPlugin backDrop = scene.getBackDrop();
			APVPlugin filter = scene.getFilter();
			APVPlugin bg = scene.getBgSys();
			APVPlugin fg = scene.getFgSys();
			
			//This is an ugly way to build a key
			StringBuilder builder = new StringBuilder();
			builder.append((backDrop != null) ? backDrop.getDisplayName() : "()").append(':'); 
			builder.append((filter != null) ? filter.getDisplayName() : "()").append(':');
			builder.append((bg != null) ? bg.getDisplayName() : "()").append(':');
			builder.append((fg != null) ? fg.getDisplayName() : "()");
			String key = builder.toString();
			
			List<Float> frames = monitorRecords.get(key);
			if (frames == null) {
				frames = new ArrayList<Float>();
				monitorRecords.put(key, frames);
			}
			
			frames.add(parent.frameRate);
		}
	}
	
	public void dumpMonitorInfo() {
		System.out.println("name, numEntries, avgTime, totalTime");
		for (Map.Entry<String, List<Float>> entry : monitorRecords.entrySet()) {
			List<Float> counts = entry.getValue();
			if (counts.size() < MIN_THRESHOLD_ENTRIES) {
				continue;
			}
			
			//get the average
			OptionalDouble average = counts.stream().mapToDouble(a -> a).average();
			System.out.println(entry.getKey() + "," + 
								counts.size() + "," + 
								decFormat.format(average.getAsDouble()) + "," +
								decFormat.format(average.getAsDouble() * counts.size()));
		}
	}
	
}
