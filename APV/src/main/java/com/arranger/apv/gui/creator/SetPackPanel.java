package com.arranger.apv.gui.creator;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JPanel;

import com.arranger.apv.Main;

@SuppressWarnings("serial")
public abstract class SetPackPanel extends JPanel {
	
	private static final Dimension PREFERRED_TAB_PANE_SIZE = new Dimension(500, 500);
	
	public static enum PANELS {
		ICONS("Icons"),
		COLORS("Colors"),
		EMOJIS("Emojis/Messages"),
		SONGS("Songs");
		
		private String title;
		
		private PANELS(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
	}
	
	protected Main parent;
	protected PANELS panel;

	public SetPackPanel(Main parent, PANELS panel) {
		this.parent = parent;
		this.panel = panel;
		setPreferredSize(PREFERRED_TAB_PANE_SIZE);
	}
	
	public PANELS getPanel() {
		return panel;
	}
	
	public abstract void createFilesForSetPack(Path parentDirectory) throws IOException;

	public abstract void updateForDemo(boolean isDemoActive);
}
