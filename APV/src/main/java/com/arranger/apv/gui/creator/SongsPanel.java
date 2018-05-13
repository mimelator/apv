package com.arranger.apv.gui.creator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.util.APVSetList;
import com.arranger.apv.util.FileHelper;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class SongsPanel extends SetPackPanel {
	
	private static final String SONGS_DIR = "songs";
	private FileHelper fileHelper;
	private JCheckBox preserveOrderCheckBox;
	private JList<SongModel> songList;
	private DefaultListModel<SongModel> modelList = new DefaultListModel<SongModel>();
	
	public SongsPanel(Main parent) {
		super(parent, PANELS.SONGS);
		fileHelper = new FileHelper(parent);
		
		songList = new JList<SongModel>(modelList);

		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		JButton randomizeButton = new JButton("Randomize");
		
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
		
		randomizeButton.addActionListener(evt -> randomize());
		
		//see if there is a current song list and add it to the modelList
		APVSetList setList = parent.getSetList();
		if (setList != null) {
			setList.getSetList().forEach(songPath -> {
				modelList.addElement(new SongModel(songPath.toFile()));
			});
		}
		
		removeButton.addActionListener(evt -> {
		    songList.getSelectedValuesList().forEach(sm -> modelList.removeElement(sm));
		});
		
		JScrollPane jScrollPane = new JScrollPane(songList);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(jScrollPane);
		
		preserveOrderCheckBox = new JCheckBox("Preserve Order", true);
		add(preserveOrderCheckBox);
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(addButton);
		btnPanel.add(removeButton);
		btnPanel.add(randomizeButton);
		add(btnPanel);
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) throws IOException {
		boolean numberFiles = preserveOrderCheckBox.isSelected();
		int index = 1;
		
		//copy all of the items in the song list to the parentDirectory
		Path songFolder = getSongsDirectoryPath(parentDirectory);
		Files.createDirectories(songFolder);
		for (Enumeration<SongModel> elements = modelList.elements(); elements.hasMoreElements();) {
			SongModel songModel = elements.nextElement();
			Path srcPath = songModel.songFile.toPath();
			String name = songModel.songFile.getName();
			
			if (numberFiles) {
				name = String.format("%03d_%s", index, name);
				index++;
			}
			
			Path destPath = songFolder.resolve(name);
			Files.copy(srcPath, destPath);
		}
	}

	public void ffwd() {
		int index = songList.getSelectedIndex() + 1;
		if (index > songList.getModel().getSize() - 1) {
			index = 0;
		}
		songList.setSelectedIndex(index);
		updateForDemo(true, null);
	}
	
	public void prev() {
		int index = songList.getSelectedIndex() - 1;
		if (index < 0) {
			index = songList.getModel().getSize() - 1;
		}
		songList.setSelectedIndex(index);
		updateForDemo(true, null);
	}
	
	public void randomize() {
		List<SongModel> list = new ArrayList<SongModel>();
		IntStream.range(0,  modelList.getSize()).forEach(i -> {
			list.add(modelList.get(i));
		});
		
		Collections.shuffle(list);
		DefaultListModel<SongModel> ml = new DefaultListModel<SongModel>();
		list.forEach(sm -> ml.addElement(sm));
		songList.setModel(ml);
		modelList = ml;
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
				//Path songFolder = getSongsDirectoryPath(parentDirectory);
				Path relativeSongsDir = parentDirectory.getFileName().resolve(SONGS_DIR);
				parent.getSetList().setRelativeConfigDirectory(relativeSongsDir.toString());
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