package com.arranger.apv.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.helpers.Switch.STATE;
import com.arranger.apv.util.Configurator;

public class SwitchStatus extends APVFrame {
	
	private List<SwitchPanel> panelList = new ArrayList<SwitchPanel>();
	private EventHandler handler;
	private JPanel panel;
	private CoreEvent event;
	
	public SwitchStatus(Main parent, boolean launchWindow) {
		super(parent);
		
		//fetch original settings
		Map<String, Switch.STATE> origStateMap = new HashMap<String, Switch.STATE>();
		Configurator config = parent.getConfigurator();
		config.loadAVPPlugins(SYSTEM_NAMES.SWITCHES).forEach(p -> {
			Switch sw = (Switch)p;
			origStateMap.put(sw.name, sw.state);
		});
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		event = parent.getDrawEvent();
		Collection<Switch> values = parent.getSwitches().values();
		
		values.forEach((sw) -> {
			SwitchPanel switchPanel = new SwitchPanel(origStateMap.get(sw.name), sw);
			panelList.add(switchPanel);
			panel.add(switchPanel);
		});
		
		if (launchWindow) {
			createFrame(getName(), 250, 150, panel, () -> event.unregister(handler));
		}
		
		handler = event.register(() -> {
			panelList.forEach(sp -> sp.updateColor());
		});
	}
	
	public SwitchStatus(Main parent) {
		this(parent, true);
	}
	
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
	@Override
	public void onClose() {
		event.unregister(handler);
	}

	@SuppressWarnings("serial")
	protected class SwitchPanel extends JLabel {
		
		private Switch.STATE origState;
		private Switch sw;
		
		public SwitchPanel(STATE origState, Switch sw) {
			this.origState = origState;
			this.sw = sw;
			setOpaque(true);
			setText(sw.name);
			
	    	addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					sw.toggle();
				}
			});
		}
		
		public void updateColor() {
			Color result = Color.GREEN ;
			if (origState != sw.state) {
				result = Switch.STATE.FROZEN == sw.state ? Color.BLUE : Color.YELLOW;
			}
			
			setBackground(result);
		}
	}
}
