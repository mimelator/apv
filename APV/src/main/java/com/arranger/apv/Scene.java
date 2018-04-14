package com.arranger.apv;

import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.filter.Filter;

public class Scene extends ShapeSystem {

	protected BackDropSystem backDrop;
	protected ShapeSystem bgSys;
	protected ShapeSystem fgSys;
	protected Filter filter;
	
	public Scene(Main parent) {
		super(parent, null);
	}
	
	public void setSystems(BackDropSystem backDrop, ShapeSystem bgSys, ShapeSystem fgSys, Filter filter) {
		this.backDrop = backDrop;
		this.bgSys = bgSys;
		this.fgSys = fgSys;
		this.filter = filter;
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
