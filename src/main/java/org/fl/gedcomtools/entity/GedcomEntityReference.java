package org.fl.gedcomtools.entity;

public class GedcomEntityReference <T extends GedcomEntity> {

	private String 	id ;
	private T 		entity ;
	
	public GedcomEntityReference(String i) {
		id 	   	   = i ;
		entity 	   = null ;
	}

	public GedcomEntityReference(String i, T e) {
		id 	       = i ;
		entity 	   = e ;
	}

	public String getId() {
		return id;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T e) {
		entity = e ;
	}
	
}
