package com.arranger.apv.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.arranger.apv.Main;

public class SetPackCreator extends APVFrame {

	private static final Dimension PREFERRED_TAB_PANE_SIZE = new Dimension(500, 500);
	
	private static final int FRAME_HEIGHT = 650;
	private static final int FRAME_WIDTH = 600;
	
	enum PANELS {
		ICONS("Icons"),
		COLORS("Colors"),
		EMOJIS("Emojis/Messages"),
		SONGS("Songs");
		
		String title;
		
		private PANELS(String title) {
			this.title = title;
		}
	}
	
	List<SetPackPanel> panels = new ArrayList<SetPackPanel>();
	JButton demoButton;
	boolean demoMode = false;

	public SetPackCreator(Main parent) {
		super(parent);
		
		JPanel panel = new JPanel();
		
		panels.add(new IconsPanel());
		panels.add(new ColorsPanel());
		panels.add(new EmojisPanel());
		panels.add(new SongsPanel());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		panels.forEach(pnl -> {
			tabbedPane.addTab(pnl.panel.title, pnl);
		});
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
	
	
	void toggleDemoMode() {
		demoMode = !demoMode;
		demoButton.setOpaque(true);
		demoButton.setBackground(demoMode ? Color.RED : null);
		
		panels.forEach(pnl -> {
			pnl.updateForDemo(demoMode);
		});
	}
	
	void createSetPack() {
		throw new RuntimeException("Not implemented yet");
	}
	
	@SuppressWarnings("serial")
	class IconsPanel extends SetPackPanel {
		IconsPanel() {
			super(PANELS.ICONS, true);
		}
		
		void updateForDemo(boolean isDemoActive) {
			
		}
	}
	
	@SuppressWarnings("serial")
	class ColorsPanel extends SetPackPanel {
		ColorsPanel() {
			super(PANELS.COLORS, true);
		}
		
		void updateForDemo(boolean isDemoActive) {
			
		}
	}
	
	@SuppressWarnings("serial")
	class EmojisPanel extends SetPackPanel {
		JTextArea textArea;
		List<String> origMsgList; 
		
		EmojisPanel() {
			super(PANELS.EMOJIS, false);
			textArea = new JTextArea(15, 30);
			
			//get the current messages
			origMsgList = parent.getRandomMessagePainter().getMsgList();
			origMsgList.forEach(msg -> {
				textArea.append(msg);
				textArea.append(System.lineSeparator());
			});
			add(textArea);
		}
		
		void updateForDemo(boolean isDemoActive) {
			if (isDemoActive) {
				parent.getRandomMessagePainter().setMsgList(getMsgList());
			} else {
				parent.getRandomMessagePainter().setMsgList(origMsgList);
			}
		}
		
		List<String> getMsgList() {
			String text = textArea.getText();
			String[] split = text.split("[\\r\\n]+"); //https://stackoverflow.com/questions/454908/split-java-string-by-new-line
			return Arrays.asList(split);
		}
	}
	
	@SuppressWarnings("serial")
	class SongsPanel extends SetPackPanel {
		SongsPanel() {
			super(PANELS.SONGS, true);
		}
		
		void updateForDemo(boolean isDemoActive) {
			
		}
	}
	
	@SuppressWarnings("serial")
	abstract class SetPackPanel extends JPanel {
		PANELS panel;
		
		SetPackPanel(PANELS panel, boolean addFiller) {
			this.panel = panel;
			setPreferredSize(PREFERRED_TAB_PANE_SIZE);
			if (addFiller) {
				addFiller();
			}
		}
		
		abstract void updateForDemo(boolean isDemoActive);
		
		void addFiller() {
			JLabel filler = new JLabel(panel.title);
			filler.setHorizontalAlignment(JLabel.CENTER);
			setLayout(new GridLayout(1, 1));
			add(filler);
		}
	}
}
