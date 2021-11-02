package org.fl.gedcomtools.filtre;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.entity.GedcomNote;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTag;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.gedcomtools.sosa.ArbreDeSosa;

import com.ibm.lge.fl.util.AdvancedProperties;

public class GedcomFiltreCondition {

	private static final String NOW = "now";
	private static final String datePatternParse  = "uuuu-MM-dd" ;
	private static final DateTimeFormatter dateTimeParser = DateTimeFormatter.ofPattern(datePatternParse,  Locale.FRANCE).withResolverStyle(ResolverStyle.STRICT) ;

	
	private final LocalDate anneeLimite ;
	private final boolean anonymisationEmail ;
	private final boolean keepOnlySourceTitle ;
	private final boolean keepOnlyOneLineNote ;
	private final boolean suppressSourceNote ;
	
	private final HashSet<GedcomTagValue> tagsToSuppress ;
	
	private final String[] titleStartsWithList ;
	private final String[] contentsToSuppress ;

	private ArbreDeSosa arbre ;
	
	public enum FiltreAction { NO_CHANGE, FILTER, SUPPRESS} ;
	
	public GedcomFiltreCondition(AdvancedProperties gedcomProp, Logger gedcomLog) {
		
		anonymisationEmail  = gedcomProp.getBoolean("gedcom.filtre.anonymisation.email",  true) ;		
		keepOnlySourceTitle = gedcomProp.getBoolean("gedcom.filtre.keepOnly.sourceTitle", true) ;
		keepOnlyOneLineNote = gedcomProp.getBoolean("gedcom.filtre.keepOnly.oneLineNote", true) ;
		contentsToSuppress  = gedcomProp.getArrayOfString("gedcom.filtre.suppressContents", ";") ;	

		anneeLimite = getAnneeLimite(gedcomProp, gedcomLog);
		
		String suppressSourceNoteWhenTitleStartsWith = gedcomProp.getProperty("gedcom.filtre.suppress.sourceNote.whenTitleStartsWith", "*") ;
		if (suppressSourceNoteWhenTitleStartsWith.equals("*")) {
			suppressSourceNote = true ;
			titleStartsWithList = null;
		} else {
			suppressSourceNote = false ;
			if (suppressSourceNoteWhenTitleStartsWith.length() < 1) {
				titleStartsWithList = null;
			} else {
				titleStartsWithList = suppressSourceNoteWhenTitleStartsWith.split(";") ;
			}
		}
		
		String[] tagsToSuppressStrings = gedcomProp.getArrayOfString("gedcom.filtre.suppressTags", ";") ;
		tagsToSuppress = new HashSet<GedcomTagValue>() ;
		if (tagsToSuppressStrings != null) {
			for (String tag :  tagsToSuppressStrings) {
				try {
					tagsToSuppress.add(GedcomTagValue.valueOf(tag)) ;
				} catch (Exception e) {
					gedcomLog.log(Level.SEVERE, "Exception parsing gedcom.filtre.suppressTags property for value " + tag, e);
				}
 			}
		}
	}
	
	public void setArbre(ArbreDeSosa arbre) {
		this.arbre = arbre;
	}

	public boolean keepOnlySourceTitle() {
		return keepOnlySourceTitle;
	}
	
	public boolean titleStartToSuppresSourceNote(String title) {
		
		if (title != null) {
			for (String start : titleStartsWithList) {
				if (title.startsWith(start)) return true ;
			}
		}
		return false;
	}
	
	private LocalDate getAnneeLimite(AdvancedProperties gedcomProp, Logger gedcomLog) {
		String dateDepartProp = "gedcom.filtre.anonymisation.dateDepart";
		String dateDepart = gedcomProp.getProperty(dateDepartProp);
		LocalDate localDateStart;
		if ((dateDepart == null) || (dateDepart.isEmpty()) || (dateDepart.equals(NOW))) {
			localDateStart = LocalDate.now();
		} else {
			try {
				localDateStart = LocalDate.parse(dateDepart, dateTimeParser);
			} catch (DateTimeParseException e) {
				gedcomLog.log(Level.SEVERE, "Exception parsing property " + dateDepartProp + "\nwith value=" + dateDepart, e);
				localDateStart = LocalDate.now();
			}
		}
		return localDateStart.minusYears(gedcomProp.getLong("gedcom.filtre.anonymisation.anneeLimite", 100));
	}
	
	private boolean isTagToSuppress(GedcomTag tag) {
		if ((tag != null) && (tag.getTagValue() != null)) { 
			return tagsToSuppress.contains(tag.getTagValue()) ;
		} else {
			return false ;
		}
	}

	private boolean isContentToSuppress(String content) {
		
		if (content != null) {
			for (String sContent : contentsToSuppress) {
				if (content.equals(sContent)) return true ;
			}
		}
		return false;
	}
	
	public boolean suppressSourceNote() {
		return suppressSourceNote ;
	}
	
	public boolean anonymiseEmail() {
		return anonymisationEmail;
	}
	
	// A family must be filtered if the marriage date is after a configured limit
	private boolean isToBefiltred(Family family) {
		
		LocalDate dateMariageMaximum = family.getDateMariageMaximum() ;
		return ((dateMariageMaximum != null) && (dateMariageMaximum.isAfter(anneeLimite))) ;
	}
	
	// an individual must be filtered if its birth date is after a configured limit
	private boolean isToBefiltred(Individual individual) {
		
		LocalDate dateNaissanceMaximum = individual.getDateNaissanceMaximum() ;
		return ((dateNaissanceMaximum != null) && (dateNaissanceMaximum.isAfter(anneeLimite))) ;
	}

	// Answer true if the gedcom line must be filtered
	// It must be filtered if the tag is part of a list of unwanted tags or 
	// if the content itself must be filtered, i.e if the contents is equal to a member of an unwanted content list
	public boolean isToBeFiltered(GedcomLine gLine) {
		return ((isTagToSuppress(gLine.getTag())) || (isContentToSuppress(gLine.getContent()))) ;
	}

	private boolean isToBeSuppressed(GedcomSource source) {
		
		for (Individual ind : source.getIndividuals()) {
			if (! isToBefiltred(ind)) return false ;
		}
		for (Family fam : source.getFamilies()) {
			if (! isToBefiltred(fam)) return false ;
		}
		return true;
	}
	
	private boolean isToBeSuppressed(GedcomMultimediaObject multimediaObject) {
		// all multimedia object are suppressed
		return true;
	}
	
	public FiltreAction getAction(Family family) {
		
		if (isToBefiltred(family)) {
			// il faut filtrer
			if (arbre.faitPartieDesSosas(family.getHusband()) && arbre.faitPartieDesSosas(family.getWife())) {
				return FiltreAction.FILTER ;
			} else {
				return FiltreAction.SUPPRESS ;
			}
		} else {
			return FiltreAction.NO_CHANGE ;
		}
	}
	
	public FiltreAction getAction(Individual individual) {
		
		if (isToBefiltred(individual)) {
			if (arbre.faitPartieDesSosas(individual)) {
				return FiltreAction.FILTER ;
			} else {
				return FiltreAction.SUPPRESS ;
			}
		} else {
			return FiltreAction.NO_CHANGE ;
		}
	}
	
	public FiltreAction getAction(GedcomNote note) {
		
		if (isOnlyLinkedToFilteredEntity(note)) {
			return FiltreAction.SUPPRESS ;
		} else if (keepOnlyOneLineNote) {
			return FiltreAction.FILTER ;
		} else {
			return FiltreAction.NO_CHANGE ;
		}
	}
	
	public FiltreAction getAction(GedcomSource source) {
		
		if (isToBeSuppressed(source)) {
			return FiltreAction.SUPPRESS ;
		} else {
			return FiltreAction.FILTER ;
		}
	}
	
	public FiltreAction getAction(GedcomMultimediaObject multimediaObject) {
		
		if (isToBeSuppressed(multimediaObject)) {
			return FiltreAction.SUPPRESS ;
		} else {
			return FiltreAction.FILTER ;
		}
	}
	
	private boolean isOnlyLinkedToFilteredEntity(GedcomNote note) {
		
		for (Individual ind : note.getIndividuals()) {
			if (! isToBefiltred(ind)) return false ;
		}
		for (Family fam : note.getFamilies()) {
			if (! isToBefiltred(fam)) return false ;
		}
		for (GedcomSource src : note.getSources()) {
			if ((! keepOnlySourceTitle()) && 
				(! suppressSourceNote())  && 
				(! isToBeSuppressed(src)) && 
				(! titleStartToSuppresSourceNote(src.getSourceTitle()))) return false ;
		}
		return true ;
	}
}
