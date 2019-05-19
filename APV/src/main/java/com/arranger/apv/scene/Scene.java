package com.arranger.apv.scene;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator;

public class Scene extends ShapeSystem {

	public static class Components {
	
		public BackDropSystem backDrop;
		public ShapeSystem bgSys;
		public ShapeSystem fgSys;
		public Filter filter;
		public Shader shader;
		public ColorSystem colorSys;
		public LocationSystem locSys;
		
		public Components() {
		}
		
		public Components(Components o) {
			this.backDrop = o.backDrop;
			this.bgSys = o.bgSys;
			this.fgSys = o.fgSys;
			this.filter = o.filter;
			this.shader = o.shader;
			this.colorSys = o.colorSys;
			this.locSys = o.locSys;
		}
		
		public APVPlugin getComponentFromSystem(Main.SYSTEM_NAMES system) {
			switch (system) {
			case BACKDROPS:
				return backDrop;
			case BACKGROUNDS:
				return bgSys;
			case FOREGROUNDS:
				return fgSys;
			case FILTERS:
				return filter;
			case SHADERS:
				return shader;
			case COLORS:
				return colorSys;
			case LOCATIONS:
				return locSys;
			default:
				return null;
			}
		}
		
		public boolean contains(APVPlugin plugin) {
			boolean found = false;
			if (backDrop != null) {
				found = backDrop.equals(plugin);
			}
			if (!found && bgSys != null) {
				found = bgSys.equals(plugin);
			}
			if (!found && fgSys != null) {
				found = fgSys.equals(plugin);
			}
			if (!found && filter != null) {
				found = filter.equals(plugin);
			}
			if (!found && shader != null) {
				found = shader.equals(plugin);
			}
			if (!found && colorSys != null) {
				found = colorSys.equals(plugin);
			}
			if (!found && locSys != null) {
				found = locSys.equals(plugin);
			}
			
			return found;
		}
		
		public List<APVPlugin> getPlugins() {
			List<APVPlugin> plugins = new ArrayList<APVPlugin>();
			
			if (backDrop != null) {
				plugins.add(backDrop);
			}
			if (bgSys != null) {
				plugins.add(bgSys);
			}
			if (fgSys != null) {
				plugins.add(fgSys);
			}
			if (filter != null) {
				plugins.add(filter);
			}
			if (shader != null) {
				plugins.add(shader);
			}
			if (colorSys != null) {
				plugins.add(colorSys);
			}
			if (locSys != null) {
				plugins.add(locSys);
			}
			return plugins;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((backDrop == null) ? 0 : backDrop.hashCode());
			result = prime * result + ((bgSys == null) ? 0 : bgSys.hashCode());
			result = prime * result + ((colorSys == null) ? 0 : colorSys.hashCode());
			result = prime * result + ((fgSys == null) ? 0 : fgSys.hashCode());
			result = prime * result + ((filter == null) ? 0 : filter.hashCode());
			result = prime * result + ((locSys == null) ? 0 : locSys.hashCode());
			result = prime * result + ((shader == null) ? 0 : shader.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Components other = (Components) obj;
			if (backDrop == null) {
				if (other.backDrop != null)
					return false;
			} else if (!backDrop.equals(other.backDrop))
				return false;
			if (bgSys == null) {
				if (other.bgSys != null)
					return false;
			} else if (!bgSys.equals(other.bgSys))
				return false;
			if (colorSys == null) {
				if (other.colorSys != null)
					return false;
			} else if (!colorSys.equals(other.colorSys))
				return false;
			if (fgSys == null) {
				if (other.fgSys != null)
					return false;
			} else if (!fgSys.equals(other.fgSys))
				return false;
			if (filter == null) {
				if (other.filter != null)
					return false;
			} else if (!filter.equals(other.filter))
				return false;
			if (locSys == null) {
				if (other.locSys != null)
					return false;
			} else if (!locSys.equals(other.locSys))
				return false;
			if (shader == null) {
				if (other.shader != null)
					return false;
			} else if (!shader.equals(other.shader))
				return false;
			return true;
		}
	}
	
	protected Components cc = new Components();
	protected int lastFrameDrawn = 0;
	
	public Scene(Main parent) {
		super(parent, null);
	}
	
	public Scene(Configurator.Context ctx) {
		this(ctx.getParent());
	}
	
	public Scene(Scene o) {
		super(o.parent, null);
		cc = new Components(o.cc);
	}

	public void setSystems(BackDropSystem backDrop, ShapeSystem bgSys, 
			ShapeSystem fgSys, Filter filter, Shader shader,
			ColorSystem cs, LocationSystem ls) {
		this.cc.backDrop = backDrop;
		this.cc.bgSys = bgSys;
		this.cc.fgSys = fgSys;
		this.cc.filter = filter;
		this.cc.shader = shader;
		this.cc.colorSys = cs;
		this.cc.locSys = ls;
	}
	
	@Override
	public String getConfig() {
		if (isLikedScene()) {
			return String.format("{%s : [%s, %s, %s, %s, %s, %s, %s]}", getName(),
						getConfig(cc.backDrop),
						getConfig(cc.bgSys),
						getConfig(cc.fgSys),
						getConfig(cc.filter),
						getConfig(cc.shader),
						getConfig(cc.colorSys),
						getConfig(cc.locSys));
		} else {
			return super.getConfig();
		}
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
	
	@Override
	public void onFactoryUpdate() {
	}

	public boolean isNew() {
		return false;
	}
	
	public boolean isAnimation() {
		return this instanceof Animation;
	}
	
	public boolean isLikedScene() {
		return this instanceof LikedScene;
	}
	
	public Components getComponentsToDrawScene() {
		return cc;
	}

	public void drawScene() {
		Components comp = getComponentsToDrawScene();
		
		if (isLikedScene()) {
			if (comp.colorSys != null) {
				parent.setNextColor(comp.colorSys, "likedScene");
			}
			
			if (comp.locSys != null) {
				parent.setNextLocation(comp.locSys, "likedScene");
			}
		}
		
		if (comp.backDrop != null) {
			parent.drawSystem(comp.backDrop, "backDrop", comp.backDrop.isSafe());
		}
		
		if (comp.bgSys != null) {
			parent.drawSystem(comp.bgSys, "bgSys");
		}
		
		if (comp.filter != null) {
			parent.getSettingsDisplay().addSettingsMessage("filter: " + comp.filter.getName());
			comp.filter.preRender();
		}
		
		if (comp.fgSys != null) {
			parent.drawSystem(comp.fgSys, "fgSys");
		}
		
		if (comp.filter != null) {
			comp.filter.postRender();
		}
		
		if (comp.shader != null) {
			parent.drawSystem(comp.shader, "shader");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cc == null) ? 0 : cc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Scene other = (Scene) obj;
		if (cc == null) {
			if (other.cc != null)
				return false;
		} else if (!cc.equals(other.cc))
			return false;
		return true;
	}
}
