package org.fl.gedcomtools.entity;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomNoteFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomNote extends GedcomEntity {

	private static GedcomNoteFiltre filtre;
	private ArrayList<Individual> 	individuals ;
	private ArrayList<Family> 		families ;
	private ArrayList<GedcomSource> sources ;
	
	
	public GedcomNote(GedcomLine gLineParts, Logger gedcomLog) {
		
		super(gLineParts, gedcomLog);
		individuals = new ArrayList<Individual>() ;
		families = new ArrayList<Family>() ;
		sources = new ArrayList<GedcomSource>() ;
		
	}
	
	public void addGedcomLine (GedcomLine gLineParts) {
		super.addGedcomLine(gLineParts) ;
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

	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	public ArrayList<GedcomSource> getSources() {
		return sources;
	}

	public ArrayList<Family> getFamilies() {
		return families;
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomNoteFiltre filtre) {
		GedcomNote.filtre = filtre;
	}
}
