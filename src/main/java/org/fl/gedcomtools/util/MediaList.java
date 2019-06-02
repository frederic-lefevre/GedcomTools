package org.fl.gedcomtools.util;

import java.util.Hashtable;

import org.fl.gedcomtools.entity.GedcomEntity;

public class MediaList {

	private Hashtable<String, GedcomEntity> listeMedia ;
	
	public MediaList() {
		listeMedia = new Hashtable<String, GedcomEntity>() ;
	}

	public void addMedia(String mediaFileName, GedcomEntity g) {
		listeMedia.put(mediaFileName, g) ;
	}
}
