package com.arranger.apv.db.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

@Entity("setpacks")
public class SetpackEntity {
	
	@Id
	private ObjectId id;
	@Property 
    private String name;
	@Property 
    private String folder;
	
	@Reference(idOnly = true) 
	private List<SongEntity> song;
	@Reference(idOnly = true)
	private List<ColorEntity> color;
	@Reference(idOnly = true)
	private List<ImageEntity> image;
	@Reference(idOnly = true)
	private List<MessageEntity> message;

	
	public SetpackEntity() {
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List<SongEntity> getSong() {
		return song;
	}

	public void setSong(List<SongEntity> song) {
		this.song = song;
	}

	public List<ColorEntity> getColor() {
		return color;
	}

	public void setColor(List<ColorEntity> color) {
		this.color = color;
	}

	public List<ImageEntity> getImage() {
		return image;
	}

	public void setImage(List<ImageEntity> image) {
		this.image = image;
	}

	public List<MessageEntity> getMessage() {
		return message;
	}

	public void setMessage(List<MessageEntity> message) {
		this.message = message;
	}
	
}
