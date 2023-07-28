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

import org.fl.gedcomtools.entity.GedcomMultimediaObject;
import org.fl.gedcomtools.line.GedcomLine;

public class GedcomMultimediaObjectFiltre extends GedcomEntityFiltre {

	public GedcomMultimediaObjectFiltre(GedcomFiltreCondition fc) {
		super(fc);
	}

	public StringBuilder filtre(GedcomMultimediaObject multimediaObject) {
		
		switch (filtreCondition.getAction(multimediaObject)) {
			
		case SUPPRESS:
			gLog.finest(() -> "Objet multimedia supprimé: " + multimediaObject.getGedcomSource());
			return  new StringBuilder("") ;
			
		case FILTER:
			gLog.finest(() -> "Objet multimedia filtrée: " + multimediaObject.getGedcomSource());
			
			StringBuilder filteredGedcom = new StringBuilder() ;			
			List<GedcomLine> gLines = multimediaObject.getGedcomLines();
			
			// add the first line (level 0)
			filteredGedcom.append(gLines.get(0).getOriginalLine()) ;

			// filter the other lines
			for (GedcomLine gLine : gLines) {
				if ((gLine.getLevel() > 0) && (! filtreCondition.isToBeFiltered(gLine))) {
					filteredGedcom.append(gLine.getOriginalLine()) ;		
				}
			}
			return super.anonymisationAdresseMail(filteredGedcom) ;
			
		default :
			// should not happen
			gLog.warning("Unknown filtre action : " + filtreCondition.getAction(multimediaObject));
			return super.filtre(multimediaObject) ;
		}
	}
}
