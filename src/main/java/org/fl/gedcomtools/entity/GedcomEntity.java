package org.fl.gedcomtools.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomEntityFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomEntity {

	private static GedcomEntityFiltre filtre ;
	
	protected List<GedcomLine> gLines ; 
	protected Logger 		   gLog ;
	private   String 		   id;
	
	public GedcomEntity(GedcomLine gLine, Logger gedcomLog) {
		
		gLog = gedcomLog ;
		if (gLine.getLevel() != 0) {
			gedcomLog.severe("Erreur dans la création d'une entité Gedcom, la première ligne n'est pas de niveau 0: " + gLine.getOriginalLine()) ;
		} else {	
			gLines = new ArrayList<>();
			gLines.add(gLine) ;
			id = gLine.getId() ;
		}	
	}

	public void addGedcomLine (GedcomLine gLine) {	
		gLines.add(gLine) ;
	}
	
	public StringBuilder getGedcomSource() {
		StringBuilder gedcomSource = new StringBuilder() ;
		for (GedcomLine gLine : gLines) {
			gedcomSource.append(gLine.getOriginalLine()) ;
		}
		return gedcomSource;
	}

	public String getId() {
		return id;
	}
	
	public List<GedcomLine> getGedcomLines() {
		return gLines ;
	}

	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomEntityFiltre filtre) {
		GedcomEntity.filtre = filtre;
	}
}
