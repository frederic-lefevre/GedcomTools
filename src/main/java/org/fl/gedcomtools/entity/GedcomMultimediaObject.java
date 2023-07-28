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
import java.util.List;

import org.fl.gedcomtools.filtre.GedcomMultimediaObjectFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObject extends GedcomEntity {

	private static GedcomMultimediaObjectFiltre filtre;
	
	private List<Individual>   individuals ;
	private List<Family> 	   families ;
	private List<GedcomSource> sources ;
	
	private String mediaFileType;
	public String getMediaFileType() {
		return mediaFileType;
	}

	private String mediaFileName;
	
	public GedcomMultimediaObject(GedcomLine gLine) {
		super(gLine);
		mediaFileType = null;
		mediaFileName = null;
		individuals = new ArrayList<>() ;
		families = new ArrayList<>() ;
		sources = new ArrayList<>() ;
	}

	public void setMediaFileType(String mediaFileType) {
		this.mediaFileType = mediaFileType;
	}

	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
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
	
	public List<Individual> getIndividuals() {
		return individuals;
	}

	public List<GedcomSource> getSources() {
		return sources;
	}

	public List<Family> getFamilies() {
		return families;
	}
	
	public StringBuilder filtre() {
		return filtre.filtre(this) ;
	}
	
	public static void setFiltre(GedcomMultimediaObjectFiltre filtre) {
		GedcomMultimediaObject.filtre = filtre;
	}
}
