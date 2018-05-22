package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.model.Creator;
import com.arranger.apv.model.IconsModel;
import com.arranger.apv.model.IconsModel.ImageHolder;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;

@SuppressWarnings("serial")
public class IconsPanel extends SetPackPanel {

	private static final String EXPLANATION_MSG = "<html>If you see a red border, that means that the selected image isn't transparent and won't look great.</html>";
	
	private FileHelper fileHelper;
	
	private JLabel label;
	private int index = 0;
	private IconsModel iconsModel;
	
	public IconsPanel(Main parent) {
		super(parent, PANELS.ICONS);
		fileHelper = new FileHelper(parent);
		iconsModel = parent.getIconsModel();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		label = new JLabel();
		label.setPreferredSize(IconsModel.PREFERRED_ICON_SIZE);
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
		
		JLabel message = new JLabel(EXPLANATION_MSG);
		message.setAlignmentX(CENTER_ALIGNMENT);
		add(message);
		
		updateLabel(index);
	}
	
	public void updateForDemo(boolean isDemoActive, Path parentDirectory) {
		new Creator(parent).updateIconsForDemo(isDemoActive, parentDirectory);
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) throws IOException {
		new Creator(parent).createIconFilesForSetPack(parentDirectory);
	}
	
	private void updateCurrentIcon(File newIconFile) {
		ImageHolder imageHolder = getCurrentImageHolder();
		imageHolder.setFile(newIconFile);
		iconsModel.checkAlpha(imageHolder);
		updateLabel(index);
	}

	private void updateLabel(int idx) {
		if (idx < 0) {
			idx += ICON_NAMES.VALUES.size();
		} else if (idx > ICON_NAMES.VALUES.size() - 1) {
			idx -= ICON_NAMES.VALUES.size();
		}
		index = idx;
		
		ImageHolder imageHolder = getCurrentImageHolder();
		label.setIcon(imageHolder.getImageIcon());
		if (imageHolder.isHasAlpha()) {
			label.setBorder(BorderFactory.createEmptyBorder());
		} else {
			label.setBorder(BorderFactory.createLineBorder(Color.RED));
		}
	}
	
	private ImageHolder getCurrentImageHolder() {
		ICON_NAMES i = ICON_NAMES.VALUES.get(index);
		return iconsModel.getIconMap().get(i);
	}
}