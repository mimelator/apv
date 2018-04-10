package com.arranger.apv.pl;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SimplePulseListener extends APVPlugin {

	/**
	 * @see http://upli.st/l/list-of-all-ascii-emoticons
	 */
	private static String [] MESSGES = {
			"¯\\_(ツ)_/¯",
			"( ͡° ͜ʖ ͡°)",
			"._.-´¯`-._.-´¯`-._.><(((º>",
			" (•̀ᴗ•́)و ̑̑ ",
			"⊂(◉‿◉)つ",
			"(ง •̀_•́)ง ",
			"(•_•) ( •_•)>⌐■-■ (⌐■_■)"
	};
	
	public SimplePulseListener(Main parent) {
		super(parent);
		
		parent.getPulseListener().registerPulseListener( ()-> { 
			parent.sendMessage(new String[]{getMessage()}); 
			}  
		);
	}

	private String getMessage() {
		return MESSGES[(int)parent.random(MESSGES.length)];
	}
}
