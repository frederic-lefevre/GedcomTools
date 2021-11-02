package org.fl.gedcomtools.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomMultimediaObjectFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObject extends GedcomEntity {

	private static GedcomMultimediaObjectFiltre filtre;
	
	private List<Individual>   individuals ;
	private List<Family> 	   families ;
	private List<GedcomSource> sources ;
	
	private String mediaFileType;
	public String getMediaFileType() {
		return mediaFileType;
	}

	private String mediaFileName;
	
	public GedcomMultimediaObject(GedcomLine gLine, Logger gedcomLog) {
		super(gLine, gedcomLog);
		mediaFileType = null;
		mediaFileName = null;
		individuals = new ArrayList<>() ;
		families = new ArrayList<>() ;
		sources = new ArrayList<>() ;
	}

	public void setMediaFileType(String mediaFileType) {
		this.mediaFileType = mediaFileType;
	}

	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}
	
	public void addIndividual(Individual i) {
		individuals.add(i) ;
	}
	
	public void addFamily(Family f) {
		families.add(f) ;
	}
	
	public void addSource(GedcomSource s) {
		sources.add(s) ;
	}
	
	public List<Individual> getIndividuals() {
		return individuals;
	}

	public List<GedcomSource> getSources() {
		return sources;
	}

	public List<Family> getFamilies() {
		return families;
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomMultimediaObjectFiltre filtre) {
		GedcomMultimediaObject.filtre = filtre;
	}
}
