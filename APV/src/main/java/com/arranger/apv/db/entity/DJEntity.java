package com.arranger.apv.db.entity;

import java.util.List;

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
	@Reference(idOnly = true)
	private List<SetpackEntity> setpack;
	
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

	public List<SetpackEntity> getSetpack() {
		return setpack;
	}

	public void setSetpack(List<SetpackEntity> setpack) {
		this.setpack = setpack;
	}
}
