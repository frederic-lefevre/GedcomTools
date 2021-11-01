package org.fl.gedcomtools.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomNoteFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomNote extends GedcomEntity {

	private static GedcomNoteFiltre filtre;
	private List<Individual>   individuals ;
	private List<Family> 	   families ;
	private List<GedcomSource> sources ;
	
	
	public GedcomNote(GedcomLine gLineParts, Logger gedcomLog) {
		
		super(gLineParts, gedcomLog);
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
	
	public static void setFiltre(GedcomNoteFiltre filtre) {
		GedcomNote.filtre = filtre;
	}
}
