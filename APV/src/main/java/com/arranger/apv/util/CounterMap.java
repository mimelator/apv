package com.arranger.apv.util;

import java.util.HashMap;
import java.util.Map;

public class CounterMap {

	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	public CounterMap() {
	}

	public void add(String key) {
		if (key == null || key.isEmpty()) {
			System.out.println("Uh oh");
			return;
		}
		
		Integer count = map.get(key);
		if (count == null) {
			map.put(key, new Integer(1));
		} else {
			map.put(key, ++count);
		}
	}
	
	public Integer get(String key) {
		return map.get(key);
	}
	
	public Map<String, Integer> getMap() {
		return map;
	}
	
}
