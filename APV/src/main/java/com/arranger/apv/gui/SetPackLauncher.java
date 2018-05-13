package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.arranger.apv.Main;
import com.arranger.apv.util.FileHelper;

public class SetPackLauncher extends APVFrame {

	private static final Dimension PREFERRED_SIZE = new Dimension(300, 150);
	private JList<String> list;
	private JPanel panel;
	private FileHelper fh;
	private DefaultListModel<String> modelList = new DefaultListModel<String>();
	
	public SetPackLauncher(Main parent, boolean launchFrame) {
		super(parent);
		fh = new FileHelper(parent);
		
		parent.getSetPackList().getList().forEach(s -> {
			modelList.addElement(s);
		});
		
		list = new JList<String>(modelList);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					String setPackName = list.getSelectedValue();
					if (setPackName != null && !setPackName.isEmpty()) {
						Path setPackPath = fh.getSetPacksFolder().toPath().resolve(setPackName);
						Path confgFile = setPackPath.resolve("application.conf");
						parent.reloadConfiguration(confgFile.toAbsolutePath().toString());
					}
				}
			}
		});
		
		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		
		addButton.addActionListener(evt -> {
			JFileChooser fc = fh.getJFileChooser();
			fc.setCurrentDirectory(fh.getSetPacksFolder());
			fc.setMultiSelectionEnabled(true);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				Arrays.asList(fc.getSelectedFiles()).forEach(file -> {
					modelList.addElement(file.toString());
				});
			}
		});
		
		removeButton.addActionListener(evt -> {
			list.getSelectedValuesList().forEach(sm -> modelList.removeElement(sm));
		});

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel("Set Packs"));
		panel.add(new JScrollPane(list), BorderLayout.PAGE_START);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setMinimumSize(PREFERRED_SIZE);
		btnPanel.add(addButton);
		btnPanel.add(removeButton);
		panel.add(btnPanel);
		
		if (launchFrame) {
			createFrame("SetPackLauncher", 300, 300, panel, () -> {}).pack();
		}
	}
	
	public SetPackLauncher(Main parent) {
		this(parent, true);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
