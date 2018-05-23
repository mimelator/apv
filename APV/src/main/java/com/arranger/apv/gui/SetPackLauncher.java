package com.arranger.apv.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.arranger.apv.Main;
import com.arranger.apv.model.SetPackModel;
import com.arranger.apv.util.FileHelper;

public class SetPackLauncher extends APVFrame {

	private static final Dimension PREFERRED_SIZE = new Dimension(300, 150);
	private JList<String> list;
	private JPanel panel;
	private FileHelper fh;
	private SetPackModel setPackModel;
	private DefaultListModel<String> modelList = new DefaultListModel<String>();
	
	public SetPackLauncher(Main parent, boolean launchFrame) {
		super(parent);
		fh = new FileHelper(parent);
		setPackModel = parent.getSetPackModel();
		sync(true);
		
		list = new JList<String>(modelList);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					int index = list.getSelectedIndex();
					if (index != -1) {
						setPackModel.launchSetPack(index);
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
				sync(false);
			}
		});
		
		removeButton.addActionListener(evt -> {
			list.getSelectedValuesList().forEach(sm -> modelList.removeElement(sm));
			sync(false);
		});

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Set Packs"));
		panel.add(new JScrollPane(list));
		
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
	
	private void sync(boolean fetch) {
		if (fetch) {
			modelList.clear();
			setPackModel.getSetPackList().forEach(s -> {
				modelList.addElement(s);
			});
		} else {
			List<String> setPackList = new ArrayList<String>();
			IntStream.range(0, modelList.getSize()).forEach(i -> {
				setPackList.add(modelList.get(i));
			});
			setPackModel.setSetPackList(setPackList);
		}
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
