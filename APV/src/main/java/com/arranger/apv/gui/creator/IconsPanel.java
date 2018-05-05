package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.ImageHelper;

import processing.core.PImage;

@SuppressWarnings("serial")
public class IconsPanel extends SetPackPanel {
	
	private static final Dimension PREFERRED_ICON_SIZE = new Dimension(400, 400);
	
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
		
		private String title;
		ICON_NAMES(String title) {
			this.title = title;
		}
		
		String getFullTitle() {
			return title + ".png";
		}
		
		static final List<ICON_NAMES> VALUES = Arrays.asList(ICON_NAMES.values());
	}
	

	private FileHelper fileHelper;
	private Map<ICON_NAMES, ImageHolder> iconMap = new HashMap<ICON_NAMES, ImageHolder>();
	private JLabel label;
	private int index = 0;
	
	public IconsPanel(Main parent) {
		super(parent, PANELS.ICONS);
		fileHelper = new FileHelper(parent);
		
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
			JFileChooser fc = fileHelper.getJFileChooser();
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
	
	public void updateForDemo(boolean isDemoActive) {
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