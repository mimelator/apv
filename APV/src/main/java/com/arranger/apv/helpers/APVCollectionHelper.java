package com.arranger.apv.helpers;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public abstract class APVCollectionHelper extends APVPlugin {

	public APVCollectionHelper(Main parent) {
		super(parent);
	}

	public void reloadConfiguration() {
		unregister();
		configure();
		register();
	}
	
	public abstract void register();
	public abstract void unregister();
	public abstract void configure();

}
