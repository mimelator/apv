package com.arranger.apv.agent;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.db.entity.DJEntity;
import com.arranger.apv.db.entity.SetpackEntity;
import com.arranger.apv.model.SongsModel;
import com.arranger.apv.util.FileHelper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAgent extends BaseAgent {
	
	private static final Logger logger = Logger.getLogger(FirebaseAgent.class.getName());
	private static final String COMPLETED_MESSAGE = "#completed";
	private static final String ERROR_MESSAGE = "#error: ";
	
	private FirebaseDatabase database;

	public FirebaseAgent(Main parent) {
		super(parent);
		
		if (!parent.isFirebaseEnabled()) {
			return; //all done :)
		}
		
		logger.info("Starting Firebase support");
		boolean needsInit = FirebaseApp.getApps().isEmpty();
		if (needsInit) {
			try {
				FileHelper fh = new FileHelper(parent);
				String fullPath = fh.getFullPath("../../admin.json");
				
				FileInputStream serviceAccount = new FileInputStream(fullPath);
				FirebaseOptions options = new FirebaseOptions.Builder()
				    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				    .setDatabaseUrl(parent.getFirebaseDBEndpoint())
				    .build();
				FirebaseApp.initializeApp(options);
			} catch (Throwable t) {
				logger.log(Level.SEVERE, t.getMessage(), t);
			}
		}
		
		database = FirebaseDatabase.getInstance();
		
		//Register for changes to the song or setpack
		parent.getSetupEvent().register(() -> {
			parent.getSongStartEvent().register(() -> {
				updateSong(parent.getSetListPlayer().getCurrentSongTitle());
			});
			
			parent.getSetPackStartEvent().register(() -> {
				updateSetPack(parent.getSetPackModel().getSetPackName());
			});
		});
		
		//Register for incoming commands
		parent.getSetupEvent().register(() -> {
			FirebaseDatabase database = getDatabase();
			DatabaseReference ref = database.getReference("liveStream/command");
			ref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String value = dataSnapshot.getValue(String.class);
					try {
						if (value != null && !value.startsWith("#")) {
							parent.getStartupCommandRunner().runCommands(Arrays.asList(new String[]{value}));
							ref.setValueAsync(COMPLETED_MESSAGE);
						}
					} catch (Throwable t) {
						t.printStackTrace();
						ref.setValueAsync(ERROR_MESSAGE + t.getMessage());
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		});
	}

	public FirebaseDatabase getDatabase() {
		return database;
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
		updateDJ();
	}
	
	protected void updateDJ() {
		SetpackEntity setpackEntity = parent.getSetPackModel().getSetpackEntity();
		if (setpackEntity != null) {
			DJEntity dj = parent.getDBSupport().getDJforSetpack(setpackEntity);
			if (dj != null) {
				DatabaseReference ref = database.getReference("liveStream/dj");
				ref.setValueAsync(dj.getName());
			}
		}
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
