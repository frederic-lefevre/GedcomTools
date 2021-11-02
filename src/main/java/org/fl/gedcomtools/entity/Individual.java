package org.fl.gedcomtools.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomIndividualFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.util.AgeMoyen;
import org.fl.gedcomtools.util.GedcomDateValue;

public class Individual extends GedcomEntity {
	
	private static GedcomIndividualFiltre filtre;
	private LocalDate 		  						  			dateNaissanceMaximum ;
	private String 			  						  			individualName ;
	private List<GedcomEntityReference<Family>> 	  			familiesAsSpouse ;
	private List<GedcomEntityReference<Family>> 	  			familiesAsChild ;
	private List<GedcomEntityReference<GedcomSource>> 			sources ;
	private List<GedcomEntityReference<GedcomNote>>   			notes ;
	private Set<String>   						  	  			professions ;
	private List<GedcomEntityReference<GedcomMultimediaObject>> multimedias ;
	
	private int nbResidence ;
	private int nbProfessions ;
	
	private GedcomDateValue dateNaissance ;
	private GedcomDateValue dateDeces ;
	
	public Individual(GedcomLine gParts, Logger gedcomLog) {
		
		super(gParts, gedcomLog);
		dateNaissanceMaximum = null ;
		nbResidence 		 = 0 ;
		nbProfessions		 = 0 ;
		individualName 		 = null ;
		familiesAsChild  	 = new ArrayList<>() ;
		familiesAsSpouse 	 = new ArrayList<>() ;
		sources		   	 	 = new ArrayList<>() ;
		notes 		   	 	 = new ArrayList<>() ;
		professions 	   	 = new HashSet<>() ;
		multimedias 		 = new ArrayList<>() ;
	}
	
	public String getIndividualName() {
		return individualName;
	}
	
	public void setIndividualName(String name) {
		if (individualName == null) {
			// If it is not null, it is a secondary name
			individualName = name ;
		}
	}
	
	public void addDateNaissance(GedcomDateValue dn) {
		dateNaissance = dn ;
		if (dateNaissance.isValid()) {
			dateNaissanceMaximum = dateNaissance.getMaxDate() ;
			if (dateNaissanceMaximum == null) {
				gLog.warning("Absence de date de naissance pour l'individu: \n" + getGedcomSource()) ;
			}
		} else {
			gLog.warning("Date de naissance invalide pour l'individu: \n" + getGedcomSource()) ;
		}
	}
	
	public void addDateDeces(GedcomDateValue dd) {
		dateDeces = dd ;
		if (! dateDeces.isValid()) {
			gLog.warning("Date de décès invalide pour l'individu: \n" + getGedcomSource()) ;
		}
	}
	
	public void addFamilyAsChild(GedcomEntityReference<Family> ref) {
		familiesAsChild.add(ref) ;
	}
	
	public void addFamilyAsSpouse(GedcomEntityReference<Family> ref) {
		familiesAsSpouse.add(ref) ;
	}
	
	public Family getMainFamily() {

		if ((familiesAsChild == null) || (familiesAsChild.isEmpty())) {
			return null ;
		} else if  (familiesAsChild.size() == 1) {
			return familiesAsChild.get(0).getEntity() ;
		} else {
			gLog.warning("Famille multiple non supporté: " + individualName ) ;
			return familiesAsChild.get(0).getEntity() ;
		}
	}
	
	public void addSourceReference(GedcomEntityReference<GedcomSource> sourceRef) {
		sources.add(sourceRef) ;
	}
	
	public void addNoteReference(GedcomEntityReference<GedcomNote> noteRef) {
		notes.add(noteRef) ;
	}
	
	public List<GedcomEntityReference<GedcomSource>> getSources() {
		return sources;
	}

	public List<GedcomEntityReference<GedcomNote>> getNotes() {
		return notes;
	}
	
	public List<GedcomEntityReference<GedcomMultimediaObject>> getMultimedias() {
		return multimedias;
	}

	public void addMultimedia(GedcomEntityReference<GedcomMultimediaObject> multimedia) {
		multimedias.add(multimedia);
	}
	
	public void addProfession(String profession) {
		nbProfessions++ ;
		professions.add(profession) ;
	}
	
	public void addResidence() {
		nbResidence++ ;
	}
	
	public String printProfession() {
		
		StringBuilder res = new StringBuilder() ;
		for (String p : professions) {
			res.append(p).append("\n") ;
		}
		return res.toString() ;
	}
	
	public int getNbAscendants() {
		
		int nbAscendant = 0 ;
		
		Family fam = getMainFamily() ;

		if (fam != null) {
			Individual mere = fam.getWife() ;
			if (mere != null) {
				nbAscendant = nbAscendant + 1 + mere.getNbAscendants() ;
			}
			Individual pere = fam.getHusband() ;
			if (pere != null) {
				nbAscendant =  nbAscendant + 1 + pere.getNbAscendants() ;
			}
		}
		return nbAscendant ;
	}
	
	// Obtenir l'age en nombre de jours si les dates de naissances et de décès sont connues et exactes
	public long getAgeExactEnJours() {
		
		long age = 0 ;
		if ((dateNaissance != null) && (dateDeces != null)) {
			if (dateNaissance.isValid() && dateNaissance.isExact() && dateDeces.isValid() && dateDeces.isExact()) {
				Period dureeVie = Period.between(dateNaissance.getMinDate(), dateDeces.getMinDate()) ;
				age = dureeVie.getDays() ;
				age = java.time.temporal.ChronoUnit.DAYS.between(dateNaissance.getMinDate(), dateDeces.getMinDate()) ;
			}
		}
		return age ;
	}
	
	public LocalDate getDateNaissanceMaximum() {
		return dateNaissanceMaximum;
	}

	public AgeMoyen getAgeMoyenAscendants() {
		
		AgeMoyen ageMoyenMere = new AgeMoyen() ;
		AgeMoyen ageMoyenPere = new AgeMoyen() ;
		
		Family fam = getMainFamily() ;
		if (fam != null) {
			Individual mere = fam.getWife() ;
			if (mere != null) {
				ageMoyenMere.addAgeEnJour(mere.getAgeExactEnJours());
				ageMoyenMere.addAgeMoyen(mere.getAgeMoyenAscendants());
			}
			Individual pere = fam.getHusband() ;
			if (pere != null) {
				ageMoyenPere.addAgeEnJour(pere.getAgeExactEnJours());
				ageMoyenPere.addAgeMoyen(pere.getAgeMoyenAscendants());
			}			
		}
		return new AgeMoyen(ageMoyenMere, ageMoyenPere) ;
	}
	
	public int getNbResidence() {
		return nbResidence ;
	}
	
	public int getNbProfession() {
		return nbProfessions ;
	}
	
	public int getNbSources() {
		return sources.size() ;
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomIndividualFiltre filtre) {
		Individual.filtre = filtre;
	}
}
