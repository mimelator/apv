package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;
import com.arranger.apv.helpers.VideoGameHelper;
import com.arranger.apv.util.CounterMap;

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
		JPanel commandFramePanel = apvCommandFrame.getPanel();
		JPanel marqueeLauncherPanel = apvMarqueeLauncher.getPanel();
		JPanel sPanel = createStats();
		commandFramePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		marqueeLauncherPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(commandFramePanel);
		centerPanel.add(marqueeLauncherPanel);
		centerPanel.add(sPanel);
		
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
		
		createFrame("APV", (int)(parent.width * .75f), (int)(parent.height * .75f), master, () -> {
			children.forEach(c -> c.onClose());
		});
	}
	
	private static final String COMMANDS = "Commands";
	private static final String COMMAND_SOURCES = "Command Sources";
	private static final String PLUGINS = "Plugins";
	private static final String PLUGIN_SOURCES = "Plugin Sources";
	private static final String SCENE_COMPONENTS = "Scene Components";
	
	private static final String [] CARD_NAMES = {COMMANDS, COMMAND_SOURCES, PLUGINS, PLUGIN_SOURCES, SCENE_COMPONENTS};
	private JPanel statsPanel;
	
	protected JPanel createStats() {
		VideoGameHelper vg = parent.getVideoGameHelper();
		
		JPanel comboBoxPane = new JPanel(); //use FlowLayout
        JComboBox<String> cb = new JComboBox<String>(CARD_NAMES);
        cb.setEditable(false);
        cb.addItemListener((evt) -> {
        	CardLayout cl = (CardLayout)(statsPanel.getLayout());
            cl.show(statsPanel, (String)evt.getItem());
        });
        comboBoxPane.add(cb);
		
		statsPanel = new JPanel(new CardLayout());
		statsPanel.add(createPanel(vg.getCommandMap()).getPanel(), COMMANDS);
		statsPanel.add(createPanel(vg.getCommandSourceMap()).getPanel(), COMMAND_SOURCES);
		statsPanel.add(createPanel(vg.getPluginMap()).getPanel(), PLUGINS);
		statsPanel.add(createPanel(vg.getPluginSourceMap()).getPanel(), PLUGIN_SOURCES);
		statsPanel.add(createPanel(vg.getSceneComponents()).getPanel(), SCENE_COMPONENTS);
		
		JPanel panel = new JPanel();
		comboBoxPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(comboBoxPane, BorderLayout.PAGE_START);
		panel.add(statsPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	protected APVStatPanel createPanel(CounterMap map) {
		return new APVStatPanel(parent, map);
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
