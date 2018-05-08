package com.arranger.apv.gui.creator;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.arranger.apv.Main;
import com.arranger.apv.gui.APVFrame;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;

public class SetPackCreator extends APVFrame {

	public static final String SET_PACK_HOME_KEY = "apv.setPack.home";
	
	private static final Logger logger = Logger.getLogger(SetPackCreator.class.getName());
	
	private static final int FRAME_HEIGHT = 650;
	private static final int FRAME_WIDTH = 600;
	
	private FileHelper fileHelper;
	private List<SetPackPanel> panels = new ArrayList<SetPackPanel>();
	private JButton demoButton;
	private boolean demoMode = false;
	
	
	public SetPackCreator(Main parent) {
		super(parent);
		fileHelper = new FileHelper(parent);
		
		panels.add(new IconsPanel(parent));
		panels.add(new ColorsPanel(parent));
		panels.add(new EmojisPanel(parent));
		panels.add(new SongsPanel(parent));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		panels.forEach(pnl -> {
			tabbedPane.addTab(pnl.getPanel().getTitle(), pnl);
		});
		
		JPanel panel = new JPanel();
		panel.add(tabbedPane);
		
		JPanel btnPanel = new JPanel();
		demoButton = new JButton("Demo");
		demoButton.addActionListener(evt -> {
			toggleDemoMode();
		});
		btnPanel.add(demoButton);
		JButton createButton = new JButton("Create Set Pack");
		createButton.addActionListener(evt -> {
			createSetPack();
		});
		btnPanel.add(createButton);
		panel.add(btnPanel);
		
		createFrame(getName(), FRAME_WIDTH, FRAME_HEIGHT, panel, () -> {});
	}
	
	private void toggleDemoMode() {
		demoMode = !demoMode;
		demoButton.setOpaque(true);
		demoButton.setBackground(demoMode ? Color.RED : null);
		
		panels.forEach(pnl -> {
			pnl.updateForDemo(demoMode, null);
		});
	}
	
	private void createSetPack() {
		//get the name
		String setPackName = JOptionPane.showInputDialog("Please enter the set pack name");
		if (setPackName == null) {
			return;
		}
		
		//get the file chooser
		JFileChooser fc = fileHelper.getJFileChooser();
		fc.setDialogTitle("Choose folder for " + setPackName);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				createSetPack(fc.getSelectedFile(), setPackName);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void createSetPack(File parentDirectory, String setPackName) throws IOException {
		Path parentDirectoryPath = parentDirectory.toPath();
		parentDirectoryPath = parentDirectoryPath.resolve(setPackName);
		Files.createDirectories(parentDirectoryPath);
		final Path parentFolderPath = parentDirectoryPath;
		
		panels.forEach(pnl -> {
			pnl.updateForDemo(true, parentFolderPath);
		});
		
		String referenceText = parent.getConfigurator().generateCurrentConfig();
		Path referencePath = parentDirectoryPath.resolve(Configurator.APPLICATION_CONF);
		
		String instructions = String.format("#java -Dconfig.file=%s/application.conf -jar apv.jar", setPackName) + System.lineSeparator();
		String setPackHome = String.format("%s = %s", SET_PACK_HOME_KEY, parentDirectory.getAbsolutePath()) + System.lineSeparator();

		String result = String.format("%s %s %s", instructions, setPackHome, referenceText);
		Files.write(referencePath, result.getBytes());
		
		panels.forEach(pnl -> {
			try {
				pnl.createFilesForSetPack(parentFolderPath);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		});
	}
}
