package com.arranger.apv.agent;

import java.util.Arrays;

import com.arranger.apv.Main;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.RuntimeHelper;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextDrawHelper;

public class DebugPulseAgent extends PulseAgent {

	private static final int TEN_SECONDS = 10000;
	
	private RuntimeHelper runtimeHelper;
	private long lastTime = System.currentTimeMillis();
	private Monitor monitor = new Monitor();
	
	public DebugPulseAgent(Main parent) {
		super(parent, 1);
		runtimeHelper = new RuntimeHelper(parent);
		parent.getSetupEvent().register(() -> {
			setup();
		});
	}

	@Override
	protected void onPulse() {
		Switch sw = parent.getSwitches().get(Main.SWITCH_NAMES.DEBUG_PULSE.name);
		if (sw.isEnabled()) {
			String msg = String.valueOf(parent.getFrameCount());
			new TextDrawHelper(parent, 10, Arrays.asList(new String[] {msg}), SafePainter.LOCATION.UPPER_RIGHT);
		}
	}
	
	
	public void setup() {
		parent.getDrawEvent().register(() -> {
			lastTime = System.currentTimeMillis();
		});
		new Thread(monitor).start();
		
//		parent.getCommandSystem().registerHandler(Command.FREEZE, e -> causeDeath());
	}
	
//	protected void causeDeath() {
//		try {
//			Thread.sleep(3 * TEN_SECONDS);
//		} catch (InterruptedException e) {
//		}
//	}
	
	private class Monitor implements Runnable {

		private static final String APV_THREAD_DUMP_TXT = "apvThreadDump.txt";

		@Override
		public void run() {
			while (true) {
				if (System.currentTimeMillis() > lastTime + TEN_SECONDS) {
					String threadDump = runtimeHelper.generateThreadDump();
					System.out.println(threadDump);
					new FileHelper(parent).saveFile(APV_THREAD_DUMP_TXT, threadDump);
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("Unexpected exception: " + e);
					e.printStackTrace();
				}
			}			
		}
	}
}
