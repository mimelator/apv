package com.arranger.apv.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.util.APVSetList;
import com.arranger.apv.util.ImageHelper;

import processing.core.PImage;

public class SetPackCreator extends APVFrame {

	private static final Dimension PREFERRED_TAB_PANE_SIZE = new Dimension(500, 500);
	private static final Dimension PREFERRED_ICON_SIZE = new Dimension(400, 400);
	
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
		
		panels.add(new IconsPanel());
		panels.add(new ColorsPanel());
		panels.add(new EmojisPanel());
		panels.add(new SongsPanel());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		panels.forEach(pnl -> {
			tabbedPane.addTab(pnl.panel.title, pnl);
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
	
	enum ICON_NAMES {
		SPRITE("sprite"),
		PURPLE("purple"),
		TRIANGLE("triangle"),
		GRADIENT_TRIANGLE("gradient-triangle"),
		SWIRL("swirl"),
		WARNING("warning"),
		THREE_D_CUBE("3dcube"),
		SILLY("Silly_Emoji"),
		SCARED("Scared_face_emoji"),
		ISLAND("emoji-island"),
		BLITZ("Emoji_Blitz_Star"),
		CIRCLE("simpleCircle"),
		THREE_D_STAR("3dstar");
		
		String title;
		ICON_NAMES(String title) {
			this.title = title;
		}
		
		String getFullTitle() {
			return title + ".png";
		}
		
		static final List<ICON_NAMES> VALUES = Arrays.asList(ICON_NAMES.values());
	}
	
	@SuppressWarnings("serial")
	class IconsPanel extends SetPackPanel {
		
		Map<ICON_NAMES, ImageHolder> iconMap = new HashMap<ICON_NAMES, ImageHolder>();
		JLabel label;
		int index = 0;
		
		IconsPanel() {
			super(PANELS.ICONS, false);
			
			ICON_NAMES.VALUES.forEach(icon -> {
				String iconFile = parent.getConfigString(icon.getFullTitle());
				Image img = parent.loadImage(iconFile).getImage();
				iconMap.put(icon, new ImageHolder(iconFile, img));
			});
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			label = new JLabel();
			label.setPreferredSize(PREFERRED_ICON_SIZE);
			label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
			label.setAlignmentX(CENTER_ALIGNMENT);
			add(label);
			
			JButton prevBtn = new JButton("Prev");
			prevBtn.addActionListener(evt -> updateLabel(--index));
			
			JButton nextBtn = new JButton("Next");
			nextBtn.addActionListener(evt -> updateLabel(++index));
			
			JButton changeBtn = new JButton("Change");
			changeBtn.addActionListener(evt -> {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				fc.setFileFilter(new FileNameExtensionFilter("PNGs", "png"));
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					 updateCurrentIcon(fc.getSelectedFile());
				}
			});
			
			JPanel btnPanel = new JPanel();
			btnPanel.add(prevBtn);
			btnPanel.add(nextBtn);
			btnPanel.add(changeBtn);
			btnPanel.setAlignmentX(CENTER_ALIGNMENT);
			add(btnPanel);
			
			updateLabel(index);
		}
		
		private void updateCurrentIcon(File newIconFile) {
			ImageHolder icon = getCurrentIcon();
			icon.setFile(newIconFile);
			updateLabel(index);
		}

		private void updateLabel(int idx) {
			if (idx < 0) {
				idx += ICON_NAMES.VALUES.size();
			} else if (idx > ICON_NAMES.VALUES.size() - 1) {
				idx -= ICON_NAMES.VALUES.size();
			}
			index = idx;
			
			ImageHolder icon = getCurrentIcon();
			label.setIcon(icon.getImageIcon());
		}
		
		private ImageHolder getCurrentIcon() {
			ICON_NAMES i = ICON_NAMES.VALUES.get(index);
			return iconMap.get(i);
		}
		
		void updateForDemo(boolean isDemoActive) {
			ImageHelper ih = parent.getImageHelper();
			ICON_NAMES.VALUES.forEach(icon -> {
				ImageHolder imgHolder = iconMap.get(icon);
				ih.updateImage(icon.getFullTitle(), isDemoActive ? imgHolder.getPImage() : imgHolder.getOriginalImage());
			});
		}
		
		class ImageHolder {
			File file;
			Image image, origImage;
			String configPath;
			
			ImageHolder(String configPath, Image image) {
				this.configPath = configPath;
				this.image = image;
				this.origImage = image;
			}
			
			ImageHolder(File file) {
				this.file = file;
			}
			
			void setFile(File file) {
				this.file = file;
				image = null;
			}

			Image getImage() {
				if (image == null) {
					image = parent.loadImage(file.getAbsolutePath()).getImage();
				}
				return image;
			}
			
			PImage getOriginalImage() {
				return new PImage(origImage);
			}
			
			PImage getPImage() {
				return new PImage(getImage());
			}
			
			ImageIcon getImageIcon() {
				Image img = getImage();
				
				//scale it
				Image scaled = img.getScaledInstance(PREFERRED_ICON_SIZE.width, PREFERRED_ICON_SIZE.height, Image.SCALE_SMOOTH);
				return new ImageIcon(scaled);
			}
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
		
		JList<SongModel> songList;
		DefaultListModel<SongModel> modelList = new DefaultListModel<SongModel>();
		
		SongsPanel() {
			super(PANELS.SONGS, false);
			
			songList = new JList<SongModel>(modelList);

			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			
			addButton.addActionListener(evt -> {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				fc.setFileFilter(new FileNameExtensionFilter("MP3s", "mp3"));
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					Arrays.asList(fc.getSelectedFiles()).forEach(f -> {
						modelList.addElement(new SongModel(f));
					});
				}
			});
			
			removeButton.addActionListener(evt -> {
				songList.remove(songList.getSelectedIndex());
			});
			
			JScrollPane jScrollPane = new JScrollPane(songList);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(jScrollPane);
			
			JPanel btnPanel = new JPanel();
			btnPanel.add(addButton);
			btnPanel.add(removeButton);
			add(btnPanel);
		}
		
		SongModel getSelectedSong() {
			SongModel song = songList.getSelectedValue();
			if (song == null) {
				ListModel<SongModel> model = songList.getModel();
				if (model.getSize() > 0) {
					song = model.getElementAt(0);
				}
			}
			return song;
		}
		
		void updateForDemo(boolean isDemoActive) {
			if (isDemoActive) {
				SongModel song = getSelectedSong();
				if (song != null) {
					parent.playSetList(song.songFile);
				}
			} else {
				APVSetList setList = parent.getSetList();
				if (setList != null) {
					setList.stop();
				}
			}
		}
		
		class SongModel  {
			File songFile;

			public SongModel(File songFile) {
				super();
				this.songFile = songFile;
			}
			
			public String toString() {
				return songFile.getName();
			}
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
