package com.arranger.apv.pl;

import java.util.Arrays;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class SimplePL extends APVPlugin {
	
	/**
	 * @see http://upli.st/l/list-of-all-ascii-emoticons
	 */
	private static String [] DEFAULT_MESSGES = {":("};
	
	private List<String> msgList = null;
	
	public SimplePL(Main parent, int pulsesToSkip) {
		this(parent, pulsesToSkip, DEFAULT_MESSGES);
	}

	public SimplePL(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, 4), ctx.getStringArray(1, null));
	}
	
	public SimplePL(Main parent, int pulsesToSkip, String [] messages) {
		super(parent);
		if (messages == null) {
			msgList = Arrays.asList(DEFAULT_MESSGES);
		} else {
			msgList = Arrays.asList(messages);
		}
		registerListener(parent, pulsesToSkip);
	}
	
	private String getMessage() {
		return msgList.get((int)parent.random(msgList.size()));
	}
	
	protected void registerListener(Main parent, int pulsesToSkip) {
		parent.getPulseListener().registerPulseListener( ()-> { 
			parent.sendMessage(new String[]{getMessage()}); 
			}  
		, pulsesToSkip); //eg: Skip every other pulse
	}
}
