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

package org.fl.gedcomtools.filtre;

import java.util.List;

import org.fl.gedcomtools.entity.GedcomSource;
import org.fl.gedcomtools.line.GedcomLine;
import org.fl.gedcomtools.line.GedcomTag;
import org.fl.gedcomtools.line.GedcomTagValue;

public class GedcomSourceFiltre extends GedcomEntityFiltre {

	public GedcomSourceFiltre(GedcomFiltreCondition fc) {
		super(fc);

	}

	public StringBuilder filtre(GedcomSource source) {

		switch (filtreCondition.getAction(source)) {

		case SUPPRESS:
			gLog.finest(() -> "Source supprimée: " + source.getGedcomSource()) ;
			return  new StringBuilder("") ;

		case FILTER:
			gLog.finest(() -> "Source filtrée: " + source.getGedcomSource()) ;

			StringBuilder filteredGedcom = new StringBuilder() ;
			List<GedcomLine> gLines = source.getGedcomLines() ;

			// add the first line (level 0)
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;

			// filter the other lines
			GedcomTag currentLevelOneTag = null ;
			for (GedcomLine gLine : gLines) {
				if (! filtreCondition.isToBeFiltered(gLine)) {
					if (gLine.getLevel() == 1) {
						currentLevelOneTag = gLine.getTag() ;
						if (gLine.tagValueEquals(GedcomTagValue.TITL)) {
							filteredGedcom.append(gLine.getOriginalLine()) ;
						} else if ((! filtreCondition.keepOnlySourceTitle()) && (! filtreCondition.suppressSourceNote())) {
							if  (gLine.tagValueEquals(GedcomTagValue.NOTE)) {
								if (! filtreCondition.titleStartToSuppresSourceNote(source.getSourceTitle())) {
									filteredGedcom.append(gLine.getOriginalLine()) ;
								}
							}
						}
					} else if ((gLine.getLevel() == 2) && 
								(currentLevelOneTag.equalsValue(GedcomTagValue.TITL)) &&
								(gLine.tagValueEquals(GedcomTagValue.CONC))) {
						// long source title split on 2 lines (or more)
						filteredGedcom.append(gLine.getOriginalLine()) ;
					}
				} 
			}
			return super.anonymisationAdresseMail(filteredGedcom) ;

		default :
			// should not happen
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(source));
			return super.filtre(source) ;
		}


	}

}
