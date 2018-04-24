package com.arranger.apv.helpers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame;
import com.arranger.apv.scene.Scene;
import com.arranger.apv.scene.Scene.Components;

public class PerformanceMonitor extends APVPlugin {

	private static final int FRAME_RATE_THRESHOLD = 30;
	private static final int MIN_THRESHOLD_ENTRIES = 3;
	
	public PerformanceMonitor(Main parent) {
		super(parent);
	}

	private Map<String, List<Float>> monitorRecords = new HashMap<String, List<Float>>();
	private static final DecimalFormat decFormat = new DecimalFormat(".##");
	
	public void doMonitorCheck(Scene scene) {
		if (parent.frameRate < FRAME_RATE_THRESHOLD) {
			
			Components c = scene.getComponentsToDrawScene();
			APVPlugin backDrop = c.backDrop;
			APVPlugin filter = c.filter;
			APVPlugin bg = c.bgSys;
			APVPlugin fg = c.fgSys;
			
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
	
	List<String> msgs;
	
	public void dumpMonitorInfo(boolean launchWindow) {
		msgs = new ArrayList<String>();
		StringBuffer header = new StringBuffer("name, numEntries, avgTime, totalTime").append(System.lineSeparator());
		msgs.add(header.toString());
		
		for (Map.Entry<String, List<Float>> entry : monitorRecords.entrySet()) {
			List<Float> counts = entry.getValue();
			if (counts.size() < MIN_THRESHOLD_ENTRIES) {
				continue;
			}
			
			//get the average
			double average = counts.stream().mapToDouble(a -> a).average().getAsDouble();
			String line = String.format("%s, %d, %s, %s", 
					entry.getKey(), 
					counts.size(),
					decFormat.format(average),
					decFormat.format(average * counts.size()));
			
			line += System.lineSeparator();
			msgs.add(line);
		}
		
		if (launchWindow) {
			new APVTextFrame(parent, 
					"perf", 
					(int)parent.width / 6, 
					(int)(parent.height * .8f), 
					parent.getDrawEvent(), 
					()-> msgs);
		} else {
			msgs.forEach(System.out::println);
		}
	}
	
}
