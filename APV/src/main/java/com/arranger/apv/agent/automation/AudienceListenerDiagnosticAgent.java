package com.arranger.apv.agent.automation;

import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.agent.BaseAgent;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;

import edu.emory.mathcs.backport.java.util.Arrays;

public class AudienceListenerDiagnosticAgent extends BaseAgent {

	private static final int STARTUP_QUIET_WINDOW = 100;
	@SuppressWarnings("unchecked")
	private static final List<String> MESSAGES = Arrays.asList(new String[]{
			"Not hearing any music.  Please check your audio routing.",
			"You can click '!' or the '@' to see the Frequency chart or the audio sensitivity response.",
			"Then you can use the '+' and '-' buttons to adjust the audio sensitivity",
			"If the vertical bars aren't appearing, that means that there is no music being listened to",
			"You can press '" + Command.SWITCH_AUDIO_LISTENER_DIAGNOSTIC.getCharKey() + "' to disable this message"
	});
	private static final int DEFAULT_LISTEN_WINDOW = 5;
	private static final int SEC_PER_MILLIS = 1000;
	
	private int pulseCount = 0;
	private long lastPulseTime;
	private long startTime;
	private int listenWindow;
	
	public AudienceListenerDiagnosticAgent(Main parent, int listenWindow) {
		super(parent);
		
		this.listenWindow = listenWindow;
		
		registerAgent(getALDAEvent(), () -> {
			if (pulseCount == 0) {
				startTime = System.currentTimeMillis() / SEC_PER_MILLIS;
			}
			pulseCount++;
			
			lastPulseTime = System.currentTimeMillis() / SEC_PER_MILLIS;
			float difference = lastPulseTime - startTime;
			if (difference > 0 && pulseCount % 10 == 0) {
				float averagePulsesPerSecond = (float)pulseCount / difference;
				//I can't really calculate BPM because it will have the average BPM for as long as the app has been listening to music
				System.out.printf("AudienceListenerDiagnosticAgent [count:avg/sec] [%s:%s]%n", pulseCount, averagePulsesPerSecond);
			}
		});
		
		registerAgent(getDrawEvent(), () ->{
			Switch sw = parent.getSwitches().get(Main.SWITCH_NAMES.AUDIO_LISTENER_DIAGNOSTIC.name);
			if (!sw.isEnabled()) {
				return;
			}
			
			//too early
			if (parent.frameCount < STARTUP_QUIET_WINDOW) {
				return;
			}
			
			//if no pulseCount in the last N seconds, flash a warning message
			long currentTime = System.currentTimeMillis() / SEC_PER_MILLIS;
			float difference = currentTime - lastPulseTime;
			if (difference > this.listenWindow) {
				new TextPainter(parent).drawText(MESSAGES, SafePainter.LOCATION.MIDDLE);
			}
		});
	}
	
	public AudienceListenerDiagnosticAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_LISTEN_WINDOW));
	}
	
	@Override
	public String getConfig() {
		//{AudienceListenerDiagnosticAgent : [5]}
		return String.format("{%s : [%s, ]}", getName(), listenWindow);
	}

}
