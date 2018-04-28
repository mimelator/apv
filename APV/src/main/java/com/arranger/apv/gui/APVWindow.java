package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;

public class APVWindow extends APVFrame {

	List<APVFrame> children = new ArrayList<APVFrame>();
	
	public APVWindow(Main parent) {
		super(parent);
		
		//Create all the panels and add them to a GridLayout?
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		
		SwitchStatus switchStatus = new SwitchStatus(parent, false);
		APVCommandFrame apvCommandFrame = new APVCommandFrame(parent, false);
		APVMarqueeLauncher apvMarqueeLauncher = new APVMarqueeLauncher(parent, false);
		APVTextFrame settings = createTextFrame(parent.getSettingsDisplay());
		APVTextFrame help = createTextFrame(parent.getHelpDisplay());
		
		master.add(switchStatus.getPanel(), BorderLayout.PAGE_START);
		master.add(apvCommandFrame.getPanel(), BorderLayout.PAGE_END);
		master.add(apvMarqueeLauncher.getPanel(), BorderLayout.CENTER);
		master.add(settings.getPanel(), BorderLayout.LINE_START);
		master.add(help.getPanel(), BorderLayout.LINE_END);
		
		children.add(switchStatus);
		children.add(apvCommandFrame);
		children.add(apvMarqueeLauncher);
		children.add(settings);
		children.add(help);
		
		createFrame("APV", parent.width / 2, parent.height / 2, master, () -> {
			children.forEach(c -> c.onClose());
		});
	}
	
	protected APVTextFrame createTextFrame(TextSupplier ts) {
		return new APVTextFrame(parent, 
				getName(),
				(int)(parent.width / 4),
				(int)(parent.height * .8f),
				parent.getDrawEvent(),
				() -> ts.getMessages(),
				false);
	}
}
