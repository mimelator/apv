package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.scene.Scene;
import com.arranger.apv.util.KeyListener.KEY_SYSTEMS;
import com.arranger.apv.util.KeyListener.KeyEventListener;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextDrawHelper;

import edu.emory.mathcs.backport.java.util.Arrays;
import processing.event.KeyEvent;

public class RewindHelper extends APVPlugin implements KeyEventListener {
	
	private static final int NUM_FRAMES_REWIND_MSG = 200;
	private static final int DEFAULT_BUFFER_SIZE = 50;
	@SuppressWarnings("unchecked")
	private static final List<String> REWIND_MSG = Arrays.asList(new String[] {"Rewind"});
	
	@FunctionalInterface
	interface RewindCommand {
		void onCommand();
	}
	
	private Map<String, RewindCommand> keyBindingMap = new HashMap<String, RewindCommand>();
	private List<Scene> sceneBuffer;
	private int index;

	public RewindHelper(Main parent) {
		super(parent);
		sceneBuffer = new ArrayList<Scene>(DEFAULT_BUFFER_SIZE);
		index = 0;
		
		keyBindingMap.put("←", () -> reverse());
		keyBindingMap.put("→", () -> foward());
		
		keyBindingMap.put(" ", () -> exitRewindMode());
		keyBindingMap.put("\n", () -> exitRewindMode());
		keyBindingMap.put("⌫", () -> exitRewindMode());
	}

	public void addScene(Scene s) {
		Scene test = getPreviousScene();
		if (test != null && test.equals(s)) {
			return;
		}
		
		sceneBuffer.add(index, new Scene(s));
		foward();
	}
	
	public Scene getScene() {
		return sceneBuffer.get(index);
	}
	
	public void reverse() {
		index--;
		if (index < 0) {
			index = sceneBuffer.size() - 1;
		}
	}
	
	public void foward() {
		index++;
		if (index >= sceneBuffer.size()) {
			index = 0;
		}
	}
	
	public void onKeyEvent(KeyEvent keyEvent) {
		String keyForKeyEvent = Command.getKeyForKeyEvent(keyEvent);
		if (keyBindingMap.containsKey(keyForKeyEvent)) {
			keyBindingMap.get(keyForKeyEvent).onCommand();
		}
	}
	
	public void enterRewindMode() {
		reverse(); 
		parent.getKeyListener().setSystem(KEY_SYSTEMS.REWIND); 
		new TextDrawHelper(parent, NUM_FRAMES_REWIND_MSG, REWIND_MSG, SafePainter.LOCATION.LOWER_RIGHT);
	}
	
	public void exitRewindMode() {
		parent.getKeyListener().setSystem(KEY_SYSTEMS.COMMAND);
	}
	
	protected Scene getPreviousScene() {
		if (sceneBuffer.isEmpty()) {
			return null;
		} 
		
		if (index == 0) {
			return sceneBuffer.get(sceneBuffer.size() - 1);
		} else {
			return sceneBuffer.get(index - 1);
		}
	}
}
