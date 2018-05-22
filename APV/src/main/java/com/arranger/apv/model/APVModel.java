package com.arranger.apv.model;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

@SuppressWarnings("rawtypes")
public abstract class APVModel extends APVPlugin {

	public APVModel(Main parent) {
		super(parent);
	}

	public abstract void reset();
	public abstract void randomize();
	
	public abstract void loadFromEntities(List entities);
	
	protected Object getRandomEntity(List entities) {
		int index = (int)parent.random(entities.size());
		Object result = entities.get(index);
		return result;
	}
}
