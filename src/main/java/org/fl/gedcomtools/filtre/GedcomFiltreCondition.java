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

package org.fl.gedcomtools.filtre;

import java.time.LocalDate;
import java.util.Set;

import org.fl.gedcomtools.Config;
import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.entity.GedcomNote;
import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.entity.Individual;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTag;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.gedcomtools.sosa.ArbreDeSosa;

public class GedcomFiltreCondition {

	private final LocalDate anneeLimite;
	private final boolean suppressSourceNote;

	private final Set<GedcomTagValue> tagsToSuppress;

	private final String[] titleStartsWithList;
	private final String[] contentsToSuppress;

	private ArbreDeSosa arbre;

	public enum FiltreAction {
		NO_CHANGE, FILTER, SUPPRESS
	};

	public GedcomFiltreCondition() {

		contentsToSuppress = Config.getContetToSuppress();
		anneeLimite = Config.getAnneeLimite();
		tagsToSuppress = Config.getTagsToSuppress();
		
		String suppressSourceNoteWhenTitleStartsWith = Config.getSuppressSourceNoteWhenTitleStartsWith();
		if (suppressSourceNoteWhenTitleStartsWith.equals("*")) {
			suppressSourceNote = true;
			titleStartsWithList = null;
		} else {
			suppressSourceNote = false;
			if (suppressSourceNoteWhenTitleStartsWith.length() < 1) {
				titleStartsWithList = null;
			} else {
				titleStartsWithList = suppressSourceNoteWhenTitleStartsWith.split(";");
			}
		}
	}
	
	public void setArbre(ArbreDeSosa arbre) {
		this.arbre = arbre;
	}

	public boolean keepOnlySourceTitle() {
		return Config.getKeepOnlySourceTitle();
	}
	
	public boolean titleStartToSuppresSourceNote(String title) {
		
		if (title != null) {
			for (String start : titleStartsWithList) {
				if (title.startsWith(start)) return true;
			}
		}
		return false;
	}
	
	private boolean isTagToSuppress(GedcomTag tag) {
		if ((tag != null) && (tag.getTagValue() != null)) { 
			return tagsToSuppress.contains(tag.getTagValue());
		} else {
			return false ;
		}
	}

	private boolean isContentToSuppress(String content) {
		
		if (content != null) {
			for (String sContent : contentsToSuppress) {
				if (content.equals(sContent)) return true;
			}
		}
		return false;
	}
	
	public boolean suppressSourceNote() {
		return suppressSourceNote;
	}
	
	public boolean anonymiseEmail() {
		return Config.getAnonymisationEmail();
	}
	
	// A family must be filtered if the marriage date is after a configured limit
	private boolean isToBefiltred(Family family) {
		
		LocalDate dateMariageMaximum = family.getDateMariageMaximum();
		return ((dateMariageMaximum != null) && (dateMariageMaximum.isAfter(anneeLimite)));
	}
	
	// an individual must be filtered if its birth date is after a configured limit
	private boolean isToBefiltred(Individual individual) {
		
		LocalDate dateNaissanceMaximum = individual.getDateNaissanceMaximum() ;
		return ((dateNaissanceMaximum != null) && (dateNaissanceMaximum.isAfter(anneeLimite)));
	}

	// Answer true if the gedcom line must be filtered
	// It must be filtered if the tag is part of a list of unwanted tags or 
	// if the content itself must be filtered, i.e if the contents is equal to a member of an unwanted content list
	public boolean isToBeFiltered(GedcomLine gLine) {
		return ((isTagToSuppress(gLine.getTag())) || (isContentToSuppress(gLine.getContent())));
	}

	private boolean isToBeSuppressed(GedcomSource source) {
		
		for (Individual ind : source.getIndividuals()) {
			if (! isToBefiltred(ind)) return false;
		}
		for (Family fam : source.getFamilies()) {
			if (! isToBefiltred(fam)) return false;
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
				return FiltreAction.FILTER;
			} else {
				return FiltreAction.SUPPRESS;
			}
		} else {
			return FiltreAction.NO_CHANGE;
		}
	}
	
	public FiltreAction getAction(Individual individual) {
		
		if (isToBefiltred(individual)) {
			if (arbre.faitPartieDesSosas(individual)) {
				return FiltreAction.FILTER;
			} else {
				return FiltreAction.SUPPRESS;
			}
		} else {
			return FiltreAction.NO_CHANGE;
		}
	}
	
	public FiltreAction getAction(GedcomNote note) {

		if (isOnlyLinkedToFilteredEntity(note)) {
			return FiltreAction.SUPPRESS;
		} else if (Config.getKeepOnlyOneLineNote()) {
			return FiltreAction.FILTER;
		} else {
			return FiltreAction.NO_CHANGE;
		}
	}

	public FiltreAction getAction(GedcomSource source) {

		if (isToBeSuppressed(source)) {
			return FiltreAction.SUPPRESS;
		} else {
			return FiltreAction.FILTER;
		}
	}

	public FiltreAction getAction(GedcomMultimediaObject multimediaObject) {

		if (isToBeSuppressed(multimediaObject)) {
			return FiltreAction.SUPPRESS;
		} else {
			return FiltreAction.FILTER;
		}
	}
	
	private boolean isOnlyLinkedToFilteredEntity(GedcomNote note) {
		
		for (Individual ind : note.getIndividuals()) {
			if (! isToBefiltred(ind)) return false;
		}
		for (Family fam : note.getFamilies()) {
			if (! isToBefiltred(fam)) return false;
		}
		for (GedcomSource src : note.getSources()) {
			if ((! keepOnlySourceTitle()) && 
				(! suppressSourceNote())  && 
				(! isToBeSuppressed(src)) && 
				(! titleStartToSuppresSourceNote(src.getSourceTitle()))) return false;
		}
		return true;
	}
}
