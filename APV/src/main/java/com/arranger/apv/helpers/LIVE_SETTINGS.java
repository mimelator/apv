package com.arranger.apv.helpers;

import com.arranger.apv.Main;
import com.arranger.apv.Main.FLAGS;
import com.arranger.apv.util.draw.DrawHelper;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.wm.WatermarkPainter;

public enum LIVE_SETTINGS {

	VERSION((p, args) -> p.getVersionInfo().getVersion()),
	OCEAN((p, args) -> p.getConfigValueForFlag(Main.FLAGS.OCEAN_NAME)),
	MODE((p, args) -> p.getCurrentControlMode().name()),
	DB((p, args) -> String.valueOf(p.getAudio().getDB())),
	SWITCH((p, args) -> getSwitchValue(p, args)),
	FLAG((p, args) -> getFlagValue(p, args)),
	FRAME_RATE((p, args) -> String.valueOf(p.frameRate));
	
	
	private CommandHandler ch;
	
	private LIVE_SETTINGS(CommandHandler ch) {
		this.ch = ch;
	}
	
	static interface CommandHandler {
		String onCommand(Main parent, String [] args);
	}
	
	private static String getSwitchValue(Main parent, String [] args) {
		Switch sw = parent.getSwitches().get(args[0]);
		return sw.name + ":" + sw.state.name();
	}
	
	private static String getFlagValue(Main parent, String [] args) {
		FLAGS flag = Main.FLAGS.valueOf(args[0]);
		return flag.name() + ":" + parent.getConfigValueForFlag(flag);
	}
	
	public void onCommand(Main parent, String [] args) {
		String msgValue = ch.onCommand(parent, args);
		String msg = this.name() + ":" + msgValue;
		new DrawHelper(parent, 1200, new WatermarkPainter(parent, 1200, msg, 1, LOCATION.MIDDLE), () -> {});
	}
}
