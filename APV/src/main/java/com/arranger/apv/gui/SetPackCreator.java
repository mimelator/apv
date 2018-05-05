package com.arranger.apv.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.arranger.apv.Main;
import com.arranger.apv.gui.creator.ColorsPanel;
import com.arranger.apv.gui.creator.EmojisPanel;
import com.arranger.apv.gui.creator.IconsPanel;
import com.arranger.apv.gui.creator.SetPackPanel;
import com.arranger.apv.gui.creator.SongsPanel;

public class SetPackCreator extends APVFrame {

	private static final int FRAME_HEIGHT = 650;
	private static final int FRAME_WIDTH = 600;
	
	
	private List<SetPackPanel> panels = new ArrayList<SetPackPanel>();
	private JButton demoButton;
	private boolean demoMode = false;

	public SetPackCreator(Main parent) {
		super(parent);
		
		panels.add(new IconsPanel(parent));
		panels.add(new ColorsPanel(parent));
		panels.add(new EmojisPanel(parent));
		panels.add(new SongsPanel(parent));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		panels.forEach(pnl -> {
			tabbedPane.addTab(pnl.getPanel().getTitle(), pnl);
		});
		
		JPanel panel = new JPanel();
		panel.add(tabbedPane);
		
		JPanel btnPanel = new JPanel();
		demoButton = new JButton("Demo");
		demoButton.addActionListener(evt -> {
			toggleDemoMode();
		});
		btnPanel.add(demoButton);
		JButton createButton = new JButton("Create Set Pack");
		createButton.addActionListener(evt -> {
			createSetPack();
		});
		btnPanel.add(createButton);
		panel.add(btnPanel);
		
		createFrame(getName(), FRAME_WIDTH, FRAME_HEIGHT, panel, () -> {});
	}
	
	private void toggleDemoMode() {
		demoMode = !demoMode;
		demoButton.setOpaque(true);
		demoButton.setBackground(demoMode ? Color.RED : null);
		
		panels.forEach(pnl -> {
			pnl.updateForDemo(demoMode);
		});
	}
	
	private void createSetPack() {
		throw new RuntimeException("Not implemented yet");
	}
}
