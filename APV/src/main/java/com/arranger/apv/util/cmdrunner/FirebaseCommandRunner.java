package com.arranger.apv.util.cmdrunner;

import java.util.Arrays;

import com.arranger.apv.Main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseCommandRunner extends StartupCommandRunner {

	public FirebaseCommandRunner(Main parent) {
		super(parent);
		parent.getSetupEvent().register(() -> {
			FirebaseDatabase database = parent.getFirebaseHelper().getDatabase();
			DatabaseReference ref = database.getReference("liveStream/command");
			ref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String value = dataSnapshot.getValue(String.class);
					runCommands(Arrays.asList(new String[]{value}));
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		});
	}
}
