package com.arranger.apv.util;

import java.lang.reflect.Field;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class ReflectionHelper<T, V> extends APVPlugin {

	private Class<T> type;
	
	public ReflectionHelper(Class<T> type, Main parent) {
		super(parent);
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public V getField(String fieldName) {
		try {
			Field field = type.getField(fieldName);
			return (V) field.get(null);
		} catch (Exception e) {
			return null;
		}
	}
}
