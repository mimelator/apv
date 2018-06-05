package com.arranger.apv.util.fb;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.model.SongsModel;
import com.arranger.apv.util.FileHelper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper extends APVPlugin {

	private static final Logger logger = Logger.getLogger(FirebaseHelper.class.getName());

	private FirebaseDatabase database;
	
	public FirebaseHelper(Main parent) {
		super(parent);
		
		try {
			FileHelper fh = new FileHelper(parent);
			String fullPath = fh.getFullPath("../../admin.json");
			
			FileInputStream serviceAccount = new FileInputStream(fullPath);
			FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			    .setDatabaseUrl("https://alpha-one-dae37.firebaseio.com")
			    .build();
			FirebaseApp.initializeApp(options);
			database = FirebaseDatabase.getInstance();
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
		
		parent.getSetupEvent().register(() -> {
			parent.getSongStartEvent().register(() -> {
				updateSong(parent.getSetListPlayer().getCurrentSongTitle());
			});
			
			parent.getSetPackStartEvent().register(() -> {
				updateSetPack(parent.getSetPackModel().getSetPackName());
			});
		});
	}
	
	protected void updateSong(String title) {
		DatabaseReference ref = database.getReference("liveStream/song");
		ref.setValueAsync(title);
		updateSongQueue();
	}
	
	protected void updateSetPack(String title) {
		DatabaseReference ref = database.getReference("liveStream/setpack");
		ref.setValueAsync(title);
		updateSongQueue();
		updateSetPacks();
	}

	protected void updateSongQueue() {
		SongsModel songsModel = parent.getSongsModel();
		List<File> songs = songsModel.getSongs();
		int index = songsModel.getIndex();
		String result = songs.subList(index, songs.size()).stream().map(f -> f.getName()).collect(Collectors.joining(", "));
		DatabaseReference ref = database.getReference("liveStream/queue");
		ref.setValueAsync(result);
	}
	
	protected void updateSetPacks() {
		String setpacks = parent.getSetPackModel().getSetPackList().stream().map(sp -> new File(sp).getName()).collect(Collectors.joining(", "));
		DatabaseReference ref = database.getReference("liveStream/setpacks");
		ref.setValueAsync(setpacks);
	}
}
