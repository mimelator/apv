package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator.Context;
import com.arranger.apv.util.VideoGameHelper;

public class VideoGameStatsAgent extends PulseAgent {
	
	private static final String STATS_NAME = "apvStats.txt";

	public VideoGameStatsAgent(Context ctx) {
		super(ctx);
	}

	public VideoGameStatsAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}

	@Override
	protected void onPulse() {
		//TODO.  New file for every date stamp
		// Put the Version Info at the top
		
		VideoGameHelper vg = parent.getVideoGameHelper();
		int totalCmds = vg.getTotalCommands();
		float cmdsPerSec = vg.getCommandsPerSec();
		String timeStamp = vg.getTimeStamp();
		//Map<String, Integer> commandStatMap = vg.getCommandStatMap();
		
		String result = String.format("NumCommands:[%1d], Cmds/Sec:[%2f], TotalTime[%3s]", totalCmds, cmdsPerSec, timeStamp) + System.lineSeparator();
		parent.getFileHelper().saveFile(STATS_NAME, result, true);
	}
}
