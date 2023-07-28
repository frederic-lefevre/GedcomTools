/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

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
import java.util.Arrays;
import java.util.List;

import org.fl.gedcomtools.filtre.GedcomSourceFiltre;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagValue;
import org.fl.gedcomtools.sourcetype.ActeEtatCivil;
import org.fl.gedcomtools.sourcetype.NomDeSource;
import org.fl.gedcomtools.sourcetype.NomDeSourceBuilder;

public class GedcomSource extends GedcomEntity {
		
	private static GedcomSourceFiltre filtre;
	
	private String 			 									sourceTitle ;
	private NomDeSource 	 									nomDeLaSource ;
	private List<Individual> 									individuals ;
	private List<Family> 	 									families ;
	private List<GedcomEntityReference<GedcomNote>> 			notes ;
	private List<String> 	 									mediaFiles ;
	private List<GedcomEntityReference<GedcomMultimediaObject>> multimedias ;

	public GedcomSource(GedcomLine gLineParts) {
		
		super(gLineParts);
		individuals = new ArrayList<>() ;
		families   	= new ArrayList<>() ;
		notes    	= new ArrayList<>() ;
		mediaFiles 	= new ArrayList<>() ;
		multimedias = new ArrayList<>() ;
	}
	
	private static List<GedcomTagValue> CONC_TITL_SOUR = Arrays.asList(GedcomTagValue.CONC, GedcomTagValue.TITL, GedcomTagValue.SOUR) ;
	
	public boolean completeAndCheckSource() {
		
		boolean success = true;
		
		// Complete source title
		for (GedcomLine gLine : gLines) {
			if ((gLine.getLevel() == 2) && (gLine.getTagChain().equals(CONC_TITL_SOUR))) {
					sourceTitle = sourceTitle + gLine.getContent() ;
			}
		}
		
		if (sourceTitle == null) {
			gLog.warning("La source (id=" + getId() + ") ne semble pas avoir de titre: \n" + getGedcomSource()) ;
			success = false;
		} else {
			// acte d'etat civil sans image
			nomDeLaSource = NomDeSourceBuilder.getNomDeSource(sourceTitle, gLog) ;
			if ((nomDeLaSource instanceof ActeEtatCivil) && hasNoMedia()) {
				gLog.warning("La source acte d'etat civil (id=" + getId() + ") n'a pas de fichier media: \n" + getGedcomSource()) ;
				success = false;
			}
		}
		
		// source non référencées
		if (hasNoReferences()) {
			gLog.warning("La source (id=" + getId() + ") ne semble pas être référencée: \n" + getGedcomSource()) ;
			success = false;
		}
		return success;
	}
	
	public boolean hasNoReferences() {
		return ((individuals.size() < 1) && (families.size() < 1)) ;
	}
	
	public void addIndividual(Individual i) {
		individuals.add(i) ;
	}
	
	public void addFamily(Family f) {
		families.add(f) ;
	}
	
	public List<GedcomEntityReference<GedcomNote>> getNotes() {
		return notes ;
	}

	public void addNote(GedcomEntityReference<GedcomNote> note) {
		notes.add(note) ;
	}
	
	public List<GedcomEntityReference<GedcomMultimediaObject>> getMultimedias() {
		return multimedias;
	}

	public void addMultimedia(GedcomEntityReference<GedcomMultimediaObject> multimedia) {
		multimedias.add(multimedia);
	}
	
	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String st) {
		sourceTitle = st ;
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
				((mediaFiles == null) || (mediaFiles.isEmpty()))) ;
	}
	
	public void addMediaFile(String mf) {
		mediaFiles.add(mf) ;
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomSourceFiltre filtre) {
		GedcomSource.filtre = filtre;
	}
}
