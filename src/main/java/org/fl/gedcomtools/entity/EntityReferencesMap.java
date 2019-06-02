package org.fl.gedcomtools.entity;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EntityReferencesMap <T extends GedcomEntity> extends HashMap<String, GedcomEntityReference<T>>{

	private static final long serialVersionUID = 1L;

	public EntityReferencesMap() {
		super() ;
 	}

	public boolean containsReferenceToEntity(String id) {		
		return containsKey(id) ;
	}

	public boolean containsEntity(String id) {
		return (containsKey(id) && (get(id) != null)) ;
	}

	public void addNewEntity(T entity) {
		
		String id = entity.getId() ;
		if (! containsEntity(id)) {
			put(id, new GedcomEntityReference<T>(id, entity)) ;
		} else {
			GedcomEntityReference<T> entityRef = get(id) ;
			entityRef.setEntity(entity) ;
		}
	}
	
	public GedcomEntityReference<T> getOrCreateEntityReference(String id) {
		
		GedcomEntityReference<T> entityReference ;
		if (containsReferenceToEntity(id)) {
			entityReference = get(id) ;
		} else {
			entityReference = new GedcomEntityReference<T>(id) ;
			put(id, entityReference) ;
		}
		return entityReference ;
	}
	
	public T getEntity(String id) {
		T entity = null ;
		if (containsReferenceToEntity(id)) {
			GedcomEntityReference<T> entityReference = get(id) ;
			if (entityReference != null) {
				entity = entityReference.getEntity() ;
			}
		}
		return entity ;
	}
	
	public List<T> getEntities() {
		return values().stream().filter(s -> s != null).map(s -> s.getEntity()).collect(Collectors.toList()) ;
	}
	
	public long getNotNullEntityNumber() {
		return values().stream().filter(s -> s != null).count() ;
	}
}
