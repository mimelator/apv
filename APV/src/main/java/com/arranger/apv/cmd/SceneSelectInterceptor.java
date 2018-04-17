package com.arranger.apv.cmd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.Scene;
import com.arranger.apv.scene.Marquee;

public class SceneSelectInterceptor extends CommandInterceptor {
	
	private static final Logger logger = Logger.getLogger(SceneSelectInterceptor.class.getName());

	private static final char SCENE_SELECTION_KEY = 'e';
	private Map<Character, Scene> sceneMap = null;
	private Map<Character, SceneSelectionHandler> handlerMap = new HashMap<Character, SceneSelectionHandler>();
	private int nextSceneIndex = 1;
	
	private SceneSelectionHandler defaultHandler = new SceneSelectionHandler() {
		@Override
		public void onSceneChange(char sceneId) {
			switchToScene(sceneId);
		}
	};
	
	@FunctionalInterface
	public static interface SceneSelectionHandler {
		public void onSceneChange(char sceneId);
	}
	
	public SceneSelectInterceptor(Main parent) {
		super(parent);
		
		parent.registerSetupListener(() -> {
			//register all the existing scenes with their hotkeys
			parent.getScenes().stream().forEach(s -> {
				registerScene(s.getHotKey(), defaultHandler);
			});
		});
	}
	
	@Override
	public String getHelpText() {
		String result = handlerMap.keySet().stream().
				map(String::valueOf).
				collect(Collectors.joining(", "));
		
		return String.format("e: Scene Selector: Selects which scene will be played next.  Currently: [%1s]", result);
	}
	
	public void registerScene(SceneSelectionHandler handler) {
		registerScene(String.valueOf(nextSceneIndex++).charAt(0), handler);
	}
	
	public void registerScene(char sceneId, SceneSelectionHandler handler) {
		handlerMap.put(sceneId, handler);
	}
	
	public void showMessageSceneWithText(String text) {
		ensureInit();
		Marquee marquee = (Marquee)sceneMap.get('m');
		marquee.setText(text);
		parent.setNextScene(marquee);
	}
	
	/**
	 * If active, then
	 * 		if it is SCENE_SELECTION_KEY then we're cancelled (not-active)
	 * 		if not the SCENE_SELECTION_KEY we need to execute the switchToScene(sceneKey) method
	 * 		
	 * If not active then
	 * 		if it is SCENE_SELECTION_KEY then we're active	 	
	 * 		if it isn't SCENE_SELECTION_KEY then it's none of our business
	 * 		
	 */
	@Override
	public boolean intercept(char key) {
		boolean handled = false;
		if (active) {
			if (SCENE_SELECTION_KEY != key) {
				//do nothing except below
				doSwitch(key);
			}
			active = false; //no matter what we're not active and it is handled
			return true; //handled
		} else {
			if (SCENE_SELECTION_KEY == key) {
				active = true;
				handled = true;
			}
		}
		
		return handled;
	}
	
	/**
	 * Delegate to the SceneSelectionHandler
	 */
	protected void doSwitch(char sceneKey) {
		SceneSelectionHandler h = handlerMap.get(sceneKey);
		if (h == null) {
			logger.warning("Unable to find a SceneSelectionHandler for: " + sceneKey);
		} else {
			h.onSceneChange(sceneKey);
		}
	}

	/**
	 * issue the commands to switch a scene
	 */
	protected void switchToScene(char sceneKey) {
		ensureInit();
		
		Scene scene = sceneMap.get(sceneKey);
		if (scene != null) {
			parent.setNextScene(scene);
		}
	}
	
	protected void ensureInit() {
		if (sceneMap != null) {
			return;
		}
		sceneMap = new HashMap<Character, Scene>();
		for (Iterator<Scene> it = parent.getScenes().iterator(); it.hasNext();) {
			Scene next = it.next();
			sceneMap.put(next.getHotKey(), next);
		}
	}
}
