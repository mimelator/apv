package com.arranger.apv.scene;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Scene;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.util.Configurator;

public class LikedScene extends Scene {

	public LikedScene(Main parent) {
		super(parent);
	}

	public LikedScene(Configurator.Context ctx) {
		this(ctx.getParent());
		
		if (ctx.argList.size() == 0) {
			return;
		}
		
		setSystems((BackDropSystem)ctx.loadPlugin(0),
				(ShapeSystem)ctx.loadPlugin(1),
				(ShapeSystem)ctx.loadPlugin(2),
				(Filter)ctx.loadPlugin(3),
				(ColorSystem)ctx.loadPlugin(4),
				(LocationSystem)ctx.loadPlugin(5));
	}

	public LikedScene(Scene o) {
		super(o);
	}

	@Override
	public void setup() {
		setup(backDrop);
		setup(bgSys);
		setup(fgSys);
		setup(filter);
		setup(colorSys);
		setup(locSys);
	}
	
	private void setup(APVPlugin ss) {
		if (ss != null) {
			ss.setup();
		}
	}
}
