package com.arranger.apv.archive;

import java.util.Collections;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.arranger.apv.db.entity.DJEntity;
import com.arranger.apv.db.entity.SetpackEntity;
import com.mongodb.MongoClient;


public class MongoDBConnectTest {

	public static void main(String[] args) throws Exception {
		Morphia morphia = new Morphia();

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("com.arranger.apv.db.entity");

		// create the Datastore connecting to the default port on the local host
		Datastore datastore = morphia.createDatastore(new MongoClient(), "test");
		test1(datastore);
	}

	protected static void test1(Datastore datastore) {
		List<SetpackEntity> asList = datastore.createQuery(SetpackEntity.class).asList();
		asList.forEach(spe -> {
			dump(spe);
			findDj(datastore, spe);
		});
	}

	protected static void findDj(Datastore datastore, SetpackEntity spe) {
		Query<DJEntity> query = datastore.createQuery(DJEntity.class);
		query.and(query.criteria("setpack").in(Collections.singletonList(spe)));

		List<DJEntity> results = query.asList();
		results.forEach(dj -> {
			System.out.println("   dj:" + dj.getName());
		});
	}
	
	protected static void dump(SetpackEntity spe) {
		System.out.println(spe.getName());
		System.out.println("   folder: " + spe.getFolder());
		System.out.println("   colorCount: " + spe.getColor().size());
		System.out.println("   imageCount: " + spe.getImage().size());
		System.out.println("   messageCount: " + spe.getMessage().size());
		System.out.println("   songCount: " + spe.getSong().size());
	}
}
