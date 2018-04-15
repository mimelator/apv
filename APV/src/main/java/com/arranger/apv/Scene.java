package com.arranger.apv;

import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.util.Configurator;

public class Scene extends ShapeSystem {

	protected BackDropSystem backDrop;
	protected ShapeSystem bgSys;
	protected ShapeSystem fgSys;
	protected Filter filter;
	
	public Scene(Main parent) {
		super(parent, null);
	}
	
	public Scene(Configurator.Context ctx) {
		this(ctx.getParent());
		
		if (ctx.argList.size() == 0) {
			return;
		}
		
		setSystems((BackDropSystem)ctx.loadPlugin(0),
				(ShapeSystem)ctx.loadPlugin(1),
				(ShapeSystem)ctx.loadPlugin(2),
				(Filter)ctx.loadPlugin(3));
	}
	
	public void setSystems(BackDropSystem backDrop, ShapeSystem bgSys, ShapeSystem fgSys, Filter filter) {
		this.backDrop = backDrop;
		this.bgSys = bgSys;
		this.fgSys = fgSys;
		this.filter = filter;
	}
	
	@Override
	public String getConfig() {
		return String.format("{%1s : [%2s, %3s, %4s, %5s]}", getName(),
					getConfig(backDrop),
					getConfig(bgSys),
					getConfig(fgSys),
					getConfig(filter)
				);
	}
	
	private String getConfig(APVPlugin plugin) {
		if (plugin != null) {
			return plugin.getConfig();
		} else {
			return "{}"; //Object notation
		}
	}
	
	@Override
	public void draw() {
		drawScene();
	}
	
	@Override
	public void setup() {
		//Do nothing.  This is called during application startup.
	}

	public boolean isNew() {
		return false;
	}
	
	public boolean isNormal() {
		return true;
	}

	public void drawScene() {
		if (backDrop != null) {
			parent.drawSystem(backDrop, "backDrop");
		}
		
		if (bgSys != null) {
			parent.drawSystem(bgSys, "bgSys");
		}
		
		if (filter != null) {
			parent.getSettingsDisplay().addSettingsMessage("filter: " + filter.getName());
			filter.preRender();
		}
		
		if (fgSys != null) {
			parent.drawSystem(fgSys, "fgSys");
		}
		
		if (filter != null) {
			filter.postRender();
		}
	}

	public BackDropSystem getBackDrop() {
		return backDrop;
	}

	public ShapeSystem getBgSys() {
		return bgSys;
	}

	public ShapeSystem getFgSys() {
		return fgSys;
	}

	public Filter getFilter() {
		return filter;
	}
}
