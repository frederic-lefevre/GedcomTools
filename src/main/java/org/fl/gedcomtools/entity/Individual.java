/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.fl.gedcomtools.filtre.GedcomIndividualFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.util.AgeMoyen;
import org.fl.gedcomtools.util.GedcomDateValue;

public class Individual extends GedcomEntity {
	
	private static GedcomIndividualFiltre filtre;
	private LocalDate dateNaissanceMaximum;
	private String individualName;
	private final List<GedcomEntityReference<Family>> familiesAsSpouse;
	private final List<GedcomEntityReference<Family>> familiesAsChild;
	private final List<GedcomEntityReference<GedcomSource>> sources;
	private final List<GedcomEntityReference<GedcomNote>> notes;
	private final Set<String> professions;
	private final List<IndividualProfession> allProfessionOccurences;
	private final List<GedcomEntityReference<GedcomMultimediaObject>> multimedias;
	private final List<Residence> residences;

	private GedcomDateValue dateNaissance;
	private GedcomDateValue dateDeces;

	public Individual(GedcomLine gParts) {

		super(gParts);
		dateNaissanceMaximum = null;
		individualName = null;
		familiesAsChild = new ArrayList<>();
		familiesAsSpouse = new ArrayList<>();
		sources = new ArrayList<>();
		notes = new ArrayList<>();
		professions = new HashSet<>();
		allProfessionOccurences = new ArrayList<>();
		multimedias = new ArrayList<>();
		residences =  new ArrayList<>();
	}
	
	public String getIndividualName() {
		return individualName;
	}
	
	public void setIndividualName(String name) {
		if (individualName == null) {
			// If it is not null, it is a secondary name
			individualName = name;
		}
	}
	
	public void addDateNaissance(GedcomDateValue dn) {
		dateNaissance = dn ;
		if (dateNaissance.isValid()) {
			dateNaissanceMaximum = dateNaissance.getMaxDate();
			if (dateNaissanceMaximum == null) {
				gLog.warning("Absence de date de naissance pour l'individu: \n" + getGedcomSource());
			}
		} else {
			gLog.warning("Date de naissance invalide pour l'individu: \n" + getGedcomSource());
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
		sources.add(sourceRef);
	}
	
	public void addNoteReference(GedcomEntityReference<GedcomNote> noteRef) {
		notes.add(noteRef);
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
		professions.add(profession);
		allProfessionOccurences.add(new IndividualProfession(profession));
	}
	
	public void addResidence() {
		residences.add(new Residence());
	}
	
	public void addLastResidenceDate(GedcomDateValue dateResidence) {
		residences.getLast().setDate(dateResidence);
	}
	
	public void addLastResidencePlace(String place) {
		residences.getLast().setPlace(place);
	}
	
	public void addLastProfessionDate(GedcomDateValue dateProfession) {
		allProfessionOccurences.getLast().setDate(dateProfession);
	}
	
	public String printProfession() {

		StringBuilder res = new StringBuilder();
		for (String p : professions) {
			res.append(p).append("\n");
		}
		return res.toString();
	}

	public int getNbAscendants() {

		int nbAscendant = 0;

		Family fam = getMainFamily();

		if (fam != null) {
			Individual mere = fam.getWife();
			if (mere != null) {
				nbAscendant = nbAscendant + 1 + mere.getNbAscendants();
			}
			Individual pere = fam.getHusband();
			if (pere != null) {
				nbAscendant = nbAscendant + 1 + pere.getNbAscendants();
			}
		}
		return nbAscendant;
	}
	
	// Obtenir l'age en nombre de jours si les dates de naissances et de décès sont connues et exactes
	public long getAgeExactEnJours() {
		
		long age = 0 ;
		if ((dateNaissance != null) && (dateDeces != null)) {
			if (dateNaissance.isValid() && dateNaissance.isExact() && dateDeces.isValid() && dateDeces.isExact()) {
				Period dureeVie = Period.between(dateNaissance.getMinDate(), dateDeces.getMinDate()) ;
				age = dureeVie.getDays();
				age = java.time.temporal.ChronoUnit.DAYS.between(dateNaissance.getMinDate(), dateDeces.getMinDate());
			}
		}
		return age ;
	}
	
	public LocalDate getDateNaissanceMaximum() {
		return dateNaissanceMaximum;
	}

	public AgeMoyen getAgeMoyenAscendants() {

		AgeMoyen ageMoyenMere = new AgeMoyen();
		AgeMoyen ageMoyenPere = new AgeMoyen();

		Family fam = getMainFamily();
		if (fam != null) {
			Individual mere = fam.getWife();
			if (mere != null) {
				ageMoyenMere.addAgeEnJour(mere.getAgeExactEnJours());
				ageMoyenMere.addAgeMoyen(mere.getAgeMoyenAscendants());
			}
			Individual pere = fam.getHusband();
			if (pere != null) {
				ageMoyenPere.addAgeEnJour(pere.getAgeExactEnJours());
				ageMoyenPere.addAgeMoyen(pere.getAgeMoyenAscendants());
			}
		}
		return new AgeMoyen(ageMoyenMere, ageMoyenPere);
	}

	public int getNbResidence() {
		return residences.size();
	}

	public int getNbProfession() {
		return allProfessionOccurences.size();
	}

	public int getNbSources() {
		return sources.size();
	}

	public List<IndividualProfession> getAllProfessionOccurences() {
		return allProfessionOccurences;
	}

	public List<Residence> getResidences() {
		return residences;
	}

	public StringBuilder filtre() {
		return filtre.filtre(this);
	}
	
	public void checkIndividual() {
		
		StringBuilder messages = new StringBuilder();
		
		if (dateNaissance == null) {
			messages.append("La date de naissance de ").append(individualName).append("n'est pas définie\n");
		}
		
		checkDates(messages, residences.stream().map(Residence::getDate), "résidence");
		checkDates(messages, allProfessionOccurences.stream().map(IndividualProfession::getDate), "profession");
		if (! messages.isEmpty()) {
			gLog.warning(messages.toString());
		}
	}
	
	private void checkDates(StringBuilder messages, Stream<GedcomDateValue> dates, String eventName) {

		dates.filter(Objects::nonNull).forEach(date -> {
			if (((dateNaissance != null) && date.isStrictlyBefore(dateNaissance)) || ((dateDeces != null) && date.isStrictlyAfter(dateDeces))) {
				messages.append("Une date de ").append(eventName).append(" n'est pas correcte pour ").append(individualName).append("\n");
				messages.append("Date de minimum de l'évennement: ").append(date.getMinDate().toString()).append("\n");
				messages.append("Date de maximum de l'évennement: ").append(date.getMaxDate().toString()).append("\n");
				messages.append("Date de naissance maximum: ").append(dateNaissance.getMaxDate().toString()).append("\n");
				messages.append("Date de décès minimum: ").append(dateDeces.getMinDate().toString()).append("\n");
			}
		});

	}
	
	public static void setFiltre(GedcomIndividualFiltre filtre) {
		Individual.filtre = filtre;
	}
}
