package com.arranger.apv.helpers;

import java.util.Observable;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class Switch extends APVPlugin {
	
	public enum STATE {FROZEN, ENABLED, DISABLED};
	
	public STATE state;
	public String name;
	public String data;
	public ObservalbleAdapter observable;
	
	public class ObservalbleAdapter extends Observable {
		
		public void update() {
			setChanged();
			notifyObservers();
		}
	}
	
	public Switch(Main parent, String name) {
		this(parent, name, true, null);
	}
	
	public Switch(Main parent, String name, boolean enabled) {
		this(parent, name, enabled, null);
	}
	
	public Switch(Main parent, String name, boolean enabled, String data) {
		super(parent);
		this.name = name;
		this.state =  enabled ? STATE.ENABLED : STATE.DISABLED;
		this.data = data;
		this.observable = new ObservalbleAdapter();
	}
	
	public Switch(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, ""), ctx.getBoolean(1, true), ctx.getString(2, null));
	}

	@Override
	public String getConfig() {
		//{Switch : [ForeGround, true]}
		return String.format("{%s : [%s, %b]}", super.getName(), getName(), isEnabled(), data);
	}

	@Override
	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}
	
	@Override
	public String getDisplayName() {
		return name + "[" + state + "]";
	}

	/**
	 * @return true if the state is enabled or frozen
	 * (Ignores frozen)
	 */
	public boolean isEnabled() {
		return state != STATE.DISABLED;
	}
	
	/**
	 * Ignores disabled
	 */
	public boolean isFrozen() {
		return state == STATE.FROZEN;
	}
	
	public void setState(STATE state) {
		this.state = state;
	}
	
	/**
	 * If frozen, set to enabled, otherwise set to Frozen
	 */
	public void toggleFrozen() {
		if (state == STATE.FROZEN) {
			state = STATE.ENABLED;
		} else {
			state = STATE.FROZEN;
		}
		observable.update();
	}
	
	/**
	 * if enabled set to disabled otherwise set to Enabled
	 */
	public void toggleEnabled() {
		if (state == STATE.ENABLED) {
			state = STATE.DISABLED;
		} else {
			state = STATE.ENABLED;
		}
		observable.update();
	}
	
	/**
	 * If the state is FROZEN or DISABLED
	 * the state become ENABLED
	 * 
	 * If the state is ENABLED it becomes DISABLED
	 */
	public void toggle() {
		switch (state) {
		case FROZEN:
		case DISABLED:
			state = STATE.ENABLED;
			break;
		case ENABLED:
			state = STATE.DISABLED;
			break;
		}
		observable.update();
	}
}
