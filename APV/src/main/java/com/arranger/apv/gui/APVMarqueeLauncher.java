package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.arranger.apv.Main;

public class APVMarqueeLauncher extends APVFrame {

	private JList<String> list;
	
	public APVMarqueeLauncher(Main parent) {
		super(parent);
		
		Vector<String> modelList = new Vector<String>();
		parent.getMarqueeList().getList().forEach(s -> {
			modelList.add(s);
		});
		
		list = new JList<String>(modelList);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					String message = list.getSelectedValue();
					parent.sendMarqueeMessage(message);
				}
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(list), BorderLayout.PAGE_START);
		
		createFrame("Commands", 300, 300, panel, () -> {}).pack();
	}
}
