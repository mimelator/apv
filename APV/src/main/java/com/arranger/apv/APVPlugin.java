package com.arranger.apv;

import java.util.HashSet;
import java.util.Set;

import com.arranger.apv.helpers.Switch;

import processing.core.PConstants;

public class APVPlugin implements PConstants {
	
	public static int NO_ALPHA = 255;
	private static int ID = 0;
	
	/**
	 * Used for {@link #getDisplayName()}
	 */
	private static final String [] CLASS_NAMES_TO_ERASE = {
			"System", "Factory", "BackDrop", "Shape", "Message", "Location", "Menu" 
	};
	
	@SuppressWarnings("serial")
	private static final Set<String> FLASHERS = new HashSet<String>() {{
	    add("Pulse");
	    add("Strobe");
	}};
	
	protected Main parent;
	protected int id;
	protected int popularityIndex = 1;
	protected boolean enabled = true;
	protected boolean supportsExtendedConfig = true;

	public APVPlugin(Main parent) {
		this.parent = parent;
		this.id = ID++;
		
		
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public Main getParent() {
		return parent;
	}
	
	public String getDisplayName() {
		String n = getName();
		for (String erase : CLASS_NAMES_TO_ERASE) {
			n = n.replaceAll(erase, "");
		}
		
		//erase too much?
		if (n.isEmpty()) {
			n = getClass().getSimpleName();
		}
		
		return n;
	}
	
	public String getConfigEx() {
		if (supportsExtendedConfig) {
			return String.format("%s {enabled:%s, popularityIndex:%s}", getConfig(), isEnabled(), getPopularityIndex());
		} else {
			return getConfig();
		}
	}
	
	public String getConfig() {
		return String.format("{%s : []}", getName());
	}
	
	public void setup() {
		
	}

	public boolean isEnabled() {
		if (enabled) {
			//check if Flasher and Flasher enabled
			if (isFlasher()) {
				Switch sw = parent.getSwitches().get(Main.SWITCH_NAMES.FLASH_FLAG.name);
//				if (!sw.isEnabled()) {
//					System.out.println("Not showing the flasher: " + getName());
//				}
				
				return sw.isEnabled();
			}
		}
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggleEnabled() {
		setEnabled(!enabled);
	}

	public int getPopularityIndex() {
		return popularityIndex;
	}

	public void setPopularityIndex(int popularityIndex) {
		this.popularityIndex = Math.max(0, popularityIndex);
	}

	public boolean isFlasher() {
		return FLASHERS.contains(getName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APVPlugin other = (APVPlugin) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
