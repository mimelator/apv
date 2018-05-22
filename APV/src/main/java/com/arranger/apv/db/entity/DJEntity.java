package com.arranger.apv.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

@Entity("djs")
public class DJEntity {

	@Id
	private ObjectId id;
	@Property 
    private String name;
	@Reference 
	private SetpackEntity setpack;
	
	public DJEntity() {
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

	public SetpackEntity getSetpack() {
		return setpack;
	}

	public void setSetpack(SetpackEntity setpack) {
		this.setpack = setpack;
	}

	
}
