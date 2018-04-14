package com.arranger.apv;

import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.filter.Filter;

public class Scene extends APVPlugin {

	protected BackDropSystem backDrop;
	protected ShapeSystem bgSys;
	protected ShapeSystem fgSys;
	protected Filter filter;
	
	
	public Scene(Main parent) {
		super(parent);
	}
	
	public Scene(Main parent, BackDropSystem backDrop, ShapeSystem bgSys, ShapeSystem fgSys, Filter filter) {
		super(parent);
		this.backDrop = backDrop;
		this.bgSys = bgSys;
		this.fgSys = fgSys;
		this.filter = filter;
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
	

}
