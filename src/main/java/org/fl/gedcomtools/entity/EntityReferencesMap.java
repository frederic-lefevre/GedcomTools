/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
