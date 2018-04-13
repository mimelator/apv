package com.arranger.apv;

import com.arranger.apv.util.Configurator;

public class Switch extends APVPlugin {
	
	public enum STATE {FROZEN, ENABLED, DISABLED};
	
	public STATE state;
	public String name;
	
	public Switch(Main parent, String name) {
		super(parent);
		this.name = name;
		this.state = STATE.ENABLED;
	}
	
	public Switch(Main parent, String name, boolean enabled) {
		super(parent);
		this.name = name;
		this.state =  enabled ? STATE.ENABLED : STATE.DISABLED;
	}
	
	public Switch(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, ""), ctx.getBoolean(1, true));
	}

	@Override
	public String getName() {
		return name;
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
	
	/**
	 * If frozen, set to enabled, otherwise set to Frozen
	 */
	public void toggleFrozen() {
		if (state == STATE.FROZEN) {
			state = STATE.ENABLED;
		} else {
			state = STATE.FROZEN;
		}
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
	}
	
}
