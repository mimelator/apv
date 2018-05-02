package com.arranger.apv.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;
import com.arranger.apv.helpers.VideoGameHelper;
import com.arranger.apv.util.CounterMap;

public class APVWindow extends APVFrame {

	private static final String COMMANDS = "Commands";
	private static final String COMMAND_SOURCES = "Command Sources";
	private static final String PLUGINS = "Plugins";
	private static final String PLUGIN_SOURCES = "Plugin Sources";
	private static final String SCENE_COMPONENTS = "Scene Components";
	private static final String [] CARD_NAMES = {COMMANDS, COMMAND_SOURCES, PLUGINS, PLUGIN_SOURCES, SCENE_COMPONENTS};
	
	private JPanel statsPanel;
	private List<APVFrame> children = new ArrayList<APVFrame>();
	
	public APVWindow(Main parent) {
		super(parent);
		
		//Create all the panels and add them to a GridLayout?
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		
		SwitchStatus switchStatus = new SwitchStatus(parent, false);
		APVTextFrame settings = createTextFrame(parent.getSettingsDisplay());
		APVTextFrame help = createTextFrame(parent.getHelpDisplay());
		children.add(switchStatus);
		children.add(settings);
		children.add(help);

		master.add(switchStatus.getPanel(), BorderLayout.PAGE_START);
		master.add(createCenterPanel(), BorderLayout.CENTER);
		master.add(settings.getPanel(), BorderLayout.LINE_START);
		master.add(help.getPanel(), BorderLayout.LINE_END);
		master.add(createFlagsPanel(), BorderLayout.PAGE_END);
		
		createFrame("APV", (int)(parent.width * .825f), (int)(parent.height * .75f), master, () -> {
			children.forEach(c -> c.onClose());
		});
	}
	
	protected JPanel createFlagsPanel() {
		JPanel panel = new JPanel();
		String result = Main.FLAGS.VALUES.stream().map(f -> {
			return f.name + '=' + parent.getConfigValueForFlag(f);
		}).collect(Collectors.joining("  |  "));
		
		panel.add(new JLabel(result));
		return panel;
	}
	
	protected JPanel createCenterPanel() {
		APVCommandFrame apvCommandFrame = new APVCommandFrame(parent, false);
		APVMarqueeLauncher apvMarqueeLauncher = new APVMarqueeLauncher(parent, false);
		children.add(apvCommandFrame);
		children.add(apvMarqueeLauncher);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel commandFramePanel = apvCommandFrame.getPanel();
		JPanel marqueeLauncherPanel = apvMarqueeLauncher.getPanel();
		JPanel sPanel = createStats();
		JButton loadConfigButton = new JButton("Load Config");
		loadConfigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					parent.reloadConfiguration(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		JButton agentInfoButton = new JButton("Agent Information");
		agentInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new APVTextFrame(parent, "Agents", 400, 400, () -> {
					return parent.getAgent().getList().stream().
							map(a -> a.getDisplayName() + ":" + a.getConfig()).
							collect(Collectors.toList());
				});
			}
		});
		
		JButton loadSetListButton = new JButton("Load Set List");
		loadSetListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					parent.playSetList(fc.getSelectedFile());
				}
			}
		});
		
		commandFramePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		marqueeLauncherPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loadConfigButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		agentInfoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		loadSetListButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		centerPanel.add(commandFramePanel);
		centerPanel.add(marqueeLauncherPanel);
		centerPanel.add(sPanel);
		centerPanel.add(loadConfigButton);
		centerPanel.add(agentInfoButton);
		centerPanel.add(loadSetListButton);
		
		return centerPanel;
	}
	
	protected JPanel createStats() {
		VideoGameHelper vg = parent.getVideoGameHelper();
		
        JComboBox<String> cb = new JComboBox<String>(CARD_NAMES);
        cb.setEditable(false);
        cb.addItemListener(evt -> {
        	CardLayout cl = (CardLayout)(statsPanel.getLayout());
            cl.show(statsPanel, (String)evt.getItem());
        });
		
		statsPanel = new JPanel(new CardLayout());
		statsPanel.add(createPanel(vg.getCommandMap()).getPanel(), COMMANDS);
		statsPanel.add(createPanel(vg.getCommandSourceMap()).getPanel(), COMMAND_SOURCES);
		statsPanel.add(createPanel(vg.getPluginMap()).getPanel(), PLUGINS);
		statsPanel.add(createPanel(vg.getPluginSourceMap()).getPanel(), PLUGIN_SOURCES);
		statsPanel.add(createPanel(vg.getSceneComponents()).getPanel(), SCENE_COMPONENTS);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		cb.setAlignmentX(Component.LEFT_ALIGNMENT);
		statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(cb);
		panel.add(statsPanel);
		
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
