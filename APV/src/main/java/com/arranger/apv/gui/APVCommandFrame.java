package com.arranger.apv.gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
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

	private JList<CMDModel> list;
	private JPanel panel;
	
	@SuppressWarnings("serial")
	public APVCommandFrame(Main parent, boolean launchFrame) {
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
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane jScrollPane = new JScrollPane(list);
		jScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(jScrollPane);
		
		JPanel p = new JPanel();
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		p.add(textField);
		p.add(button);
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(p);
		
		if (launchFrame) {
			createFrame("Commands", 300, 300, panel, () -> {}).pack();
		}
	}
	
	public APVCommandFrame(Main parent) {
		this(parent, true);
	}
	
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
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
	
}
