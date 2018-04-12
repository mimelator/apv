package com.arranger.apv.pl;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SimplePL extends APVPlugin {
	
	private static final int PULSES_TO_SKIP = 2;

	/**
	 * @see http://upli.st/l/list-of-all-ascii-emoticons
	 */
	private static String [] MESSGES = {
			"ʘ‿ʘ",
			"ಠ_ಠ",
			"¯\\_(ツ)_/¯",
			"( ͡° ͜ʖ ͡°)",
			"._.-´¯`-._.-´¯`-._.><(((º>",
			" (•̀ᴗ•́)و ̑̑ ",
			"(ง •̀_•́)ง ",
			"(•_•) ( •_•)>⌐■-■ (⌐■_■)"
	};
	
	public SimplePL(Main parent) {
		super(parent);
		
		parent.getPulseListener().registerPulseListener( ()-> { 
			parent.sendMessage(new String[]{getMessage()}); 
			}  
		, PULSES_TO_SKIP); //eg: Skip every other pulse
	}

	private String getMessage() {
		return MESSGES[(int)parent.random(MESSGES.length)];
	}
}
