package com.arranger.apv.db;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity("colors")
public class ColorEntity {

	@Id
	private ObjectId id;
	@Property 
    private String name;
	@Property 
    private String colorData;
	
	public ColorEntity() {
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

	public String getColorData() {
		return colorData;
	}

	public void setColorData(String colorData) {
		this.colorData = colorData;
	}
}
