package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;

public class APVWindow extends APVFrame {

	List<APVFrame> children = new ArrayList<APVFrame>();
	
	public APVWindow(Main parent) {
		super(parent);
		
		//Create all the panels and add them to a GridLayout?
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		
		SwitchStatus switchStatus = new SwitchStatus(parent, false);
		APVCommandFrame apvCommandFrame = new APVCommandFrame(parent, false);
		APVMarqueeLauncher apvMarqueeLauncher = new APVMarqueeLauncher(parent, false);
		APVTextFrame settings = createTextFrame(parent.getSettingsDisplay());
		APVTextFrame help = createTextFrame(parent.getHelpDisplay());
		
		master.add(switchStatus.getPanel(), BorderLayout.PAGE_START);
		
		//Big Center Panel
		Panel centerPanel = new Panel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(apvCommandFrame.getPanel());
		centerPanel.add(apvMarqueeLauncher.getPanel());
		apvCommandFrame.getPanel().setAlignmentX(Component.LEFT_ALIGNMENT);
		apvMarqueeLauncher.getPanel().setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton button = new JButton("Load Config");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					parent.reloadConfiguration(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		centerPanel.add(button);
		
		master.add(centerPanel, BorderLayout.CENTER);
		master.add(settings.getPanel(), BorderLayout.LINE_START);
		master.add(help.getPanel(), BorderLayout.LINE_END);
		
		children.add(switchStatus);
		children.add(apvCommandFrame);
		children.add(apvMarqueeLauncher);
		children.add(settings);
		children.add(help);
		
		createFrame("APV", parent.width / 2, parent.height / 2, master, () -> {
			children.forEach(c -> c.onClose());
		});
	}
	
	protected APVTextFrame createTextFrame(TextSupplier ts) {
		return new APVTextFrame(parent, 
				getName(),
				(int)(parent.width / 4),
				(int)(parent.height * .8f),
				parent.getDrawEvent(),
				() -> ts.getMessages(),
				false);
	}
}
