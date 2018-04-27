package com.arranger.apv.agent.automation;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class Conditions extends APVPlugin {
	
	private List<Condition> conditions;
	private boolean isAnd;

	public Conditions(Main parent, boolean isAnd, List<Condition> conditions) {
		super(parent);
		this.conditions = conditions;
		this.isAnd = isAnd;
	}
	
	@SuppressWarnings("unchecked")
	public Conditions(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getBoolean(0, true), (List<Condition>)ctx.loadRemainingPlugins(1));
	}
	
	@Override
	public String getConfig() {
		//{Conditions : [{Condition : [...]} ...]}
		String childrenConfig = conditions.stream().map(c -> c.getConfig()).collect(Collectors.joining(","));
		return String.format("{%s : [%b, %s]}", getName(), isAnd, childrenConfig);
	}

	public boolean isTrue() {
		if (isAnd) {
			return conditions.stream().allMatch(c -> c.isTrue());
		} else {
			return conditions.stream().anyMatch(c -> c.isTrue());
		}
	}
}
