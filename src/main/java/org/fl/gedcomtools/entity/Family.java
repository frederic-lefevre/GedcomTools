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
import java.util.ArrayList;
import java.util.List;

import org.fl.gedcomtools.filtre.GedcomFamilyFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.util.GedcomDateValue;

public class Family extends GedcomEntity {
	
	private static GedcomFamilyFiltre filtre;
	
	private LocalDate dateMariageMaximum;
	private final List<GedcomEntityReference<Individual>> children;
	private final List<GedcomEntityReference<GedcomSource>> sources;
	private final List<GedcomEntityReference<GedcomNote>> notes;
	private GedcomEntityReference<Individual> wifeRef;
	private GedcomEntityReference<Individual> husbandRef;

	private GedcomDateValue dateMariage;

	public Family(GedcomLine gLineParts) {

		super(gLineParts);
		dateMariage = null;
		dateMariageMaximum = null;
		wifeRef = null;
		husbandRef = null;

		children = new ArrayList<>();
		sources = new ArrayList<>();
		notes = new ArrayList<>();
	}

	public Individual getWife() {
		if (wifeRef != null) {
			return wifeRef.getEntity();
		} else {
			return null;
		}
	}

	public Individual getHusband() {
		if (husbandRef != null) {
			return husbandRef.getEntity();
		} else {
			return null;
		}
	}
	
	public void setWifeRef(GedcomEntityReference<Individual> wifeRef) {
		this.wifeRef = wifeRef;
	}

	public void setHusbandRef(GedcomEntityReference<Individual> husbandRef) {
		this.husbandRef = husbandRef;
	}

	public void addChild(GedcomEntityReference<Individual> child) {
		children.add(child);
	}

	public void setDateMariage(GedcomDateValue dm) {
		dateMariage = dm;
		if (dateMariage.isValid()) {
			dateMariageMaximum = dateMariage.getMaxDate();
		} else {
			gLog.warning("Date de mariage invalide pour le mariage: \n" + getGedcomSource());
		}
	}

	public List<GedcomEntityReference<GedcomSource>> getSources() {
		return sources;
	}

	public void addSource(GedcomEntityReference<GedcomSource> source) {
		sources.add(source);
	}

	public List<GedcomEntityReference<GedcomNote>> getNotes() {
		return notes;
	}

	public void addNote(GedcomEntityReference<GedcomNote> note) {
		notes.add(note);
	}

	public LocalDate getDateMariageMaximum() {
		return dateMariageMaximum;
	}

	public boolean hasMarriageDateInfo() {
		return ((dateMariage != null) && (dateMariage.isValid()));
	}

	public StringBuilder filtre() {
		return filtre.filtre(this);
	}

	public static void setFiltre(GedcomFamilyFiltre filtre) {
		Family.filtre = filtre;
	}

}
