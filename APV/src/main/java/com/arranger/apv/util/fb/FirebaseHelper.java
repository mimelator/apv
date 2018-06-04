package com.arranger.apv.util.fb;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
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
	}
	
	protected void updateSetPack(String title) {
		DatabaseReference ref = database.getReference("liveStream/setpack");
		ref.setValueAsync(title);
	}

}
