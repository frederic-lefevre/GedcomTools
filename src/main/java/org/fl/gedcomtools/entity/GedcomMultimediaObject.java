package org.fl.gedcomtools.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObject extends GedcomEntity {

	private List<Individual>   individuals ;
	private List<Family> 	   families ;
	private List<GedcomSource> sources ;
	
	private String mediaFileType;
	private String mediaFileName;
	
	public GedcomMultimediaObject(GedcomLine gLine, Logger gedcomLog) {
		super(gLine, gedcomLog);
		mediaFileType = null;
		mediaFileName = null;
		individuals = new ArrayList<>() ;
		families = new ArrayList<>() ;
		sources = new ArrayList<>() ;
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
}
