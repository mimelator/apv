package com.arranger.apv;

import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.scene.Animation;
import com.arranger.apv.util.Configurator;

public class Scene extends ShapeSystem {

	protected BackDropSystem backDrop;
	protected ShapeSystem bgSys;
	protected ShapeSystem fgSys;
	protected Filter filter;
	protected ColorSystem colorSys;
	protected LocationSystem locSys;
	
	protected int lastFrameDrawn = 0;
	
	public Scene(Main parent) {
		super(parent, null);
	}
	
	public Scene(Configurator.Context ctx) {
		this(ctx.getParent());
	}
	
	public Scene(Scene o) {
		super(o.parent, null);
		
		setSystems(o.getBackDrop(), o.getBgSys(), o.getFgSys(), o.getFilter(), o.getColorSys(), o.getLocSys());
	}
	
	public void setSystems(BackDropSystem backDrop, ShapeSystem bgSys, 
			ShapeSystem fgSys, Filter filter,
			ColorSystem cs, LocationSystem ls) {
		this.backDrop = backDrop;
		this.bgSys = bgSys;
		this.fgSys = fgSys;
		this.filter = filter;
		this.colorSys = cs;
		this.locSys = ls;
	}
	
	public char getHotKey() {
		return '0';
	}
	
	@Override
	public String getConfig() {
		return String.format("{%s : [%s, %s, %s, %s, %s, %s]}", getName(),
					getConfig(backDrop),
					getConfig(bgSys),
					getConfig(fgSys),
					getConfig(filter),
					getConfig(colorSys),
					getConfig(locSys)
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
		lastFrameDrawn = parent.getFrameCount();
		drawScene();
	}
	
	public boolean isNew() {
		return false;
	}
	
	public boolean isNormal() {
		return !(this instanceof Animation);
	}

	public void drawScene() {
		if (colorSys != null) {
			parent.setNextColor(colorSys);
		}
		
		if (locSys != null) {
			parent.setNextLocation(locSys);
		}
		
		if (backDrop != null) {
			parent.drawSystem(backDrop, "backDrop", backDrop.isSafe());
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
	
	public ColorSystem getColorSys() {
		return colorSys;
	}
	
	public LocationSystem getLocSys() {
		return locSys;
	}
}
