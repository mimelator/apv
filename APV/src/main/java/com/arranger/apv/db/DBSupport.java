package com.arranger.apv.db;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.db.entity.ColorEntity;
import com.arranger.apv.db.entity.DJEntity;
import com.arranger.apv.db.entity.ImageEntity;
import com.arranger.apv.db.entity.MessageEntity;
import com.arranger.apv.db.entity.SetpackEntity;
import com.arranger.apv.db.entity.SongEntity;
import com.arranger.apv.model.ColorsModel;
import com.arranger.apv.model.Creator;
import com.arranger.apv.model.EmojisModel;
import com.arranger.apv.model.IconsModel;
import com.arranger.apv.model.SongsModel;
import com.arranger.apv.util.FileHelper;
import com.mongodb.MongoClient;

public class DBSupport extends APVPlugin {

	private static final Logger logger = Logger.getLogger(DBSupport.class.getName());
	
	private static final String ENTITY_PACKAGE = ColorEntity.class.getPackage().getName(); 
	private Morphia morphia;

	private Datastore datastore;
	private Boolean DB_ENABLED;
	
	public DBSupport(Main parent) {
		super(parent);
		morphia  = new Morphia();
		morphia.mapPackage(ENTITY_PACKAGE);
	}

	public SetpackEntity findSetpackEntityByName(String name) {
		if (!isEnabled()) {
			return null;
		}
		
		Datastore datastore = getDatastore();
		
		Query<SetpackEntity> query = datastore.createQuery(SetpackEntity.class);
		query.and(query.criteria("name").equal(name));
		
		List<SetpackEntity> results = query.asList();
		return results.isEmpty() ? null : results.get(0);	
	}
	
	public DJEntity getDJforSetpack(SetpackEntity setpackEntity) {
		if (!isEnabled()) {
			return null;
		}
		
		Datastore datastore = getDatastore();
		
		Query<DJEntity> query = datastore.createQuery(DJEntity.class);
		query.and(query.criteria("setpack").in(Collections.singletonList(setpackEntity)));
		
		List<DJEntity> results = query.asList();
		return results.isEmpty() ? null : results.get(0);	
	}
	
	public void dbRefreshSetPackConfiguration() {
		if (!isEnabled()) {
			throw new IllegalStateException("Can't referesh db setpack folder without setting apv.mongoHostPort");
		}
		
		FileHelper fileHelper = new FileHelper(parent);
		
		Datastore datastore = getDatastore();
		List<SetpackEntity> results = datastore.createQuery(SetpackEntity.class).asList();
		results.forEach(setPack -> {
			System.out.println("Refreshing setPack: " + setPack.getName());
			
			loadSetPackIntoModels(setPack);
			try {
				final Creator creator = new Creator(parent);
				creator.createSetPackApplication(fileHelper.getSetPacksFolder(), setPack.getName(), 
						(isDemoActive, pd) -> {
							creator.updateIconsForDemo(isDemoActive, pd);
							creator.setSongsRelativeDir(pd);
						});
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		});
		
	}
	
	public void dbCreateSetPackFolders() {
		if (!isEnabled()) {
			throw new IllegalStateException("Can't create db setpack folder without setting apv.mongoHostPort");
		}
		
		FileHelper fileHelper = new FileHelper(parent);
		
		//find all of the SetPacks that don't have a folder
		Datastore datastore = getDatastore();
		Query<SetpackEntity> query = datastore.createQuery(SetpackEntity.class);
		query.or(
				query.criteria("folder").doesNotExist(),
				query.criteria("folder").equal(null),
				query.criteria("folder").equal("")
				);
		List<SetpackEntity> results = query.asList();
		
		results.forEach(setPack -> {
			System.out.println("Creating setPack for: " + setPack.getName());
			
			SongsModel songsModel = loadSetPackIntoModels(setPack);
			
			try {
				final Creator creator = new Creator(parent);
				creator.createSetPack(fileHelper.getSetPacksFolder(), setPack.getName(), 
						(isDemoActive, pd) -> {
							creator.updateIconsForDemo(isDemoActive, pd);
							creator.setSongsRelativeDir(pd);
						},
						
						(pd) -> {
							try {
								creator.createIconFilesForSetPack(pd);
								creator.createSongFilesForSetPack(pd, false, songsModel.getSongs());
							} catch (IOException e) {
								logger.log(Level.SEVERE, e.getMessage(), e);
							}
						});
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			setPack.setFolder(setPack.getName());
			datastore.save(setPack);
		});
	}

	protected SongsModel loadSetPackIntoModels(SetpackEntity setPack) {
		List<ColorEntity> colors = setPack.getColor();
		List<ImageEntity> images = setPack.getImage();
		List<MessageEntity> messages = setPack.getMessage();
		List<SongEntity> songs = setPack.getSong();
		
		ColorsModel colorsModel = parent.getColorsModel();
		IconsModel iconsModel = parent.getIconsModel();
		EmojisModel emojisModel = parent.getEmojisModel();
		SongsModel songsModel = parent.getSongsModel();
		
		colorsModel.loadFromEntities(colors);
		iconsModel.loadFromEntities(images);
		emojisModel.loadFromEntities(messages);
		songsModel.loadFromEntities(songs);
		return songsModel;
	}

	protected Datastore getDatastore() {
		if (!isEnabled()) {
			return null;
		}
		if (datastore == null) {
			String dbName = parent.getConfigValueForFlag(Main.FLAGS.MONGO_DB_NAME);
			String serverHostPort = parent.getConfigValueForFlag(Main.FLAGS.MONGO_HOST_PORT);
			datastore = morphia.createDatastore(new MongoClient(serverHostPort), dbName);
		}
		return datastore;
	}
	
	protected boolean isEnabled() {
		if (DB_ENABLED == null) {
			String serverHostPort = parent.getConfigValueForFlag(Main.FLAGS.MONGO_HOST_PORT);
			boolean disabled = (serverHostPort == null || serverHostPort.isEmpty());
			DB_ENABLED = new Boolean(!disabled); 
		}
		return DB_ENABLED;
	}
}
