package com.arranger.apv.gui.creator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

import com.arranger.apv.Main;
import com.arranger.apv.model.Creator;
import com.arranger.apv.model.SongsModel;
import com.arranger.apv.util.FileHelper;

@SuppressWarnings("serial")
public class SongsPanel extends SetPackPanel {
	
	private FileHelper fileHelper;
	private JCheckBox preserveOrderCheckBox;
	
	private SongsModel songsModel;
	
	private JList<SongModel> songList;
	private DefaultListModel<SongModel> modelList;
	
	public SongsPanel(Main parent) {
		super(parent, PANELS.SONGS);
		fileHelper = new FileHelper(parent);
		songsModel = parent.getSongsModel();
		
		songList = new JList<SongModel>();
		songList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = songList.locationToIndex(e.getPoint());
					play(index);
				}
			}
		});
		syncModels(true);

		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		JButton randomizeButton = new JButton("Randomize");
		
		addButton.addActionListener(evt -> {
			JFileChooser fc = fileHelper.getJFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				List<Path> allMp3sFromDir = new FileHelper(parent).getAllMp3sFromDir(fc.getSelectedFile().toPath());
				allMp3sFromDir.forEach(path -> {
					modelList.addElement(new SongModel(path.toFile()));
				});
				syncModels(false);
			}
		});
		
		randomizeButton.addActionListener(evt -> randomize());
		removeButton.addActionListener(evt -> {
		    songList.getSelectedValuesList().forEach(sm -> modelList.removeElement(sm));
		    syncModels(false);
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
		List<File> songList = new ArrayList<File>();
		for (Enumeration<SongModel> elements = modelList.elements(); elements.hasMoreElements();) {
			songList.add(elements.nextElement().songFile);
		}
		
		new Creator(parent).createSongFilesForSetPack(parentDirectory, preserveOrderCheckBox.isSelected(), songList);
	}

	public void ffwd() {
		int index = songList.getSelectedIndex() + 1;
		if (index > songList.getModel().getSize() - 1) {
			index = 0;
		}
		play(index);
	}
	
	public void prev() {
		int index = songList.getSelectedIndex() - 1;
		if (index < 0) {
			index = songList.getModel().getSize() - 1;
		}
		play(index);
	}

	protected void play(int index) {
		songList.setSelectedIndex(index);
		updateForDemo(true, null);
	}
	
	public void randomize() {
		songsModel.randomize();
		syncModels(true);
	}

	/**
	 * Updates our modelList with the Songs from the Song Model
	 * if fetch is true, get the list, if not push our current list
	 */
	private void syncModels(boolean fetch) {
		if (fetch) {
			DefaultListModel<SongModel> ml = new DefaultListModel<SongModel>();
			songsModel.getSongs().forEach(f -> ml.addElement(new SongModel(f)));
			songList.setModel(ml);
			modelList = ml;
		} else {
			List<File> songs = new ArrayList<File>();
			IntStream.range(0,  modelList.getSize()).forEach(i -> {
				songs.add(modelList.get(i).songFile);
			});
			songsModel.setSongs(songs);
		}
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
				new Creator(parent).setSongsRelativeDir(parentDirectory);
			}
			
			songsModel.setSongs(filesToPlay);
			songsModel.playSong(index);
		} else {
			songsModel.stop();
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