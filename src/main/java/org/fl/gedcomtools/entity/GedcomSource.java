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

import java.util.ArrayList;
import java.util.List;

import org.fl.gedcomtools.filtre.GedcomSourceFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.sourcetype.ActeEtatCivil;
import org.fl.gedcomtools.sourcetype.GenealogySource;
import org.fl.gedcomtools.sourcetype.GenealogySourceBuilder;

public class GedcomSource extends GedcomEntity {
		
	private static GedcomSourceFiltre filtre;
	
	private StringBuilder sourceTitle;
	private GenealogySource genealogySource;
	private final List<Individual> individuals;
	private final List<Family> families;
	private final List<GedcomEntityReference<GedcomNote>> notes;
	private final List<String> mediaFiles;
	private final List<GedcomEntityReference<GedcomMultimediaObject>> multimedias;

	public GedcomSource(GedcomLine gLineParts) {

		super(gLineParts);
		individuals = new ArrayList<>();
		families = new ArrayList<>();
		notes = new ArrayList<>();
		mediaFiles = new ArrayList<>();
		multimedias = new ArrayList<>();
		sourceTitle = new StringBuilder(120);
	}
	
	public boolean createGenealogySource() {
		
		boolean success = true;
		String sourceTitleString = sourceTitle.toString();
		if (sourceTitleString.isBlank()) {
			gLog.warning("La source (id=" + getId() + ") ne semble pas avoir de titre: \n" + getGedcomSource());
			success = false;
		} else {
			// acte d'etat civil sans image
			genealogySource = GenealogySourceBuilder.getGenealogySource(sourceTitleString);
			if ((genealogySource instanceof ActeEtatCivil) && hasNoMedia()) {
				gLog.warning("La source acte d'etat civil (id=" + getId() + ") n'a pas de fichier media: \n" + getGedcomSource());
				success = false;
			}
		}
		return success;
	}
	
	public boolean checkSource() {
		
		boolean success = true;
		// source non référencées
		if (hasNoReferences()) {
			gLog.warning("La source (id=" + getId() + ") ne semble pas être référencée: \n" + getGedcomSource());
			success = false;
		}
		return success;
	}
	
	public boolean hasNoReferences() {
		return ((individuals.size() < 1) && (families.size() < 1));
	}

	public void addIndividual(Individual i) {
		individuals.add(i);
	}

	public void addFamily(Family f) {
		families.add(f);
	}

	public List<GedcomEntityReference<GedcomNote>> getNotes() {
		return notes;
	}

	public void addNote(GedcomEntityReference<GedcomNote> note) {
		notes.add(note);
	}
	
	public List<GedcomEntityReference<GedcomMultimediaObject>> getMultimedias() {
		return multimedias;
	}

	public void addMultimedia(GedcomEntityReference<GedcomMultimediaObject> multimedia) {
		multimedias.add(multimedia);
	}
	
	public String getSourceTitle() {
		return sourceTitle.toString();
	}

	public void appendToSourceTitle(String titlePart) {
		sourceTitle.append(titlePart);
	}
	
	public List<Individual> getIndividuals() {
		return individuals;
	}

	public List<Family> getFamilies() {
		return families;
	}

	public List<String> getMediaFiles() {
		return mediaFiles;
	}
	
	private boolean hasNoMedia() {
		return (((multimedias == null) || (multimedias.isEmpty())) &&
				((mediaFiles == null) || (mediaFiles.isEmpty())));
	}
	
	public void addMediaFile(String mf) {
		mediaFiles.add(mf);
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this);
	}
	
	public static void setFiltre(GedcomSourceFiltre filtre) {
		GedcomSource.filtre = filtre;
	}
}
