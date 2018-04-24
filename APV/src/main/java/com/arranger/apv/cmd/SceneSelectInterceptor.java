package com.arranger.apv.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.scene.Marquee;
import com.arranger.apv.scene.Scene;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextDrawHelper;

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
		
		parent.getSetupEvent().register(() -> {
			//register all the existing scenes with their hotkeys
			parent.getScenes().stream().forEach(s -> {
				registerScene(s.getHotKey(), defaultHandler);
			});
			
			initialize();
		});
	}
	
	@Override
	public String getHelpText() {
		List<Character> keys = new ArrayList<Character>(handlerMap.keySet());
		keys.sort(null);
		
		String result = keys.stream().
				map(String::valueOf).
				collect(Collectors.joining(", "));
		
		return String.format("e: Scene Selector: Selects which scene will be played next.  Currently: [%s]", result);
	}
	
	public void registerScene(SceneSelectionHandler handler) {
		registerScene(String.valueOf(nextSceneIndex++).charAt(0), handler);
	}
	
	public void registerScene(char sceneId, SceneSelectionHandler handler) {
		handlerMap.put(sceneId, handler);
	}
	
	public void showMessageSceneWithText(String text) {
		Marquee marquee = (Marquee)sceneMap.get('m');
		marquee.setText(text);
		parent.setNextScene(marquee);
		
		//Send the message to the lower right for awhile
		new TextDrawHelper(parent, 1200, Arrays.asList(new String[] {text}), SafePainter.LOCATION.LOWER_RIGHT); 
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
		Scene scene = sceneMap.get(sceneKey);
		if (scene != null) {
			parent.setNextScene(scene);
		}
	}
	
	protected void initialize() {
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
