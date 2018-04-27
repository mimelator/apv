package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.cmd.CommandSystem.RegisteredCommandHandler;

public class APVCommandFrame extends APVFrame {

	static class CMDModel implements Comparable<CMDModel> {
		Command command;
		
		public CMDModel(Command command) {
			this.command = command;
		}

		@Override
		public int compareTo(CMDModel o) {
			return toString().compareTo(o.toString());
		}

		@Override
		public String toString() {
			return command.name();
		}
	}
	
	private JList<CMDModel> list;
	
	@SuppressWarnings("serial")
	public APVCommandFrame(Main parent) {
		super(parent);
		CommandSystem cs = parent.getCommandSystem();
		
		Vector<CMDModel> modelList = new Vector<CMDModel>();
		cs.getCommands().entrySet().forEach(e -> {
			RegisteredCommandHandler c = e.getValue().get(0);
			modelList.add(new CMDModel(c.getCommand()));
		});
		
		modelList.sort(null);
		list = new JList<CMDModel>(modelList) {
			@Override
			public String getToolTipText(MouseEvent event) {
				int index = locationToIndex(event.getPoint());
				CMDModel mdl = getModel().getElementAt(index);
				return String.format("[%s]: %s", mdl.command.getKey(), mdl.command.getHelpText());
			}
		};
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					CMDModel model = list.getSelectedValue();
					cs.invokeCommand(model.command, getName());
				}
			}
		});
		
		JTextField textField = new JTextField(30); //max length of Marquee
		JButton button = new JButton("Send Message");
		button.addActionListener(l -> {
			parent.sendMarqueeMessage(textField.getText());
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(list), BorderLayout.PAGE_START);
		
		JPanel p = new JPanel();
		p.add(textField);
		p.add(button);
		panel.add(p, BorderLayout.PAGE_END);
		
		createFrame("Commands", 300, 300, panel, () -> {}).pack();
	}
}
