package com.arranger.apv.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
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
	
	public APVCommandFrame(Main parent) {
		super(parent);
		CommandSystem cs = parent.getCommandSystem();
		
		Map<Character, List<APVCommand>> charCommands = cs.getCharCommands();
		Vector<CMDModel> modelList = new Vector<CMDModel>();
		charCommands.entrySet().forEach(e -> {
			APVCommand c = e.getValue().get(0);
			modelList.add(new CMDModel(c.getCharKey(), c.getName(), c.getHelpText()));
		});
		
		modelList.sort(null);
		list = new JList<CMDModel>(modelList);
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
		panel.add(new JScrollPane(list));
		panel.add(textField);
		panel.add(button);
		createFrame("Commands", 300, 300, panel, () -> {}).pack();
	}
}
