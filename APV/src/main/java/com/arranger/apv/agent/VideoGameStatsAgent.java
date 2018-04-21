package com.arranger.apv.agent;

import java.util.Calendar;
import java.util.Date;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator.Context;
import com.arranger.apv.util.VideoGameHelper;

public class VideoGameStatsAgent extends PulseAgent {
	
	private static final String STATS_NAME = "apvStats";
	private String fileName = null;

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
		//Map<String, Integer> commandStatMap = vg.getCommandStatMap();

		//Create file with version info and header
		if (fileName == null) {
			fileName = String.format("%1s-%2s.txt", STATS_NAME, timestamp());
			StringBuffer buffer = new StringBuffer("APV Version: " + parent.getVersionInfo().getVersion());
			buffer.append(System.lineSeparator());
			buffer.append("NumCommands, Cmds/Sec, TotalTime");
			buffer.append(System.lineSeparator());
			parent.getFileHelper().saveFile(fileName, buffer.toString(), false);
		}
		
		String result = String.format("%1d, %2f, %3s", totalCmds, cmdsPerSec, timeStamp) + System.lineSeparator();
		parent.getFileHelper().saveFile(fileName, result, true);
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
