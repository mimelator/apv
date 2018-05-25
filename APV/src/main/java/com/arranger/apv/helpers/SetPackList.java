package com.arranger.apv.helpers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.JSONQuoter;
import com.typesafe.config.ConfigList;


public class SetPackList extends APVPlugin {

	private static final String SET_PACK_LIST = "setPackList";
	private static final String [] DEFAULT_SETS = {""};
	private List<String> stringList;
	
	public SetPackList(Main parent) {
		super(parent);
		setupRegistration();
	}

	protected void setupRegistration() {
		parent.getSetupEvent().register(() -> {
			// get the strings
			ConfigList configList = parent.getConfigurator().getRootConfig().getList(SET_PACK_LIST);
			stringList = configList.stream().map(e -> (String) e.unwrapped()).collect(Collectors.toList());

			if (stringList.isEmpty()) {
				File spFolder = new FileHelper(parent).getSetPacksFolder();
				System.out.println("No setpack list defined.  Loading all available setpacks in setpack directory: " + spFolder.getAbsolutePath());
				
				File[] listFiles = spFolder.listFiles(f -> f.isDirectory());
				stringList = Arrays.asList(listFiles).stream().map(f -> f.getName()).collect(Collectors.toList());
			}
			
			parent.getSetPackModel().setSetPackList(stringList);
		});
	}
	
	public void reset() {
		stringList.clear();
		setupRegistration();
	}
	
	@Override
	public String getConfig() {
		if (stringList == null) {
			stringList = Arrays.asList(DEFAULT_SETS);
		}
		
		//setPackList : [${songs}]
		JSONQuoter quoter = new JSONQuoter(parent);
		String msgs = stringList.stream().map(e -> quoter.quote(e)).collect(Collectors.joining(","));
		
		return String.format(SET_PACK_LIST + " : [%s]", msgs);
	}
	
	public List<String> getList() {
		return stringList;
	}
}
