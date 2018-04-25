package com.arranger.apv.util;

import java.util.Collection;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class RandomHelper extends APVPlugin {

	public RandomHelper(Main parent) {
		super(parent);
	}

	/**
	 * https://stackoverflow.com/questions/21092086/get-random-element-from-collection
	 */
	public <T> T random(Collection<T> coll) {
	    int num = (int) (Math.random() * coll.size());
	    for(T t: coll) if (--num < 0) return t;
	    throw new AssertionError();
	}
}
