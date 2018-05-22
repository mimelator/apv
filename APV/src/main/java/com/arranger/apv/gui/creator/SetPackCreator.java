package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVFrame;
import com.arranger.apv.model.Creator;
import com.arranger.apv.util.FileHelper;

public class SetPackCreator extends APVFrame {
	
	private static final Logger logger = Logger.getLogger(SetPackCreator.class.getName());
	
	private static final int FRAME_HEIGHT = 650;
	private static final int FRAME_WIDTH = 600;
	
	private FileHelper fileHelper;
	private List<SetPackPanel> panels = new ArrayList<SetPackPanel>();
	private JButton demoButton;
	private boolean demoMode = false;
	
	private IconsPanel iconsPanel;
	private ColorsPanel colorsPanel;
	private EmojisPanel emojisPanel;
	private SongsPanel songsPanel;
	
	public SetPackCreator(Main parent) {
		super(parent);
		fileHelper = new FileHelper(parent);
		
		panels.add(iconsPanel = new IconsPanel(parent));
		panels.add(colorsPanel = new ColorsPanel(parent));
		panels.add(emojisPanel = new EmojisPanel(parent));
		panels.add(songsPanel = new SongsPanel(parent));
		
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
	
	public ColorsPanel getColorsPanel() {
		return colorsPanel;
	}

	public IconsPanel getIconsPanel() {
		return iconsPanel;
	}

	public EmojisPanel getEmojisPanel() {
		return emojisPanel;
	}

	public SongsPanel getSongsPanel() {
		return songsPanel;
	}

	private void toggleDemoMode() {
		demoMode = !demoMode;
		demoButton.setOpaque(true);
		demoButton.setBackground(demoMode ? Color.RED : null);
		
		panels.forEach(pnl -> {
			pnl.updateForDemo(demoMode, null);
		});
	}
	
	private void createSetPack() {
		//get the name
		String setPackName = JOptionPane.showInputDialog("Please enter the set pack name");
		if (setPackName == null) {
			return;
		}
		
		try {
			createSetPack(fileHelper.getSetPacksFolder(), setPackName);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void createSetPack(File parentDirectory, String setPackName) throws IOException {
		new Creator(parent).createSetPack(parentDirectory, setPackName, 
				(isDemoActive, pd) -> {
					panels.forEach(pnl -> {
						pnl.updateForDemo(true, pd);
					});
				},
				
				(pd) -> {
					panels.forEach(pnl -> {
						try {
							pnl.createFilesForSetPack(pd);
						} catch (IOException e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						}
					});
				});
	}
}
