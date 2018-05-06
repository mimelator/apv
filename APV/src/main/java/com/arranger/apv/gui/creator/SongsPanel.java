package com.arranger.apv.gui.creator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.util.APVSetList;
import com.arranger.apv.util.FileHelper;

@SuppressWarnings("serial")
public class SongsPanel extends SetPackPanel {
	
	private static final String SONGS_DIR = "songs";
	private FileHelper fileHelper;
	private JList<SongModel> songList;
	private DefaultListModel<SongModel> modelList = new DefaultListModel<SongModel>();
	
	public SongsPanel(Main parent) {
		super(parent, PANELS.SONGS);
		fileHelper = new FileHelper(parent);
		
		songList = new JList<SongModel>(modelList);

		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		
		addButton.addActionListener(evt -> {
			JFileChooser fc = fileHelper.getJFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(true);
			fc.setFileFilter(new FileNameExtensionFilter("MP3s", "mp3"));
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				Arrays.asList(fc.getSelectedFiles()).forEach(f -> {
					modelList.addElement(new SongModel(f));
				});
			}
		});
		
		removeButton.addActionListener(evt -> {
			songList.remove(songList.getSelectedIndex());
		});
		
		JScrollPane jScrollPane = new JScrollPane(songList);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(jScrollPane);
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(addButton);
		btnPanel.add(removeButton);
		add(btnPanel);
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) throws IOException {
		//copy all of the items in the song list to the parentDirectory
		Path songFolder = getSongsDirectoryPath(parentDirectory);
		Files.createDirectories(songFolder);
		for (Enumeration<SongModel> elements = modelList.elements(); elements.hasMoreElements();) {
			SongModel songModel = elements.nextElement();
			Path srcPath = songModel.songFile.toPath();
			Path destPath = songFolder.resolve(songModel.songFile.getName());
			Files.copy(srcPath, destPath);
		}
	}

	protected Path getSongsDirectoryPath(Path parentDirectory) {
		Path songFolder = parentDirectory.resolve(SONGS_DIR);
		return songFolder;
	}
	
	public void updateForDemo(boolean isDemoActive, Path parentDirectory) {
		if (isDemoActive) {
			int index = songList.getSelectedIndex();
			if (index < 0) {
				index = 0;
			}
			
			List<File> filesToPlay = new ArrayList<File>();
			for (Enumeration<SongModel> elements = modelList.elements(); elements.hasMoreElements();) {
				filesToPlay.add(elements.nextElement().songFile);
			}
			if (parentDirectory != null) {
				Path songFolder = getSongsDirectoryPath(parentDirectory);
				parent.getSetList().setConfigDirectory(songFolder.toString());
			}
			parent.play(filesToPlay, index);
		} else {
			APVSetList setList = parent.getSetList();
			if (setList != null) {
				setList.stop();
			}
		}
	}

	private class SongModel  {
		private File songFile;

		private SongModel(File songFile) {
			this.songFile = songFile;
		}
		
		public String toString() {
			return songFile.getName();
		}
	}
}