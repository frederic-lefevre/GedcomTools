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
import java.util.logging.Logger;

import org.fl.gedcomtools.filtre.GedcomEntityFiltre;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomEntity {

	protected static final Logger gLog = Logger.getLogger(GedcomEntity.class.getName());
	
	private static GedcomEntityFiltre filtre ;
	
	protected List<GedcomLine> gLines ; 
	private   String 		   id;
	
	public GedcomEntity(GedcomLine gLine) {
		
		if (gLine.getLevel() != 0) {
			gLog.severe("Erreur dans la création d'une entité Gedcom, la première ligne n'est pas de niveau 0: " + gLine.getOriginalLine());
		} else {	
			gLines = new ArrayList<>();
			gLines.add(gLine);
			id = gLine.getId();
		}	
	}

	public void addGedcomLine (GedcomLine gLine) {	
		gLines.add(gLine) ;
	}
	
	public StringBuilder getGedcomSource() {
		StringBuilder gedcomSource = new StringBuilder();
		for (GedcomLine gLine : gLines) {
			gedcomSource.append(gLine.getOriginalLine());
		}
		return gedcomSource;
	}

	public String getId() {
		return id;
	}
	
	public List<GedcomLine> getGedcomLines() {
		return gLines;
	}

	public StringBuilder filtre() {
		return filtre.filtre(this);
	}
	
	public static void setFiltre(GedcomEntityFiltre filtre) {
		GedcomEntity.filtre = filtre;
	}
}
