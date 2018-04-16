package com.arranger.apv.cmd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arranger.apv.Main;
import com.arranger.apv.Scene;

public class SceneSelectInterceptor extends CommandInterceptor {

	private static final char SCENE_SELECTION_KEY = 'e';
	private Map<Character, Scene> sceneMap = null;
	
	
	public SceneSelectInterceptor(Main parent) {
		super(parent);
	}
	
	@Override
	public String getHelpText() {
		return "e: Scene Selector: Selects which scene will be played next.  Currently: [0, t, m]";
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
				switchToScene(key);
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

	protected void switchToScene(char sceneKey) {
		if (sceneMap == null) {
			init();
		}
		
		Scene scene = sceneMap.get(sceneKey);
		if (scene != null) {
			parent.setNextScene(scene);
		}
	}
	
	protected void init() {
		sceneMap = new HashMap<Character, Scene>();
		for (Iterator<Scene> it = parent.getScenes().iterator(); it.hasNext();) {
			Scene next = it.next();
			sceneMap.put(next.getHotKey(), next);
		}
	}
}
