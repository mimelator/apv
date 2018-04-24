package com.arranger.apv.util;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.SceneSelectInterceptor;
import com.typesafe.config.ConfigList;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SceneList extends APVPlugin {

	private static final String [] DEFAULT_SCENES = {"No scenes for you"};
	private List<String> stringList;
	
	public SceneList(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			// get the strings
			ConfigList configList = parent.getConfigurator().getRootConfig().getList("marqueeList");
			stringList = configList.stream().map(e -> (String) e.unwrapped()).collect(Collectors.toList());

			// for each string register a custom action
			SceneSelectInterceptor ssi = parent.getCommandSystem().getSceneSelectInterceptor();

			stringList.stream().forEach(s -> {
				ssi.registerScene(c -> {
					ssi.showMessageSceneWithText(s);
				});
			});
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getConfig() {
		if (stringList == null) {
			stringList = Arrays.asList(DEFAULT_SCENES);
		}
		
		//song-list : [${songs}]
		JSONQuoter quoter = new JSONQuoter(parent);
		String msgs = stringList.stream().map(e -> quoter.quote(e)).collect(Collectors.joining(","));
		
		return String.format("marqueeList : [%s]", msgs);
	}
}
