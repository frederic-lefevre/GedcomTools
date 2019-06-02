package org.fl.gedcomtools.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomFamilyFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.util.GedcomDateValue;

public class Family extends GedcomEntity {

	private static GedcomFamilyFiltre filtre;
	
	private LocalDate							      dateMariageMaximum ;
	private List<GedcomEntityReference<Individual>>   children ;
	private List<GedcomEntityReference<GedcomSource>> sources ;
	private List<GedcomEntityReference<GedcomNote>>   notes ;
	private GedcomEntityReference<Individual> 		  wifeRef ;
	private GedcomEntityReference<Individual> 		  husbandRef ;
	
	private GedcomDateValue dateMariage ;
	
	public Family(GedcomLine gLineParts, Logger gedcomLog) {
		
		super(gLineParts, gedcomLog);
		dateMariage 	   = null ;
		dateMariageMaximum = null ;
		wifeRef			   = null ;
		husbandRef		   = null ;
		
		children = new ArrayList<GedcomEntityReference<Individual>>() ;
		sources  = new ArrayList<GedcomEntityReference<GedcomSource>>() ;
		notes 	 = new ArrayList<GedcomEntityReference<GedcomNote>>() ;
	}

	public void addGedcomLine (GedcomLine gLine) {
		super.addGedcomLine(gLine) ;
	}
	
	public Individual getWife() {
		if (wifeRef != null) {
			return wifeRef.getEntity() ;
		} else {
			return null ;
		}
	}

	public Individual getHusband() {
		if (husbandRef != null) {
			return husbandRef.getEntity() ;
		} else {
			return null ;
		}
	}
	
	public void setWifeRef(GedcomEntityReference<Individual> wifeRef) {
		this.wifeRef = wifeRef;
	}

	public void setHusbandRef(GedcomEntityReference<Individual> husbandRef) {
		this.husbandRef = husbandRef;
	}

	public void addChild(GedcomEntityReference<Individual> child) {
		children.add(child) ;
	}
	
	public void setDateMariage(GedcomDateValue dm) {
		dateMariage = dm ;
		if (dateMariage.isValid()) {
			dateMariageMaximum = dateMariage.getMaxDate() ;
		} else {
			gLog.warning("Date de mariage invalide pour le mariage: \n" + getGedcomSource()) ;
		}
	}
	
	public List<GedcomEntityReference<GedcomSource>> getSources() {
		return sources;
	}
	
	public void addSource(GedcomEntityReference<GedcomSource> source) {
		sources.add(source) ;
	}
	
	public List<GedcomEntityReference<GedcomNote>> getNotes() {
		return notes;
	}
	
	public void addNote(GedcomEntityReference<GedcomNote> note) {
		notes.add(note) ;
	}
	
	public LocalDate getDateMariageMaximum() {
		return dateMariageMaximum;
	}

	public boolean hasMarriageDateInfo() {
		return ((dateMariage != null) && (dateMariage.isValid())) ;
	}

	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomFamilyFiltre filtre) {
		Family.filtre = filtre;
	}

}
