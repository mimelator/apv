package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.FreqDetector;

public class AudioTestMenu extends CommandBasedMenu {
	
	protected FreqDetector freqDetector;
	
	public AudioTestMenu(Main parent) {
		super(parent);
		freqDetector = new FreqDetector(parent);
		showDetails = false;
	}
	
	public void draw() {
		super.draw();
		freqDetector.draw();
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuAdapterCallback> results = new ArrayList<MenuAdapterCallback>();
		
		String msg = String.format("Responsiveness: [%s]", parent.getAutoAudioAdjuster().getTargetCmdsPerSec());
		results.add(new MenuAdapterCallback(parent, msg, ()-> updateResponsiveness()));
		
		return results;
	}
	
	protected void updateResponsiveness() {
		String result = JOptionPane.showInputDialog("Responsiveness level");
		try {
			float floatResult = Float.parseFloat(result);
			parent.getAutoAudioAdjuster().setTargetCmdsPerSec(floatResult);
			shouldSaveOnDeactivate = true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Unable to parse number: " + result, "Responsiveness", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
