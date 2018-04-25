package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.RuntimeHelper;

public class MonitorAgent extends BaseAgent {
	
private static final int TEN_SECONDS = 10000;
	
	private RuntimeHelper runtimeHelper;
	private long lastTime = System.currentTimeMillis();
	private Monitor monitor = new Monitor();
	
	private boolean enabled;

	public MonitorAgent(Main parent, boolean enabled) {
		super(parent);
		this.enabled = enabled;
		
		if (enabled) {
			runtimeHelper = new RuntimeHelper(parent);
			parent.getSetupEvent().register(() -> {
				setup();
			});
		}
	}
	
	public MonitorAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getBoolean(0, true));
	}
	
	@Override
	public String getConfig() {
		//{MonitorAgent : [true]}
		return String.format("{%s : [%b]", getName(), enabled);
	}

	public void setup() {
		parent.getDrawEvent().register(() -> {
			lastTime = System.currentTimeMillis();
		});
		new Thread(monitor).start();
		
	}
	
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
