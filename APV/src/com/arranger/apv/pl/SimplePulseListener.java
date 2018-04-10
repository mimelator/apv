package com.arranger.apv.pl;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SimplePulseListener extends APVPlugin {

	public SimplePulseListener(Main parent) {
		super(parent);
		
		parent.getPulseListener().registerPulseListener( ()->{ parent.sendMessage(new String[]{"Pulse"}); }  );
	}

}
