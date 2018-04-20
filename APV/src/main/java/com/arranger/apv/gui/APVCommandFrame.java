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

import com.arranger.apv.CommandSystem;
import com.arranger.apv.CommandSystem.APVCommand;
import com.arranger.apv.Main;

public class APVCommandFrame extends APVFrame {

	static class CMDModel implements Comparable<CMDModel> {
		char c;
		String text;
		String helpText;
		
		public CMDModel(char c, String text, String helpText) {
			this.c = c;
			this.text = text;
			this.helpText = helpText;
		}

		@Override
		public int compareTo(CMDModel o) {
			return text.compareTo(o.text);
		}

		@Override
		public String toString() {
			return text;
		}
	}
	
	private JList<CMDModel> list;
	
	@SuppressWarnings("serial")
	public APVCommandFrame(Main parent) {
		super(parent);
		CommandSystem cs = parent.getCommandSystem();
		
		Vector<CMDModel> modelList = new Vector<CMDModel>();
		cs.getCharCommands().entrySet().forEach(e -> {
			APVCommand c = e.getValue().get(0);
			modelList.add(new CMDModel(c.getCharKey(), c.getName(), c.getHelpText()));
		});
		
		modelList.sort(null);
		list = new JList<CMDModel>(modelList) {
			@Override
			public String getToolTipText(MouseEvent event) {
				int index = locationToIndex(event.getPoint());
				CMDModel mdl = getModel().getElementAt(index);
				return mdl.helpText;
			}
		};
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					CMDModel model = list.getSelectedValue();
					cs.invokeCommand(model.c);
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
