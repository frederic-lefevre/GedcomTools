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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.fl.gedcomtools.entity.Family;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomFamilyFiltre  extends GedcomEntityFiltre {

	private DateTimeFormatter formatter ;
	
	public GedcomFamilyFiltre(GedcomFiltreCondition fc) {
		super(fc);
		formatter = DateTimeFormatter.ofPattern(datePattern, Locale.FRANCE);		
	}

	private final static String datePattern = "dd L uuuu";
	
	public StringBuilder filtre(Family family) {
		
		return switch (filtreCondition.getAction(family)) {
		
		case SUPPRESS -> {
			addAction("Eliminé: " + family.getId() + "; Mariage: " + family.getDateMariageMaximum().format(formatter));
			yield new StringBuilder("");
		}
		case FILTER -> {
			addAction("Filtré: " + family.getId() + "; Mariage: " + family.getDateMariageMaximum().format(formatter)) ;
			
			StringBuilder filteredGedcom = new StringBuilder() ;
			List<GedcomLine> gLines = family.getGedcomLines() ;
			
			// keep the first line
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;
			
			// filter the other : keep only wife, husband and children
			for (GedcomLine gLineParts : gLines) {
				if ((gLineParts.tagValueEquals(GedcomTagValue.WIFE)) ||	
					(gLineParts.tagValueEquals(GedcomTagValue.HUSB))||  
					(gLineParts.tagValueEquals(GedcomTagValue.CHIL))) {
				
					filteredGedcom.append(gLineParts.getOriginalLine()) ;
				}
			}
			yield filteredGedcom ;
		}
		case NO_CHANGE -> {
			yield super.filtre(family);
		}	
		};
	}	
}
